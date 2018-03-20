package net.aicoder.tcom.poi.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.aicoder.tcom.poi.data.IDataContext;
import net.aicoder.tcom.poi.data.IDataOper;
import net.aicoder.tcom.tools.util.AiStringUtil;

public class DefaultDataContext implements IDataContext{
	//private Map<String, Object> inParams; // 输入参数
	private Map<String, Object> outData = new HashMap<String, Object>(); // 输出数据(Map)

	private Map<String, Object> dataOpers; // 数据处理对象
	private String dataSeq; //数据处理顺序定义
	private List<String> dataSeqList; //数据处理顺序定义(List)

	@Override
	public void produceData() {
		if (dataSeqList == null || dataSeqList.size() == 0) {
			return;
		}
		if (dataOpers == null || dataOpers.size() == 0) {
			return;
		}
		
		Set<String> dataSeqSet = new HashSet<String>();
		for (String dataKey : dataSeqList) {
			produceOneData(dataKey);
			dataSeqSet.add(dataKey);
		}
		for (Map.Entry<String, Object> entry : dataOpers.entrySet()) {
			String dataKey = entry.getKey();
			if (!dataSeqSet.contains(dataKey)) {
				produceOneData(dataKey);
			}
		}
	}

	private void produceOneData(String dataKey) {
		Map<String, Object> data = this.getOutData();
		Object dataValue;
		Object value = dataOpers.get(dataKey);

		if (value instanceof IDataOper) {
			IDataOper dataOper = (IDataOper) dataOpers.get(dataKey);
			dataOper.setDataKey(dataKey);
			dataOper.setDataContext(this);
			
			dataOper.preProduce();
			dataOper.produce();
			dataOper.postProduce();
		} else {
			dataValue = value;
			outData.put(dataKey, dataValue);
		}
		this.setOutData(data);
	}

	// getter/setter
	@Override
	public Map<String, Object> getOutData() {
		return outData;
	}

	@Override
	public void setOutData(Map<String, Object> outData) {
		this.outData = outData;
	}
	
	@Override
	public void putOneOutData(String key, Object value){
		if(this.outData == null){
			this.outData = new HashMap<String, Object>();
		}
		this.outData.put(key, value);
	}
	
	@Override
	public Object getOneOutData(String key){
		if(this.outData == null){
			return null;
		}else{
			return this.outData.get(key);
		}
	}

	@Override
	public List<String> getDataSeqList() {
		return dataSeqList;
	}
	
	@Override
	public String getDataSeq() {
		return dataSeq;
	}

	@Override
	public void setDataSeq(String dataSeq) {
		this.dataSeq = dataSeq;
		this.dataSeqList = new ArrayList<String>();
		if (!AiStringUtil.isEmpty(dataSeq)) {
			List<String> tmpList = AiStringUtil.splitToList(",", dataSeq);
			if (tmpList != null) {
				for (String str : tmpList) {
					this.dataSeqList.add(str.trim());
				}
			}
		}
	}

	@Override
	public Map<String, Object> getDataOpers() {
		return dataOpers;
	}

	@Override
	public void setDataOpers(Map<String, Object> dataOpers) {
		this.dataOpers = dataOpers;
	}
}
