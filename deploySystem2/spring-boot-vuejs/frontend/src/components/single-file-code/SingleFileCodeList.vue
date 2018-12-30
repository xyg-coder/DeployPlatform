<template>
    <div>
        <Table :columns="singleFileCodeTableColumns" :data="singleFileCodes"></Table>
        <Page :total="totalCount" :page-size="pageSize" @on-change="getSingleFileCodes" show-total/>
        <Button><router-link to="/single-file-code/new">Create New Single File System</router-link> </Button>
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
        }
    },
    mounted() {
        this.getTotalCount();
        this.getSingleFileCodes(1);
    },
    methods: {
        showCodeById(id) {
            var apiUrl = `/api/single-file-code/${id}`;
            axios
                .get(apiUrl)
                .then((response) => {
                    var code = response.data.code;
                    this.$Modal.info({
                        render: (h) => {
                            return h('xmp', {}, code);
                        }
                    })
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