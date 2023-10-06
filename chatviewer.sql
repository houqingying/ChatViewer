-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: 39.105.199.147    Database: blog
-- ------------------------------------------------------
-- Server version	5.7.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `blog_answer`
--

DROP TABLE IF EXISTS `blog_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_answer` (
  `answer_id` bigint(20) NOT NULL,
  `problem_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `conversation_id` bigint(20) DEFAULT NULL,
  `answer_content` varchar(1024) DEFAULT NULL,
  `answer_audio` varchar(256) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_answer`
--

LOCK TABLES `blog_answer` WRITE;
/*!40000 ALTER TABLE `blog_answer` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_article`
--

DROP TABLE IF EXISTS `blog_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_article` (
  `article_id` bigint(64) NOT NULL,
  `article_title` varchar(256) DEFAULT NULL,
  `article_content` mediumtext,
  `category_id` bigint(64) DEFAULT NULL,
  `article_abstract` text,
  `article_pic` varchar(256) DEFAULT NULL,
  `user_id` bigint(64) DEFAULT NULL,
  `like_counts` int(11) DEFAULT '0',
  `comment_counts` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`article_id`),
  KEY `USER_INDEX` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_article`
--

LOCK TABLES `blog_article` WRITE;
/*!40000 ALTER TABLE `blog_article` DISABLE KEYS */;
INSERT INTO `blog_article` VALUES (1703374330079252482,'【操作系统】进程和线程','# 1 进程的概念与组成\n\n**程序**：静态的，存放在磁盘里的可执行文件，一系列的指令集合。\n\n**进程(Process)**：动态的，程序的一次执行过程。同一个程序的多次执行会产生不同的进程，OS会在进程创建时为其分配一个唯一的、不重复的进程ID (PID)。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/2e408e28-f7e4-4861-a7fc-1adba23c0448.png)\n\n**进程控制块(PCB)**：操作系统需要对各个并发的进程进行管理，但凡管理时需要的信息，都会被放在PCB中。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/c0093ca8-90e9-4eaf-8d55-162d950d9605.png)\n\n**程序的运行过程**：编译链接产生可执行文件，运行前创建对应的进程，即创建相应的PCB，把程序指令装入内存的程序段，运行过程中产生的各种数据装入数据段。\n\n# 2 进程的状态与转换\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/1183c855-c59c-437b-ad7b-288434bc1a73.png)\n\n> 注：在虚拟内存管理的操作系统中，通常会把阻塞状态的进程的物理内存空间换出到硬盘，等需要再次运行的时候，再从硬盘换入到物理内存，以避免浪费物理内存。进程没有占用实际的物理内存空间的情况，这个状态就是**挂起状态**。\n\n**PCB的组织形式**：(1)通常使用**链表**，把具有相同状态的进程链在一起，组成各种队列。(2)也有使用**索引**方式，将同一状态的进程组织在一个索引表中，索引表项指向相应的PCB，不同状态对应不同的索引表。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/9dc6fe3b-9e70-44cc-b182-4790e6c82f5d.png)\n\n# 3 进程的控制\n\n实现进程的**创建、终止、阻塞、唤醒**，这些过程也就是进程的控制。理解即可，不需要死记硬背过程。\n\n**原语**：一种特殊的程序，执行具有原子性，即这段程序的运行必须一气呵成、不可中断。\n\n### 1 进程创建（创建原语）\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/26f41401-1174-4e43-85ba-9d20fc9b643b.png)\n\n### 2 进程终止（撤销原语）\n\n进程可以有3种终止方式：正常结束、异常结束以及外界干预（`kill` 掉）。\n\n```mermaid\ngraph LR;\n		A(查找终止进程的PCB)-->B(正在运行?<br>剥夺CPU<br>CPU分给其他进程)\n		B-->C(有子进程?<br>子进程交给1号进程接管)\n		C-->D(资源归还OS)\n		D-->E(将PCB从所在队列删除)\n```\n\n### 3 进程阻塞\n\n终止和阻塞的上一个状态只能是**运行态**。\n\n```mermaid\ngraph LR;\n		A(找到被阻塞进程对应PCB)-->B(正在运行?<br>保护现场)\n		B-->C(PCB插入阻塞队列)\n```\n\n### 4 唤醒进程\n\n因为何事阻塞，就应该由何事唤醒。\n\n进程由「运行」转变为「阻塞」状态必须等待某一事件的完成，处于阻塞状态的进程是绝对不可能叫醒自己的。\n\n```mermaid\ngraph LR;\n		A(阻塞队列中<BR>找到相应进程的PCB)-->B(将其从阻塞队列中移出<BR>置其状态为就绪状态)\n		B-->C(PCB插入到就绪队列中<BR>等待调度程序调度)\n```\n\n### 5 进程的切换\n\n**CPU 上下文切换**：先把前一个**任务**的 CPU 上下文（CPU 寄存器和程序计数器）保存起来，系统内核会存储保持下来的上下文信息，然后加载新任务的上下文到这些寄存器和程序计数器，最后再跳转到程序计数器所指的新位置，运行新任务。\n\n> 上面说到所谓的「任务」，主要包含进程、线程和中断。所以，可以根据任务的不同，把 CPU 上下文切换分成：**进程上下文切换、线程上下文切换和中断上下文切换**。\n\n**进程的上下文切换**：当时间片耗尽、当前进程主动阻塞、优先级更高进程到达、当前进程终止时，会发生进程上下文的切换。进程上下文不仅包含了虚拟内存、栈、全局变量等**用户空间**的资源，还包括了内核堆栈、寄存器等**内核空间**的资源。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/e9cf6f54-a149-4c16-9f2c-979ca8e77b51.png)\n\n# 4 进程的通信\n\n每个进程的用户地址空间都是独立的，一般而言是不能互相访问的，但内核空间是每个进程都共享的，所以进程之间要通信必须通过内核。\n\n![img](https://cdn.xiaolincoding.com/gh/xiaolincoder/ImageHost/%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/%E8%BF%9B%E7%A8%8B%E9%97%B4%E9%80%9A%E4%BF%A1/4-%E8%BF%9B%E7%A8%8B%E7%A9%BA%E9%97%B4.jpg)\n\n## 1 管道\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/78ebd993-f4a0-4044-8fbc-5832753019e3.png)\n\n比如在linux系统中可以通过以下方式创建一个管道：`ps auxf`的输出将作为`grep mysql`的输入。也可以通过`mkfifo`创建一个命名管道。管道只能传输字节流。\n\n```bash\n$ ps auxf | grep mysql\n\n$ mkfifo myPipe\n$ echo \"hello\" > myPipe\n$ cat < myPipe\n```\n\n## 2 消息队列\n\n管道的通信方式效率比较低，不适合进程之间频繁地交换数据。\n\n**消息队列是保存在内核中的消息链表**，在发送数据时，会分成一个一个独立的数据单元，也就是消息体（数据块），消息体是用户自定义的数据类型，消息的发送方和接收方要约定好消息体的数据类型，所以每个消息体都是固定大小的存储块，不像管道是无格式的字节流数据。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/07f8c521-9172-4054-9653-72aa241da976.png)\n\n- 不适合比较大数据的传输\n- 消息队列通信过程中，存在用户态和内核态的拷贝开销\n\n## 3 共享内存\n\n共享内存的机制，就是拿出一块虚拟地址空间来，映射到相同的物理内存中。但需要采取保护机制，使得共享的资源，在任一时刻只能被一个进程访问。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/75ed8e39-6a24-4726-9711-d3576fc0e5c9.png)\n\n信号量提供了这一保护机制，信号量其实是一个整型的计数器，主要用于实现进程间的互斥与同步，PV操作需要同时出现。\n\n- 一个是 **P 操作**，这个操作会把信号量减去 1，**相减后**如果信号量 < 0，则表明资源已被占用，进程需阻塞等待；相减后如果信号量 >= 0，则表明还有资源可使用，进程可正常继续执行。\n- 另一个是 **V 操作**，这个操作会把信号量加上 1，**相加后**如果信号量 <= 0，则表明当前有阻塞中的进程，于是会将该进程唤醒运行；相加后如果信号量 > 0，则表明当前没有阻塞中的进程。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/05373a73-08f5-4606-8e78-a7eba608831d.png)\n\n**同步信号量**：在多进程里，每个进程并不一定是顺序执行的，它们基本是以各自独立的、不可预知的速度向前推进。比如进程 A 负责生产数据，进程 B 是负责读取数据，这两个进程是相互合作、相互依赖，进程 A 必须先生产了数据，进程 B 才能读取到数据，所以执行是有前后顺序的。\n\n## 4 Socket\n\n跨网络进程通信，创建 socket 的系统调用(指定协议族与通信特性)：\n\n```c\nint socket(int domain, int type, int protocal)\n```\n\n- domain：指定协议族，比如 AF_INET 用于 IPV4、AF_INET6 用于 IPV6、AF_LOCAL/AF_UNIX 用于本机；\n- type ：指定通信特性，比如 SOCK_STREAM 表示的是字节流，对应 TCP；SOCK_DGRAM 表示的是数据报，对应 UDP；SOCK_RAW 表示的是原始套接字。\n- protocal ：原本用来指定通信协议的，现在基本废弃。因为协议已经通过前面两个参数指定完成，protocol 目前一般写成 0 即可。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/c567fb40-5d85-414f-8411-5affcbde6d37.png)\n\nTCP：监听的 socket 和真正用来传送数据的 socket，是「**两个**」 socket，一个叫作**监听 socket**，一个叫作**已完成连接 socket**。\n\n# 5 线程的概念\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/446e7f68-832b-4891-b829-8a62d51f5c62.png)\n\n有的进程可能会同时做很多事。\n\n- [x] Choose 1：在同一个进程中直接串行运行不同功能，显然不行\n- [x] Choose2：将不同功能拆分为不同进程，但这种方式维护进程的系统开销较大(如创建进程时，分配资源、建立 PCB；终止进程时，回收资源、撤销 PCB；进程切换时，保存当前进程的状态信息)\n- [ ] 线程：并发运行且共享相同的地址空间。\n\n## 1 什么是线程\n\n**线程是进程当中的一条执行流程，是程序执行流的最小单位**。一个进程中可以同时存在多个线程，各个线程之间可以并发执行，各个线程之间可以共享地址空间和文件等资源。\n\n同一个进程内多个线程之间可以共享代码段、数据段、打开的文件等资源，但每个线程各自都有一套独立的寄存器和栈，这样可以确保线程的控制流是相对独立的。\n\n<img src=\"https://cdn.xiaolincoding.com/gh/xiaolincoder/ImageHost/%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/%E8%BF%9B%E7%A8%8B%E5%92%8C%E7%BA%BF%E7%A8%8B/16-%E5%A4%9A%E7%BA%BF%E7%A8%8B%E5%86%85%E5%AD%98%E7%BB%93%E6%9E%84.jpg\" alt=\"多线程\" style=\"zoom: 50%;\" />\n\n## 2 线程的实现方式\n\n- **用户级线程**：由应用程序通过线程库实现，所有的线程管理工作都由应用程序负责（包括线程切换），**无需操作系统干预**，在用户看来有多个线程，但操作系统并不能意识到线程的存在。缺点：当一个用户级线程被阻塞后，整个进程都会被阻塞，并发度不高，多个线程不可以在多核处理机上并行运行。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/b0220650-fa53-4a8c-a33a-a3cf7295c44e.png)\n\n- **内核级线程**：一个线程被阻塞后，别的线程还可以继续执行，并发能力强，多线程可以在多核处理机上并行执行。缺点：一个用户进程会占用多个内核级线程，线程切换由操作系统完成，需要切换到内核态，线程管理的成本高，开销大。\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/a544e562-3dd0-46d8-a3e9-dd153ffc4eb9.png)\n\n- **多线程模型**：\n  - 一对一模型：一个用户级线程映射到一个内核级线程，有点像上述的内核级线程\n  - 多对一模型：多个用户级线程映射到一个内核级线程，像上述的应用级线程\n  - 多对多模型：折中，克服了多对一模型并发度不高的缺点 和 一对一模型占用太多内核级线程，开销太大的缺点\n\n![image.png](https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/2ffc367c-da08-4767-9fd3-eb9cf7682c54.png)\n\n## 3进程与线程的比较\n\n### 1 进程\n\n操作系统进行资源分配的单位(比如打印机、内存空间等)，是一个独立运行的程序实体。拥有包括PCB、代码段、数据段等资源，进程之间的资源是相互隔离的，进程间通信需要通过**操作系统提供的特定机制**进行(管道、消息队列、共享内存等)，进程间的切换与调度涉及到代码段和程序段的切换，这个过程开销较大。\n\n### 2 线程\n\n操作系统调度执行的最小单位，是进程内的一个执行流。一个进程可以拥有多个线程，这些线程共享进程的代码段、数据段、打开的文件等资源，但每个线程各自都有一套**独立的寄存器和栈**，这样可以确保线程的控制流是相对独立的。\n\n由于线程共享相同的资源，**线程间通信相对简单**，可以直接通过**共享变量、锁**等方式进行。线程相较于进程，上下文切换和调度开销较小。但多个线程并发执行时，需要处理好同步和互斥问题，以避免数据不一致或竞争条件。\n\n# 6 线程的状态与转换\n\nTODO\n\n',1688873487067418626,'线程是进程当中的一条执行流程，是程序执行流的最小单位。一个进程中可以同时存在多个线程，各个线程之间可以并发执行，各个线程之间可以共享地址空间和文件等资源。','https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/4555b395-0748-4edf-886f-903625442b1e.png',1703365214623535105,0,0,'2023-09-17 19:44:22','2023-09-17 19:44:22'),(1710296011876118529,'项目部署流程','- IDEA中 Java maven install打个jar包，丢到 `/root/code/chatviewer`文件夹下，执行\r   `nohup java -jar blog-0.0.1-SNAPSHOT.jar > ./log/start.log 2>&1` & 命令启动\r - WebStorm中 `npm run build-only`，项目将生成在dist文件夹下\r   将文件夹中内容搬到 `/home/nginx/html`文件夹\r - 修改nginx的conf配置： `vim /home/nginx/conf/nginx.conf`\r \r ```\r user  root;\r worker_processes  auto;\r \r error_log  /var/log/nginx/error.log notice;\r pid        /var/run/nginx.pid;\r \r \r events {\r     worker_connections  1024;\r }\r \r \r http {\r     include       /etc/nginx/mime.types;\r     default_type  application/octet-stream;\r \r     log_format  main  \'$remote_addr - $remote_user [$time_local] \"$request\" \'\r                       \'$status $body_bytes_sent \"$http_referer\" \'\r                       \'\"$http_user_agent\" \"$http_x_forwarded_for\"\';\r \r     access_log  /var/log/nginx/access.log  main;\r \r     sendfile        on;\r     #tcp_nopush     on;\r \r     keepalive_timeout  65;\r \r     #gzip  on;\r \r     #include /etc/nginx/conf.d/*.conf;\r \r     server {\r  	listen 80;\r      	server_name 39.105.199.147;\r      	location / {\r 		root   /usr/share/nginx/html;\r      		index  index.html index.htm;\r 		try_files $uri $uri/ /index.html;\r      	}\r 	\r 	location /api/ {\r 		proxy_pass http://39.105.199.147:8080/;\r 	}	\r     }\r }\r ```\r \r - 查看nginx错误日志：`/home/nginx/log/error.log`，删除当前运行的nginx镜像 `docker rm -f nginx`\r - 启动新的nginx镜像：\r \r ```bash\r docker run -p 1022:80 --name nginx -v /home/nginx/conf/nginx.conf:/etc/nginx/nginx.conf -v /home/nginx/conf/conf.d:/etc/nginx/conf.d -v /home/nginx/log:/var/log/nginx -v /home/nginx/html:/usr/share/nginx/html -d nginx:latest\r ```\r \r - 访问 http://<IP>:1022/ 即可。\r魔戒重置连接，导入windows for clash，订阅连接：魔戒“使用其他客户端进行订阅”；secret-key：General->Open Folder->config.yaml中的secret-key。   \r \r ',1688874327190704130,'mark一下部署流程','https://blog-picture-upload-bucket.oss-cn-beijing.aliyuncs.com/article_picture/ava.jpeg',1703380258652459010,0,0,'2023-10-06 22:08:40','2023-10-06 22:08:40');
/*!40000 ALTER TABLE `blog_article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_category`
--

DROP TABLE IF EXISTS `blog_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_category` (
  `category_id` bigint(64) NOT NULL,
  `category_name` varchar(45) DEFAULT NULL,
  `parent_id` bigint(64) DEFAULT NULL,
  `left_index` int(11) DEFAULT NULL,
  `right_index` int(11) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_category`
--

LOCK TABLES `blog_category` WRITE;
/*!40000 ALTER TABLE `blog_category` DISABLE KEYS */;
INSERT INTO `blog_category` VALUES (0,'根分类',-1,1,54),(1688872497937289217,'算法',0,2,19),(1688872498683875329,'树',1688872497937289217,3,4),(1688872592334295041,'链表',1688872497937289217,5,6),(1688872622424231938,'栈和队列',1688872497937289217,7,8),(1688872651511730177,'字符串',1688872497937289217,9,10),(1688872678049091586,'数组',1688872497937289217,11,12),(1688872705106546690,'动态规划',1688872497937289217,13,14),(1688872746038759425,'回溯',1688872497937289217,15,16),(1688872773482090497,'其他',1688872497937289217,17,18),(1688872842130264065,'数据库',0,20,27),(1688872842818129922,'MySQL',1688872842130264065,21,22),(1688872887932063746,'Redis',1688872842130264065,23,24),(1688872936032342018,'ElasticSearch',1688872842130264065,25,26),(1688873233597239298,'Java',0,28,35),(1688873234268327938,'Java基础',1688873233597239298,29,30),(1688873262043009025,'多线程',1688873233597239298,31,32),(1688873283979218945,'设计模式',1688873233597239298,33,34),(1688873487067418626,'操作系统',0,36,37),(1688873517996216322,'计算机网络',0,38,45),(1688873707801055233,'中间件',0,46,49),(1688873708618944514,'kafka',1688873707801055233,47,48),(1688874234358173697,'Spring框架',0,50,51),(1688874327190704130,'本站开发',0,52,53),(1706976554962857985,'传输层',1688873517996216322,39,44),(1706976555759775745,'TCP协议',1706976554962857985,40,41),(1706977163560517634,'UDP协议',1706976554962857985,42,43);
/*!40000 ALTER TABLE `blog_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_chatgpt_message`
--

DROP TABLE IF EXISTS `blog_chatgpt_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_chatgpt_message` (
  `message_id` bigint(64) NOT NULL,
  `message_direction` int(11) DEFAULT NULL,
  `conversation_id` bigint(64) DEFAULT NULL,
  `content` varchar(1600) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_chatgpt_message`
--

LOCK TABLES `blog_chatgpt_message` WRITE;
/*!40000 ALTER TABLE `blog_chatgpt_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_chatgpt_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_comment`
--

DROP TABLE IF EXISTS `blog_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_comment` (
  `comment_id` bigint(64) NOT NULL,
  `comment_content` varchar(300) DEFAULT NULL,
  `comment_type` int(11) DEFAULT NULL,
  `article_id` bigint(64) DEFAULT NULL,
  `parent_id` bigint(64) DEFAULT NULL,
  `target_id` bigint(64) DEFAULT NULL,
  `user_id` bigint(64) DEFAULT NULL,
  `like_counts` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_comment`
--

LOCK TABLES `blog_comment` WRITE;
/*!40000 ALTER TABLE `blog_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_conversation`
--

DROP TABLE IF EXISTS `blog_conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_conversation` (
  `conversation_id` bigint(64) NOT NULL,
  `user_id` bigint(64) DEFAULT NULL,
  `conversation_type` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_conversation`
--

LOCK TABLES `blog_conversation` WRITE;
/*!40000 ALTER TABLE `blog_conversation` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_conversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_like`
--

DROP TABLE IF EXISTS `blog_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_like` (
  `like_id` bigint(64) NOT NULL,
  `user_id` bigint(64) DEFAULT NULL,
  `entity_type` int(11) DEFAULT NULL,
  `entity_id` bigint(64) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`like_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_like`
--

LOCK TABLES `blog_like` WRITE;
/*!40000 ALTER TABLE `blog_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_message`
--

DROP TABLE IF EXISTS `blog_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_message` (
  `message_id` bigint(64) NOT NULL,
  `from_id` bigint(64) DEFAULT NULL,
  `to_id` bigint(64) DEFAULT NULL,
  `content` varchar(256) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `idx_from_id` (`from_id`),
  KEY `idx_to_id` (`to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_message`
--

LOCK TABLES `blog_message` WRITE;
/*!40000 ALTER TABLE `blog_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_notice`
--

DROP TABLE IF EXISTS `blog_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_notice` (
  `notice_id` bigint(64) NOT NULL,
  `receiver_id` bigint(64) DEFAULT NULL,
  `sender_id` bigint(64) DEFAULT NULL,
  `notice_type` int(11) DEFAULT NULL,
  `entity_type` int(11) DEFAULT NULL,
  `entity_id` bigint(64) DEFAULT NULL,
  `article_id` bigint(64) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `notice_content` varchar(256) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_notice`
--

LOCK TABLES `blog_notice` WRITE;
/*!40000 ALTER TABLE `blog_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_problem`
--

DROP TABLE IF EXISTS `blog_problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_problem` (
  `problem_id` bigint(20) NOT NULL,
  `problem_title` varchar(512) DEFAULT NULL,
  `problem_answer` varchar(1024) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_problem`
--

LOCK TABLES `blog_problem` WRITE;
/*!40000 ALTER TABLE `blog_problem` DISABLE KEYS */;
INSERT INTO `blog_problem` VALUES (1703378122199830529,'TCP为什么需要三次握手建立连接？','略',1706976555759775745,'2023-09-17 20:03:03'),(1703382421101383682,'TCP为什么需要四次挥手断开连接？','略',1706976555759775745,'2023-09-17 20:03:03'),(1709919563354382338,'进程和线程的区别是?','略',1688873487067418626,'2023-10-05 21:12:47'),(1710288946706685954,'什么是缓存穿透、缓存击穿、缓存雪崩？','略',1688872887932063746,'2023-10-06 21:40:35');
/*!40000 ALTER TABLE `blog_problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_user`
--

DROP TABLE IF EXISTS `blog_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_user` (
  `user_id` bigint(64) NOT NULL,
  `user_phone` varchar(11) DEFAULT NULL,
  `user_password` varchar(256) DEFAULT NULL,
  `user_name` varchar(60) DEFAULT NULL,
  `user_avatar` varchar(256) DEFAULT NULL,
  `user_token_count` int(11) DEFAULT NULL,
  `user_api_key` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_user`
--

LOCK TABLES `blog_user` WRITE;
/*!40000 ALTER TABLE `blog_user` DISABLE KEYS */;
INSERT INTO `blog_user` VALUES (1703365214623535105,'13856729411','$2a$10$3GfXBU2.Lmdhoqn63OC4POVZdKnlNvVivezoSYs3Rw.XXl6foLGrC','小扣','https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',9989,'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'),(1703380258652459010,'13515699807','$2a$10$f8zhjk5RD5TvkKNFD5KiMuRrHrZh/GOrYjFOwThKPgEgCbN41R1Le','虎皮蛋糕卷','https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',9,NULL);
/*!40000 ALTER TABLE `blog_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-10-07  3:51:09
