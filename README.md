# Excel文件导入/导出工具
依据定义的Excel模板，结合程序生成相应的数据对象(POJO)，实现数据的导出为Excel文件；
反之，将按Excel模板填写的文件，通过程序生成相应的数据对象(POJO)。

# 一、使用步骤
##1. 定义Excel导入/导出模板
###1.1. 定义Excel导入/导出模板
        实例参见：tcom-poi\poi-example\data\Template\tbl_tpl.xlsx
        模板定义参见：二、Excel模板定义说明
###1.2. 生成Excel导入/导出数据定义的配置文件
        通过Excel VBA读取模板定义，生成导入/导出数据定义的配置文件
        导入/导出VBA工具：tcom-poi\poi-example\data\Template\TplDefine2Xml.xlsm
        工具生成的配置文件实例：tcom-poi\poi-example\data\Template\tbl_tpl.xml

## 2. 开发Excel导入/导出程序
###2.1. 创建Java工程（基于Spring-boot）
        实例参见：tcom-poi\poi-example
###2.2. 放置Excel模板及配置文件
        将以上生成的Excel导入/导出模板文件，以及数据定义的配置文件放置到相应的目录；
        实例参见：tcom-poi\poi-example\src\test\resources\poi\template\*.*
###2.3. 设置Spring配置文件及参数
        实例参见：tcom-poi\poi-example\src\test\resources\poi\spring-poi.xml
                  tcom-poi\poi-example\src\test\resources\poi\poi.properties
###2.4. 定义数据Bean（POJO）
        实例参见Package：net.aicoder.exsys.module.entity.*
###2.5. 开发数据导入/导出的服务
        实例参见Package: net.aicoder.exsys.module.submodule.service.*.*
###2.6. 实例化数据导入/导出的操作对象
        实例参见Package: net.aicoder.exsys.module.submodule.dataoper.*
###2.7. 封装Excel导入/导出的入口方法
        实例参见Package: net.aicoder.exsys.module.submodule.controller.*
###2.8. 测试导入/导出功能(文件存放于测试目录)
        实例参见Package: net.aicoder.exsys.module.submodule.*

----------
# 二、Excel模板定义说明
##1. Excel模板的构成
![](~/Excel_Tpl.jpg)
说明：
Excel模板由2大部分构成，即：
1、“模板控制区”，模板定义的控制信息，包括以下控制信息：

