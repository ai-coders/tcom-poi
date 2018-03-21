package net.aicoder.tcom.poi.data;


public interface IDataOper {

	/**
	 * 前置处理
	 */
	public abstract void preProduce();

	/**
	 * 数据处理
	 */
	public abstract void produce();

	/**
	 * 后置处理
	 */
	public abstract void postProduce();

	// setter/getter
	/**
	 * 获取数据处理标识
	 * @return
	 */
	public abstract String getDataKey();

	/**
	 * 设置数据处理标识
	 * @param dataKey
	 */
	public abstract void setDataKey(String dataKey);

	/**
	 * 获取数据处理上下文
	 * @return
	 */
	public abstract IDataContext getDataContext();

	/**
	 * 设置数据处理上下文
	 * @param dataContext
	 */
	public abstract void setDataContext(IDataContext dataContext);

}