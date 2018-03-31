package net.aicoder.cd.report.dataoper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import net.aicoder.cd.entity.CdRequirement;
import net.aicoder.cd.report.service.IPrjWeeklyReportService;
import net.aicoder.tcom.poi.data.impl.BaseDataOper;

public class PrjReqListExpOper extends BaseDataOper {
	private static final String KEY_PRJ_CODE = "prjCode";
	private static final String KEY_PRJ_REQ_LIST = "prjReqList";

	@Autowired
	IPrjWeeklyReportService prjWeeklyReportService;

	@Override
	public void preProduce() {
	}

	@Override
	public void produce() {
		String prjCode = (String)dataContext.getOneOutData(KEY_PRJ_CODE);
		List<CdRequirement> list = prjWeeklyReportService.listCdRequirement(prjCode);
		dataContext.putOneOutData(KEY_PRJ_REQ_LIST, list);
	}

	@Override
	public void postProduce() {
	}
}
