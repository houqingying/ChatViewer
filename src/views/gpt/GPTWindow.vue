<template>
  <div class="chat-window">
    <div class="bottom">
      <div class="chat-content" id="chatContent">
        <div class="chat-wrapper" v-for="item in chatList" :key="item.messageId">
          <div class="chat-friend" v-if="item.messageDirection !== 0">
            <div class="info-time">
              <span>{{ item.createTime }}</span>
            </div>
            <div v-html="renderContent(item.content)" class="chat-text"></div>
          </div>
          <div class="chat-me" v-else>
            <div class="info-time">
              <span>{{ item.createTime }}</span>
            </div>
            <div v-html="renderContent(item.content)" class="chat-text"></div>
          </div>
        </div>
        <div v-if="answerContent !== ''" class="chat-wrapper">
          <div class="chat-friend">
            <div v-html="renderContent(answerContent)" class="chat-text"></div>
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
import { getGptMessage } from "@/api/getData";
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import { getCurrentDateTime } from '@/components/utils/FormatChecker'
import { useUserStore } from "@/stores/user";
import {fetchEventSource} from "@microsoft/fetch-event-source";
import {Marked} from "marked";
import {markedHighlight} from "marked-highlight";
import hljs from 'highlight.js';
import '@/assets/css/atom-one-dark.css'

const emit = defineEmits(['convCardSort'])
const convCardSort = (id) => {
  // 传递给父组件
  emit('convCardSort', id)
}
// 父组件传进来参数
const props = defineProps({
  conversationInfo: Object
})

const userStore = reactive(useUserStore())

const chatList = ref([])
const inputMsg = ref("")
const answerContent = ref('')

onMounted(() => {
  getFriendChatMsg();
})

watch(props, getFriendChatMsg)

// 设置markdown解析器及其参数
const marked = new Marked(
    markedHighlight({
      langPrefix: 'hljs language-',
      highlight(code, lang) {
        const language = hljs.getLanguage(lang) ? lang : 'plaintext';
        return hljs.highlight(code, { language }).value;
      }
    })
);

function renderContent(value) {
  return marked.parse(value)
}

//获取聊天记录
function getFriendChatMsg() {
  const param = props.conversationInfo.conversationId
  getGptMessage(param).then((res) => {
    chatList.value = res;
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
      messageDirection: 0,
      content: inputMsg.value,
      createTime: getCurrentDateTime()
    };
    sendMsg(chatMsg);
    convCardSort(props.conversationInfo.conversationId)
    ask(inputMsg.value)
    inputMsg.value = "";
  } else {
    this.$message({
      message: "消息不能为空哦~",
      type: "warning",
    });
  }
}

function ask(prompt) {
  let controller = new AbortController()
  let eventSource = fetchEventSource("http://localhost:8080/gpt/chat", {
    method: 'POST',
    headers: {
      Accept: "text/event-stream",
      "Content-Type": "application/json",
      "Token" : userStore["token"]
    },
    body: JSON.stringify({
      useToken: userStore["useToken"],
      conversationId: props.conversationInfo.conversationId,
      prompt: prompt
    }),
    signal: controller.signal,
    openWhenHidden: true,
    onopen() {

    },
    onmessage(event) {
      answerContent.value += JSON.parse(event.data).content
    },
    onclose() {
      sendMsg({
        messageDirection: 1,
        content: answerContent.value,
        createTime: getCurrentDateTime()
      })
      answerContent.value = ''
      scrollBottom();
      controller.abort();
      eventSource.close();
    },
    onerror() {
      //出错后不要重试
      answerContent.value = ''
      controller.abort();
      eventSource.close();
    }
  })
}

</script>

<style lang="scss" scoped>

.chat-window {
  width: 100%;
  height: 100%;
  margin-left: 20px;
  .bottom {
    width: 100%;
    height: 86vh;
    background-color: var(--md-ref-palette-neutral-variant100);
    border-radius: 20px;
    border:  1px solid var(--md-ref-palette-neutral-variant90);
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
            padding: 3px 20px 3px 20px;
            border-radius: 20px 20px 20px 5px;
            background-color: var(--md-sys-color-secondary-container-light);
            color: var(--md-sys-color-primary-on-secondary-light);
            &:hover {
              background-color: var(--md-sys-color-secondary-container-light);
            }
          }
          .info-time {
            margin: 10px 0;
            color: #fff;
            font-size: 14px;
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
            padding: 3px 20px 3px 20px;
            border-radius: 20px 20px 5px 20px;
            background-color: var(--md-sys-color-primary-container-light);
            color: var(--md-sys-color-primary-on-container-light);
          }
          .info-time {
            margin: 10px 0;
            color: #fff;
            font-size: 14px;
            display: flex;
            justify-content: flex-end;
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
      .inputs {
        width: 90%;
        height: 50px;
        background-color: var(--md-ref-palette-neutral-variant100);
        border-radius: 15px;
        border: 2px solid rgb(34, 135, 225);
        padding: 10px;
        box-sizing: border-box;
        transition: 0.2s;
        font-size: 15px;
        color: var(--md-sys-color-on-surface-light);
        font-weight: 100;
        margin: 0 20px;
        &:focus {
          outline: none;
        }
      }
      .send {
        background-color: var(--md-sys-color-primary-light);
        border: 0;
        transition: 0.3s;
        box-shadow: 0 0 5px 0 var(--md-sys-color-primary-light);
        &:hover {
          box-shadow: 0 0 10px 0 var(--md-sys-color-primary-light);
        }
      }
    }
  }
}
</style>
