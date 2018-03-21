package net.aicoder.exsys.module.submodule.dataoper;

import org.springframework.beans.factory.annotation.Autowired;

import net.aicoder.exsys.module.submodule.service.IEntityImpService;
import net.aicoder.tcom.poi.data.impl.BaseDataOper;

public class EntityDataImpOper  extends BaseDataOper{
	
	@Autowired
	IEntityImpService entityImpService;

	@Override
	public void preProduce() {
	}

	@Override
	public void produce() {
		entityImpService.importEntity(dataContext);
	}

	@Override
	public void postProduce() {
		
	}

}
