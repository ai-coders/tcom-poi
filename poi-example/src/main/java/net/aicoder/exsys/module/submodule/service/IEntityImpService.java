package net.aicoder.exsys.module.submodule.service;

import net.aicoder.tcom.poi.data.IDataContext;

public interface IEntityImpService {

	//public void impEntityList();

	public void importEntityList(IDataContext dataContext);

	public void importEntity(IDataContext dataContext);

}