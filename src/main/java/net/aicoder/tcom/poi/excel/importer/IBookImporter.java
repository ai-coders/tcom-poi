package net.aicoder.tcom.poi.excel.importer;

import java.util.Map;

import net.aicoder.tcom.poi.data.IDataContext;
import net.aicoder.tcom.poi.data.IDataOper;

public interface IBookImporter {

	public static final String DEFINE_PATH = "definePath";
	public static final String DEFINE_SHORT_PATH = "defineShortPath";
	public static final String DEFINE_FILE_NAME = "defineFileName";
	//public static final String TPL_PATH = "tplPath";
	//public static final String TPL_SHORT_PATH = "tplShortPath";
	//public static final String TPL_FILE_NAME = "tplFileName";
	public static final String INPUT_PATH = "inputPath";
	public static final String INPUT_SHORT_PATH = "inputShortPath";
	public static final String INPUT_FILE_NAME = "inputFileName";
	
	/**
	 * 数据导入
	 */
	public void doImport();

	/**
	 * 导出BookDefine配置文件(XML)
	 */
	public void dumpBookDefine();

	// Property Getter/Setter
	/**
	 * 获取数据上下文
	 * @return
	 */
	public IDataContext getDataContext();

	/**
	 * 设置数据上下文
	 * @param dataContext
	 */
	public void setDataContext(IDataContext dataContext);

	/**
	 * 获取工作表数据处理定义
	 * @return
	 */
	public Map<String, IDataOper> getSheetDataOpers();

	/**
	 * 设置工作表数据处理定义
	 * @param sheetDataOpers
	 */
	public void setSheetDataOpers(Map<String, IDataOper> sheetDataOpers);

	/**
	 * 获取所有参数定义
	 * @return
	 */
	public Map<String, String> getBookProperties();

	/**
	 * 设置所有参数定义
	 * @param bookExporterProps
	 */
	public void setBookProperties(Map<String, String> bookExporterProps);

	/**
	 * 获取单个参数定义
	 * @param propertyName
	 * @return
	 */
	public String getBookProperty(String propertyName);

	/**
	 * 设置单个参数定义
	 * @param key
	 * @param value
	 */
	public void setBookProperty(String key, String value);

	/**
	 * 获取配置文件全路径
	 * @return
	 */
	public String getDefinePath();

	/**
	 * 设置 配置文件全路径
	 * @param value
	 */
	public void setDefinePath(String value);

	/**
	 * 获取配置文件路径（相对于缺省配置目录）
	 * @return
	 */
	public String getDefineShortPath();

	/**
	 * 设置配置文件路径（相对于缺省配置目录）
	 * @param value
	 */
	public void setDefineShortPath(String value);

	/**
	 * 获取配置文件名称
	 * @return
	 */
	public String getDefineFileName();

	/**
	 * 设置配置文件名称
	 * @param value
	 */
	public void setDefineFileName(String value);

	/**
	 * 获取输出文件全路径
	 * @return
	 */
	public String getInputPath();

	/**
	 * 设置输出文件全路径
	 * @param value
	 */
	public void setInputPath(String value);

	/**
	 * 获取输出文件路径（相对于缺省配置目录）
	 * @return
	 */
	public String getInputShortPath();

	/**
	 * 设置输出文件路径（相对于缺省配置目录）
	 * @param value
	 */
	public void setInputShortPath(String value);

	/**
	 * 获取输出文件名称
	 * @return
	 */
	public String getInputFileName();

	/**
	 * 设置输出文件名称
	 * @param value
	 */
	public void setInputFileName(String value);
	
	/**
	 * 获取导出数据
	 * @return
	 */
	public Map<String, Object> getOutData();

	/**
	 * 设置导出数据
	 * @param outData
	 */
	public void setOutData(Map<String, Object> outData);

	/**
	 * 设置单个导出数据
	 * @param key
	 * @param value
	 */
	public void putOneOutData(String key, Object value);
}