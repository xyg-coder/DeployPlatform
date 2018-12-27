<template>
        <Row type="flex" justify="center" align="top" class="code-row-bg">
            <Col span="16">
            <AceEditor v-model="content" @init="editorInit" lang="java" theme="chrome" width="50%" height="800px"></AceEditor>
            </Col>
            <Col span="8">
            Col 8
            </Col>
        </Row>
</template>

<style>
    
</style>

<script>
import AceEditor from 'vue2-ace-editor';
/* eslint-disable */
export default {
    name: "singleFileCodeDetail",
    data() {
        return {
            content: "// this is java mode, enjoy coding",
        }
    },
    components: {
        AceEditor
    },
    methods: {
        editorInit: function () {
            require('brace/ext/language_tools') //language extension prerequsite...
            require('brace/mode/java')    //language
            require('brace/mode/less')
            require('brace/theme/chrome')
            require('brace/snippets/javascript') //snippet
        }
    },
}
</script>