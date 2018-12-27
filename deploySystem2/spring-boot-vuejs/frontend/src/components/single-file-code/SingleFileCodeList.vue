<template>
    <div>
        <Table :columns="singleFileCodeTableColumns" :data="singleFileCodes"></Table>
        <Button><router-link to="/single-file-code/1">Create New Single File System</router-link> </Button>
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
                },
                {
                    title: 'Type',
                    key: 'type',
                },
                {
                    title: 'Description',
                    key: 'description',
                },
            ],
            singleFileCodes: []
        }
    },
    mounted() {
        var apiUrl = "/api/single-file-code";
        axios
            .get(apiUrl)
            .then((response) => {
                this.singleFileCodes = response.data;
            })
            .catch(function (error) {
                alert(error);
            });
    },
}
</script>