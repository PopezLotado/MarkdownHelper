# MarkdownHelper
###Auto-upload image from clipboard or file and return url to clipboard.

## 说明 ##
**一个方便markdown上传图片的小工具，使用maven管理项目，图床采用七牛云存储，具体AK与SK可在config.properties中配置**

**此外config.properties中还需要配置你七牛云的存储空间名和访问的域名**

**配置完成后执行 mvn package 即可在target下打出一个zip包,解压后目录如下**


![](http://heartbeats.qiniudn.com/dc77bf84b9294398a542db0be3bbf52f.png)


## 使用 ##

**在windows下可配合bat与快捷方式的快捷键来使用**

- 在jar所在目录创建两个bat文件，分别写

	`java -jar MarkdownHelper-0.0.1-SNAPSHOT.jar -c ` 

	`java -jar MarkdownHelper-0.0.1-SNAPSHOT.jar -f `

	**-c代表从剪贴板获取图片,-f代表从文件获取图片**

-	然后创建2个bat的快捷方式到桌面,分别右键->属性 设置各自快捷键，如：

	 ![](http://heartbeats.qiniudn.com/d06a2388610a490fad0cb0dd1300b899.png)

	**并设置最小化运行**

-	然后再编辑md的时候就可以配合QQ截图或者系统截图来使用,触发bat后等待一会，即可按ctrl+v粘贴url到md中,方便快捷。


## 补充 ##

**也可自行修改代码使其适应其他图床**
