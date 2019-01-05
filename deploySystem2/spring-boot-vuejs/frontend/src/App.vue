<template>
    <div class="layout">
        <Layout>
            <Header :style="{position: 'fixed', width: '100%'}">
                <Menu mode="horizontal" theme="dark" active-name="1">
                    <div class="layout-logo"></div>
                    <div class="layout-nav">
                        <MenuItem name="1">
                            <Icon type="ios-navigate"></Icon>
                            <router-link class="nav-router-line" to="/single-file-code">Single File Code</router-link>
                        </MenuItem>
                        <MenuItem name="2">
                            <Icon type="ios-keypad"></Icon>
                            <router-link class="nav-router-line" to="/web-code">Web Code</router-link>
                        </MenuItem>
                    </div>
                </Menu>
            </Header>
            <Content :style="{margin: '88px 20px 0', background: '#fff', minHeight: '500px'}">
              <router-view />
            </Content>
            <Footer class="layout-footer-center">2019 &copy; Xinyuan Gui</Footer>
        </Layout>
    </div>
</template>
<style scoped>
    .layout{
        border: 1px solid #d7dde4;
        background: #f5f7f9;
        position: relative;
        border-radius: 4px;
        overflow: hidden;
    }
    .layout-logo{
        width: 100px;
        height: 40px;
        background: url(../src/assets/DPS.png) no-repeat center center; 
        border-radius: 3px;
        float: left;
        position: relative;
        top: 15px;
        left: 20px;
    }
    .layout-nav{
        width: 420px;
        margin: 0 auto;
        margin-right: 20px;
    }
    .layout-footer-center{
        text-align: center;
    }
    .nav-router-line {
        color: white;
    }
    .ivu-layout-header {
        z-index: 999;
    }
</style>

<script>
export default {
    name: "app",
}
</script>