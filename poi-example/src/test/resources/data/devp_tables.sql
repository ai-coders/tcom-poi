--  Table DDL: [t_dev_system] 系统定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_system` CASCADE;

CREATE TABLE `t_dev_system` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '系统代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '系统名称'
,`abbr_code` VARCHAR(10)  COMMENT '系统代号简称'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
,`m_name` VARCHAR(50)  COMMENT '修改用户名'
,`m_time` DATETIME COMMENT '修改时间'
) ENGINE=InnoDB COMMENT='系统定义';

ALTER TABLE `t_dev_system` ADD CONSTRAINT `PK_t_dev_system` PRIMARY KEY 
(`id`);
ALTER TABLE `t_dev_system` ADD UNIQUE `UQ_t_dev_system_code` 
(`code`);

SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_module] 模块定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_module` CASCADE;

CREATE TABLE `t_dev_module` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`sys_code` VARCHAR(50)  NOT NULL  COMMENT '系统代号'
,`full_code` VARCHAR(255)  NOT NULL  COMMENT '模块全代号-含父级别模块代号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '模块代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '模块名称'
,`seq` INTEGER(0)  COMMENT '序号'
,`parent_id` VARCHAR(50)  COMMENT '父模块记录号'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
,`m_name` VARCHAR(50)  COMMENT '修改用户名'
,`m_time` DATETIME COMMENT '修改时间'
) ENGINE=InnoDB COMMENT='模块定义';

ALTER TABLE `t_dev_module` ADD CONSTRAINT `PK_t_dev_module` PRIMARY KEY 
(`id`);
ALTER TABLE `t_dev_module` ADD UNIQUE `UQ_t_dev_modue_full_code` 
(`sys_code`,`full_code`);

ALTER TABLE `t_dev_module` ADD FOREIGN KEY `FK_t_dev_module_system`  
  (`sys_code`) 
 REFERENCES `t_dev_system` (`code`);
ALTER TABLE `t_dev_module` ADD FOREIGN KEY `FK_t_dev_module_module`  
  (`parent_id`) 
 REFERENCES `t_dev_module` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_entity] 实体定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_entity` CASCADE;

CREATE TABLE `t_dev_entity` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '实体代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '实体名称'
,`sys_code` VARCHAR(50)  NOT NULL  COMMENT '系统代号'
,`mod_code` VARCHAR(255)  COMMENT '模块代号-含父级别模块代号'
,`seq` INTEGER(0)  COMMENT '序号'
,`dbms` VARCHAR(50)  COMMENT '数据库类型'
,`owner` VARCHAR(50)  COMMENT '数据库用户'
,`status` VARCHAR(50)  COMMENT '开发状态-00_Draft,20_Official,60_Dev,80_GoLive'
,`phase` VARCHAR(50)  COMMENT '开发阶段'
,`dev_version` VARCHAR(50)  COMMENT '版本号'
,`mod_id` VARCHAR(50)  COMMENT '模块记录号'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
,`m_name` VARCHAR(50)  COMMENT '修改用户名'
,`m_time` DATETIME COMMENT '修改时间'
) ENGINE=InnoDB COMMENT='实体定义';

ALTER TABLE `t_dev_entity` ADD CONSTRAINT `PK_t_dev_entity` PRIMARY KEY 
(`id`);
ALTER TABLE `t_dev_entity` ADD UNIQUE `UQ_t_dev_entity_code` 
(`code`,`sys_code`);

ALTER TABLE `t_dev_entity` ADD FOREIGN KEY `FK_t_dev_entity_module`  
  (`mod_id`) 
 REFERENCES `t_dev_module` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_attribute] 属性字段定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_attribute` CASCADE;

CREATE TABLE `t_dev_attribute` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`ent_id` VARCHAR(50)  NOT NULL  COMMENT '实体记录号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '属性代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '属性名称'
,`seq` INTEGER(0)  COMMENT '序号'
,`data_type` VARCHAR(50)  NOT NULL  COMMENT '数据类型'
,`length` INTEGER(0)  COMMENT '数据长度'
,`precision` INTEGER(0)  COMMENT '数据精度-数字总位数'
,`scale` INTEGER(0)  COMMENT '数据刻度-小数点后位数'
,`nn_flag` VARCHAR(10)  COMMENT '是否为非空-Y=非空'
,`pk_flag` VARCHAR(10)  COMMENT '是否为主键-Y=主键'
,`uq_flag` VARCHAR(10)  COMMENT '是否唯一-Y=数据唯一'
,`ai_flag` VARCHAR(10)  COMMENT '自增长类型-UUID,++=自增长'
,`dft_value` VARCHAR(255)  COMMENT '缺省值'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
,`m_name` VARCHAR(50)  COMMENT '修改用户名'
,`m_time` DATETIME COMMENT '修改时间'
) ENGINE=InnoDB COMMENT='属性字段定义';

ALTER TABLE `t_dev_attribute` ADD CONSTRAINT `PK_t_dev_attribute` PRIMARY KEY 
(`id`);
ALTER TABLE `t_dev_attribute` ADD UNIQUE `UQ_t_dev_attribute_code` 
(`ent_id`,`code`);

ALTER TABLE `t_dev_attribute` ADD FOREIGN KEY `FK_t_dev_attribute_entity`  
  (`ent_id`) 
 REFERENCES `t_dev_entity` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_ent_key] 实体相关键定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_ent_key` CASCADE;

CREATE TABLE `t_dev_ent_key` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`ent_id` VARCHAR(50)  NOT NULL  COMMENT '实体记录号'
,`type` VARCHAR(10)  NOT NULL  COMMENT '关系类型-PK,UQ,IDX,FK'
,`code` VARCHAR(50)  NOT NULL  COMMENT '关系代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '关系名称'
,`seq` INTEGER(0)  COMMENT '序号'
,`uq_flag` VARCHAR(10)  COMMENT '是否唯一-Y=数据唯一'
,`cl_flag` VARCHAR(10)  COMMENT '是否为聚簇-CL=聚簇,NCL=非聚簇'
,`order_flag` VARCHAR(10)  COMMENT '排序类型-ASC=升序,DESC=降序'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
,`m_name` VARCHAR(50)  COMMENT '修改用户名'
,`m_time` DATETIME COMMENT '修改时间'
) ENGINE=InnoDB COMMENT='实体相关键定义，包括：主键、唯一约束、索引、外键';

ALTER TABLE `t_dev_ent_key` ADD CONSTRAINT `PK_t_dev_ent_key` PRIMARY KEY 
(`id`);

ALTER TABLE `t_dev_ent_key` ADD FOREIGN KEY `FK_t_dev_ent_key_entity`  
  (`ent_id`) 
 REFERENCES `t_dev_entity` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_key_attribute] 实体内部关联属性定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_key_attribute` CASCADE;

CREATE TABLE `t_dev_key_attribute` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`key_id` VARCHAR(50)  NOT NULL  COMMENT '实体关系记录号'
,`attr_id` VARCHAR(50)  NOT NULL  COMMENT '属性字段记录号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '实体关联属性名称'
,`seq` INTEGER(0)  COMMENT '序号'
,`order_flag` VARCHAR(10)  COMMENT '排序类型-ASC=升序,DESC=降序'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
,`m_name` VARCHAR(50)  COMMENT '修改用户名'
,`m_time` DATETIME COMMENT '修改时间'
) ENGINE=InnoDB COMMENT='实体内部关联属性定义';

ALTER TABLE `t_dev_key_attribute` ADD CONSTRAINT `PK_t_dev_key_attribute` PRIMARY KEY 
(`id`);

ALTER TABLE `t_dev_key_attribute` ADD FOREIGN KEY `FK_t_dev_key_attribute_attr`  
  (`attr_id`) 
 REFERENCES `t_dev_attribute` (`id`);
ALTER TABLE `t_dev_key_attribute` ADD FOREIGN KEY `FK_t_dev_key_attribute_key`  
  (`key_id`) 
 REFERENCES `t_dev_ent_key` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_ent_relation] 实体间关系定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_ent_relation` CASCADE;

CREATE TABLE `t_dev_ent_relation` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`src_ent_id` VARCHAR(50)  NOT NULL  COMMENT '源实体记录号-对应子表'
,`dest_ent_id` VARCHAR(50)  NOT NULL  COMMENT '目标实体记录号-对应父表'
,`type` VARCHAR(10)  NOT NULL  COMMENT '关系类型-FK'
,`src_key_id` VARCHAR(50)  NOT NULL  COMMENT '源键记录号-FK子表键记录号'
,`dest_key_id` VARCHAR(50)  COMMENT '目标键记录号-FK父表键记录号'
,`src_key_code` VARCHAR(50)  COMMENT '源键代号-FK子表键物理名称'
,`dest_key_code` VARCHAR(50)  COMMENT '目标键代号-FK主表键物理名称'
,`src_multi` VARCHAR(10)  COMMENT '源对应数量-1=1对1,*=多对1'
,`dest_multi` VARCHAR(10)  COMMENT '目标对应数量-1=1对1,*=多对1'
,`on_delete` VARCHAR(10)  COMMENT '删除联动操作'
,`on_update` VARCHAR(10)  COMMENT '更新联动操作'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
,`m_name` VARCHAR(50)  COMMENT '修改用户名'
,`m_time` DATETIME COMMENT '修改时间'
) ENGINE=InnoDB COMMENT='实体间关系定义：外键';

ALTER TABLE `t_dev_ent_relation` ADD CONSTRAINT `PK_t_dev_ent_relation` PRIMARY KEY 
(`id`);

ALTER TABLE `t_dev_ent_relation` ADD FOREIGN KEY `FK_t_dev_er_src_entity`  
  (`src_ent_id`) 
 REFERENCES `t_dev_entity` (`id`);
ALTER TABLE `t_dev_ent_relation` ADD FOREIGN KEY `FK_t_dev_er_dest_entity`  
  (`dest_ent_id`) 
 REFERENCES `t_dev_entity` (`id`);
ALTER TABLE `t_dev_ent_relation` ADD FOREIGN KEY `FK_t_dev_er_src_key`  
  (`src_key_id`) 
 REFERENCES `t_dev_ent_key` (`id`);
ALTER TABLE `t_dev_ent_relation` ADD FOREIGN KEY `FK_t_dev_er_dest_key`  
  (`dest_key_id`) 
 REFERENCES `t_dev_ent_key` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_er_attribute] 实体关系属性字段定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_er_attribute` CASCADE;

CREATE TABLE `t_dev_er_attribute` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`er_id` VARCHAR(50)  NOT NULL  COMMENT '关联关系记录号'
,`seq` INTEGER(0)  COMMENT '序号'
,`src_attr_id` VARCHAR(50)  NOT NULL  COMMENT '源属性字段记录号'
,`dest_attr_id` VARCHAR(50)  NOT NULL  COMMENT '目标属性字段记录号'
,`src_attr_code` VARCHAR(50)  COMMENT '源属性字段代号'
,`dest_attr_code` VARCHAR(50)  COMMENT '目标属性字段代号'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
,`m_name` VARCHAR(50)  COMMENT '修改用户名'
,`m_time` DATETIME COMMENT '修改时间'
) ENGINE=InnoDB COMMENT='实体关系属性字段定义';

ALTER TABLE `t_dev_er_attribute` ADD CONSTRAINT `PK_t_dev_er_attribute` PRIMARY KEY 
(`id`);

ALTER TABLE `t_dev_er_attribute` ADD FOREIGN KEY `FK_t_dev_er_attribute_src_attr`  
  (`src_attr_id`) 
 REFERENCES `t_dev_attribute` (`id`);
ALTER TABLE `t_dev_er_attribute` ADD FOREIGN KEY `FK_t_dev_er_attribute_dest_attr`  
  (`dest_attr_id`) 
 REFERENCES `t_dev_attribute` (`id`);
ALTER TABLE `t_dev_er_attribute` ADD FOREIGN KEY `FK_t_dev_er_attribute_er`  
  (`er_id`) 
 REFERENCES `t_dev_ent_relation` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_function] 功能定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_function` CASCADE;

CREATE TABLE `t_dev_function` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '功能代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '功能名称'
,`type` VARCHAR(50)  COMMENT '功能类型'
,`sys_id` VARCHAR(50)  COMMENT '系统记录编号'
,`mod_id` VARCHAR(50)  COMMENT '模块记录编号'
,`seq` INTEGER(0)  COMMENT '序号'
,`pattern_id` VARCHAR(50)  COMMENT '样例记录编号'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
) ENGINE=InnoDB COMMENT='功能定义';

ALTER TABLE `t_dev_function` ADD CONSTRAINT `PK_t_dev_function` PRIMARY KEY 
(`id`);

ALTER TABLE `t_dev_function` ADD FOREIGN KEY `FK_t_dev_function_sys`  
  (`sys_id`) 
 REFERENCES `t_dev_system` (`id`);
ALTER TABLE `t_dev_function` ADD FOREIGN KEY `FK_t_dev_function_mod`  
  (`mod_id`) 
 REFERENCES `t_dev_module` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_page] 交互页面定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_page` CASCADE;

CREATE TABLE `t_dev_page` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '页面代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '页面名称'
,`type` VARCHAR(50)  NOT NULL  COMMENT '页面类型'
,`fun_id` VARCHAR(50)  COMMENT '功能记录编号'
,`seq` INTEGER(0)  COMMENT '序号'
,`pattern_id` VARCHAR(50)  COMMENT '样例记录编号'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
) ENGINE=InnoDB COMMENT='交互页面定义';

ALTER TABLE `t_dev_page` ADD CONSTRAINT `PK_t_dev_page` PRIMARY KEY 
(`id`);

ALTER TABLE `t_dev_page` ADD FOREIGN KEY `FK_t_dev_page_fun`  
  (`fun_id`) 
 REFERENCES `t_dev_function` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_ui_area] 页面区域定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_ui_area` CASCADE;

CREATE TABLE `t_dev_ui_area` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '页面区域代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '页面区域名称'
,`type` VARCHAR(50)  NOT NULL  COMMENT '页面区域类型-tab,panel,'
,`page_id` VARCHAR(50)  COMMENT '页面记录编号'
,`seq` INTEGER(0)  COMMENT '序号'
,`pattern_id` VARCHAR(50)  COMMENT '样例记录编号'
,`parent_id` VARCHAR(50)  COMMENT '父区域记录号'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
) ENGINE=InnoDB COMMENT='页面区域定义';

ALTER TABLE `t_dev_ui_area` ADD CONSTRAINT `PK_t_dev_ui_area` PRIMARY KEY 
(`id`);

ALTER TABLE `t_dev_ui_area` ADD FOREIGN KEY `FK_t_dev_ui_area_page`  
  (`page_id`) 
 REFERENCES `t_dev_page` (`id`);
ALTER TABLE `t_dev_ui_area` ADD FOREIGN KEY `FK_t_dev_ui_area_ui_area`  
  (`parent_id`) 
 REFERENCES `t_dev_ui_area` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_ui_field] 页面栏位定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_ui_field` CASCADE;

CREATE TABLE `t_dev_ui_field` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '栏位代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '栏位名称'
,`type` VARCHAR(50)  NOT NULL  COMMENT '栏位类型'
,`io_model` VARCHAR(50)  COMMENT '栏位输入方式-栏位输入方式,如: 必输,隐藏,只读…'
,`dft_value` VARCHAR(255)  COMMENT '缺省值'
,`length` INTEGER(0)  COMMENT '资料长度'
,`format` VARCHAR(255)  COMMENT '栏位格式-资料格式，如: X(n), 9(n).99,-9(n).9(m)…'
,`validation` VARCHAR(255)  COMMENT '栏位验证规则'
,`page_id` VARCHAR(50)  COMMENT '页面记录编号'
,`uiar_id` VARCHAR(50)  COMMENT '区域记录编号'
,`seq` INTEGER(0)  COMMENT '序号'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
) ENGINE=InnoDB COMMENT='页面栏位定义';

ALTER TABLE `t_dev_ui_field` ADD CONSTRAINT `PK_t_dev_ui_field` PRIMARY KEY 
(`id`);

ALTER TABLE `t_dev_ui_field` ADD FOREIGN KEY `FK_t_dev_ui_field`  
  (`page_id`) 
 REFERENCES `t_dev_page` (`id`);
ALTER TABLE `t_dev_ui_field` ADD FOREIGN KEY `FK_t_dev_ui_filed_ui_area`  
  (`uiar_id`) 
 REFERENCES `t_dev_ui_area` (`id`);
SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_ui_data] 页面数据映射定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_ui_data` CASCADE;

CREATE TABLE `t_dev_ui_data` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`uie_type` VARCHAR(10)  NOT NULL  COMMENT '界面元素类型-PAGE,AREA,FIELD'
,`uie_id` VARCHAR(50)  NOT NULL  COMMENT '界面元素记录编号'
,`dte_type` VARCHAR(10)  NOT NULL  COMMENT '数据元素类型-ENT,ATTR,APP,SESSION,PAGE'
,`dte_id` VARCHAR(50)  COMMENT '数据元素记录编号'
,`type` VARCHAR(50)  COMMENT '映射类型'
,`dte_code` VARCHAR(50)  COMMENT '数据元素代号'
,`dte_name` VARCHAR(50)  COMMENT '数据元素名称'
,`u2d_tran` VARCHAR(255)  COMMENT '界面到数据转换规则'
,`d2u_tran` VARCHAR(255)  COMMENT '数据到界面转换规则'
,`u2d_check` VARCHAR(255)  COMMENT '界面到数据检查规则'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
) ENGINE=InnoDB COMMENT='页面数据映射定义';

ALTER TABLE `t_dev_ui_data` ADD CONSTRAINT `PK_t_dev_ui_data` PRIMARY KEY 
(`id`);

SET FOREIGN_KEY_CHECKS=1;

--  Table DDL: [t_dev_ui_action] 页面栏位定义
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `t_dev_ui_action` CASCADE;

CREATE TABLE `t_dev_ui_action` (
`id` VARCHAR(50)  NOT NULL  COMMENT '记录编号'
,`code` VARCHAR(50)  NOT NULL  COMMENT '动作代号'
,`name` VARCHAR(50)  NOT NULL  COMMENT '动作名称'
,`type` VARCHAR(50)  NOT NULL  COMMENT '动作类型'
,`uie_type` VARCHAR(10)  NOT NULL  COMMENT '界面元素类型'
,`uie_id` VARCHAR(50)  NOT NULL  COMMENT '界面元素记录编号'
,`seq` INTEGER(0)  COMMENT '序号'
,`notes` VARCHAR(255)  COMMENT '描述'
,`c_uid` VARCHAR(50)  COMMENT '创建用户号'
,`c_name` VARCHAR(50)  COMMENT '创建用户名'
,`c_time` DATETIME COMMENT '创建时间'
,`m_uid` VARCHAR(50)  COMMENT '修改用户号'
) ENGINE=InnoDB COMMENT='页面栏位定义';

ALTER TABLE `t_dev_ui_action` ADD CONSTRAINT `PK_t_dev_ui_action` PRIMARY KEY 
(`id`);

SET FOREIGN_KEY_CHECKS=1;

