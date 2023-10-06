<template>
  <NavigateBar></NavigateBar>
  <div class="editor-wrapper" >
    <div class="el-row card">
      <div class="el-col-12">
        <div style="display:flex;margin:auto;">
          <img class="icon-image" alt="Icon" style="display:flex;margin:auto;width:100%;"
               src="../../assets/index/card1.png">
        </div>
      </div>
      <div class="el-col-12" style="margin-top:15vh;">

        <el-row style="margin-bottom:20px;height:3em;">
          <el-input class="el-col-20 editor-title-input"
                    v-model="title"
                    size="large"
                    input-style="color:var(--md-sys-color-primary-light)"
                    placeholder="Title"
          />
        </el-row>
        <el-row style="margin-bottom:20px;">
          <el-cascader class="el-col-20"
                       size="large"
                       v-model="category"
                       :options="categoryOptions"
          />
        </el-row>
        <el-row style="margin-bottom:20px;">
          <el-input class="el-col-20"
                    v-model="answer"
                    :autosize="{ minRows: 4, maxRows: 8}"
                    size="large"
                    input-style="line-height:1.5em;color:var(--md-sys-color-primary-light);max-height: 35vh;"
                    type="textarea"
                    placeholder="Please input"
          />
        </el-row>
        <el-row>
          <div class="el-col-6"></div>
          <el-button round class="el-col-7 editor-submit-button" @click="submit_problem">发布问题</el-button>
        </el-row>

      </div>


    </div>

  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import NavigateBar from '@/components/base/NavigateBar.vue'
import 'vditor/dist/index.css';

import { useUserStore } from "@/stores/user"
import { reactive, ref } from "vue"
import {ElMessage} from "element-plus";

// 用于监控是否加载目录
const loadingOver = ref(false)
// 文章信息
const title = ref('')
const category = ref('')
const answer = ref('')
const userStore = reactive(useUserStore())
const categoryOptions = ref([])

// 加载目录数据
load_category()

function load_category() {
  axios.get('/category/allCategory')
      .then(function (response) {
        if (response.data.code === 200) {
          categoryOptions.value = response.data.data;
          loadingOver.value = true
        } else {
          console.log('获取目录失败!')
        }
      })
}

function submit_problem () {
  if (userStore["token"] === '') {
    alert("您尚未登录，不能上传习题!")
  }
  else {
    axios.post('/problem/add', {
      problemTitle: title.value,
      problemAnswer: answer.value,
      categoryList: category.value,
    })
        .then(function (response) {
          if (response.data.code === 200) {
            ElMessage({type: 'success', message: '习题发布成功.'})
          } else {
            ElMessage({type: 'fail', message: '习题发布失败.'})
          }
        })
  }
}


</script>

<style scoped>

.editor-wrapper{
  background: linear-gradient(var(--md-ref-palette-primary95), var(--md-ref-palette-tertiary95));
  z-index: -1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: calc(100vh - 56px);
}

.card {
  display: flex;
  margin: auto;
  border-radius: 20px;
  width: 85vw;
  height: 80vh;
  background-color: rgba(255, 255, 255, 0.41);
}


.editor-submit-button {
  background: var(--md-sys-color-primary-light);
  border-color: var(--md-sys-color-primary-light);
  color: var(--md-sys-color-on-primary-light);
  box-shadow:  2px 2px 3px var(--md-ref-palette-primary90), 1px 1px 5px var(--md-sys-color-background-light);
}

.editor-submit-button:hover {
  transform: scale(1.1);
}


</style>
