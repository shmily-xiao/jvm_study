# JFR 配置
来自：https://docs.oracle.com/javacomponents/jmc-5-5/jfr-runtime-guide/run.htm#JFRRT175

You can configure an explicit recording in a number of other ways. These techniques work the same regardless of how you start a recording (that is, either by using the command-line approach or by using diagnostic commands).
(您可以通过多种其他方式配置显式录制。 无论您如何开始记录（即，通过使用命令行方法或使用诊断命令），这些技术的工作方式都相同。)

## Setting Maximum Size and Age
You can configure an explicit recording to have a maximum size or age by using the following parameters:
（你可以通过下面的参数 配置文件大小，也可以配置文件存活的时间:）

```maxsize=size```

Append the letter k or K to indicate kilobytes, m or M to indicate megabytes, g or G to indicate gigabytes, or do not specify any suffix to set the size in bytes.
(附加字母 k 或 K 表示千字节，m 或 M 表示兆字节，g 或 G 表示千兆字节，或者不指定任何后缀以设置字节大小。)

```maxage=age```

Append the letter s to indicate seconds, m to indicate minutes, h to indicate hours, or d to indicate days.
(附加字母 s 表示秒，m 表示分钟，h 表示小时，或 d 表示天)

If both a size limit and an age are specified, the data is deleted when either limit is reached.
(如果同时指定了大小限制和年龄，则在达到任一限制时删除数据。)。

## delay

When scheduling a recording. you might want to add a delay before the recording is actually started; for example, when running from the command line, you might want the application to boot or reach a steady state before starting the recording. To achieve this, use the delay parameter:
（安排录制时。 您可能希望在实际开始录制之前添加延迟； 例如，从命令行运行时，您可能希望应用程序在开始录制之前启动或达到稳定状态。 为此，请使用延迟参数）

```delay=delay```

Append the letter s to indicate seconds, m to indicate minutes, h to indicate hours, or d to indicate days.

## Compression

Although the recording file format is very compact, you can compress it further by adding it to a ZIP archive. To enable compression, use the following parameter:
(支持压缩)

```compress=true```

Note that CPU resources are required for the compression, which can negatively impact performance.
（请注意，压缩需要 CPU 资源，这会对性能产生负面影响。）

## 自动记录

When running with a default recording you can configure Java Flight Recorder to automatically save the current in-memory recording data to a file whenever certain conditions occur. If a disk repository is used, the current information in the disk repository will also be included.
（当使用默认记录运行时，您可以配置 Java Flight Recorder 以在特定条件发生时自动将当前内存中的记录数据保存到文件中。 如果使用磁盘存储库，则还将包括磁盘存储库中的当前信息。）

### Creating a Recording On Exit

To save the recording data to the specified path every time the JVM exits, start your application with the following option:
(要在每次 JVM 退出时将记录数据保存到指定路径，请使用以下选项启动您的应用程序：)

```aidl
-XX:FlightRecorderOptions=defaultrecording=true,dumponexit=true,dumponexitpath=path
```

Set path to the location where the recording should be saved. If you specify a directory, a file with the date and time as the name is created in that directory. If you specify a file name, that name is used. If you do not specify a path, the recording will be saved in the current directory.
You can also specify dumponexit=true as a parameter to the -XX:StartFlightRecording option:
(将路径设置为应保存JFR的位置。 如果指定目录，则会在该目录中创建以日期和时间为名称的文件。 如果指定文件名，则使用该名称。 如果不指定路径，JFR文件将保存在当前目录中。

您还可以指定 dumponexit=true 作为 -XX:StartFlightRecording 选项的参数：)

```aidl
-XX:StartFlightRecording=name=test,filename=D:\test.jfr,dumponexit=true
```

In this case, the dump file will be written to the location defined by the filename parameter.
（在这种情况下，转储文件将写入文件名参数定义的位置。）

### 使用jmc工具
您可以使用 Java Mission Control 中的控制台来设置触发器。 触发器是在规则指定的条件为真时执行操作的规则。 例如，您可以创建一个规则，当堆大小超过 100 MB 时触发飞行记录开始。 Java Mission Control 中的触发器可以使用通过 JMX MBean 公开的任何属性作为规则的输入。 除了 Flight Recorder 转储之外，它们还可以启动许多其他操作。

在 JMX 控制台的触发器选项卡上定义触发器。 有关如何创建触发器的更多信息，请参阅 Java Mission Control 帮助。