package net.aicoder.exsys.module.submodule.dataoper;

import org.springframework.beans.factory.annotation.Autowired;

import net.aicoder.exsys.module.submodule.service.IEntityExpService;
import net.aicoder.tcom.poi.data.impl.BaseDataOper;

public class EntityDataExpOper extends BaseDataOper {
	@Autowired
	IEntityExpService entityExpService;

	@Override
	public void preProduce() {
	}

	@Override
	public void produce() {
		entityExpService.getOneEntity(dataContext);
	}

	@Override
	public void postProduce() {
	}

}
