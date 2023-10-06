<template>
  <div class="head-portrait">
    <img :src="imgUrl == null ? 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png': imgUrl" alt="">
  </div>
</template>

<script setup>

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps({
  imgUrl: String
})

</script>

<style lang="scss" scoped>
.head-portrait {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    // border: 2px solid rgb(137,140,151);
    border: 2px solid rgb(255, 255, 255);
    position:relative;
    img {
        width: 45px;
        height: 45px;
        border-radius: 50%;
        // padding: 2px;
        box-sizing: border-box;
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        vertical-align: middle;
    }
}
</style>
