基于大模型的音乐推荐系统

技术：SpringBoot+Vue+Deepseek+MySQL

版本：jdk8+Maven3.6+Vue2.9+SpringBoot2

在别人的项目上二次开发：https://github.com/Yin-Hongwei

![image-20250531194423972](https://18334034784.oss-cn-chengdu.aliyuncs.com/lin-oss/image-20250531194423972.png)

music-client：前端项目，音乐平台前端

music-manage：前端项目，音乐平台后台管理

music-server：后端项目

下载项目后需要：

1.   后端的application.properties文件中修改数据库中的数据源

2.   后端的application.properties文件中修改deepseek的APIkey

3.   DeepSeekController中修改deepseek的APIkey（本人的余额还有一些，可以直接拿去用）

4.   将项目的setting.xml修改成自己的

5.   进入到music-client目录下打开cmd，输入`npm install`安装项目，之后在vscode中输入`npm run dev`启动项目，music-manage项目同理

     

效果图如下：

系统首页：

![image-20250531193420288](https://18334034784.oss-cn-chengdu.aliyuncs.com/lin-oss/image-20250531193420288.png)



歌单信息：

![image-20250531193539365](https://18334034784.oss-cn-chengdu.aliyuncs.com/lin-oss/image-20250531193539365.png)

我的音乐：

![](https://18334034784.oss-cn-chengdu.aliyuncs.com/lin-oss/image-20250531193539365.png)

播放音乐：

![image-20250531193620036](https://18334034784.oss-cn-chengdu.aliyuncs.com/lin-oss/image-20250531193620036.png)

歌曲推荐：

![image-20250531193638195](https://18334034784.oss-cn-chengdu.aliyuncs.com/lin-oss/image-20250531193638195.png)

系统管理：

![image-20250531193711371](https://18334034784.oss-cn-chengdu.aliyuncs.com/lin-oss/image-20250531193711371.png)

给我点个star后可以联系我哟！

QQ：2201716544（备注github-music）
