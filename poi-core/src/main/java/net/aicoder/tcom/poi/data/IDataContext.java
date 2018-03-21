package net.aicoder.tcom.poi.data;

import java.util.List;
import java.util.Map;

public interface IDataContext {

	/**
	 * 依据输入参数生成输出数据
	 */
	public abstract void produceData();

	/**
	 * 依据输入参数生成输出数据
	 * @param inParams
	 */
	//public abstract void produceData(Map<String, Object> outData);

	// getter/setter
	/**
	 * 获取输出数据
	 * @return
	 */
	public abstract Map<String, Object> getOutData();

	/**
	 * 设置输出数据
	 * @param outData
	 */
	public abstract void setOutData(Map<String, Object> outData);

	/**
	 * 依据KEY来设置单个输出数据
	 * @param key
	 * @param value
	 */
	public abstract void putOneOutData(String key, Object value);

	/**
	 * 依据KEY来获取单个输出数据
	 * @param key
	 * @return
	 */
	public abstract Object getOneOutData(String key);

	/**
	 * 获取数据处理顺序定义
	 * @return
	 */
	public abstract List<String> getDataSeqList();

	/**
	 * 获取数据处理顺序定义
	 * @return
	 */
	public abstract String getDataSeq();

	/**
	 * 设置数据处理顺序定义
	 * @param dataSeq
	 */
	public abstract void setDataSeq(String dataSeq);

	/**
	 * 获取数据处理对象
	 * @return
	 */
	public abstract Map<String, Object> getDataOpers();

	/**
	 * 设置数据处理对象
	 * @param dataOpers
	 */
	public abstract void setDataOpers(Map<String, Object> dataOpers);

}