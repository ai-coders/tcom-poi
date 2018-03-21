package net.aicoder.tcom.poi.config;

import net.aicoder.tcom.tools.util.AiStringUtil;
import net.aicoder.tcom.tools.util.SystemUtil;

public class PoiConfig {
	private String prjHome;
	private String poiHome;
	private String tplHome;

	public String defaultTplPath() {
		return defaultTplPath("");
	}
	
	public String defaultTplPath(String templateShortPath) {
		StringBuffer pathSB = new StringBuffer();
		String activePrjHome = SystemUtil.getClassPath();
		pathSB.append(activePrjHome);
		String templateHome = getTplHome();
		pathSB.append(templateHome + "/");
		if(!AiStringUtil.isEmpty(templateShortPath)){
			pathSB.append(templateShortPath);
		}
		String templatePath = pathSB.toString();
		return templatePath;
	}

	public String defaultOutputPath(){
		return defaultOutputPath("");
	}

	public String defaultOutputPath(String outputShortPath){
		String defaultOutputPath = "";
		String homePath = outputHomePath();
		if(AiStringUtil.isEmpty(outputShortPath)){
			defaultOutputPath = homePath;
		}else{
			defaultOutputPath = homePath + outputShortPath;
		}
		return defaultOutputPath;
	}

	private String outputHomePath(){
		StringBuffer pathSB = new StringBuffer();
		String projectHome = getPrjHome();
		if(!AiStringUtil.isEmpty(projectHome)){
			pathSB.append(projectHome);
			pathSB.append("/");
		}
		String poiHome = getPoiHome();
		if(!AiStringUtil.isEmpty(poiHome)){
			pathSB.append(poiHome);
			pathSB.append("/");
		}

		return pathSB.toString();
	}
	
	// Property's getter & setter
	public String getPrjHome() {
		return prjHome;
	}
	public void setPrjHome(String prjHome) {
		this.prjHome = prjHome;
	}
	public String getPoiHome() {
		return poiHome;
	}
	public void setPoiHome(String poiHome) {
		this.poiHome = poiHome;
	}
	
	public String getTplHome() {
		return tplHome;
	}
	
	
	public void setTplHome(String tplHome) {
		this.tplHome = tplHome;
	}
}
