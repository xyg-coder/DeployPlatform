<template>
    <div>
        <Table :columns="singleFileCodeTableColumns" :data="singleFileCodes"></Table>
        <Page :total="totalCount" :page-size="pageSize" @on-change="getSingleFileCodes" show-total/>
        <Button><router-link to="/single-file-code/new">Create New Single File System</router-link> </Button>
        <Modal v-model="codeModal" width="720" :scrollable="true" :closable="false">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="ios-information-circle"></Icon>
                <span>Code</span>
            </p>
            <xmp>{{codeModalCode}}</xmp>
            <div slot="footer">
                <Button type="info" size="large" long @click="codeModal = false">Close</Button>
            </div>
        </Modal>
    </div>
</template>
<script>
import axios from 'axios';
/* eslint-disable */
export default {
    name: "singleFileCodeList",
    data() {
        return {
            singleFileCodeTableColumns: [
                {
                    title: 'Name',
                    key: 'name',
                    render: (h, params) =>{
                        var id = params.row.id;
                        var name = params.row.name;
                        return h('router-link', {
                            props: {
                                to: `/single-file-code/${id}`,
                            }
                        }, name);
                    }
                },
                {
                    title: 'Type',
                    key: 'type',
                },
                {
                    title: 'Description',
                    key: 'description',
                },
                {
                    title: 'Action',
                    key: 'action',
                    width: 150,
                    align: 'center',
                    render: (h, params) => {
                        return h('div', [
                            h('Button', {
                                props: {
                                    type: 'primary',
                                    size: 'small'
                                }, style: {
                                    marginRight: '5px',
                                }, on: {
                                    click: () => {
                                        var id = params.row.id;
                                        this.showCodeById(id);
                                    }
                                }
                            }, 'View Code')
                        ]);
                    }
                }
            ],
            singleFileCodes: [],
            totalCount: 0,
            pageSize: 10,
            codeModal: false,
            codeModalCode: '',
        }
    },
    mounted() {
        this.getTotalCount();
        this.getSingleFileCodes(1);
    },
    methods: {
        showCodeById(id) {
            var apiUrl = `/api/single-file-code/${id}`;
            var _this = this;
            axios
                .get(apiUrl)
                .then((response) => {
                    var code = response.data.code;
                    _this.codeModalCode = code;
                    _this.codeModal = true;
                })
                .catch(function(error) {
                    alert(error);
                });
        },
        getTotalCount() {
            var apiUrl = "/api/single-file-code/count";
            axios
                .get(apiUrl)
                .then((response) => {
                    this.totalCount = response.data;
                })
                .catch((error) => {
                    alert(error);
                });
        },
        getSingleFileCodes(pageIndex) {
            console.log("getSingleFileCodes is called", pageIndex);
            var apiUrl = `/api/single-file-code?pageIndex=${pageIndex - 1}&countPerPage=${this.pageSize}`;
            axios
                .get(apiUrl)
                .then((response) => {
                    this.singleFileCodes = response.data;
                })
                .catch((error) => {
                    alert(error);
                });
        }
    },
}
</script>
<style scoped>
xmp {
    white-space: pre-wrap;       /* css-3 */
    white-space: -moz-pre-wrap;  /* Mozilla, since 1999 */
    white-space: -pre-wrap;      /* Opera 4-6 */
    white-space: -o-pre-wrap;    /* Opera 7 */
    word-wrap: break-word;       /* Internet Explorer 5.5+ */
}
</style>