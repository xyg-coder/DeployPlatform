import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import SingleFileCodeList from './components/single-file-code/SingleFileCodeList'
import SingleFileCodeDetail from './components/single-file-code/SingleFileCodeDetail'
import WebCodeList from './components/web-code/WebCodeList';

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/single-file-code',
      name: 'single-file-code',
      component: SingleFileCodeList
    },
    {
      path: '/single-file-code/:id',
      name: 'single-file-code-detail',
      component: SingleFileCodeDetail
    },
    {
      path: '/single-file-code/new',
      name: 'new-single-file-code',
      component: SingleFileCodeDetail
    },
    {
      path: '/web-code',
      name: 'web-code',
      component: WebCodeList
    }
  ]
})
