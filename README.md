# ChatViewer

为ChatViewer的前端项目，后端项目在main分支下。

[⭐B站演示视频](https://www.bilibili.com/video/BV18u411c7sz/?share_source=copy_web&vd_source=21cac58a94f918e40674f147fb0b2fc2) ：基于SpringBoot与Vue的博客+刷题网站，让ChatGPT帮你准备面试吧~

**接口文档**：https://chat-viewer.apifox.cn/

**后端技术栈**：

- 😄SpringBoot + MyBatis-Plus
- 🔒SpringSecurity + JWT：鉴权
- ❤Redis：点赞业务缓存、高频访问数据缓存
- 📃Kafka： 解耦点赞、评论操作 与 通知生成
- ✉️短信服务、文件存储 
- 🧚‍♂️[PlexPt ChatGPT SDK](https://github.com/PlexPt/chatgpt-java)：流式问答
- ⛷️CompletableFuture

**前端技术栈**：Vite(Vue3) + Element-UI Plus + Pinia + Vditor (因为没有找到比较符合的前端项目所以只好自己写了一下，但代码规范性和合理性可能会差一些)

**部署**：Docker + Nginx

**服务器代理**：[clash-for-linux](https://github.com/wanhebin/clash-for-linux)

**代码规范**：后端代码全部通过阿里编码规约扫描。

![image](https://github.com/houqingying/ChatViewer/assets/59137245/19f7c50e-17a7-4f96-8ebf-14080d5f43a6)


**TODO**：

- [ ] ES全文搜索
- [ ] 更完善的权限控制
- [ ] 拆为微服务

ChatViewer还有很多不完善的地方，如果您有宝贵的建议、不满 or 疑问，欢迎提issue！作者将尽快回复。

如果你对这个项目感兴趣，想要一起让它变得更好，欢迎联系我一起contribute！mail：18231041@buaa.edu.cn


## 运行

clone frontend分支下的前端项目。

### Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```
