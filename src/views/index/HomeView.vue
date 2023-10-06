<template>
  <NavigateBar></NavigateBar>
  <div class="container">
    <div class="el-row">
      <!-- 左边区域，网站名称介绍-->
      <div class="el-col-8 intro">
        <h1>ChatViewer</h1>
        <h3> Talk to ChatGPT Interviewer </h3>
      </div>
      <!-- 右边区域，网站介绍-->
      <div class="el-col-16">
        <el-carousel height="70vh" :interval="5000" class="right" >
          <el-carousel-item>
            <el-card shadow="hover" body-style="" class="card">
              <div class="el-row">
                <div class="el-col-16">
                  <img class="card-image"  src="../../assets/index/card1.png" alt="hi">
                </div>
                <div class="el-col-8 card-intro">
                  <div class="card-title">
                    文章
                    <router-link to="/articles">
                      <el-button color="#5194da" style="margin: 5px 0 10px 0;" round plain>Explore</el-button>
                    </router-link>
                  </div>
                  <div class="card-text">
                    分类查看文章，发表知识总结，分享您的思考
                  </div>

                </div>
              </div>
            </el-card>
          </el-carousel-item>
          <el-carousel-item>
            <el-card shadow="hover" body-style="" class="card">
              <div class="el-row">
                <div class="el-col-16">
                  <img class="card-image" src="../../assets/index/card2.png" alt="hi">
                </div>
                <div class="el-col-8 card-intro">
                  <div class="card-title">
                    对话
                    <router-link to="/chatgpt">
                      <el-button color="#5194da" style="margin: 5px 0 10px 0;" round plain>Explore</el-button>
                    </router-link>
                  </div>
                  <div class="card-text">
                    提供ChatGPT接口，通过Token或OpenAI Api-Key访问，学习与工作的好帮手
                  </div>
                </div>
              </div>
            </el-card>
          </el-carousel-item>
          <el-carousel-item>
            <el-card shadow="hover" body-style="" class="card">
              <div class="el-row">
                <div class="el-col-16">
                  <img class="card-image"  src="../../assets/index/card3.png" alt="hi">
                </div>
                <div class="el-col-8 card-intro">
                  <div class="card-title">
                    刷题
                    <router-link to="/problems">
                      <el-button color="#5194da" style="margin: 5px 0 10px 0;" round plain>Explore</el-button>
                    </router-link>
                  </div>
                  <div class="card-text">
                    速通高频面试题，记录回答音频，ChatGPT辅助解答，完善您的答案
                  </div>
                </div>
              </div>
            </el-card>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>
  </div>
</template>

<script setup>
import NavigateBar from '@/components/base/NavigateBar.vue'


</script>

<style scoped>

:root {
  font-size: 15px;
}

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

.right {
  height: 70vh;
  margin: 12vh 0 10vh 0;
}

.intro {
  margin: auto;
}

h1 {
  color: rgba(246, 247, 248, 0.82);
  font-size: 60px;
  margin: 0;
}

h3 {
  color: rgba(246, 247, 248, 0.82);
  font-size: 28px;
  margin: 0;
}

.card {
  height: 70vh;
  background-color: rgba(255, 255, 255, 0.58);
  border: none;
  border-radius: 20px;
}

.card-image {
  height: 70vh;
}


.card-title {
  margin: 25vh 20px 10px 20px;
  color: rgba(75, 98, 147, 0.6);
  font-size: xx-large;
  font-weight: bold;
}

.card-text {
  margin: 0 20px 10px 20px;
  color: rgba(75, 98, 147, 0.6);
  font-size: large;
  line-height: 1.5em;
}



@media screen and (max-width: 1200px) {

  h1 {
    font-size: 30px;
  }

  h3 {
    display: none;
  }
}

</style>
