package net.aicoder.exsys.module.submodule.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import net.aicoder.exsys.module.entity.TDevEntity;
import net.aicoder.exsys.module.submodule.service.IEntityExpService;
import net.aicoder.tcom.poi.data.IDataContext;
import net.aicoder.tcom.tools.util.JsonUtil;

@Service("entityExpService")
//@Transactional
public class EntityExpService implements IEntityExpService{
	private static final String KEY_SYS_CODE = "sysCode";
	private static final String KEY_ENTITY_LIST = "entityList";
	private static final String KEY_ENTITY = "entity";
	
	private static final Log log = LogFactory.getLog(EntityExpService.class);
	
	@Override
	public void listEntity(IDataContext dataContext){
		String sysCode = (String)dataContext.getOneOutData(KEY_SYS_CODE);
		List<TDevEntity> entityList = listEntity(sysCode);
		dataContext.putOneOutData(KEY_ENTITY_LIST, entityList);
		if(entityList == null){
			log.debug("listEntity>> key=" + KEY_ENTITY_LIST + "; list is null!!!");
		}else{
			log.debug("listEntity>> key=" + KEY_ENTITY_LIST + "; listNum=" + entityList.size());
		}
	}

	@Override
	public void getOneEntity(IDataContext dataContext){
		TDevEntity entityIn = (TDevEntity)dataContext.getOneOutData(KEY_ENTITY);
		TDevEntity entity = getEntity(entityIn);
		dataContext.putOneOutData(KEY_ENTITY, entity);
		if(entity == null){
			log.debug("getOneEntity>> key=" + KEY_ENTITY + "; entity is null!!!");
		}else{
			log.debug("getOneEntity>> key=" + KEY_ENTITY + "; entity=" + entity.getCode());
		}
	}

	@SuppressWarnings("unchecked")
	private List<TDevEntity>  listEntity(String sysLid){
		List<TDevEntity> tDevEntityList = new ArrayList<TDevEntity>();
		
//		ID	No.	Name (physical name)	Name (logical name)		Description	System	Module	Status	Version	Author	Created date	Last Updated
//		2c90804e5fcdf435015fcdf443c40000		t_dev_system	系统定义	系统定义	devp	dev	00_Draft	0.2.0	StoneShi	2015/10/28	2017/11/18
//		2c90804e5fcdf435015fcdf448f00010		t_dev_module	模块定义	模块定义	devp	dev	00_Draft	0.2.0	StoneShi	2015/10/28	2017/11/18
//		2c90804e5fcdf435015fcdf4500b0028		t_dev_entity	实体定义	实体定义	devp	dev	00_Draft	0.2.0	StoneShi	2015/10/28	2017/11/18
		
		Date dateNow = new Date();
		TDevEntity entSystem = new TDevEntity();
		entSystem.setId("2c90804e5fcdf435015fcdf443c40000");
		entSystem.setCode("t_dev_system");
		entSystem.setName("系统定义");
		entSystem.setNotes("系统定义");
		entSystem.setSysCode("devp");
		entSystem.setModCode("dev");
		entSystem.setStatus("00_Draft");
		entSystem.setDevVersion("0.2.0");
		entSystem.setMName("StoneShi");
		entSystem.setCTime(dateNow);
		entSystem.setMTime(dateNow);
		tDevEntityList.add(entSystem);
		
		TDevEntity entModule = new TDevEntity();
		entModule.setId("2c90804e5fcdf435015fcdf448f00010");
		entModule.setCode("t_dev_module");
		entModule.setName("模块定义");
		entModule.setNotes("模块定义");
		entModule.setSysCode("devp");
		entModule.setModCode("dev");
		entModule.setStatus("00_Draft");
		entModule.setDevVersion("0.2.0");
		entModule.setMName("StoneShi");
		entModule.setCTime(dateNow);
		entModule.setMTime(dateNow);
		tDevEntityList.add(entModule);
		
		TDevEntity entEntity = new TDevEntity();
		entEntity.setId("2c90804e5fcdf435015fcdf4500b0028");
		entEntity.setCode("t_dev_entity");
		entEntity.setName("实体定义");
		entEntity.setNotes("实体定义");
		entEntity.setSysCode("devp");
		entEntity.setModCode("dev");
		entEntity.setStatus("00_Draft");
		entEntity.setDevVersion("0.2.0");
		entEntity.setMName("StoneShi");
		entEntity.setCTime(dateNow);
		entEntity.setMTime(dateNow);
		tDevEntityList.add(entEntity);
/**		
		try{
			String fileName = "data/Exp-EntityList.json";
			File file = new File(fileName);
			Path path = file.toPath();
			List<TDevEntity> tDevEntityListTmp = null;
			tDevEntityListTmp = JsonUtil.load(path, List.class);
			for(TDevEntity entityTmp:tDevEntityListTmp){
				String jsonStr = JsonUtil.serialize(entityTmp);
				TDevEntity entity = JsonUtil.deserialize(jsonStr, TDevEntity.class);
				tDevEntityList.add(entity);
			}
		}catch(IOException ioe){
			log.error(ioe.getMessage());
		}
**/		
		return tDevEntityList;
	}

	private TDevEntity getEntity(TDevEntity entityIn){
		TDevEntity entity = null;
		
		try{
			String fileName = "data/Exp-"+entityIn.getCode()+".json";
			File file = new File(fileName);
			Path path = file.toPath();
			entity = JsonUtil.load(path, TDevEntity.class);
		}catch(IOException ioe){
			log.error(ioe.getMessage());
		}
		
		return entity;
	}
	
/**	
	private List<TDevEntity>  listEntity(String sysLid){
		//String hql = "from TDevEntity as ent where 1 = 1 AND ent.sysCode = ? order by ent.sysCode, ent.modCode, ent.Seq";
		String hql = "from TDevEntity as ent where 1 = 1 AND ent.sysCode = ? ";
		
		List<TDevEntity> tDevEntityList;
		//tDevEntityList = commonDao.findHql(hql, sysLid,modLid);
		tDevEntityList = commonDao.findHql(hql, sysLid);
		
		return tDevEntityList;
	}

	private TDevEntity getEntity(TDevEntity entityIn){
		TDevEntity entity = commonDao.get(TDevEntity.class, entityIn.getId());
		return entity;
	}
**/	
}