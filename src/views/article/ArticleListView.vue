<template>
  <NavigateBar/>
  <div class="article-list-wrapper">
    <!-- 左侧类别菜单 -->
    <ArticleCategoryMenu v-on:handleSelect="handleSelect" v-on:handleSelectAll="handleSelectAll" class="article-list-menu-wrapper"/>
    <!-- 右侧文章内容 -->
    <div class="article-list-body">
      <!-- 文章列表 -->
      <div class="article-list-region">
        <div v-for="post in posts" :key="post">
          <div class="article-region">
            <ArticleCard v-bind="post" class="card"/>
          </div>
        </div>
      </div>
      <!-- 分页器 -->
      <div class="page-region">
        <el-pagination v-model:current-page="pageIndex" :hide-on-single-page="true"
                       :total="total" :page-size="pageSize" background layout="prev, pager, next"  class="page-roller" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import { ref, watch } from "vue";
import ArticleCategoryMenu from '@/components/article/CategoryMenu.vue'
import ArticleCard from '@/components/article/ArticleCard.vue'
import NavigateBar from "@/components/base/NavigateBar.vue";
import { getArticlePage, getCategoryArticlePage } from "@/api/article";

const total = ref(0)
const pageIndex = ref(1)
const pageSize = ref(10)
const categoryId = ref('')
const posts = ref([ ])

// 当选择页号改变时，发送请求
getPage()
watch(pageIndex, () => {
  getPage()
})

function getPage() {
  getArticlePage(pageIndex.value,pageSize.value).then((res) => {
    posts.value = res["records"];
    total.value = res["total"];
  })

}

// 选择分类改变时，发送请求
function handleSelect(key: string, keyPath: string[]) {
  console.log(key, keyPath)
  categoryId.value = key
  pageIndex.value = 1
  getCategoryArticlePage(pageIndex.value, pageSize.value, categoryId.value).then((res) => {
    posts.value = res["records"];
    total.value = res["total"];
  })
}

// 点击reset按钮，获得所有分类
function handleSelectAll() {
  categoryId.value = ''
  getPage()
}

</script>

<style scoped>

.article-list-wrapper{
  position: absolute;
  top: 0;
  left: 0;
  min-height: 100%;
  width: 100%;
  background:  var(--md-ref-palette-neutral98);
  z-index: -1;
}

.article-list-menu-wrapper {
  margin-top: 56px;
  position: fixed;
  z-index: 1;
}

.article-list-body {
  margin-left: 80px;
  margin-top: 10%;
}

.article-list-region {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.article-region{
  margin-bottom: 40px;
}

.card{
  left: 0;
  bottom: 0;
  top: 0;
  margin: auto;
  background-color: var(--md-ref-palette-neutral100);
}

.page-region{
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

</style>
