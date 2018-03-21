package net.aicoder.exsys.module.submodule.service;

import net.aicoder.tcom.poi.data.IDataContext;

public interface IEntityExpService {

	//public void expEntityList();

	public void listEntity(IDataContext dataContext);

	public void getOneEntity(IDataContext dataContext);

}