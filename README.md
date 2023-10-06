# ChatViewer

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

![image](https://github.com/houqingying/ChatViewer/assets/59137245/badc9261-dea7-4d6a-b861-c2752dca7dc3)


**TODO**：

- [ ] ES全文搜索
- [ ] 更完善的权限控制
- [ ] 拆为微服务

ChatViewer还有很多不完善的地方，如果您有宝贵的建议、不满 or 疑问，欢迎提issue！作者将尽快回复。

如果你对这个项目感兴趣，想要一起让它变得更好，欢迎联系我一起contribute！mail：18231041@buaa.edu.cn

## 效果预览

![image](https://github.com/houqingying/ChatViewer/assets/59137245/fb391e8a-d655-4694-b82c-383802732a9e)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/e4b00671-e7d5-442b-9d0c-01988609adf2)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/013325f7-7f4e-4c71-896b-4ae93fbc4d96)

## 接口文档

**接口文档**：https://chat-viewer.apifox.cn/

![image](https://github.com/houqingying/ChatViewer/assets/59137245/54bb5900-5c42-44c2-a50b-54c3b01f126a)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/7d7bd125-f741-43fa-a0dd-4a6aba93a0ce)


## 运行

本地新建SpringBoot项目，将ChatViewer项目clone至对应文件夹下。

![image](https://github.com/houqingying/ChatViewer/assets/59137245/50ae2193-a566-432a-8ec5-ba4258c7574b)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/9b34aaab-5a3f-4ebc-ba88-2039c0b69f52)


删除`application.properties`文件。

运行有可能出现编码问题，在settings中更改编码，apply。然后将`application.yml`中的内容复制到记事本，删除`application.yml`文件并新建`application.yml`文件，粘贴记事本内容，Run以测试SpringBoot项目能否启动。

若能够启动，更改`application.yml`中MySQL、Redis、Kafka相关设置，以及GPT与阿里云key，再次Run，项目应能正确运行。

![image](https://github.com/houqingying/ChatViewer/assets/59137245/2936cef0-c0b7-4b4e-8e26-9fc31f3ceb88)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/13bc6ad2-08fd-4a77-9091-a19e2025236a)

![image](https://github.com/houqingying/ChatViewer/assets/59137245/66a00f7b-db8a-4ee0-80e2-72b62c199e26)

