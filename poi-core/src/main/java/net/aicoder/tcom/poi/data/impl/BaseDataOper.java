package net.aicoder.tcom.poi.data.impl;

import net.aicoder.tcom.poi.data.IDataContext;
import net.aicoder.tcom.poi.data.IDataOper;

public abstract class BaseDataOper implements IDataOper{
	protected IDataContext dataContext; // 数据处理上下文
	private String dataKey; // 数据处理标识

	@Override
	public abstract void preProduce();

	@Override
	public abstract void produce();
	
	@Override
	public abstract void postProduce();
	
	// setter/getter
	@Override
	public String getDataKey() {
		return dataKey;
	}
	
	@Override
	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}

	@Override
	public IDataContext getDataContext() {
		return dataContext;
	}

	@Override
	public void setDataContext(IDataContext dataContext) {
		this.dataContext = dataContext;
	}
}
