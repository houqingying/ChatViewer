<template>
  <div> {{ this.$data.token }} </div>
</template>

<script setup >
import { useUserStore } from "@/stores/user";
import {reactive} from "vue";

const userStore = reactive(useUserStore())

</script>

<style scoped>

</style>
