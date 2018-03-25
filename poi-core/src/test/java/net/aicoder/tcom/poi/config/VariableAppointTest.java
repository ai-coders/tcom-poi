package net.aicoder.tcom.poi.config;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.aicoder.tcom.poi.TcomPoiApplication;
import net.aicoder.tcom.poi.config.VariableAppoint;
import net.aicoder.tcom.poi.config.VariableDefine;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TcomPoiApplication.class)
@SuppressWarnings("unused")
public class VariableAppointTest {
	private static final Log log = LogFactory
			.getLog(VariableAppointTest.class);
	private Map<String, VariableDefine> variablesMap;

	private static String configStrs1[] = {
			"$[entityList]{org.jeap.devp.entity.dev.TDevEntity}", "$[:id]",
			"$[_COMMON.LIST_INDEX]", "$[:entityName]", "$[:sysLid]" };

	private static String configStrs2[] = { "$[entityList]",
			"TBL_$[:entityName]" };

	private static String configStrs3[] = {
			"{org.jeap.devp.entity.dev.TDevEntity}$[entity.id]",
			"$[entity.entityName]" };

	/**	
		private static String configStrs4[] = {
			"{org.jeap.devp.entity.dev.TDevEntity}$[entity.id]" 
			, "$[entity.entityName]"
			};
	**/

	private static String configStrs5[] = {
			"{org.stone.devp.entity.dev.TDevAttribute}$[entity.attributes]",
			"$[:id]", "$[:notNull,(Y=○)]" };

	@Test
	public void testVarsMap() {
		if (variablesMap == null) {
			variablesMap = new HashMap<String, VariableDefine>();
		}
		parserStrs(configStrs1, false);
		parserStrs(configStrs2, false);
		parserStrs(configStrs3, true);
		parserStrs(configStrs5, false);
	}

	private void parserStrs(String[] strs, boolean isAllProperty) {
		if (variablesMap == null) {
			variablesMap = new HashMap<String, VariableDefine>();
		}

		int idx = 0;
		VariableDefine defaultVarDefine = null;
		for (String configStr : strs) {
			if (idx++ == 0) {
				VariableAppoint varAppoint = new VariableAppoint(variablesMap);
				if (isAllProperty) {
					varAppoint.parserVariable(configStr, true);
					defaultVarDefine = varAppoint.getVariableDefine()
							.getParent();
				} else {
					varAppoint.parserVariable(configStr);
					defaultVarDefine = varAppoint.getVariableDefine();
				}
				log.debug(varAppoint.dumpStr());
			} else {
				VariableAppoint varAppoint = new VariableAppoint(variablesMap);
				varAppoint.parserVariable(configStr, defaultVarDefine, true);
				log.debug(varAppoint.dumpStr());
			}
		}
	}

}
