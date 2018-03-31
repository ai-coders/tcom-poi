package net.aicoder.cd.report.dataoper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import net.aicoder.cd.entity.CdRequirement;
import net.aicoder.cd.entity.CdTask;
import net.aicoder.cd.report.service.IPrjWeeklyReportService;
import net.aicoder.tcom.poi.data.impl.BaseDataOper;

public class PrjTaskListExpOper extends BaseDataOper {
	private static final String KEY_PRJ_CODE = "prjCode";
	private static final String KEY_PRJ_TASK_LIST = "prjTaskList";

	@Autowired
	IPrjWeeklyReportService prjWeeklyReportService;

	@Override
	public void preProduce() {
	}

	@Override
	public void produce() {
		String prjCode = (String)dataContext.getOneOutData(KEY_PRJ_CODE);
		List<CdTask> list = prjWeeklyReportService.listCdList(prjCode);
		dataContext.putOneOutData(KEY_PRJ_TASK_LIST, list);
	}

	@Override
	public void postProduce() {
	}
}
