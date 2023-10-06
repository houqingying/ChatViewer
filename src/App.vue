<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script setup>

import { onMounted } from "vue";

onMounted(() => {

});

</script>

<style>

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

html,
body{
  margin: 0;
  padding: 0;
}

@media (prefers-color-scheme: light) {
  /* Your styles for light mode */
}
</style>
