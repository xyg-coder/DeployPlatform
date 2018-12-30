<template>
  <div class="home">
    <h2>Welcome to Deploy System.</h2>
    <div id="nav">
      <router-link to="/single-file-code">Single File Code</router-link> |
      <router-link to="/web-code">Web Code</router-link>
    </div> 
  </div>
</template>

<script>
// @ is an alias to /src

export default {
  name: 'home',
  components: {
  }
}
</script>

<style scoped>
.home {
  height: 100px;
  width: 30%;
  text-align:center;
  line-height:100px;
}
#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}

</style>