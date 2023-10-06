<template>
  <a href="#" @click="to_detail">
    <article class="problem-card">
      <div class="problem-content" >
        <h3 class="problem-title">
          {{ problemTitle }}
        </h3>
      </div>
    </article>
  </a>
</template>


<script setup>

import { useRouter } from 'vue-router';

const props = defineProps({
  problemId: String,
  problemTitle: String,
})

const router = useRouter()

// 跳转至详情页
function to_detail() {
  router.push({
    name: 'problem',
    query: { problemId: props.problemId }
  })
}

</script>


<style scoped>

:root {

  /**
   * Colors
   */

  --very-dark-grayish-blue: hsl(217, 19%, 35%);
  --light-grayish-blue:     hsl(210, 46%, 95%);
  --white:                  hsl(0, 0%, 100%);

  /**
   * Typography
   */

  --fs-13: 13px;
}

*, *::before, *::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

a {
  text-decoration: none;
  color: var(--white);
}

button {
  border: none;
  outline: none;
  cursor: pointer;
}

html {
  font-family: 'Manrope', sans-serif;
  font-weight: 500;
}

body {
  min-height: 100vh;
  display:         flex;
  justify-content: center;
  align-items:     center;
  background: var(--light-grayish-blue);
  padding: 20px;
}


/*-----------------------------------*\
 #MAIN STYLE
\*-----------------------------------*/

/**
 * problem card
 */

.problem-card {
  display: grid;
  grid-template-columns: 100% minmax(300px, 845px);
  max-width: 1000px;
  border-radius: 10px;
  box-shadow: 6px 6px 5px hsla(0, 0%, 0%, 0.02),
  25px 25px 20px hsla(0, 0%, 0%, 0.03),
  100px 100px 80px hsla(0, 0%, 0%, 0.05);
  font-family: 等线, serif;
  background-color: var(--md-ref-palette-neutral100);
}


.problem-card:hover { transform: scale(1.05); }

.problem-content {
  width: 100%;
  background: var(--white);
  padding: 32px 32px;
  border-radius: 0 10px 10px 0;
  display: flex;
  flex-direction: column;
  justify-content: left;
}

.problem-title {
  font-size: 20px;
  color: var(--very-dark-grayish-blue);
  margin-bottom: 12px;
  line-height: 1.4;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.problem-title:hover { color: var(--md-sys-color-primary-light); }


@media screen and (max-width: 768px) {

  :root {

    /**
     * Typography
     */

    --fs-13: 12px;

  }

  /**
   * problem card responsive
   */

  .problem-card {
    grid-template-columns: 100%;
    max-width: 294px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .problem-content {
    padding: 30px 28px 20px 20px;
  }

  .problem-title {
    font-size: 15px;

  }


}

</style>
