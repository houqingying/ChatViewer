<template>
  <NavigateBar></NavigateBar>
  <div class="container">
    <div class="left-container">
      <div class="question-container" v-if="loadingProblem">
        <el-button round color="#3c813f" plain @click="toList" class="return-button">
          <el-icon><Back/></el-icon>
        </el-button>
        <el-button round color=#3c813f plain @click="getGptHelp" v-if="userStore.token !== ''" >求助ChatGPT</el-button>
        <el-button round color=#3c813f plain @click="saveAnswer" v-if="userStore.token !== ''" >保存我的回答</el-button>
        <div class="question-title">
          <img :src="glassIcon" style="width:50px;height:50px;" alt="fail"/>
          {{ problem.problemTitle }}
        </div>
        <div  class="answer-container">
          <div class="audio-card" v-if="userStore.token !== ''">
            <audio controls ref="audio" class="aud" v-if="loadingAnswer && (answerAudio != null)" :src="answerAudio">
            </audio>
            <div class="buttons">
              <el-button color=#3c813f plain text round v-if="!isRecording" @click="startRecordAudio">
                <img :src="playIcon" style="width:20px;height:20px;" alt="fail"/>录音
              </el-button>
              <el-button round color=#3c813f plain text v-else @click="stopRecordAudio">
                <img :src="recordIcon" style="width:20px;height:20px;" alt="fail"/>{{ recorder.duration.toFixed(4) }} 停止
              </el-button>
              <el-button round color=#3c813f plain text @click="playRecordAudio">播放</el-button>

              <el-button round color=#3c813f plain text @click="uploadWAVData">
                <img :src="uploadIcon" style="width:20px;height:20px;" alt="fail"/>上传
              </el-button>
              <el-button round color=#3c813f plain text @click="downloadWAVRecordAudioData">下载</el-button>
            </div>
          </div>
          <el-collapse style="font-size:20px;margin-top: 30px;">
            <el-collapse-item title="参考答案" name="1">
              <div>
                {{ problem.problemAnswer }}
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </div>
    <div class="right-container" v-if="userStore.token !== ''">
      <div class="gpt-container" v-if="loadingAnswer">
        <GPT :conversationInfo="conversationInfo"></GPT>
      </div>
      <div class="editor-container">
        <div id="vditor" />
      </div>
    </div>
    <div class="right-container" v-else>
      <div class="default-area">
        <img class="default-image" src="../../assets/index/card4.png" alt="hi">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import NavigateBar from "@/components/base/NavigateBar.vue";
import GPT from "./GPT.vue";
import { ref, reactive } from "vue";
import Vditor from 'vditor';
import 'vditor/dist/index.css';
import axios from "axios";
import glassIcon from '@/assets/icon/glass.gif'
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";
import Recorder from "js-audio-recorder";
import recordIcon from '@/assets/icon/record.gif'
import playIcon from '@/assets/icon/play.gif'
import uploadIcon from '@/assets/icon/upload.gif'

// 初始化vditor
const vditor = ref<Vditor | null>(null);

// 得到problemId
const router = useRouter()
const route = useRoute()
const problemId = ref(route.query.problemId)
const userStore = reactive(useUserStore())

// 加载problem
const loadingProblem = ref(false)
const problem = ref({})
function load_problem() {
  axios.get('/problem?problemId=' + problemId.value)
      .then(function (response) {
        if (response.data.code === 200) {
          problem.value = response.data.data
          loadingProblem.value = true;
          loadAnswer()
        } else {
          console.log('获取问题失败!')
        }
      })
}
load_problem()


// 查询用户回答信息：answer中包含：用户是否回答过该问题(Content)，是否存在与chatgpt的对话(conversationId)
const conversationInfo = ref({})
const answerInfo = ref<NonNullable<unknown> | null>()
const answerAudio = ref("")
const loadingAnswer = ref(false)
function loadAnswer() {
  axios.get('/problem/answer?problemId=' + problemId.value + '&userId=' + userStore["user"].userId)
      .then(function (response) {
        if (response.data.code === 200) {
          conversationInfo.value = response.data.data
          answerInfo.value = response.data.data
          answerAudio.value = response.data.data != null ? response.data.data.answerAudio : null;
          loadingAnswer.value = true
          loadVditor()
        } else {
          console.log('获取回答失败!')
        }
      })
}

// 取得文件扩展名
function getFileExtension(filename) {
  return filename.split(".").pop()
}

function loadVditor() {
  vditor.value = new Vditor('vditor', {
    width: "100%",
    height: "45vh",
    cache: {
      "enable": false
    },
    tab: "\t",
    mode: "ir",
    preview: {
      mode: "both"
    },
    upload: {
      accept: 'image/jpeg,image/png,image/gif,image/jpg,image/bmp,.wav', // 图片格式
      max: 2 * 1024 * 1024,  // 控制大小
      multiple: false, // 是否允许批量上传
      fieldName: 'file', // 上传字段名称
      // 文件名安全处理
      filename(name) {
        return name
            .replace(/[^(a-zA-Z\d\u4e00-\u9fa5.)]/g, '')
            .replace(/[?\\/:|<>*[\]()$%{}@~]/g, '')
            .replace('/\\s/g', '');
      },
      handler: (files: File[]) => {
        const file = files[0]
        const name = file.name
        const formData = new FormData()
        formData.append('file', file)
        axios.post('/article/uploadFile', {
          file: file
        },{
          headers:{'Content-Type': 'multipart/form-data'}
        })
            .then(function (response) {
              if (response.data.code === 0) {
                ElMessage({type: 'success', message: '上传成功.'})
                if (getFileExtension(name) in [".jpeg", ".jpg", '.png']) {
                  vditor.value!.insertValue(`![${name}](${response.data.data.succMap[name]})`); // 文本编辑器中插入图片
                }
                else {
                  vditor.value!.insertValue(`${response.data.data.succMap[name]}`); // 文本编辑器中插入图片
                }
              } else {
                ElMessage({type: 'fail', message: '上传失败.'})
              }
            })
      }
    },
    after: () => {
      // vditor.value is a instance of Vditor now and thus can be safely used here
      // eslint-disable-next-line no-prototype-builtins
      if (answerInfo.value != null && answerInfo.value.hasOwnProperty("answerContent") && answerInfo.value.answerContent != null) {
        vditor.value!.setValue(answerInfo.value.answerContent);
      }
      else {
        vditor.value!.setValue('请在此输入你的答案，输入完成后可使用ChatGPT矫正，或保存答案❤️ ');
      }
    },
  });
}

// 保存回答
function saveAnswer() {
  axios.post('/problem/answer', {
    problemId: problemId.value,
    userId: userStore["user"].userId,
    answerContent: vditor.value.getValue()
  })
      .then(function (response) {
        if (response.data.code === 200) {
          ElMessage({type: 'success', message: '保存成功.'})
        } else {
          ElMessage({type: 'fail', message: '保存失败.'})
        }
      })
}

// GPT纠正答案
function getGptHelp() {
  axios.post('/problem/help', {
    problemId: problemId.value,
    userId: userStore["user"].userId,
    answerContent: vditor.value.getValue()
  })
      .then(function (response) {
        if (response.data.code === 200) {
          conversationInfo.value = response.data.data;
        } else {
          alert('查询GPT失败!')
        }
      })
}

// 跳转至列表页
function toList() {
  router.push({
    name: 'problems',
  })
}

const tmp = new Recorder({
  sampleBits: 16, // 采样位数，支持 8 或 16，默认是16
  sampleRate: 16000, // 采样率，支持 11025、16000、22050、24000、44100、48000，根据浏览器默认值，我的chrome是48000
  numChannels: 1, // 声道，支持 1 或 2， 默认是1
  // compiling: false,(0.x版本中生效,1.x增加中)  // 是否边录边转换，默认是false
})
const recorder = ref(tmp)

const isRecording = ref(false)

//开始录音
function startRecordAudio() {
  Recorder.getPermission().then(
      () => {
        console.log("开始录音");
        isRecording.value = true
        recorder.value.start(); // 开始录音
      },
      (error) => {
        ElMessage({
          message: "请先允许该网页使用麦克风",
          type: "info",
        });
        console.log(`${error.name} : ${error.message}`);
      }
  );
}

//停止录音
function stopRecordAudio() {
  console.log("停止录音");
  recorder.value.stop();
  isRecording.value = false
}

//播放录音
function playRecordAudio() {
  console.log("播放录音");
  recorder.value.play();
}

//下载WAV录音文件
function downloadWAVRecordAudioData() {
  recorder.value.downloadWAV("badao");
}

const changeAudio = ref(false)
//上传wav录音数据
function uploadWAVData() {
  const wavBlob = recorder.value.getWAVBlob();
  // 创建一个formData对象
  const formData = new FormData();
  // 此处获取到blob对象后需要设置fileName满足当前项目上传需求，其它项目可直接传把blob作为file塞入formData
  const newbolb = new Blob([wavBlob], { type: 'audio/wav' })
  //获取当时时间戳作为文件名
  const filename = new Date().getTime() + '.wav'
  const fileOfBlob = new File([newbolb], filename)
  formData.append('file', fileOfBlob)
  // 尝试上传文件
  axios.post('/article/uploadFile', formData)
      .then(function (response) {
        if (response.data.code === 0) {
          // 上传成功，保存录音地址
          const url = response.data.data.succMap[filename]
          console.log(url)
          axios.post('/problem/answer', {
            problemId: problemId.value,
            userId: userStore["user"].userId,
            answerAudio: url
          })
              .then(function (response) {
                if (response.data.code === 200) {
                  ElMessage({type: 'success', message: '保存成功.'})
                  answerAudio.value = url
                  changeAudio.value = !changeAudio.value
                } else {
                  ElMessage({type: 'fail', message: '保存失败.'})
                }
              })
        } else {
          alert('上传失败!')
        }});
}
</script>

<style lang="scss" scoped>
.container {
  background-color: var(--md-sys-color-surface-variant-light);
  display: flex;
  width: 100%;
  height: calc(100vh - 56px);
  position: absolute;
}

.left-container{
  width: 50%;
  height: 100%;
  .question-container {
    height: calc(98% - 20px);
    border-radius: 5px;
    margin: 5px 5px 10px 10px;
    padding: 20px 30px 0 30px;
    background-color: var(--md-ref-palette-neutral100);
    .title-button {
      margin-bottom: 10px;
      height: 30px;
      width: 80px;
      border-radius: 30px;
      font-size: 20px;
      line-height: 30px;
      text-align: center;
      color: var(--md-sys-color-on-primary-light);
      background-color: var(--md-sys-color-primary-light);
    }
    .question-title {
      margin-top: 50px;
      font-size: 18px;

    }
    .answer-container {
      margin-top: 20px;
      .audio-card {
        border-radius: 15px;
        color: var(--md-sys-color-on-primary-container-light);
        background-color: var(--md-ref-palette-primary95);
        box-shadow:  2px 3px 10px var(--md-ref-palette-primary90), 1px 1px 5px var(--md-sys-color-background-light);
        padding: 20px;
        .hint {
          font-size: 13px;
          margin-top: 10px;
          margin-bottom: 10px
        }
        .aud {
          width: 100%;
          height: 40px;
        }
      }
      .buttons {
        margin-top: 10px;
      }
    }
  }

}

.right-container {
  width: 50%;
  .gpt-container {
    margin: 5px 10px 0 5px;
    height: 50%;
  }
  .editor-container {
    margin: 0 10px 0 5px;


  }
}

.default-area {
  height: 100%;
  background-color: #f1f1f1;
  border-radius: 10px;
  display: flex;
  content: "";
  opacity: 0.5;
}

.default-image {
  display: flex;
  margin: auto;
  width: 50vw;
  height: 40vw;
}

</style>
