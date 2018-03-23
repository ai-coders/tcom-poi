package net.aicoder.exsys.module.submodule.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.aicoder.tcom.poi.excel.importer.IBookImporter;

@Component
public class EntityImpController {
	private static final String KEY_SYS_CODE = "sysCode";

	private static final Log log = LogFactory.getLog(EntityImpController.class);

	@Autowired
	IBookImporter entityBookImporter;

	public void impEntityList() {
		String sysCode = "devp";
		log.debug("expEntityList>> key=" + KEY_SYS_CODE + "; value=" + sysCode);
		entityBookImporter.putOneOutData(KEY_SYS_CODE, sysCode);
		entityBookImporter.doImport();
	}
}
