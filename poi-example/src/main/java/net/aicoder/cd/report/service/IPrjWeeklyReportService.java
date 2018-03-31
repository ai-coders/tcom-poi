package net.aicoder.cd.report.service;

import java.util.List;

import net.aicoder.cd.entity.CdRequirement;
import net.aicoder.cd.entity.CdTask;

public interface IPrjWeeklyReportService {
	
	public List<CdRequirement> listCdRequirement(String prjCode);
	
	public List<CdTask> listCdList(String prjCode);
	
}
