package net.aicoder.cd.report.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import net.aicoder.cd.entity.CdRequirement;
import net.aicoder.cd.entity.CdTask;
import net.aicoder.cd.report.service.IPrjWeeklyReportService;

@Service("prjWeeklyReportService")
public class PrjWeeklyReportService implements IPrjWeeklyReportService{
	private static final Log log = LogFactory.getLog(PrjWeeklyReportService.class);
	
	@Override
	public List<CdRequirement> listCdRequirement(String prjCode){
		List<CdRequirement> cdReqList = _listCdRequirement(prjCode);
		if(cdReqList == null){
			log.debug("listCdRequirement>> key=" + prjCode + "; list is null!!!");
		}else{
			log.debug("listCdRequirement>> key=" + prjCode + "; listNum=" + cdReqList.size());
		}
		return cdReqList;
	}
	
	@Override
	public List<CdTask> listCdList(String prjCode){
		List<CdTask> cdReqList = _listCdTask(prjCode);
		if(cdReqList == null){
			log.debug("listCdTask>> key=" + prjCode + "; list is null!!!");
		}else{
			log.debug("listCdTask>> key=" + prjCode + "; listNum=" + cdReqList.size());
		}
		return cdReqList;
	}
	
	private List<CdRequirement> _listCdRequirement(String prjCode){
		List<CdRequirement> cdReqList = new ArrayList<CdRequirement>();
		
		CdRequirement req1 = new CdRequirement();
		req1.setProjectCode(prjCode);
		req1.setStoryId("R0100");
		req1.setProductId("P100");
		req1.setProductName("产品1");
		req1.setStoryBranch("iOS");
		cdReqList.add(req1);
		
		CdRequirement req2 = new CdRequirement();
		req2.setProjectCode(prjCode);
		req2.setStoryId("R0200");
		req2.setProductId("P200");
		req2.setProductName("产品2");
		req2.setStoryBranch("");
		cdReqList.add(req2);

		CdRequirement req3 = new CdRequirement();
		req3.setProjectCode(prjCode);
		req3.setStoryId("R0300");
		req3.setProductId("P300");
		req3.setProductName("产品3");
		req3.setStoryBranch("");
		cdReqList.add(req3);

		return cdReqList;
	}

	private List<CdTask> _listCdTask(String prjCode){
		List<CdTask> cdTaskList = new ArrayList<CdTask>();
		
		CdTask task1 = new CdTask();
		task1.setProjectCode(prjCode);
		task1.setTaskId("T0010");
		task1.setTaskParentId("T0010");
		task1.setTaskPtype("");
		task1.setTaskName("任务1");
		task1.setTaskType("需求");
		cdTaskList.add(task1);
		
		CdTask task2 = new CdTask();
		task2.setProjectCode(prjCode);
		task2.setTaskId("T0011");
		task2.setTaskParentId("T0010");
		task2.setTaskPtype("子");
		task2.setTaskName("任务1.1");
		task2.setTaskType("开发");
		cdTaskList.add(task2);
		
		CdTask task3 = new CdTask();
		task3.setProjectCode(prjCode);
		task3.setTaskId("T0012");
		task3.setTaskParentId("T0010");
		task3.setTaskPtype("子");
		task3.setTaskName("任务1.2");
		task3.setTaskType("开发");
		cdTaskList.add(task3);
		
		return cdTaskList;
	}
	
}
