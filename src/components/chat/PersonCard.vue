<template>

  <div class="person-card" :class="{ activeCard: personInfo.id === pcCurrent }">
    <div class="info">
          <el-badge v-if="personInfo.unReadCounts > 0" :value="personInfo.unReadCounts" :max="99" class="item">
            <HeadPortrait :imgUrl="personInfo.headImg"></HeadPortrait>
          </el-badge>
          <HeadPortrait v-else :imgUrl="personInfo.headImg"></HeadPortrait>
          <div class="info-detail">
            <div class="name">{{ personInfo.name }}</div>
            <div class="detail">{{ personInfo.lastMsg }}</div>
          </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import HeadPortrait from "./HeadPortrait.vue";
import { defineProps } from "vue";

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const props = defineProps({
  personInfo: Object,
  pcCurrent: String
})

</script>

<style lang="scss" scoped>
.person-card {
  width: 250px;
  height: 80px;
  border-radius: 10px;
  background-color: rgb(50, 54, 68);
  position: relative;
  margin: 25px 0;
  cursor: pointer;
  .info {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 90%;
    transform: translate(-50%, -50%);

    display: flex;
    .info-detail {
      margin-top: 5px;
      margin-left: 20px;
      .name {
        color: #fff;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-bottom: 5px;
      }
      .detail {
        color: #5c6675;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        font-size: 12px;
      }
    }
  }
  &:hover {
    background-color: #1d90f5;
    transition: 0.3s;
    box-shadow: 0px 0px 10px 0px rgba(0, 136, 255);
    // box-shadow:  0 5px 20px rgba(251, 152, 11, .5);
    .info {
      .info-detail {
        .detail {
          color: #fff;
        }
      }
    }
  }
}
.activeCard {
    background-color: #1d90f5;
    transition: 0.3s;
    box-shadow: 3px 2px 10px 0px rgba(0, 136, 255);
    .info {
      .info-detail {
        .detail {
          color: #fff;
        }
      }
    }
}
</style>
