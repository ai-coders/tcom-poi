package net.aicoder.exsys.module.submodule.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.aicoder.tcom.poi.excel.exporter.IBookExporter;

@Component
//@Transactional
public class EntityExpController {
	private static final String KEY_SYS_CODE = "sysCode";

	private static final Log log = LogFactory.getLog(EntityExpController.class);

	@Autowired
	IBookExporter entityBookExporter;

	//@Override
	public void expEntityList(){
		String sysCode = "devp";
		log.debug("expEntityList>> key=" + KEY_SYS_CODE + "; value=" + sysCode);
		entityBookExporter.putOneOutData(KEY_SYS_CODE, sysCode);
		entityBookExporter.doExport();
	}
}
