<template>
  <article class="article-card">
    <!-- 左侧，文章主图像 -->
    <div class="img-box">
      <el-image :src="articlePic" style="width:100%;height:100%" fit="cover" class="article-banner" />
    </div>
    <!-- 右侧，文章内容 -->
    <div class="article-content" >
      <!-- 标题 -->
      <a href="#" @click="to_detail">
        <h3 class="article-title">
          {{ articleTitle }}
        </h3>
      </a>
      <!-- 摘要 -->
      <p class="article-text">
        {{ articleAbstract }}
      </p>
      <!-- 作者与点赞评论数 -->
      <div class="article-content-footer">
        <!-- 作者 -->
        <div class="author">
          <el-image :src="userAvatar" class="author-avatar"/>
          <div class="author-info">
            <h4 class="author-name"> {{ userName }} </h4>
            <div class="publish-date">{{ updateTime }}</div>
          </div>
        </div>
        <!-- 点赞评论 -->
        <div class="share">
          <div class="static-button">
            <el-icon>
              <CaretTop />
            </el-icon>
            {{ likeCounts }}
          </div>
          <div class="static-button">
            <el-icon >
              <ChatDotRound />
            </el-icon>
            {{ commentCounts }}
          </div>
        </div>
      </div>
    </div>

  </article>
</template>

<script setup>

import { useRouter } from 'vue-router';
import { ChatDotRound, CaretTop } from '@element-plus/icons-vue'

const props = defineProps({
  articleId: String,
  articleTitle: String,
  articlePic: String,
  articleAbstract: String,
  userName: String,
  userAvatar: String,
  likeCounts: Number,
  commentCounts: Number,
  updateTime: String
})

const router = useRouter()

// 跳转至详情页
function to_detail() {
  router.push({
    name: 'articleDetail',
    query: { articleId: props.articleId }
  })
}

</script>

<style scoped>

*, *::before, *::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

a {
  text-decoration: none;
  color: var(--white);
}


/*-----------------------------------*\
 #MAIN STYLE
\*-----------------------------------*/

.article-card {
  display: grid;
  grid-template-columns: 220px minmax(300px, 100%);
  max-width: 90%;
  border-radius: 10px;
  box-shadow: 6px 6px 5px hsla(0, 0%, 0%, 0.02),
  25px 25px 20px hsla(0, 0%, 0%, 0.03),
  100px 100px 80px hsla(0, 0%, 0%, 0.05);
  font-family: 等线, serif;
}


.img-box {
  border-radius: 10px 0 0 10px;
  overflow: hidden;
}

.article-banner {
  object-position: left;
  transition: all 0.5s ease;
}

.article-card:hover .article-banner {
  transform: scale(1.1);
}

.article-content {
  background: var(--white);
  padding: 32px 40px;
  border-radius: 0 10px 10px 0;
  display: flex;
  flex-direction: column;
  justify-content: left;
}

.article-title {
  font-size: 20px;
  color: var(--very-dark-grayish-blue);
  margin-bottom: 12px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-text {
  font-size: var(--fs-13);
  line-height: 1.5;
  margin-bottom: 18px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical
}

.article-content-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author {
  display: flex;
  align-items: center;
}

.author-avatar {
  width:  40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 15px;
}

.author-name {
  color: var(--very-dark-grayish-blue);
  font-size: var(--fs-13);
}

.publish-date {
  color: var(--very-dark-grayish-blue);
  font-size: var(--fs-13);
}

.share {
  position: relative;
  display: flex;
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

.article-title:hover {
  color: var(--md-sys-color-primary-light);
}


@media screen and (max-width: 768px) {

  :root {
    --fs-13: 12px;
  }

  .article-card {
    grid-template-columns: 1fr;
    max-width: 294px;
    overflow: hidden;
  }

  .img-box, .article-content {
    border-radius: 0;
  }

  .img-box {
    height: auto;
  }

  .article-content {
    padding: 30px 28px 20px;
    width: 294px;
  }

  .article-title {
    font-size: 15px;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .article-text {
    margin-bottom: 30px;
  }

  .article-banner {
    width: 294px;
    height: 294px;
  }

  .author-avatar {
    width:  35px;
    height: 35px;
  }

  .author-name {
    width: 7em;
    overflow: hidden;
    /* 显示省略符号来代表被修剪的文本。 */
    text-overflow: ellipsis;
    /* 文本不换行 */
    white-space: nowrap;
  }

  .share {
    position: static;
    z-index: 1;
  }


  .static-button {
    display: none;
  }
}

</style>
