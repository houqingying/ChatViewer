<template>
  <div class="menu-component-container">
    <div class="menu-buttons-block">
      <el-button @click="handleSelectAll" v-if="!isCollapse" class="menu-collapse-button" color="#535f70">
        <el-icon><RefreshRight /></el-icon>
      </el-button>
      <el-button class="menu-collapse-button" @click="change_collapse" color="#0461a3">
        <el-icon v-if="isCollapse"><ArrowRight /></el-icon>
        <el-icon v-else><ArrowLeft /></el-icon>
      </el-button>
    </div>
    <el-menu @select="handleSelect" class="el-menu-vertical-demo" :collapse="isCollapse" :unique-opened="true">
      <div v-for="(first_category, i) in categoryOptions" :key="first_category">
        <el-sub-menu v-if="first_category.children.length !== 0" :index="first_category.value" class="first-menu-item">
          <template #title>
            <el-icon v-if="i % 7 === 0"><Watermelon /></el-icon>
            <el-icon v-else-if="i % 7 === 1"><Pear /></el-icon>
            <el-icon v-else-if="i % 7 === 2"><Sugar /></el-icon>
            <el-icon v-else-if="i % 7 === 3"><GobletFull /></el-icon>
            <el-icon v-else-if="i % 7 === 4"><Lollipop/></el-icon>
            <el-icon v-else-if="i % 7 === 5"><Coffee /></el-icon>
            <el-icon v-else><Apple /></el-icon>
            <span>{{ first_category.label }}</span>
          </template>
          <div v-for="second_category in first_category.children" :key="second_category" class="second-menu-item">
            <el-sub-menu v-if="second_category.children.length !== 0" :index="second_category.value">
              <template #title>
                <span>{{ second_category.label }}</span>
              </template>
              <div v-for="third_category in second_category.children" :key="third_category">
                <el-menu-item :index="third_category.value" class="third-menu-item">
                  {{ third_category.label }}
                </el-menu-item>
              </div>
            </el-sub-menu>
            <el-menu-item v-else :index="second_category.value" class="second-menu-item">
              {{ second_category.label }}
            </el-menu-item>
          </div>
        </el-sub-menu>
        <el-menu-item v-else :index="first_category.value" class="first-menu-item">
          <el-icon v-if="i % 7 === 0"><Watermelon /></el-icon>
          <el-icon v-else-if="i % 7 === 1"><Pear /></el-icon>
          <el-icon v-else-if="i % 7 === 2"><Sugar /></el-icon>
          <el-icon v-else-if="i % 7 === 3"><GobletFull /></el-icon>
          <el-icon v-else-if="i % 7 === 4"><Lollipop/></el-icon>
          <el-icon v-else-if="i % 7 === 5"><Coffee /></el-icon>
          <el-icon v-else><Apple /></el-icon>
          <template #title> {{ first_category.label }} </template>
        </el-menu-item>
      </div>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref, defineEmits } from 'vue'
import axios from "axios";
import { RefreshRight, ArrowRight, ArrowLeft, Watermelon, Pear, Sugar, Lollipop, GobletFull, Coffee, Apple } from '@element-plus/icons-vue'

// 更改菜单栏的展开、收缩状态
const isCollapse = ref(true)
function change_collapse() {
  isCollapse.value = !isCollapse.value
}

// 定义一个事件，以方便父组件调用
const emit = defineEmits(['handleSelect', 'handleSelectAll'])
const handleSelect = (key: string, keyPath: string[]) => {
  // 传递给父组件
  emit('handleSelect', key, keyPath)
}
const handleSelectAll = ( ) => {
  // 传递给父组件
  emit('handleSelectAll')
}

// 目录是否加载完成
const loadingOver = ref(false)
const categoryOptions = ref([])
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

</script>

<style>

.menu-component-container {
  position: absolute;
  height: 100%;
  background-color: var(--md-ref-palette-neutral90);
  display: flex;
  flex-direction: column;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
}

.el-menu--collapse  .el-sub-menu__title span{
  display: none;
}

.el-menu--collapse  .el-sub-menu__title .el-sub-menu__icon-arrow{
  display: none;
}

.menu-buttons-block {
  margin-right: auto;
  margin-left: auto;
}

.menu-collapse-button {
  margin-left: auto;
  margin-right: auto;
  margin-top: 10px;
}

.first-menu-item {
  background-color: var(--md-ref-palette-neutral90);
}

.second-menu-item {
  background-color: var(--md-ref-palette-neutral95);
}

.third-menu-item {
  background-color: var(--md-ref-palette-neutral95);
}

</style>
