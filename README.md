# Excel文件导入/导出工具
依据定义的Excel模板，结合程序生成相应的数据对象(POJO)，实现数据的导出为Excel文件；
反之，将按Excel模板填写的文件，通过程序生成相应的数据对象(POJO)。
 
# 一、使用步骤
## 1. 定义Excel导入/导出模板
### 1.1. 定义Excel导入/导出模板
        实例参见：tcom-poi\poi-example\data\Template\tbl_tpl.xlsx
》模板定义参见：[Excel模板定义说明](README.md#二excel模板定义说明)
### 1.2. 生成Excel导入/导出数据定义的配置文件
        通过Excel VBA读取模板定义，生成导入/导出数据定义的配置文件
        导入/导出VBA工具：tcom-poi\poi-example\data\Template\TplDefine2Xml.xlsm
        工具生成的配置文件实例：tcom-poi\poi-example\data\Template\tbl_tpl.xml

## 2. 开发Excel导入/导出程序
### 2.1. 创建Java工程（基于Spring-boot）
        实例参见：tcom-poi\poi-example
### 2.2. 放置Excel模板及配置文件
        将以上生成的Excel导入/导出模板文件，以及数据定义的配置文件放置到相应的目录；
        实例参见：tcom-poi\poi-example\src\test\resources\poi\template\*.*
### 2.3. 设置Spring配置文件及参数
        实例参见：tcom-poi\poi-example\src\test\resources\poi\spring-poi.xml
                  tcom-poi\poi-example\src\test\resources\poi\poi.properties
### 2.4. 定义数据Bean（POJO）
        实例参见Package：net.aicoder.exsys.module.entity.*
### 2.5. 开发数据导入/导出的服务
        实例参见Package: net.aicoder.exsys.module.submodule.service.*.*
### 2.6. 实例化数据导入/导出的操作对象
        实例参见Package: net.aicoder.exsys.module.submodule.dataoper.*
### 2.7. 封装Excel导入/导出的入口方法
        实例参见Package: net.aicoder.exsys.module.submodule.controller.*
### 2.8. 测试导入/导出功能(文件存放于测试目录)
        实例参见Package: net.aicoder.exsys.module.submodule.*

----------
# 二、Excel模板定义说明
## 1. Excel模板的构成
![](poi-doc/images/Excel_Tpl.jpg)

        说明：Excel模板由2大部分构成，即：
1、“模板控制区”，定义模板的控制信息，该控制区域为1~15行，**在模板定义时必须按控制区域的格式进行维护，并且该区域的行和列不能增减**。控制参数的设定从B2单元格开始，具体含义如下：
- Sheet定义

|单元格|示例  |    参数说明      |
| ---- | ---- | ---------------- |
|	B2	|	_TBL_	|	Sheet定义之ID，多Sheet之间不能重复	|
|	B3	|	区域填充类型	|	数据区定义的参数名称(仅提示用)	|
|	B4	|	起始单元格	|	数据区定义的参数名称(仅提示用)	|
|	B5	|	结束单元格	|	数据区定义的参数名称(仅提示用)	|
|	B6	|	数据非空检查行/列	|	数据区定义的参数名称(仅提示用)	|
|	B7	|	标题起始行/列(相对起始位)	|	数据区定义的参数名称(仅提示用)	|
|	B8	|	标题行数/列数	|	数据区定义的参数名称(仅提示用)	|
|	B9	|	模板行/列(相对起始位)	|	数据区定义的参数名称(仅提示用)	|
|	B10	|	模板行数/列	|	数据区定义的参数名称(仅提示用)	|
|	B11	|	数据起始行/列(相对起始位)	|	数据区定义的参数名称(仅提示用)	|
|	B12	|	数据结束行/列(相对结束位)	|	数据区定义的参数名称(仅提示用)	|
|	B13	|	\$\[entity\]	|	Sheet数据取值的变量名	|
|	B14	|	\$\[entityList:code\]	|	Sheet名称定义，可对应变量名也可为字符串常量	|

