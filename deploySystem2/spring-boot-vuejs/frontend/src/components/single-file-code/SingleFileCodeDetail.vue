<template>
        <Row type="flex" justify="center" class="code-row-bg">
            <Col span="8">
                <AceEditor v-model="curStatus.code" @init="editorInit" :lang="editorLang" theme="chrome" width="100%" height="800px"></AceEditor>
            </Col>
            <Col span="4">
                <div>
                    Name:
                    <Input v-model="curStatus.name" placeholder="Enter name" style="width: auto"/>
                    <Button style="margin-left: 10px" type="primary" shape="circle" icon="md-refresh"></Button>
                </div>
                <div style="margin-top: 20px">
                    Description:
                    <Input v-model="curStatus.description" placeholder="Enter description" type="textarea" style="width: auto" icon="md-refresh" />
                    <Button style="margin-left: 10px" type="primary" shape="circle" icon="md-refresh"></Button>
                </div>
                <div style="margin-top: 20px">
                    Type:
                    <Select v-model="curStatus.type" style="width: auto">
                        <Option v-for="type in supportTypes" :value="type" :key="type">{{ type }}</Option>
                    </Select>
                </div>
                <div style="margin-top: 20px">
                    Stdin:
                    <Input v-model="runStdin" placeholder="Input stdin here" type="textarea" style="width: auto" icon="md-refresh" />
                    <Button style="margin-left: 10px" type="primary" shape="circle" icon="md-refresh"></Button>
                </div>
                <div style="margin-top: 20px">
                    <Button type="success" long> Save </Button>
                    <br><br>
                    <Button type="success" long> Run </Button>
                    <br><br>
                    <Button type="success" long> Check Result </Button>
                    <br><br>
                    <Button type="error" long> Reset </Button>
                    <br><br>
                    <Button type="error" long> Delete </Button>
                    <br><br>
                </div>
            </Col>
            <Col span="4">
            </Col>
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
                'if __name__ == "__main__":\n' +
                '   print(\'Hello World\')',
                cpp: 
                '# include<iostream>\n' +
                '\n' + 
                'int main(int argc, char *argv[]) {\n' +
                '   std::cout << "Hello World" << std::endl;\n' +
                '}',
            },
            pageMode: 'create',  // pageMode can be 'create' or 'update'
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
        saveChange() {
        },
        runCode() {
        },
    },
    computed: {
        editorLang() {
            if (this.curStatus.type === 'cpp') {
                return "c_cpp";
            } else {
                return this.curStatus.type;
            }
        }
    },
    created() {
        if (this.$route.params.id === "new") {
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
            this.curStatus.code = this.defaultCode[newType];
        }
    },
}
</script>