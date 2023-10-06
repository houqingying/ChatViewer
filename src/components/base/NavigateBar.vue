<template>
  <el-affix :offset="0">
    <div class="bar">
      <router-link to="/">
        <div class="navi-logo">
          <img :src="interviewIcon" style="width:45px;margin-top:auto;"/>
        </div>
      </router-link>
      <div class="navi-list">
        <el-dropdown class="navi-item" trigger="click">
        <span class="el-dropdown-link">
          文章
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
          <template #dropdown>
            <el-dropdown-menu>
              <router-link to="/articles" class="non-underline">
                <el-dropdown-item>
                  <img :src="articlesIcon" style="width:35px;height:35px;"/>
                  所有文章
                </el-dropdown-item>
              </router-link>
              <router-link to="/editor" class="non-underline" v-if="userStore.token !== ''">
                <el-dropdown-item>
                  <img :src="editIcon" style="width:35px;height:35px;"/>
                  发布文章
                </el-dropdown-item>
              </router-link>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-dropdown class="navi-item" trigger="click">
        <span class="el-dropdown-link">
          ChatGPT接口
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
          <template #dropdown>
            <el-dropdown-menu>
              <router-link to="/chatgpt" class="non-underline">
                <el-dropdown-item>
                  <img :src="apiIcon" style="width:35px;height:35px;"/>
                  在线提问
                </el-dropdown-item>
              </router-link >
              <router-link to="/problems" class="non-underline">
                <el-dropdown-item>
                  <img :src="problemIcon" style="width:35px;height:35px;"/>
                  AI刷题
                </el-dropdown-item>
              </router-link>
              <router-link to="/problem/add" class="non-underline" v-if="userStore.token !== ''">
                <el-dropdown-item>
                  <img :src="editIcon" style="width:35px;height:35px;"/>
                  添加习题
                </el-dropdown-item>
              </router-link>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="navi-user-and-search">
        <div class="search">
          <el-button :icon="Search" size="large" round class="search-button">Search</el-button>
        </div>
        <!-- 登录后才进行显示的组件 -->
        <div v-if="userStore.token !== ''" class="login-show">
          <!-- 聊天框触发按钮 -->
          <el-button text style="margin: auto;width:55px;" @click="drawer = true">
            <img :src="chatIcon" style="width:40px;height:40px;"/>
          </el-button>
          <!-- 消息列表触发按钮 -->
          <el-popover placement="bottom" :width="350" trigger="click">
            <template #reference>
              <el-button text style="margin: auto;width:55px;">
                <img :src="messageIcon" style="width:40px;height:40px;"/>
              </el-button>
            </template>
            <div v-for="brief in noticeBriefs" :key="brief">
              <!-- 如果存在该类别消息 -->
              <div v-if="brief.latestNotice != null" class="brief-item" @click="clickNoticeBrief(brief.noticeType);brief.unreadCount = 0;">
                <div class="brief-item-title">
                  <img :src="brief.noticeType === 0 ? heartIcon : starIcon" style="width:30px;height:30px;margin-top: auto;margin-bottom: auto;"/>
                  <div class="brief-item-title-text">
                    <el-badge :hidden="brief.unreadCount <= 0" :value="brief.unreadCount" :max="99">
                      <div> {{ brief.noticeType === 0 ? "收到的赞" : "回复我的" }} </div>
                    </el-badge>
                  </div>
                </div>
                <div>
                  {{ brief.latestNotice.senderName }} {{ brief.latestNotice.content }}
                </div>
              </div>
            </div>
            <div v-if="noticeBriefs.length == 0">
              <img class="default-image" style="width:280px;display:flex;margin:auto;" src="../../assets/img/no_message.png" alt="hi">
            </div>
          </el-popover>
          <!-- 用户头像触发按钮 -->
          <el-popover placement="bottom" :width="300" trigger="click">
            <template #reference>
              <el-button text style="margin-top:12px;" class="avatar">
                <el-avatar :src="userStore.user.userAvatar" class="avatar-pic"/>
              </el-button>>
            </template>
            <el-row class="user-info-row">
              <el-button round size="small" color="#bc8711" plain class="el-col-4" style="margin:auto 10px auto 0;"
                         @click="edit_userInfo('userName')">edit</el-button>
              <el-text tag="b" class="el-col-9"> 用户名 </el-text>
              <el-text class="el-col-10"> {{ userStore.user.userName }}  </el-text>
            </el-row>
            <el-row class="user-info-row">
              <el-button round size="small" color="#0361a3" class="el-col-4" style="margin:auto 10px auto 0;">buy</el-button>
              <el-text tag="b" class="el-col-9"> GPT Token </el-text>
              <el-text class="el-col-10"> {{ userStore.user.userTokenCount }}  </el-text>
            </el-row>
            <el-row class="user-info-row">
              <el-button round size="small" color="#0361a3" class="el-col-4" style="margin:auto 10px auto 0;"
                         @click="edit_userInfo('userApiKey')">edit</el-button>
              <el-text tag="b" class="el-col-9"> Api Key </el-text>
              <el-text class="el-col-10">
                {{ userStore.user.userApiKey === null ? "未设置ApiKey" : userStore.user.userApiKey }}
              </el-text>
            </el-row>
            <el-row>
              <el-switch
                  v-model="userStore.useToken"
                  class="ml-2"
                  inline-prompt
                  style="--el-switch-on-color: rgba(3,163,35,0.49);
                   --el-switch-off-color: rgba(163,22,3,0.49); margin: auto"
                  active-text="使用Token"
                  inactive-text="使用Api Key"
              />
            </el-row>
          </el-popover>
        </div>
        <div class="login">
          <router-link to="/login" class="non-underline">
            <el-button round v-if="userStore.token === ''" size="large" class="login-button"> 登录 </el-button>
          </router-link>
          <el-button round v-if="userStore.token !== ''" @click="logout" size="large" class="login-button"> 登出 </el-button>
        </div>
      </div>
    </div>
  </el-affix>

  <!-- 聊天框 -->
  <el-drawer v-model="drawer" title="I am the title" :with-header="false" direction="ltr" size="900">
    <ChatHome></ChatHome>
  </el-drawer>

  <el-drawer v-model="noticeDrawer" title="I am the title" :with-header="false" direction="ltr" size="700">
    <NoticeList></NoticeList>
  </el-drawer>

</template>

<script lang="ts" setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from "@/stores/user";
import axios from "axios";
import { ref, reactive, onMounted } from "vue";
import ChatHome from '@/views/chat/ChatListView.vue'
import NoticeList from '@/views/notice/NoticeList.vue'

import chatIcon from '@/assets/icon/chat.gif'
import editIcon from '@/assets/icon/edit.gif'
import articlesIcon from '@/assets/icon/ariticles.gif'
import apiIcon from '@/assets/icon/api.gif'
import messageIcon from '@/assets/icon/message.gif'
import heartIcon from '@/assets/icon/heart.gif'
import starIcon from '@/assets/icon/star.gif'
import problemIcon from '@/assets/icon/problem.gif'
import interviewIcon from '@/assets/icon/interview.gif'

const drawer = ref(false)
const noticeDrawer = ref(false)

const noticeBriefs = ref([])
const noticeLoadingOver = ref(false)

const userStore = reactive(useUserStore())

function logout() {
  axios.get('/user/logout')
      .then(function (response) {
        if (response.data.code === 200) {
          userStore.$patch({token: ''})
          localStorage.clear()
        } else {
          alert('有错误')
        }
      })
}

function noticeBrief() {
  axios.get('/notice/brief?userId=' + userStore["user"].userId)
      .then(function (response) {
        if (response.data.code === 200) {
          noticeBriefs.value = response.data.data
          noticeLoadingOver.value = true
        } else {
          console.log('获取目录失败!')
        }
      })
      .catch(function (error) {
        console.log(error)
        // 加载页面时如果出现错误，可能是token的问题，清除token和用户信息
        localStorage.clear()
        userStore.$patch({token: ''})
        userStore.$patch({user: {}})
      })
}


function clickNoticeBrief(noticeType) {
  noticeDrawer.value = true
  axios.put('/notice/read?userId=' + userStore["user"].userId +"&noticeType=" + noticeType)
      .then(function (response) {
        if (response.data.code === 200) {
          /* empty */
        } else {
          console.log('设置已读失败!')
        }
      })
}

function edit_userInfo(param) {

  let hint
  switch (param) {
    case 'userName':
      hint = ['Please input your user name', '修改用户名']
      break
    case 'userApiKey':
      hint = ['请输入你的Chatgpt Api Key', '修改Api Key']
      break
  }

  ElMessageBox.prompt(hint[0], hint[1], {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel',
    inputPattern: /.+/,
    inputErrorMessage: '更改值不能为空!',
  })
      .then(({ value }) => {
        let payload
        switch (param) {
          case 'userName':
            payload = {
              userId: userStore["user"].userId,
              userName: value
            };
            break
          case 'userApiKey':
            hint = ['请输入你的Chatgpt Api Key', '修改Api Key']
            payload = {
              userId: userStore["user"].userId,
              userApiKey: value
            };
            break
        }
        axios.put('/user/userInfo', payload)
            .then(function (response) {
              if (response.data.code === 200) {
                userStore.$patch({user: response.data.data})
                localStorage.setItem("user", JSON.stringify(response.data.data))
                ElMessage({
                  message: '修改成功',
                  type: 'success',
                })
              } else {
                ElMessage({
                  message: '修改失败.',
                  type: 'error',
                })
              }
            })
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: 'Input canceled',
        })
      })
}

onMounted(() => {
  let userInfo = JSON.parse(localStorage.getItem("user"))
  if (userInfo == null) {
    return;
  }
  userStore.$patch({token: localStorage.getItem("token")})
  userStore.$patch({user: userInfo})
  noticeBrief()
})

</script>


<style scoped>
.bar {
  height: 56px;
  width: 100%;
  box-shadow:  2px 2px 5px #b9b9b9, 2px 2px 5px var(--md-sys-color-primary-container-light);
  display: flex;
  background-color: white;
}

.navi-logo{
  height: 100%;
  margin-right: auto;
  margin-left: 10px;
}

.navi-bar-icon{
  height: 100%;
}

.navi-list{
  height: 100%;
  width: 600px;
  display: flex;
  justify-content: center;
  margin-left: auto;
}

.navi-item{
  height: 100%;
  margin: auto;
}

.el-dropdown-link {
  cursor: pointer;
  color: black;
  display: flex;
  align-items: center;
}

.non-underline{
  color: #222222;
  text-decoration: none;
}

.navi-user-and-search{
  display: flex;
  margin-left: auto;
  margin-right: 10px;
}

.search{
  margin-top: auto;
  margin-bottom: auto;
  margin-right: 20px;
}

.search-button{
  margin: auto;
  background-color: #f8f8f8;
}

.login-show {
  display: flex;
  flex-direction: row;
}

.avatar{
  margin-left: 20px;
  margin-right: 20px;
  margin-top: 3%;
}

.avatar-pic{
  margin: auto;
}

.user-info-row {
  padding: 10px;
}

.user-info-row:hover {
  background-color: var(--md-sys-color-primary-container-light);
  border-radius: 5px;
}

.login{
  margin: auto;
}

.login-button{
  margin: auto;
  width: 100%;
  background: var(--md-sys-color-secondary-light);
  border-color: var(--md-sys-color-secondary-light);
  color: var(--md-sys-color-on-secondary-light);
  box-shadow: 1px 1px #eaeaea;
}

.brief-item {
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.brief-item:hover {
  background-color: var(--md-ref-palette-neutral98);
  border-radius: 15px;
}

.brief-item-title {
  height: 50px;
  display: flex;
  flex-direction: row;
}

.brief-item-title-text {
  margin-top: auto;
  margin-bottom: auto;
  font-weight: bold;
}


@media screen and (max-width: 768px) {
  .navi-list {
    display: none;
  }
}

</style>


