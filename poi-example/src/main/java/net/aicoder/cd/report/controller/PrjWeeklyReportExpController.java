package net.aicoder.cd.report.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.aicoder.tcom.poi.excel.exporter.IBookExporter;

@Component
public class PrjWeeklyReportExpController {
	private static final Log log = LogFactory.getLog(PrjWeeklyReportExpController.class);

	private static final String KEY_PRJ_CODE = "prjCode";

	@Autowired
	IBookExporter cdPrjWeeklyReportExporter;

	public void expPrjWeeklyReport(){
		String prjCode = "1801001_NIP_V3.0";
		log.debug("expEntityList>> key=" + KEY_PRJ_CODE + "; value=" + prjCode);
		cdPrjWeeklyReportExporter.putOneOutData(KEY_PRJ_CODE, prjCode);
		cdPrjWeeklyReportExporter.doExport();
	}
}
