<template>
  <NavigateBar></NavigateBar>
  <div class="problem-list-wrapper">
    <ArticleCategoryMenu v-on:handleSelect="handleSelect" v-on:handleSelectAll="handleSelectAll" class="problem-list-menu-wrapper"/>
    <div class="body-wrapper">
      <div class="problem-list-body">
        <div class="problem-list-region">
          <div v-for="(post, i) in posts" :key="post">
            <el-row :gutter="30" v-if="i % 3 == 0" class="problem-region">
              <el-col :span="8">
                <ProblemCard v-bind="post" class="card"/>
              </el-col>
              <el-col :span="8" v-if="i + 1 < posts.length">
                <ProblemCard v-bind="posts[i+1]" class="card"/>
              </el-col>
              <el-col :span="8" v-if="i + 2 < posts.length">
                <ProblemCard v-bind="posts[i+2]" class="card"/>
              </el-col>
            </el-row>
          </div>
        </div>
        <div class="page-region">
          <el-pagination v-model:current-page="page_index" :hide-on-single-page="true"
                         :total="total" :page-size="page_size" background layout="prev, pager, next"  class="page-roller" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import ArticleCategoryMenu from '@/components/article/CategoryMenu.vue'
import NavigateBar from "@/components/base/NavigateBar.vue";
import {ref, watch} from "vue";
import axios from 'axios'
import ProblemCard from "@/components/problem/ProblemCard.vue";

const total = ref(0)
const page_index = ref(1)
const page_size = ref(12)
const category_id = ref('')
const posts = ref([ ])

// 当选择页号改变时，发送请求
get_page()
watch(page_index, () => {
  get_page()
})
function get_page() {
  axios.get('/problem/page?page='+page_index.value+'&pageSize='+page_size.value)
      .then(function (response) {
        if (response.data.code === 200) {
          posts.value = response.data.data.records
          total.value = response.data.data.total
        } else {
          console.log('获取目录失败!')
        }
      })
}

// 选择分类改变时，发送请求
function handleSelect(key: string, keyPath: string[]) {
  console.log(key, keyPath)
  category_id.value = key
  page_index.value = 1
  axios.get('/problem/page?page='+page_index.value+'&pageSize='+page_size.value+'&categoryId='+category_id.value)
      .then(function (response) {
        if (response.data.code === 200) {
          posts.value = response.data.data.records
          total.value = response.data.data.total
        } else {
          console.log('获取目录失败!')
        }
      })
}

// 获得所有分类
function handleSelectAll() {
  category_id.value = ''
  get_page()
}

</script>

<style scoped>
.problem-list-wrapper{
  position: absolute;
  top: 0;
  left: 0;
  min-height: 100%;
  width: 100%;
  background:  var(--md-ref-palette-neutral98);
  z-index: -1;
}

.problem-list-menu-wrapper {
  margin-top: 56px;
  position: fixed;
  z-index: 1;
}

.body-wrapper {
  display: flex;
  margin: auto;
}

.problem-list-body {
  margin-left: 80px;
  margin-top: 80px;
}

.problem-list-region {
  width: calc(100% - 60px);
  display: flex;
  flex-direction: column;
}

.problem-region{
  margin-bottom: 30px;
}

.card{
  left: 0;
  bottom: 0;
  top: 0;
  bottom: 0;
  margin: auto;
  background-color: var(--md-ref-palette-neutral100);
}

.card-middle {
  left: 100px;
  bottom: 0;
  top: 0;
  bottom: 0;
  margin: auto;
  background-color: var(--md-ref-palette-neutral100);
}

.page-region{
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}



</style>
