<template>
  <NavigateBar></NavigateBar>
  <div class="container">
    <div class="login-wrapper">
      <div class="login-main-body">
        <div class="info">
          <div>
            <img class="icon-image" alt="Icon" src="../assets/login.png">
          </div>
        </div>
        <div class="login-form">
          <el-tabs v-model="activeName" class="tabs">
            <el-tab-pane label="登录" name="login">
              <div class="hint"> 手机号 </div>
              <div class="input-line-phone">
                <el-input @input="$forceUpdate" v-model="user_phone" placeholder="phone number" class="no-border"></el-input>
              </div>
              <!-- 验证码
              <div class="hint"> 验证码 </div>
              <div class="cap-input">
                <el-input @input="$forceUpdate" v-model="captcha" placeholder="captcha" class="input-line"></el-input>
                <el-button round class="send-button"  :disabled ="forbidden" @click="send_captcha"> {{ captcha_hint }} </el-button>
              </div>
              -->
              <div class="hint" > 密码 </div>
              <div class="input-line-phone">
                <el-input @input="$forceUpdate" v-model="psw" placeholder="password" class="no-border" type="password"></el-input>
              </div>
              <div class="submit-area">
                <el-button round class="submit-button" @click="login"> 提交 </el-button>
              </div>
            </el-tab-pane>
            <el-tab-pane label="注册" name="register">
              <div class="hint"> 手机号 </div>
              <div class="input-line-phone">
                <el-input @input="$forceUpdate" v-model="user_phone" placeholder="phone number" class="no-border"></el-input>
              </div>
              <div class="hint"> 验证码 </div>
              <div class="cap-input">
                <el-input @input="$forceUpdate" v-model="captcha" placeholder="captcha" class="input-line"></el-input>
                <el-button round class="send-button" :disabled ="forbidden" @click="send_captcha"> {{ captcha_hint }} </el-button>
              </div>
              <div class="hint"> 密码 </div>
              <div class="input-line-phone">
                <el-input @input="$forceUpdate" v-model="psw" placeholder="password" class="no-border" type="password"></el-input>
              </div>
              <div class="hint"> 确认密码 <el-text type="warning" v-if="psw !== psw_confirm" size="small"> 两次输入密码不一致 </el-text></div>
              <div class="input-line-phone">
                <el-input @input="$forceUpdate" v-model="psw_confirm" placeholder="confirm password" class="no-border" type="password"></el-input>
              </div>
              <div class="submit-area">
                <el-button round class="submit-button" @click="register"> 提交 </el-button>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import axios from 'axios'
import NavigateBar from '@/components/base/NavigateBar.vue'
import { isPhoneNumber } from '@/components/utils/FormatChecker'
import { useUserStore } from "@/stores/user";
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

const activeName = ref('login')
const user_phone = ref('')
const captcha = ref('')

const forbidden = ref(false)
const time = ref(0)
const captcha_hint = ref('发送')
const timer = ref(0)

const psw = ref('')
const psw_confirm = ref('')

const userStore = reactive(useUserStore())
const router = useRouter()

onMounted(() => {
  if (userStore["token"] !== '') {
    router.push({
      name: 'index',
    })
    ElMessage({
      message: '您已登录，不必重复登录',
      type: 'success',
    })
  }
})

function send_captcha () {
  if (isPhoneNumber(user_phone.value)) {
    axios.get('/user/sendPhoneCaptcha?phone=' + user_phone.value)
        .then(function(response) {
          ElMessage({
            message: '验证码[' + response.data.data + ']已发送，有效期10分钟。(短信有点贵的啦！假装发到手机上哩！)',
            type: 'success',
          })
          forbidden.value = true
          time.value = 60;  // 60秒后能继续按按钮
          timer1();
        })
        .catch(function () {
          localStorage.clear()
          userStore.$patch({token: ''})
          userStore.$patch({user: {}})
        })
  } else {
    alert('手机号输入错误，请重新输入')
  }
}

function timer1(){   //验证码60s内不能继续点击的函数
  if (time.value > 0) {
    time.value--;
    captcha_hint.value = time.value + ""
    timer.value = setTimeout(timer1,1000)
  }
  else {
    time.value = 0;
    captcha_hint.value = "发送";
    forbidden.value = false
    clearTimeout(timer.value);
  }
}


function login () {
  axios.post('/user/login', {
    phone: user_phone.value,
    // captcha: captcha.value,
    password: psw.value
  })
      .then(function (response) {
        if (response.data.code === 200) {
          userStore.$patch({token: response.data.data.token})
          userStore.$patch({user: response.data.data.user})
          localStorage.setItem("token", response.data.data.token)
          localStorage.setItem("user", JSON.stringify(response.data.data.user))
          ElMessage({
            message: '登录成功',
            type: 'success',
          })
          router.push({
            name: 'articles',
          })
        } else {
          ElMessage({
            message: '用户未注册或验证码不正确.',
            type: 'error',
          })
        }
      })
      .catch(() => {
        ElMessage({
          message: '用户未注册或密码不正确.',
          type: 'error',
        })
      })
}

function register () {
  if (psw.value !== psw_confirm.value) {
    ElMessage({
      message: '两次密码输入不一致，请确认密码.',
      type: 'warning',
    })
    return
  }
  axios.post('/user/register', {
    phone: user_phone.value,
    captcha: captcha.value,
    password: psw.value
  })
      .then(function (response) {
        if (response.data.code === 200) {
          userStore.$patch({token: response.data.data.token})
          userStore.$patch({user: response.data.data.user})
          localStorage.setItem("token", response.data.data.token)
          localStorage.setItem("user", JSON.stringify(response.data.data.user))
          ElMessage({
            message: '注册成功',
            type: 'success',
          })
          router.push({
            name: 'articles',
          })
        } else {
          ElMessage({
            message: '注册失败',
            type: 'error',
          })
        }
      })
}

</script>

<style scoped>

.container {
  font-family: 'Quicksand', sans-serif;
  height: calc(100vh - 56px);
  background-color: #bcd7f5;
  background-image:
      radial-gradient(closest-side, rgb(139, 211, 250, 1), rgba(254, 234, 131, 0)),
      radial-gradient(closest-side, rgb(168, 207, 245, 1), rgba(51, 122, 190, 0)),
      radial-gradient(closest-side, rgba(178, 222, 158, 1), rgba(178, 222, 158, 0)),
      radial-gradient(closest-side, rgba(41, 184, 165, 1), rgba(41, 184, 165, 0)),
      radial-gradient(closest-side, rgba(248, 192, 147, 1), rgba(248, 192, 147, 0));
  background-size:
      130vmax 130vmax,
      80vmax 80vmax,
      90vmax 90vmax,
      110vmax 110vmax,
      90vmax 90vmax;
  background-position:
      -80vmax -80vmax,
      60vmax -30vmax,
      10vmax 10vmax,
      -30vmax -10vmax,
      50vmax 50vmax;
  background-repeat: no-repeat;
  animation: 10s movement linear infinite;
  padding-left: 8%;
  padding-right: 8%;
  box-sizing: border-box;
  display: flex;
}

@keyframes movement {
  0%, 100% {
    background-size:
        130vmax 130vmax,
        80vmax 80vmax,
        90vmax 90vmax,
        110vmax 110vmax,
        90vmax 90vmax;
    background-position:
        -80vmax -80vmax,
        60vmax -30vmax,
        10vmax 10vmax,
        -30vmax -10vmax,
        50vmax 50vmax;
  }
  25% {
    background-size:
        100vmax 100vmax,
        90vmax 90vmax,
        100vmax 100vmax,
        90vmax 90vmax,
        60vmax 60vmax;
    background-position:
        -60vmax -90vmax,
        50vmax -40vmax,
        0 -20vmax,
        -40vmax -20vmax,
        40vmax 60vmax;
  }
  50% {
    background-size:
        80vmax 80vmax,
        110vmax 110vmax,
        80vmax 80vmax,
        60vmax 60vmax,
        80vmax 80vmax;
    background-position:
        -50vmax -70vmax,
        40vmax -30vmax,
        10vmax 0,
        20vmax 10vmax,
        30vmax 70vmax;
  }
  75% {
    background-size:
        90vmax 90vmax,
        90vmax 90vmax,
        100vmax 100vmax,
        90vmax 90vmax,
        70vmax 70vmax;
    background-position:
        -50vmax -40vmax,
        50vmax -30vmax,
        20vmax 0,
        -10vmax 10vmax,
        40vmax 60vmax;
  }
}


.login-wrapper{
  height: 90%;
  display: flex;
  margin: auto;
  background-color: rgba(255, 255, 255, 0.63);
  border-radius: 30px;
}

.login-main-body{
  display: flex;
  flex-direction: row;
  margin: auto;
  height: 80%;
}

.icon-image{
  width: 80%;
}

.login-form{
  margin-bottom: auto;
  margin-top: auto;
  margin-left: -10%;
  padding-left: 5%;
  width: 40%;
  min-height: 80%;
  border-radius: 3%;
}

.tabs {
  margin-top: 50px;
}

/*隐藏tab下面的一横*/
::v-deep(.el-tabs__active-bar) {
  display: none;
}

/* 隐层灰色分割线 */
::v-deep(.el-tabs__nav-wrap::after) {
  position: static !important;
}

::v-deep(.el-tabs__nav-scroll) {
  height: 35px;
  background-color: var(--md-sys-color-secondary-light);
  width: 200px;
  border-radius: 30px;
  box-shadow: 1px 1px 5px var(--md-ref-palette-primary80),
  1px 1px 5px var(--md-sys-color-primary-container-light);
}


::v-deep(.el-tabs--top .el-tabs__item.is-top:nth-child(2)) {
  padding-left: 20px;
}

::v-deep(.el-tabs--top .el-tabs__item.is-top:last-child) {
  padding-right: 20px;
}

::v-deep(.el-tabs__item) {
  height: 35px;
  border-radius: 30px;
  color: var(--md-sys-color-on-primary-light);
  width: 100px;
}

/*悬浮样式*/
::v-deep(.el-tabs__item:hover) {
  color: var(--md-sys-color-on-primary-light);
}

/*选中样式*/
::v-deep(.el-tabs__item.is-active) {
  border-radius: 30px;
  width: 100px;
  color: #fff;
  font-weight: bold;
  background-color: var(--md-sys-color-primary-light);
}

.hint{
  font-family: "等线 Light", serif;
  margin-top: 3%;
  margin-bottom: 3%;
  font-size: medium;
  font-weight: bold;
  text-align: left;
  color: var(--md-sys-color-on-primary-container-light)
}

.input-line-phone{
  width: 80%;
  border: white;
}

.cap-input{
  display: flex;
  flex-direction: row;
}

.input-line{
  width: 64%;
  margin-right: 1%;
}

.send-button{
  width: 15%;
  font-family: "等线", serif;
  background-color: var(--md-sys-color-tertiary-light);
  border-color: var(--md-sys-color-tertiary-light);
  color: var(--md-sys-color-on-tertiary-light);
  box-shadow:  2px 2px 3px var(--md-ref-palette-primary80), 1px 1px 5px var(--md-sys-color-background-light);
}

.submit-area{
  margin-top: 50px;
  width: 80%;
}

.submit-button{
  font-family: "等线", serif;
  width: 50%;
  background: var(--md-sys-color-primary-light);
  border-color: var(--md-sys-color-primary-light);
  color:  var(--md-sys-color-on-primary-light);
  box-shadow:  2px 2px 3px var(--md-ref-palette-primary80), 1px 1px 5px var(--md-sys-color-background-light);
  margin-bottom: 30px;
}

.submit-button:hover,
.send-button:hover { transform: scale(1.1); }

.info{
  width: 53%;
  margin-left: 2%;
}

.title{
  font-family: 微軟正黑體, serif;
  font-size: xxx-large;
  font-style: italic;
  font-weight: bold;
  color: #314456;
}

@media screen and (max-width: 768px) {
  .login-form {
    width: 100%;
  }
}

</style>
