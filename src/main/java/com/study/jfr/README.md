# JFR
## 摘要
来自：
https://juejin.cn/post/6959405798556434440

得到JFR的文件有三种方式：

- 使用JMC可视化的界面

- 使用Java自带的jcmd命令，采用sidecar的方式开启：
  ```  
  使用jcmd命令行解锁JFR功能权限。
    jcmd process_id VM.unlock_commercial_features 解锁JFR记录功能权限
  
  如：
  使用jcmd命令行开启一个记录线程，duration记录的时间段，默认为0s，代表无限制，以下代码使用200s，表示记录200s结束。filename表示保存的文件名。
  jcmd激活 :
    jcmd process_id JFR.start duration=100s filename=flight.jfr （JDK 11版本前需要先激活对应的功能）
  ```
  
- 使用Java的启动参数：
  ```
  java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder
  -XX:StartFlightRecording=duration=200s,filename=flight.jfr
  ```
  
## 介绍第二种
  假如我们的java进程ID是`14161`。JFR关联的jcmd命令有以下四种：

- JFR.start——启动一个新的JFR记录线程
- JFR.check——检查正在运行的JFR记录线程
- JFR.stop——停止一个指定的JFR记录线程
- JFR.dump——拷贝一个指定的JFR记录线程的内容进入文件中

例如，查看一个JFR.check的命令行使用方式，
  ```
  jcmd 14161 help JFR.check
  ```

### JFR.start

| 参数         | 说明         | 值类型         | 默尔值 
| :---        | :---        |  :---        | :---
| name       | 记录线程的名称       | String   | 无
| settings   | 服务端模版        | String      | 无
|defaultrecording|	开始默认记录	|Boolean	|False
|delay	|开始记录的延迟时间	|Time	|0s
|duration	|记录的时长	|Time	|0s(表示永远，不中断)
|filename	|记录的名称	|String	| 用户指定
|compress	|使用GZip压缩记录的结果文件	|Boolean	|False
|maxage	| 缓冲区数据的最长使用期限	| Time	| 0s代表没有时间期限制
|maxsize |	缓存容量的最大数量	 | Long	| 0代表没有最大大小


### JFR.check
| 参数         | 说明         | 值类型         | 默尔值
| :---        | :---        |  :---        | :---
| name       | 记录线程的名称       | String   | 无
| recording	 | 记录线程的ID值	| Long	| 1
| verbose	| 是否打印详细数据信息	| Boolean	| False


### JFR.stop
| 参数         | 说明         | 值类型         | 默尔值
| :---        | :---        |  :---        | :---
| name       | 记录线程的名称       | String   | 无
| recording	 | 记录线程的ID值	| Long	| 1
| discard	| 抛弃记录数据	| Boolean	| 无
| copy_to_file	| 拷贝记录数据到文件	| String	| 无
| compress_copy	| GZip压缩“copy_to_file”的文件	| Boolean	| False


### JFR.dump
| 参数         | 说明         | 值类型         | 默尔值
| :---        | :---        |  :---        | :---
| name       | 记录线程的名称       | String   | 无
| recording	 | 记录线程的ID值	| Long	| 1
| copy_to_file	| 拷贝记录数据到文件	| String	| 无
| compress_copy	| GZip压缩“copy_to_file”的文件	| Boolean	| False


## 如何生成JFR文件
可以参考 jfr print xxx.jfr 的值。

使用 jps 查找 当前可用的线程
```shell
// 开启 jfr
jcmd 24598 JFR.start 

// 导出数据
jcmd 24598 JFR.dump name=2 filename=myflight.jfr

// 多执行几次
jcmd 24598 JFR.dump name=2 filename=myflight.jfr

jcmd 24598 JFR.dump name=2 filename=myflight.jfr

// 结束
jcmd 24598 JFR.stop
```


