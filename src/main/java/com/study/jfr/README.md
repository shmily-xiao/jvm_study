# JFR
得到JFR的文件有三种方式：

- 使用JMC可视化的界面

- 使用Java自带的jcmd命令，采用sidecar的方式开启：
  ````
  使用jcmd命令行解锁JFR功能权限。
    jcmd process_id VM.unlock_commercial_features 解锁JFR记录功能权限
  
  如：
  使用jcmd命令行开启一个记录线程，duration记录的时间段，默认为0s，代表无限制，以下代码使用200s，表示记录200s结束。filename表示保存的文件名。
  jcmd激活 :
    jcmd process_id JFR.start duration=100s filename=flight.jfr （JDK 11版本前需要先激活对应的功能）

  
- 使用Java的启动参数：
  ````
  java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder
  -XX:StartFlightRecording=duration=200s,filename=flight.jfr
