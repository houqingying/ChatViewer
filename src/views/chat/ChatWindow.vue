<template>
  <div class="chat-window">
    <div class="top">
      <div class="head-pic">
        <HeadPortrait :imgUrl="friendInfo.headImg"></HeadPortrait>
      </div>
      <div class="info-detail">
        <div class="name">{{ friendInfo.name }}</div>
      </div>
    </div>
    <div class="bottom">
      <div class="chat-content" id="chatContent">
        <div class="chat-wrapper" v-for="item in chatList" :key="item.id">
          <div class="chat-friend" v-if="item.fromId !== userStore.user.userId">
            <div class="chat-text">
              {{ item.content }}
            </div>
            <div class="info-time">
              <span>{{ friendInfo.name }}</span>
              <span>{{ item.createTime }}</span>
            </div>
          </div>
          <div class="chat-me" v-else>
            <div class="chat-text">
              {{ item.content }}
            </div>
            <div class="info-time">
              <span>{{ item.createTime }}</span>
              <span>{{ userStore.user.userName }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="chatInputs">
        <input class="inputs" v-model="inputMsg" @keyup.enter="sendText" />
        <div class="send boxinput" @click="sendText">
          <img src="@/assets/img/rocket.png" alt="" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { animation } from "@/util/util";
import { getChatMsg, sendChatMsg } from "@/api/getData";
import { nextTick, onActivated, onMounted, reactive, ref, watch } from 'vue'
import HeadPortrait from "@/components/chat/HeadPortrait.vue";
import { useUserStore } from "@/stores/user";

const emit = defineEmits(['personCardSort'])
const personCardSort = (id) => {
  // 传递给父组件
  emit('personCardSort', id)
}
const props = defineProps({
  friendInfo: Object
})

const userStore = reactive(useUserStore())
const chatList = ref([])
const inputMsg = ref("")

onMounted(() => {
  getFriendChatMsg();
})

onActivated(() => {
  getFriendChatMsg()
})

watch(props, getFriendChatMsg)

//获取聊天记录
function getFriendChatMsg() {
  let params = {
    id1: props.friendInfo.id
  };
  getChatMsg(params).then((res) => {
    chatList.value = res;
    scrollBottom();
  });
}

//发送信息
function sendMsg(msgList) {
  chatList.value.push(msgList);
  scrollBottom();
}

//获取窗口高度并滚动至最底层
async function scrollBottom() {
  await nextTick()
  let chatContent = document.getElementById('chatContent')
  animation(chatContent, chatContent.scrollHeight - chatContent.offsetHeight);
}

//发送文字信息
function sendText() {
  if (inputMsg.value) {
    let chatMsg = {
      fromId: userStore["user"].userId,
      toId: props.friendInfo.id,
      content: inputMsg.value
    };
    sendMsg(chatMsg);
    personCardSort(props.friendInfo.id)
    sendChatMsg(chatMsg)
    inputMsg.value = "";
  } else {
    this.$message({
      message: "消息不能为空哦~",
      type: "warning",
    });
  }
}

</script>

<style lang="scss" scoped>
.chat-window {
  width: 100%;
  height: 100%;
  margin-left: 20px;
  position: relative;

  .top {
    margin-bottom: 50px;
    &::after {
      content: "";
      display: block;
      clear: both;
    }
    .head-pic {
      float: left;
    }
    .info-detail {
      float: left;
      margin: 5px 20px 0;
      .name {
        font-size: 20px;
        font-weight: 600;
        color: #fff;
      }
      .detail {
        color: #9e9e9e;
        font-size: 12px;
        margin-top: 2px;
      }
    }
    .other-fun {
      float: right;
      margin-top: 20px;
      span {
        margin-left: 30px;
        cursor: pointer;
      }
      // .icon-tupian {

      // }
      input {
        display: none;
      }
    }
  }
  .bottom {
    width: 100%;
    height: 80vh;
    background-color: rgb(50, 54, 68);
    border-radius: 20px;
    padding: 20px;
    box-sizing: border-box;
    position: relative;
    .chat-content {
      width: 100%;
      height: 85%;
      overflow-y: scroll;
      padding: 20px;
      box-sizing: border-box;
      &::-webkit-scrollbar {
        width: 0; /* Safari,Chrome 隐藏滚动条 */
        height: 0; /* Safari,Chrome 隐藏滚动条 */
        display: none; /* 移动端、pad 上Safari，Chrome，隐藏滚动条 */
      }
      .chat-wrapper {
        position: relative;
        word-break: break-all;
        .chat-friend {
          width: 100%;
          float: left;
          margin-bottom: 20px;
          display: flex;
          flex-direction: column;
          justify-content: flex-start;
          align-items: flex-start;
          .chat-text {
            max-width: 90%;
            padding: 20px;
            border-radius: 20px 20px 20px 5px;
            background-color: rgb(56, 60, 75);
            color: #fff;
            &:hover {
              background-color: rgb(39, 42, 55);
            }
          }
          .chat-img {
            img {
              width: 100px;
              height: 100px;
            }
          }
          .info-time {
            margin: 10px 0;
            color: #fff;
            font-size: 14px;
            img {
              width: 30px;
              height: 30px;
              border-radius: 50%;
              vertical-align: middle;
              margin-right: 10px;
            }
            span:last-child {
              color: rgb(101, 104, 115);
              margin-left: 10px;
              vertical-align: middle;
            }
          }
        }
        .chat-me {
          width: 100%;
          float: right;
          margin-bottom: 20px;
          position: relative;
          display: flex;
          flex-direction: column;
          justify-content: flex-end;
          align-items: flex-end;
          .chat-text {
            float: right;
            max-width: 90%;
            padding: 20px;
            border-radius: 20px 20px 5px 20px;
            background-color: rgb(29, 144, 245);
            color: #fff;
            &:hover {
              background-color: rgb(26, 129, 219);
            }
          }
          .chat-img {
            img {
              max-width: 300px;
              max-height: 200px;
              border-radius: 10px;
            }
          }
          .info-time {
            margin: 10px 0;
            color: #fff;
            font-size: 14px;
            display: flex;
            justify-content: flex-end;

            img {
              width: 30px;
              height: 30px;
              border-radius: 50%;
              vertical-align: middle;
              margin-left: 10px;
            }
            span {
              line-height: 30px;
            }
            span:first-child {
              color: rgb(101, 104, 115);
              margin-right: 10px;
              vertical-align: middle;
            }
          }
        }
      }
    }
    .chatInputs {
      width: 90%;
      position: absolute;
      bottom: 0;
      margin: 3%;
      display: flex;
      .boxinput {
        width: 50px;
        height: 50px;
        background-color: rgb(66, 70, 86);
        border-radius: 15px;
        border: 1px solid rgb(80, 85, 103);
        position: relative;
        cursor: pointer;
        img {
          width: 30px;
          height: 30px;
          position: absolute;
          left: 50%;
          top: 50%;
          transform: translate(-50%, -50%);
        }
      }
      .emoji {
        transition: 0.3s;
        &:hover {
          background-color: rgb(46, 49, 61);
          border: 1px solid rgb(71, 73, 82);
        }
      }

      .inputs {
        width: 90%;
        height: 50px;
        background-color: rgb(66, 70, 86);
        border-radius: 15px;
        border: 2px solid rgb(34, 135, 225);
        padding: 10px;
        box-sizing: border-box;
        transition: 0.2s;
        font-size: 20px;
        color: #fff;
        font-weight: 100;
        margin: 0 20px;
        &:focus {
          outline: none;
        }
      }
      .send {
        background-color: rgb(29, 144, 245);
        border: 0;
        transition: 0.3s;
        box-shadow: 0 0 5px 0 rgba(0, 136, 255);
        &:hover {
          box-shadow: 0 0 10px 0 rgba(0, 136, 255);
        }
      }
    }
  }
}
</style>
