<template>
  <NavigateBar></NavigateBar>
  <div class="editor-wrapper" >
    <el-upload class="editor-main-block" drag :show-file-list="false" action="no_use" :http-request="upload" multiple>
        <div v-if="beforeUploading" class="editor-main-pic">
          <el-icon class="el-icon--upload">
            <UploadFilled style="color: var(--md-sys-color-primary-light)" />
          </el-icon>
          <div class="el-upload__text">
            Drop file here or <em>click to upload</em> your banner picture
          </div>
        </div>
        <img v-else :src="mainPicUrl" class="editor-main-pic upload-pic"/>
    </el-upload>
    <div class="editor-submit-block">
      <el-button round class="editor-submit-button" @click="submit_article">发布文章</el-button>
    </div>
    <div class="editor-view-header">
          <div class="editor-input-hint"> 标题 </div>
          <el-input class="editor-title-input" v-model="title" placeholder="Title" />
          <div class="editor-input-hint"> 分类  </div>
          <el-cascader v-model="category" :options="categoryOptions" />
          <div class="editor-input-hint"> 摘要  </div>
          <el-input :autosize="{ minRows: 2, maxRows: 4 }"
                  type="textarea" class="editor-abs-input" v-model="abstract" placeholder="Abstract" />
    </div>
    <div class="editor-main-body">
      <div id="vditor" />
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import NavigateBar from '@/components/base/NavigateBar.vue'
import Vditor from 'vditor';
import 'vditor/dist/index.css';

import { useUserStore } from "@/stores/user"
import { reactive, ref, onMounted } from "vue"
import { UploadFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter()
// 用于监控是否加载目录
const loadingOver = ref(false)
// 用于监控是否上传图片
const beforeUploading = ref(true)
const mainPicUrl = ref('')
// 文章信息
const title = ref('')
const category = ref('')
const abstract = ref('')
const userStore = reactive(useUserStore())
const categoryOptions = ref([])
// 初始化vditor
const vditor = ref<Vditor | null>(null);

// 加载目录数据
load_category()

onMounted(() => {
  vditor.value = new Vditor('vditor', {
    width: "90%",
    minHeight: 800,
    cache: {
      "enable": false
    },
    tab: "\t",
    mode: "sv",
    preview: {
      mode: "both"
    },
    upload: {
      accept: 'image/jpeg,image/png,image/gif,image/jpg,image/bmp', // 图片格式
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
                ElMessage({type: 'success', message: '图片上传成功.'})
                vditor.value!.insertValue(`![${name}](${response.data.data.succMap[name]})`); // 文本编辑器中插入图片
              } else {
                ElMessage({type: 'fail', message: '图片上传失败.'})
              }
            })
      }
    },
    after: () => {
      // vditor.value is a instance of Vditor now and thus can be safely used here
      vditor.value!.setValue('❤️ ');
    },
  });
});

function load_category() {
  axios.get('/category/allCategory')
      .then(function (response) {
        if (response.data.code === 200) {
          categoryOptions.value = response.data.data;
          loadingOver.value = true
        } else {
          console.log('获取目录失败!')
        }
      })
}

function submit_article () {
  if (userStore["token"] === '') {
    ElMessage({type: 'warning', message: '您尚未登录，不能上传文章.'})
  }
  else {
    axios.post('/article/add', {
      articleTitle: title.value,
      articleContent: vditor.value.getValue(),
      articleCategoryList: category.value,
      articleAbstract: abstract.value,
      articlePic: mainPicUrl.value,
      userId: userStore["user"].userId
    })
        .then(function (response) {
          if (response.data.code === 200) {
            ElMessage({type: 'success', message: '文章发布成功.'})
            router.push({
              name: 'articles',
            })
          } else {
            ElMessage({type: 'fail', message: '文章发布失败.'})
          }
        })
  }
}

function upload (param) {
  const formData = new FormData()
  formData.append('file', param.file)
  axios.post('/article/uploadFile', formData)
      .then(function (response) {
        if (response.data.code === 0) {
          beforeUploading.value = false
          mainPicUrl.value = response.data.data.succMap[param.file.name]
        } else {
          ElMessage({type: 'fail', message: '文件上传失败.'})
        }
      })
}

</script>

<style scoped>

.editor-wrapper{
  background: linear-gradient(var(--md-ref-palette-primary95), var(--md-ref-palette-tertiary95));
  z-index: -1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 100vh;
}

.editor-main-block {
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-left: auto;
  margin-right: auto;
  margin-top: 20px;
  width: 500px;
}

.editor-main-pic{
  width: 500px;
  height: 150px;
  padding: 0px;
}

.upload-pic {
  object-fit: scale-down;
  overflow: clip;
  display: block;
}

.editor-submit-block {
  display: flex;
  flex-direction: row;
  justify-content: center;
}

.editor-submit-button {
  margin-top: 10px;
  width: 150px;
  background: var(--md-sys-color-primary-light);
  border-color: var(--md-sys-color-primary-light);
  color: var(--md-sys-color-on-primary-light);
  box-shadow:  2px 2px 3px var(--md-ref-palette-primary90), 1px 1px 5px var(--md-sys-color-background-light);
}

.editor-submit-button:hover {
  transform: scale(1.1);
}

.editor-main-body{
  display: flex;
  flex-direction: row;
  justify-content: center;
}

#vditor {
  margin-top: 3%;
  margin-bottom: 5%;
}

.editor-view-header {
  width: 90%;
  margin: auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.editor-input-hint {
  border-radius: 20px;
  background-color: var(--md-ref-palette-tertiary80);
  color:  var(--md-ref-palette-tertiary10);
  box-shadow:  1px 1px 5px var(--md-sys-color-background-light), 1px 1px 3px var(--md-ref-palette-tertiary30);
  width: 100px;
  line-height: 30px;
  text-align: center;
  font-family: 微软雅黑;
  margin-top: 10px;
  margin-bottom: 10px;
}

</style>
