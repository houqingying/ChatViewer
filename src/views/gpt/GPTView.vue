<template>
  <NavigateBar></NavigateBar>
  <div class="home">
    <div class="chatHome">
    <div class="chatLeft">
      <NewButton style="margin-left: 10px;margin-bottom: 20px" @click="createChat"></NewButton>
      <div class="online-person">
        <div class="person-cards-wrapper">
          <div class="conversationList" v-for="conversationInfo in conversationList" :key="conversationInfo.conversationId" @click="clickPerson(conversationInfo);">
            <ConversationCard :conversationInfo="conversationInfo" :pcCurrent="pcCurrent" @deleteConversation="deleteConversation"></ConversationCard>
          </div>
        </div>
      </div>
    </div>
    <div class="chatRight">
      <div v-if="showChatWindow">
        <GPTWindow :conversationInfo="chatWindowInfo" @convCardSort="convCardSort"></GPTWindow>
      </div>
      <div v-else>
        <div class="default-area">
          <img class="default-image" src="../../assets/index/card2.png" alt="hi">
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup lang="ts">
import ConversationCard from "@/components/gpt/ConversationCard.vue";
import GPTWindow from "./GPTWindow.vue";
import { getChatgptLog, createChatgptConversation, deleteChatgptConversation  } from "@/api/getData";
import { ref, onMounted, reactive } from "vue";
import { useUserStore } from "@/stores/user";
import NavigateBar from "@/components/base/NavigateBar.vue";
import NewButton from "@/components/gpt/NewButton.vue";
import {ElMessage} from "element-plus";

const pcCurrent = ref("")
const conversationList =  ref([])
const showChatWindow =  ref(false)
const chatWindowInfo = ref({})

const userStore = reactive(useUserStore())

onMounted(() => {
  getChatgptLog()
      .then((res) => {
        conversationList.value = res;
      })
      .catch(function () {
        localStorage.clear()
        userStore.$patch({token: ''})
        userStore.$patch({user: {}})
        ElMessage({
          showClose: true,
          message: '对不起，此功能需要登录后使用！',
          type: 'warning',
        })
      });
})

function clickPerson(info) {
  chatWindowInfo.value = info;
  pcCurrent.value = info.conversationId;
  showChatWindow.value = true;
}

function convCardSort(id) {
  if (id !== conversationList.value[0].conversationId) {
    let nowPersonInfo;
    for (let i = 0; i < conversationList.value.length; i++) {
      if (conversationList.value[i].conversationId == id) {
        nowPersonInfo = conversationList.value[i];
        conversationList.value.splice(i, 1);
        break;
      }
    }
    conversationList.value.unshift(nowPersonInfo);
  }
}

function deleteConversation(id) {
  deleteChatgptConversation(id)
  conversationList.value = conversationList.value.filter(item => item.conversationId != id)
}


function createChat() {
  createChatgptConversation({
    conversationType: 0
  }).then((res) => {
    conversationList.value.unshift({
      conversationId: res,
      firstMessage: "尽情提问吧~"
    })
  });
}

</script>

<style lang="scss" scoped>
.home {
  width: 100%;
  height: 100%;
  background-color: var(--md-ref-palette-neutral98);
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: -1;
}
.chatHome {
  margin-top: 86px;
  margin-left: 20px;
  display: flex;
  .chatLeft {
    .title {
      color: #fff;
      padding-left: 10px;
    }
    .online-person {
      .online-text {
        padding-left: 10px;
        color: rgb(176, 178, 189);
      }
      .person-cards-wrapper {
        padding-left: 10px;
        height: 75vh;
        overflow: hidden;
        overflow-y: scroll;
        box-sizing: border-box;
        &::-webkit-scrollbar {
          width: 0; /* Safari,Chrome 隐藏滚动条 */
          height: 0; /* Safari,Chrome 隐藏滚动条 */
          display: none; /* 移动端、pad 上Safari，Chrome，隐藏滚动条 */
        }
      }
    }
  }

  .chatRight {
    width: 100%;
    padding-right: 30px;
  }

  .default-area {
    width: 90%;
    background-color: #f1f1f1;
    border-radius: 20px;
    display: flex;
    margin: auto;
    content: "";
    opacity: 0.5;
  }

  .default-image {
    display: flex;
    margin: auto;
    width: 85vh;
    height: 85vh;
  }
}

@media screen and (max-width: 768px) {
  .chatHome{
    .chatLeft {
      display: none;
    }
  }
}
</style>
