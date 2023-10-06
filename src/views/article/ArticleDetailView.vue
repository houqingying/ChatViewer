<template>
  <NavigateBar/>
  <div class="background">
    <div class="article-detail-wrapper">
      <!-- 左边栏：用户信息 -->
      <div class="left">
        <div class="user-info-wrapper">
          <div class="author-info">
            <el-button round color="#0361a3" @click="toList" class="return-button">
              <el-icon><Back/></el-icon>
            </el-button>
            <UserCard :article-info="article"></UserCard>
          </div>
        </div>
      </div>
      <!--中间栏：文章 -->
      <div class="mid">
        <div class="context-wrapper" >
          <div class="article-title">
            {{ article.articleTitle }}
          </div>
          <div class="article-info">
            <button @click="likeArticle" :class="isLike ?'like-button-click':'like-button'">
              <el-icon>
                <CaretTop />
              </el-icon>
              {{ article.likeCounts }}
            </button>
            <div class="static-button">
              <el-icon>
                <ChatDotRound />
              </el-icon>
              {{ article.commentCounts }}
            </div>
          </div>
          <div id="article_content" >
          </div>
          <div class="comment-wrapper">
            <u-comment :config="config" @submit="submit" @like="like" relative-time>
              <!-- <template>导航栏卡槽</template> -->
              <!-- <template #info>用户信息卡槽</template> -->
              <!-- <template #card>用户信息卡片卡槽</template> -->
              <!-- <template #opearte>操作栏卡槽</template> -->
            </u-comment>
          </div>
        </div>
      </div>
      <!--右边栏：暂时空白 -->
      <div class="right">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import emoji from './emoji'
import { useRouter, useRoute } from 'vue-router';
import {computed, reactive, ref} from 'vue'
import { Back, CaretTop, ChatDotRound } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { CommentApi, ConfigApi, SubmitParamApi } from 'undraw-ui'
import Vditor from 'vditor';
import NavigateBar from "@/components/base/NavigateBar.vue";
import UserCard from "@/components/user/UserCard.vue";
import { useUserStore } from "@/stores/user";
import { getArticleDetail, getArticleComments, clickLike, addComment } from "@/api/article";

const router = useRouter()
const route = useRoute()
const articleId = ref(route.query.articleId)
const article = ref({})
const isLike = computed(() => article.value.isLike)
const comments = ref<CommentApi[] | null>(null)

// 0、从pinia中获得用户信息，配置undraw-ui的comment组件config
const userStore = reactive(useUserStore())
const config = reactive<ConfigApi>({
  user: {
    id: userStore["user"].userId,
    username: userStore["user"].userName,
    avatar: userStore["user"].userAvatar,
    // 点赞评论id数组 建议:存储方式用户uid和评论id组成关系,根据用户uid来获取对应点赞评论id,然后加入到数组中返回
    likeIds: []
  },
  emoji: emoji,
  comments: [],
  total: 10
})

// 1、加载文章信息，渲染至article_content元素
function loadArticle() {
  getArticleDetail(articleId.value).then((res) => {
    article.value = res
    Vditor.preview(document.getElementById('article_content'), article.value.articleContent, {
      "hljs": {
        "style": "solarized-light",
        "lineNumber": true,
        "defaultLang": "java"
      }
    })
  })
}
loadArticle()

// 3、加载文章评论信息
function loadComments() {
  getArticleComments(articleId.value).then((res) => {
    comments.value = res["comments"]
    config["comments"] = comments
    config["user"].likeIds = res["likeList"]
  })
}
loadComments()

// 4、文章点赞/取消点赞事件
function likeArticle() {
  const data = {
    entityType: 0,
    entityId: articleId.value
  }
  clickLike(data)
      .then(() => {
        if (article.value.isLike == true) {
          article.value.likeCounts = article.value.likeCounts - 1
          ElMessage('取消点赞.')
        }
        else {
          article.value.likeCounts = article.value.likeCounts + 1
          ElMessage({type: 'success', message: '点赞成功.'})
        }
        article.value.isLike = !article.value.isLike
      })
      .catch(() => {
        ElMessage({type: 'warning', message: '请先登录.'})
      })
}

// 5、提交评论事件
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const submit = ({ content, parentId, files, finish, reply }: SubmitParamApi) => {
  const data = {
    commentContent: content,
    commentType: parentId == null ? 0 : 1,
    articleId: articleId.value,
    parentId: parentId == null ? articleId.value : parentId,
    targetId: parentId == null ? articleId.value : reply.id
  }
  let comment: CommentApi = {}
  addComment(data).then((res) => {
    comment = res
    finish(comment)
    ElMessage({type: 'success', message: '评论成功.'})
  })
}

// 6、评论点赞按钮事件 将评论id返回后端判断是否点赞，然后在处理点赞状态
const like = (id: string, finish: () => void) => {
  const data = {
    entityType: 1,
    entityId: id
  }
  clickLike(data).then(() => {
    ElMessage({type: 'success', message: '操作成功.'})
  })
  setTimeout(() => {
    finish()
  }, 200)
}

// 跳转至列表页
function toList() {
  router.push({
    name: 'articles',
  })
}
</script>

<style scoped>

.return-button {
  margin-top: 10px;
  margin-left: 10px;
  width: 20%;
}

.left, .right{
  width: 20%;
}

.mid {
  width: 60%;
}

.background {
  background-color: var(--md-ref-palette-primary98);
  z-index: -1;
  min-height: calc(100vh - 56px);
}

.article-detail-wrapper {
  padding-top: 20px;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: row;
}

.user-info-wrapper {
  position: fixed;
  width: 20%;
  display: flex;
}

.author-info {
  margin-left: 5%;
  width: 90%;
  border-radius: 20px;
  background-color: var(--md-ref-palette-neutral100);
  box-shadow:  2px 2px 8px 2px var(--md-ref-palette-neutral-variant80), 2px 2px 8px 2px var(--md-ref-palette-neutral-variant100);
  overflow: hidden;
}

.context-wrapper {
  padding: 5%;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
  background-color: var(--md-ref-palette-neutral100);
  border-radius: 10px;
  box-shadow:  3px 3px 10px 6px var(--md-ref-palette-neutral-variant80), 3px 3px 10px 3px var(--md-ref-palette-neutral-variant100);
}

.article-title {
  margin: 20px 0 30px 0;
  font-size: xx-large;
  word-wrap: break-word;
  word-break: break-all;
}

.article-info {
  margin-left: auto;
  width: 160px;
  display: flex;
  flex-direction: row;
  justify-content: left;
}

.like-button {
  margin: auto;
  width: 60px;
  background: var(--md-sys-color-primary-container-light);
  padding: 6px;
  border: none;
  border-radius: 20px;
  font-size: 20px;
  color: var(--md-sys-color-primary-light);
  transition: all 0.25s ease;
}

.like-button:hover {
  background: var(--md-sys-color-primary-light);
  color: var(--md-sys-color-on-primary-light);
  transform: scale(1.1);
}

.like-button-click {
  margin: auto;
  width: 60px;
  background: var(--md-sys-color-primary-light);
  padding: 6px;
  border: none;
  border-radius: 20px;
  font-size: 20px;
  color: var(--md-sys-color-on-primary-light);
  transition: all 0.25s ease;
}

.like-button-click:hover {
  transform: scale(1.1);
}

.static-button {
  margin: auto;
  background: var(--light-grayish-blue);
  padding: 6px;
  border-radius: 20px;
  font-size: 20px;
  color: var(--md-sys-color-secondary-light);
  transition: all 0.25s ease;
}

.comment-wrapper {
  width:100%;
}


@media screen and (max-width: 768px) {
  .left {
    display: none;
  }
  .mid{
    width: 100vw;
    margin: 0;
  }
  .context-wrapper {
    width: 100%;
  }
}

</style>
