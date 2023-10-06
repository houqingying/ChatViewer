<template>
  <div class="card">
    <div class="imgBx">
      <el-image :src="articleInfo.userAvatar" style="width: 100%;height: 100%;" :fit="'cover'">
        <template #error>
          <div class="image-slot">
            <el-icon style="width:100%;height:100%;margin: auto" :size="150" color="#bbc7db"><Loading /></el-icon>
          </div>
        </template>
      </el-image>
    </div>
    <div class="content">
      <div class="details">
        <h2>{{ articleInfo.userName }}<br><span>Hello World!</span></h2>
        <div class="actionBtn">
          <el-button :size="'large'" @click="open" v-if="userStore.token !== '' && articleInfo.userId !== userStore.user.userId">Message</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Loading } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reactive, defineProps } from "vue";
import { useUserStore } from "@/stores/user";
import { sendChatMsg } from "@/api/getData";

const userStore = reactive(useUserStore())

const props = defineProps({
  articleInfo: Object
})

const open = () => {
  ElMessageBox.prompt('想对作者说些什么呢？', 'Message', {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel',
    inputPattern: /.+/,
    inputErrorMessage: '不可以发送空消息哦',
  })
      .then(({ value }) => {
        //发送文字信息
        let chatMsg = {
          fromId: userStore["user"].userId,
          toId: props.articleInfo.userId,
          content: value
        };
        sendChatMsg(chatMsg)
            .then(() => {
              ElMessage({
                type: 'success',
                message: `消息发送成功，如需要发送更多消息，请打开导航栏消息界面~`,
              })
            })
            .catch(() => {
              ElMessage({
                type: 'fail',
                message: '发送失败啦，怎么回事呢？',
              })
            })

      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: 'Input canceled',
        })
      })
}

</script>

<style scoped>
/* 这是引入了一些字体 */
@import url('https://fonts.googleapis.com/css?family=Poppins:100,200,300,400,500,600,700,800,900');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Poppins', sans-serif;
}

body {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(45deg, #fbda61, #ff5acd);
}

.card {
  margin-top: 80px;
  position: relative;
  height: 190px;
  /* height: 450px; */
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 35px 80px rgba(0, 0, 0, 0.15);
  transition: 0.5s;
}

.card:hover {
  height: 300px;
}

.imgBx {
  position: absolute;
  left: 50%;
  top: -50px;
  transform: translateX(-50%);
  width: 150px;
  height: 150px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.35);
  overflow: hidden;
  transition: 0.5s;
}

.card:hover .imgBx {
  width: 200px;
  height: 200px;
}

.imgBx img{
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card .content {
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  overflow: hidden;
}

.card .content .details {
  padding: 20px;
  text-align: center;
  width: 100%;
  transition: 0.5s;
  transform: translateY(150px);
}

.card:hover .content .details {
  transform: translateY(0px);
}

.card .content .details h2 {
  font-size: 1.25em;
  font-weight: 600;
  color: #555;
  line-height: 1.2em;
}

.card .content .details h2 span {
  font-size: 0.75em;
  font-weight: 500;
  opacity: 0.5;
}

.card .content .details .actionBtn {
  display: flex;
  justify-content: center;
  margin: auto;
}

.card .content .details .actionBtn button {
  padding: 10px 30px;
  border-radius: 5px;
  border: none;
  outline: none;
  font-size: 1em;
  font-weight: 500;
  background: #ff5f95;
  color: #fff;
  cursor: pointer;
}

</style>
