<template>
  <div class="tabs">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="收到的赞" name="like" ></el-tab-pane>
      <el-tab-pane label="回复我的" name="comment" ></el-tab-pane>
    </el-tabs>
  </div>
  <el-timeline class="messages">
    <div v-for="notice in notices" :key="notice">
      <el-timeline-item :timestamp="notice.createTime.substring(0, 16)" placement="top">
        <el-card>
          <h4> {{ notice.senderName }}</h4>
          <el-button text>
            <p @click="to_article_detail(notice.articleId)"> {{ notice.content }} </p>
          </el-button>
        </el-card>
      </el-timeline-item>
    </div>

  </el-timeline>
</template>


<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useUserStore } from "@/stores/user";
import {computed, onUpdated, reactive, ref} from "vue";
import axios from "axios";

const router = useRouter()
const userStore = reactive(useUserStore())

// 控制选项bar的数据展示
const activeName = ref("like")
// 根据activeName计算属性notices
const loadingOver = ref(false)
const likeNotices = ref([])
const commentNotices = ref([])
const notices = computed(() => {
  return activeName.value === "like" ? likeNotices.value : commentNotices.value;
})

function notice_of_type(type){
  axios.get('/notice?userId=' + userStore.user.userId +"&noticeType=" + type)
      .then(function (response) {
        if (response.data.code === 200) {
          if (type === 0) {
            likeNotices.value = response.data.data;
          } else {
            commentNotices.value = response.data.data;
            loadingOver.value = true
          }
        } else {
          console.log('获取通知列表失败!')
        }
      })
}

onUpdated(() => {
  notice_of_type(0)
  notice_of_type(1)
})
notice_of_type(0)
notice_of_type(1)

// 跳转至文章详情页
function to_article_detail(articleId) {
  router.push({
    name: 'articleDetail',
    query: { articleId: articleId }
  })
}

// eslint-disable-next-line no-undef, @typescript-eslint/no-unused-vars
const handleClick = (tab: TabsPaneContext, event: Event) => {
  axios.put('/notice/read?userId=' + userStore.user.userId +"&noticeType=" + (tab.props.name == 'like' ? '0' : '1'))
      .then(function (response) {
        if (response.data.code === 200) {
          /* empty */
        } else {
          console.log('设置已读失败!')
        }
      })
}


</script>


<style scoped>

.tabs {
  margin-left: 50px;
  margin-top: 50px;
  margin-bottom: 50px;
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

.messages  {
  width: 90%;
}

</style>
