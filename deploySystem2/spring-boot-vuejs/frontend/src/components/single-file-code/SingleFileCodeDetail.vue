<template>
        <Row type="flex" justify="center" class="code-row-bg">
            <Col span="8">
                <Button type="primary" icon="md-refresh" v-on:click="resetCodeToOrigin">Reset To Origin</Button>
                <Button type="primary" icon="md-refresh" style="margin-left: 10px" v-on:click="curStatus.code=defaultCode[curStatus.type]">Reset To Default</Button>
                <AceEditor v-model="curStatus.code" @init="editorInit" :lang="editorLang" theme="chrome" width="100%" height="800px" style="margin-top: 10px"></AceEditor>
            </Col>
            <Col span="4">
                <div>
                    Name:
                    <Input v-model="curStatus.name" placeholder="Enter name" style="width: auto"/>
                    <Button style="margin-left: 10px" type="primary" shape="circle" icon="md-refresh" v-on:click="curStatus.name=originStatus.name"></Button>
                </div>
                <div style="margin-top: 20px">
                    Description:
                    <Input v-model="curStatus.description" placeholder="Enter description" type="textarea" style="width: auto" icon="md-refresh" />
                    <Button style="margin-left: 10px" type="primary" shape="circle" icon="md-refresh" v-on:click="curStatus.description=originStatus.description"></Button>
                </div>
                <div style="margin-top: 20px">
                    Type:
                    <Select v-model="curStatus.type" style="width: auto">
                        <Option v-for="type in supportTypes" :value="type" :key="type">{{ type }}</Option>
                    </Select>
                </div>
                <div style="margin-top: 20px">
                    Stdin:(don't press enter, use escape character)
                    <Input v-model="runStdin" placeholder="Input stdin here" type="textarea" style="width: auto" icon="md-refresh" />
                    <Button style="margin-left: 10px" type="primary" shape="circle" icon="md-refresh" v-on:click="runStdin=''"></Button>
                </div>
                <div v-if="canUploadFile" style="margin-top: 20px">
                    <Upload 
                        :action="uploadUrl" :show-upload-list="false" :max-size="maxUploadSize"
                        :on-exceeded-size="uploadSizeExceed" :on-progress="disableFileOperations"
                        :on-success="uploadFileSuccess" :on-error="uploadFileFail">
                        <Button icon="ios-cloud-upload-outline">Upload files</Button>
                    </Upload>
                    <br/>
                    <Button v-if="canUploadFile" v-on:click="getFileList">Operate Files</Button>
                </div>
                <div style="margin-top: 20px">
                    <Button type="success" :disabled="saveDisabled" long v-on:click="saveUpdate"> Save </Button>
                    <br><br>
                    <Button type="success" :disabled="!canRun" long v-on:click="runCode"> Run </Button>
                    <br><br>
                    <Button type="success" long v-on:click="fileReadModalOpen('result.log', 'Result Log')" :disabled="!canCheckResult"> Check Result </Button>
                    <br><br>
                    <Button type="error" long v-on:click="reset"> Reset All </Button>
                    <br><br>
                    <Button type="error" :disabled="$route.params.id==='new'" v-on:click="deleteConfirmModal=true" long> Delete </Button>
                    <br><br>
                </div>
            </Col>
            <Col span="4">
            </Col>
            <Modal v-model="deleteConfirmModal" title="Delete Confirm" :loading="deleting" @on-ok="deleteCode">
                <p>Are you sure you want to delete this code?</p>
            </Modal>
            <Modal v-model="fileReadModal" width="720" :scrollable="true" :closable="false">
                <p slot="header" style="color:#f60;text-align:center">
                    <Icon type="ios-information-circle"></Icon>
                    <span>File Reading Modal</span>
                </p>
                <div style="text-align:center" v-html="fileReadModalContent">
                </div>
                <div slot="footer">
                    <Button type="info" size="large" long @click="fileReadModalClose">Close</Button>
                </div>
            </Modal>
            <Modal v-model="fileOperationModal">
                <div style="border-bottom: 1px solid #e9e9e9;padding-bottom:6px;margin-bottom:6px;">
                    <Checkbox
                        :indeterminate="indeterminate"
                        :value="checkAll"
                        @click.prevent.native="handleCheckAll">Pick All</Checkbox>
                </div>
                <CheckboxGroup v-model="checkAllGroup" @on-change="checkAllGroupChange">
                    <Checkbox v-for="item in browserFileList" :label="item"></Checkbox>
                </CheckboxGroup>
                <div slot="footer">
                    <Button type="error" :disabled="!canDeleteFile" v-on:click="deleteFiles" :loading="fileLoading">Delete</Button>
                    <Button type="info" :disabled="!canReadFile" :loading="fileLoading" v-on:click="readFile">Read</Button>
                    <Button type="info" :disabled="!canDownloadFile" :loading="fileLoading" v-on:click="downloadFile">Download</Button>
                </div>
            </Modal>
        </Row>
</template>

<style>
    
</style>

<script>
import AceEditor from 'vue2-ace-editor';
import axios from 'axios';
/* eslint-disable */
export default {
    name: "singleFileCodeDetail",
    data() {
        return {
            supportTypes: ['java', 'python', 'cpp'],
            runStdin: "",
            originStatus: {
                name: '',
                description: '',
                type: 'java',
                code: 
                '// Default Java Code. Please don\'t change class name\n'  + 
                'public class Main{\n'  + 
                '    public static void main(String[] args) {\n'  + 
                '        System.out.println("Hello World");\n'  + 
                '    }\n'  + 
                '}',
            },
            curStatus: {
                name: '',
                description: '',
                type: 'java',
                code: 
                '',
            },
            defaultCode: {
                java: 
                '// Default Java Code. Please don\'t change class name\n'  + 
                'public class Main{\n'  + 
                '    public static void main(String[] args) {\n'  + 
                '        System.out.println("Hello World");\n'  + 
                '    }\n'  + 
                '}',
                python: 
                'if __name__ == "__main__":\n',
                cpp: 
                '# include<iostream>\n' +
                '\n' + 
                'int main(int argc, char *argv[]) {\n' +
                '   std::cout << "Hello World" << std::endl;\n' +
                '}',
            },
            pageMode: 'create',  // pageMode can be 'create' or 'update'
            saveDisabled: false,
            deleting: false,
            deleteConfirmModal: false,
            logWebsocket: null,
            fileReadModalContent: '',
            fileReadModalTitle: '',
            fileReadModal: false,
            canCheckResult: true,
            uploadUrl: '',
            maxUploadSize: 1024,
            canUploadFile: true,
            browserFileList: [],
            fileOperationModal: false,
            indeterminate: true,
            checkAll: false,
            checkAllGroup: [],
            fileLoading: false,
        }
    },
    components: {
        AceEditor
    },
    methods: {
        editorInit() {
            require('brace/ext/language_tools'); //language extension prerequsite...
            require('brace/mode/java');    //language
            require('brace/mode/c_cpp');
            require('brace/mode/python');
            require('brace/theme/chrome');
            require('brace/snippets/javascript'); //snippet
        },
        runCode() {
            console.log(this.runStdin);
            var id = this.$route.params.id;
            this.canRun = false;
            var _this = this;
            this.canCheckResult = false;
            // enable the log after 5 secons
            setTimeout(() => {
                _this.canCheckResult = true;
            }, 2000);
            axios
                .post("/api/single-file-code/run", {
                    id: id,
                    type: this.curStatus.type.toUpperCase(),
                    stdin: this.runStdin,
                })
                .then(function (response) {
                    console.log(response);
                    _this.canRun = true;
                })
                .catch(function (error) {
                    alert(error);
                    _this.canRun = true;
                });
        },
        reset() {
            this.curStatus.name = this.originStatus.name;
            this.curStatus.description = this.originStatus.description;
            this.curStatus.type = this.originStatus.type;
            this.curStatus.code = this.originStatus.code;
        },
        resetCodeToOrigin() {
            this.curStatus.code = this.originStatus.code;
            this.curStatus.type = this.originStatus.type;
        },
        saveUpdate() {
            if (this.curStatus.code === '' || this.curStatus.name === ''
            || this.curStatus.description === '' || this.curStatus.type === '') {
                alert('Please fill all inputs to save update');
                return;
            }
            var id = this.$route.params.id;
            if (id === 'new') {
                var apiUrl = `/api/single-file-code`;
                var _this = this;
                axios
                    .post(apiUrl, {
                        name: this.curStatus.name,
                        description: this.curStatus.description,
                        code: this.curStatus.code,
                        type: this.curStatus.type.toUpperCase(),
                    })
                    .then(function(response) {
                        _this.$router.push({path: `/single-file-code/${response.data.id}`});
                        _this.curStatus.name = response.data.name;
                        _this.originStatus.name = response.data.name;
                        _this.curStatus.description = response.data.description;
                        _this.originStatus.description = response.data.description;
                        _this.curStatus.code = response.data.code;
                        _this.originStatus.code = response.data.code;
                        _this.curStatus.type = response.data.type.toLowerCase();
                        _this.originStatus.type = response.data.type.toLowerCase();
                        _this.saveDisabled = false;
                    })
                    .catch(function(error) {
                        alert(error);
                        _this.saveDisabled = false;
                    });
                this.saveDisabled = true;
            } else {
                var apiUrl = `/api/single-file-code/${id}`;
                var _this = this;
                axios
                    .put(apiUrl, {
                        id: id,
                        name: this.curStatus.name,
                        description: this.curStatus.description,
                        code: this.curStatus.code,
                        type: this.curStatus.type.toUpperCase(),
                    })
                    .then(function(response) {
                        _this.curStatus.name = response.data.name;
                        _this.originStatus.name = response.data.name;
                        _this.curStatus.description = response.data.description;
                        _this.originStatus.description = response.data.description;
                        _this.curStatus.code = response.data.code;
                        _this.originStatus.code = response.data.code;
                        _this.curStatus.type = response.data.type.toLowerCase();
                        _this.originStatus.type = response.data.type.toLowerCase();
                        _this.saveDisabled = false;
                    })
                    .catch(function(error) {
                        alert(error);
                        _this.saveDisabled = false;
                    });
                this.saveDisabled = true;
            }
        },
        deleteCode() {
            this.deleting = true;
            var id = this.$route.params.id;
            if (id === 'new') {
                this.deleting = false;
                this.deleteConfirmModal = false;
                return;
            } else {
                var url = `/api/single-file-code/${id}`;
                var router = this.$router;
                var _this = this;
                axios
                    .delete(url)
                    .then(function (response) {
                        router.push('/single-file-code');
                    })
                    .catch(function (error) {
                        _this.deleting = false;
                        _this.deleteConfirmModal = false;
                        alert(error);
                    });
            }
        },
        fileReadModalOpen(fileName, title) {
            var id = this.$route.params.id;
            if (id === 'new') {
                alert('Please save and run first');
                return;
            }
            var url = `ws://${window.location.host}/file-read?type=singleFileCode&id=${id}&fileName=${fileName}`;
            this.logWebsocket = new WebSocket(url);
            var _this = this;
            this.logWebsocket.onmessage = function (ev) {
                _this.fileReadModalContent = _this.fileReadModalContent + ev.data;
            };
            this.fileReadModal = true;
            this.fileReadModalTitle = title;
        },
        fileReadModalClose() {
            this.fileReadModalContent = '';
            this.logWebsocket.close();
            this.fileReadModal = false;
        },
        uploadSizeExceed() {
            alert("File size exceeds the limit, the limit is " + this.maxUploadSize + "kb");
        },
        disableFileOperations() {
            this.canUploadFile = false;
        },
        uploadFileSuccess(response, file) {
            alert(file + " uploads successfully");
            this.canUploadFile = true;
        },
        uploadFileFail(error, file) {
            alert(file + " uploads failed " + error);
            this.canUploadFile = true;
        },
        getFileList() {
            var id = this.$route.params.id;
            if (id === "new") {
                return;
            }
            var url = `/api/single-file-code/file-list/${id}`;
            var _this = this;
            axios
                .get(url)
                .then(function (response) {
                    _this.browserFileList = response.data;
                    _this.fileOperationModal = true;
                })
                .catch(function (error) {
                    alert(error);
                });
        },
        handleCheckAll() {
            if (this.indeterminate) {
                this.checkAll = false;
            } else {
                this.checkAll = !this.checkAll;
            }
            this.indeterminate = false;

            if (this.checkAll) {
                this.checkAllGroup = this.browserFileList;
            } else {
                this.checkAllGroup = [];
            }
        },
        checkAllGroupChange (data) {
            if (data.length === this.browserFileList.length) {
                this.indeterminate = false;
                this.checkAll = true;
            } else if (data.length > 0) {
                this.indeterminate = true;
                this.checkAll = false;
            } else {
                this.indeterminate = false;
                this.checkAll = false;
            }
        },
        deleteFiles() {
            var id = this.$route.params.id;
            if (id === 'new') {
                return;
            }
            var api = `/api/single-file-code/file-list/${id}`;
            var deleteFiles = this.checkAllGroup;
            var _this = this;
            this.fileLoading = true;
            axios
                .post(api, {
                    names: deleteFiles,
                })
                .then(function (response) {
                    _this.browserFileList = response.data;
                    _this.fileLoading = false;
                })
                .catch(function (error) {
                    alert(error);
                    _this.fileLoading = false;
                });
        },
        readFile() {
            var fileName = this.checkAllGroup[0];
            this.fileReadModalOpen(fileName, fileName);
        },
        downloadFile() {
            var id = this.$route.params.id;
            if (id === 'new') {
                return;
            }
            var fileName = this.checkAllGroup[0];
            var apiUrl = `/api/single-file-code/file-list/${id}?fileName=${fileName}`;
            axios({
                url: apiUrl,
                method: 'GET',
                responseType: 'blob',
            }).then((response) => {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', fileName);
                document.body.appendChild(link);
                link.click();
            });
        }
    },
    computed: {
        editorLang() {
            if (this.curStatus.type === 'cpp') {
                return "c_cpp";
            } else {
                return this.curStatus.type;
            }
        },
        canRun() {
            return this.curStatus.code === this.originStatus.code &&
            this.$route.params.id !== "new";
        },
        canDeleteFile() {
            return this.checkAllGroup.length > 0;
        },
        canReadFile() {
            return this.checkAllGroup.length === 1;
        },
        canDownloadFile() {
            return this.checkAllGroup.length === 1;
        },
    },
    created() {
        if (this.$route.params.id === "new") {
            this.canUploadFile = false;
            this.pageMode = 'create';
            this.originStatus = {
                name: '',
                description: '',
                type: 'java',
                code: this.defaultCode.java
            };
            this.curStatus = {
                name: '',
                description: '',
                type: 'java',
                code: this.defaultCode.java
            }
        } else {
            this.pageMode = 'update';
            var id = this.$route.params.id;
            var url = `/api/single-file-code/${id}`;
            this.uploadUrl = `/api/single-file-code/upload/${id}`;
            axios
                .get(url)
                .then((response) => {
                    this.originStatus = {
                        name: response.data.name,
                        description: response.data.description,
                        type: response.data.type.toLowerCase(),
                        code: response.data.code,
                    };
                    this.curStatus = {
                        name: response.data.name,
                        description: response.data.description,
                        type: response.data.type.toLowerCase(),
                        code: response.data.code,
                    };
                });
        }
    },
    watch: {
        'curStatus.type': function (newType) {
            if (this.curStatus.code !== this.originStatus.code || 
            this.curStatus.type !== this.originStatus.type) {
                this.curStatus.code = this.defaultCode[newType];
            }
        },
        '$route.params.id': function (newId) {
            if (newId === 'new') {
                this.canUploadFile = false;
            } else {
                this.canUploadFile = true;
                this.uploadUrl = `/api/single-file-code/upload/${newId}`;
            }
        }
    },
}
</script>