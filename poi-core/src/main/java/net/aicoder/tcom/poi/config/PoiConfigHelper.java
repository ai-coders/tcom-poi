package net.aicoder.tcom.poi.config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PoiConfigHelper {
	private static final Log log = LogFactory.getLog(PoiConfigHelper.class);
	
	private static final String DEFAULT_PROPERTIES_FILE = "poi/poi";
	
	private static PoiConfig DEFAUT_POI_CONFIG = null;
	
	public static PoiConfig getPoiConfig(){
		if(DEFAUT_POI_CONFIG == null){
			DEFAUT_POI_CONFIG = loadPoiConfig();
		}
		
		return DEFAUT_POI_CONFIG;
	}
	
	private static PoiConfig loadPoiConfig(){
		PoiConfig config = new PoiConfig();
		String cfgPropertiesFile = DEFAULT_PROPERTIES_FILE;
		
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(cfgPropertiesFile);

			String prjHome = bundle.getString("project_home");
			config.setPrjHome(prjHome);
			String poiHome = bundle.getString("poi_home");
			config.setPoiHome(poiHome);
			String tplHome = bundle.getString("template_home");
			config.setTplHome(tplHome);
		}catch(MissingResourceException ex){
			log.error(ex.toString());
		}
		
		return config;
	}
}
