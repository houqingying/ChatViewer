import { createApp, reactive } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入element icon
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import { createPinia } from 'pinia'
// 引入UndrawUi评论区组件
import UndrawUi from 'undraw-ui'
import 'undraw-ui/dist/style.css'

import App from './App.vue'
import router from './router'
import './assets/css/tokens.css'
import { useUserStore } from "@/stores/user";

//  引入pinia
const pinia = createPinia()

const app = createApp(App)

// 引入coin
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}


app.use(UndrawUi)
app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')

const userStore = reactive(useUserStore())
axios.defaults.baseURL = '/api'
axios.interceptors.request.use(config => {
    // console.log(config)
    if (userStore.token != "") {
        config.headers.Token = userStore.token
    }
    // 在最后必须 return config
    return config
})

