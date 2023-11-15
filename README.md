# ChatViewer

[⭐B站演示视频](https://www.bilibili.com/video/BV18u411c7sz/?share_source=copy_web&vd_source=21cac58a94f918e40674f147fb0b2fc2) ：基于SpringBoot与Vue的博客+刷题网站，让ChatGPT帮你准备面试吧~

**接口文档**：https://chat-viewer.apifox.cn/

**后端技术栈**：

- 😄SpringBoot + MyBatis-Plus
- 🔒SpringSecurity + JWT：鉴权
- ❤Redis：点赞业务缓存、高频访问数据缓存
- 📃Kafka： 解耦点赞、评论操作 与 通知生成
- ✉️短信服务、文件存储 
- 🧚‍♂️[PlexPt ChatGPT SDK](https://github.com/PlexPt/chatgpt-java)：流式问答，支持使用官方API-Key和限额token两种方式。
- 🛒Redisson分布式锁、Lua脚本、RabbitMQ消息队列实现限量免费token秒杀
- ⛷️CompletableFuture

**前端技术栈**：Vite(Vue3) + Element-UI Plus + Pinia + Vditor (因为没有找到比较符合的前端项目所以只好自己写了一下，但代码规范性和合理性可能会差一些)

**部署**：Docker + Nginx

**服务器代理**：[clash-for-linux](https://github.com/wanhebin/clash-for-linux)

**代码规范**：后端代码全部通过阿里编码规约扫描。

![image](https://github.com/houqingying/ChatViewer/assets/59137245/77f83c9a-a958-4395-abe3-e95f5734a4b7)


**TODO**：

- [ ] ES全文搜索
- [ ] 更完善的权限控制
- [ ] 拆为微服务

ChatViewer还有很多不完善的地方，如果您有宝贵的建议、不满 or 疑问，欢迎提issue！作者将尽快回复。

如果你对这个项目感兴趣，想要一起让它变得更好，欢迎联系我一起contribute！mail：18231041@buaa.edu.cn

## 效果预览

![image](https://github.com/houqingying/ChatViewer/assets/59137245/f7121bad-d7cc-4444-ab15-53c390e41320)


![image](https://github.com/houqingying/ChatViewer/assets/59137245/c50d2e7c-0555-4e49-845f-fc0c11d110f1)


![image](https://github.com/houqingying/ChatViewer/assets/59137245/b1976342-6af1-4aa2-850a-a37c68ffcbdd)


## 接口文档

**接口文档**：https://chat-viewer.apifox.cn/

![image](https://github.com/houqingying/ChatViewer/assets/59137245/7680c9eb-1043-4b50-9f5c-a5f07f3ab7f9)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/1be3e81f-45ba-410f-a2b3-526b8f7a25da)



## 运行

后端项目在main分支下，前端项目在frontend分支下。

本地新建SpringBoot项目，将ChatViewer项目clone至对应文件夹下。

![image](https://github.com/houqingying/ChatViewer/assets/59137245/e2b42eeb-3cc3-4287-94fa-d53299865664)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/a290956c-741c-450b-a8c4-7884e6eaaffe)



删除`application.properties`文件。

运行有可能出现编码问题，在settings中更改编码，apply。然后将`application.yml`中的内容复制到记事本，删除`application.yml`文件并新建`application.yml`文件，粘贴记事本内容，Run以测试SpringBoot项目能否启动。

若能够启动，更改`application.yml`中MySQL、Redis、Kafka相关设置，以及GPT与阿里云key，再次Run，项目应能正确运行。

![image](https://github.com/houqingying/ChatViewer/assets/59137245/1ae9346d-36f1-4682-af11-caf33c5c8d71)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/254c9926-cb6f-42b7-89a2-f6ac1867905a)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/9697567c-34fe-40a2-84bc-dfcafc427569)


