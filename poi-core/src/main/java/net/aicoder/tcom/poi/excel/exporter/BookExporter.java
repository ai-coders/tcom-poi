package net.aicoder.tcom.poi.excel.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;

import net.aicoder.tcom.poi.config.BookDefine;
import net.aicoder.tcom.poi.config.PoiConfigHelper;
import net.aicoder.tcom.poi.config.SheetDefine;
import net.aicoder.tcom.poi.config.VariableAppoint;
import net.aicoder.tcom.poi.config.VariableDefine;
import net.aicoder.tcom.poi.data.IDataContext;
import net.aicoder.tcom.poi.data.IDataOper;
import net.aicoder.tcom.poi.data.impl.DefaultDataContext;
import net.aicoder.tcom.poi.util.ExcelUtil;
import net.aicoder.tcom.tools.util.JaxbUtil;
import net.aicoder.tcom.tools.util.AiStringUtil;

public class BookExporter implements IBookExporter {
	private static final Log log = LogFactory.getLog(BookExporter.class);

	private IDataContext dataContext; // 数据上下文
	private Map<String, String> bookProperties; // 参数定义
	private Map<String, IDataOper> sheetDataOpers; // 工作表数据处理定义

	private Map<String, VariableAppoint> bookPropertyVars; // 参数定义的变量
	private Map<String, VariableDefine> variablesMap = new HashMap<String, VariableDefine>(); // 变量结构定义

	private BookDefine bookDefine = null; // 配置文件剖析后的定义

	private Workbook tplWorkbook = null; // 模板工作簿
	private Workbook outWorkbook = null; // 输出工作簿

	private InputStream tplInputStream; // 模板文件输入流
	private OutputStream outputStream; // 输出文件输出流

	public BookExporter() {
		super();
	}

	@Override
	public void doExport(IDataContext dataContext) {
		setDataContext(dataContext);
		doExport();
	}

	@Override
	public void doExport() {
		// 开始时间
		Long begTime = System.currentTimeMillis();
		log.debug("Export Book begin:" + new Date(begTime));
		if (dataContext == null) {
			dataContext = new DefaultDataContext();
		} else {
			dataContext.produceData();
		}

		exportInitialize();
		exportSheets();
		exportTerminate();

		// 结束时间
		Long endTime = System.currentTimeMillis();
		log.debug("Export book end:" + new Date(endTime) + ";Run time:"
				+ (endTime - begTime));
	}

	@SuppressWarnings("unchecked")
	private void exportSheets() {
		Map<String, Object> outData = this.getDataContext().getOutData();
		for (SheetDefine sheetDefine : this.bookDefine.getSheetDefineList()) {
			log.debug("export sheet>> id=" + sheetDefine.getId() + ";name="
					+ sheetDefine.getSheetName());
			IDataOper dataOper = getDataOper(sheetDefine.getId());
			VariableAppoint repeatByVar = sheetDefine.getRepeatByVar();
			if (repeatByVar == null) {
				SheetExporter sheetExporter = new SheetExporter(sheetDefine);
				sheetExporter.setOutWorkbook(outWorkbook);
				sheetExporter.loadTplSheet();
				sheetExporter.setDataContext(dataContext);
				sheetExporter.setDataOper(dataOper);
				sheetExporter.doExport();
			} else {
				Object repeatDataObj = repeatByVar.dataValue(outData); //获取输出数据中需要重复多页的变量值
				if (repeatDataObj == null) {
					log.debug("repeatBy>> var=" + sheetDefine.getRepeatBy()
							+ ";repeatDataObj=" + repeatDataObj);
					SheetExporter sheetExporter = new SheetExporter(sheetDefine);
					sheetExporter.setOutWorkbook(outWorkbook);
					sheetExporter.loadTplSheet();
					sheetExporter.setDataContext(dataContext);
					sheetExporter.setDataOper(dataOper);
					sheetExporter.doExport();
				} else if (repeatDataObj instanceof List) { //依据List数据生成多页
					List<Object> repeatDataList = (List<Object>) repeatDataObj;
					log.debug("repeatBy>> var=" + sheetDefine.getRepeatBy()
							+ ";repeatDataObj is List, size="
							+ repeatDataList.size());
					exportRepeatSheets(sheetDefine, repeatDataList);
				} else {
					log.debug("repeatBy>> var=" + sheetDefine.getRepeatBy()
							+ ";repeatDataObj=" + repeatDataObj);
					SheetExporter sheetExporter = new SheetExporter(sheetDefine);
					sheetExporter.setOutWorkbook(outWorkbook);
					sheetExporter.loadTplSheet();
					sheetExporter.setDataContext(dataContext);
					sheetExporter.setRepeatData(repeatDataObj);
					sheetExporter.setDataOper(dataOper);
					sheetExporter.doExport();
				}
			}
		}
	}

	private void exportRepeatSheets(SheetDefine sheetDefine,
			List<Object> repeatDataList) {
		IDataOper dataOper = getDataOper(sheetDefine.getId());
		SheetExporter sheetExporter = new SheetExporter(sheetDefine);
		sheetExporter.setOutWorkbook(outWorkbook);
		sheetExporter.loadTplSheet();
		
		for (int index = 0; index < repeatDataList.size(); index++) {
			Object oneRepeatData = repeatDataList.get(index);
			log.debug("export repeat sheet>> index=" + index + ";vlue="
					+ oneRepeatData);
			sheetExporter.setDataContext(dataContext);
			sheetExporter.setDataOper(dataOper);
			sheetExporter.setRepeatData(oneRepeatData); //List中单个元素数据
			sheetExporter.setRepeatIndex(index); //List数据对应的序号
			sheetExporter.doExport();
		}
	}

	private IDataOper getDataOper(String defineId) {
		IDataOper dataOper = null;
		if (sheetDataOpers == null) {
			return dataOper;
		} else {
			dataOper = sheetDataOpers.get(defineId);
		}
		return dataOper;
	}

	/**
	 * 导出前置准备:<br>
	 * 1. 加载Excel导出配置, 创建bookDefine<br>
	 * 2. 如设定模板文件：Open Template Workbook<br>
	 * 3. Open output Workbook<br>
	 */
	private void exportInitialize() {
		loadBookDefine();
		openTplWorkbook();
		openOutWorkbook();
	}

	private void exportTerminate() {
		finishWorkbook();
	}

	private void openTplWorkbook() {
		String pathFullName = "";
		String fileName = getTplFileName();
		String fileFullName;

		if (AiStringUtil.isEmpty(fileName)) {
			return;
		}

		if (AiStringUtil.isEmpty(getTplPath())) {
			pathFullName = PoiConfigHelper.getPoiConfig().defaultTplPath(
					getTplShortPath());
		} else {
			pathFullName = getTplPath();
		}

		fileFullName = pathFullName + "/" + fileName;
		try {
			tplInputStream = new FileInputStream(fileFullName);

			String filePostfix = AiStringUtil.filePostfix(fileName);
			tplWorkbook = ExcelUtil.openWorkbook(tplInputStream, filePostfix);
			log.debug("Open template excel:" + fileFullName);
		} catch (FileNotFoundException e) {
			log.error(e.toString());
		}
	}

	private void openOutWorkbook() {
		String pathFullName = "";
		String fileName = getOutputFileName();
		String fileFullName;

		if (AiStringUtil.isEmpty(fileName)) {
			return;
		}

		if (AiStringUtil.isEmpty(getOutputPath())) {
			pathFullName = PoiConfigHelper.getPoiConfig().defaultOutputPath(
					getOutputShortPath());
		} else {
			pathFullName = getOutputPath();
		}

		fileFullName = pathFullName + "/" + fileName;
		try {
			outputStream = new FileOutputStream(fileFullName);
			log.debug("Open output file:" + fileFullName);

			if (tplWorkbook == null) {
				String filePostfix = AiStringUtil.filePostfix(fileName);
				outWorkbook = ExcelUtil.openWorkbook(filePostfix);
				log.debug("New out Workbook:" + fileName);
			} else {
				outWorkbook = tplWorkbook;
				log.debug("outWorkbook = tplWorkbook");
			}
		} catch (FileNotFoundException e) {
			log.error(e.toString());
		}
	}

	private void finishWorkbook() {
		if (tplInputStream != null) {
			try {
				tplInputStream.close();
			} catch (IOException e) {
				// e.printStackTrace();
				log.error(e.toString());
			}
		}

		try {
			outWorkbook.write(outputStream);
		} catch (IOException e) {
			// e.printStackTrace();
			log.error(e.toString());
		}

		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				// e.printStackTrace();
				log.error(e.toString());
			}
		}
	}

	private void loadBookDefine() {
		String defineFullPath;
		if (AiStringUtil.isEmpty(getDefinePath())) {
			defineFullPath = PoiConfigHelper.getPoiConfig().defaultTplPath(
					getDefineShortPath());
		} else {
			defineFullPath = getDefinePath();
		}

		String fileFullName = defineFullPath + "/" + getDefineFileName();
		log.debug("BookDefine file name=[" + fileFullName + "]");

		File file = new File(fileFullName);
		bookDefine = JaxbUtil.convertToJavaBean(BookDefine.class, file);
		dumpBookDefine();
		bookDefine.resetDefine(this.variablesMap);

		log.debug("loadBookDefine succeed!!!");
	}

	@Override
	public void dumpBookDefine() {
//		String bookDefineXml = "";
//		bookDefineXml = JaxbUtil.convertToXml(bookDefine);
		log.debug("----Begine Dump Book Define-----");
		log.debug("\n" + JaxbUtil.convertToXml(bookDefine) + "\n");
		log.debug("----End Dump Book Define-----");
	}

	// Property Getter/Setter
	@Override
	public IDataContext getDataContext() {
		return dataContext;
	}

	@Override
	public void setDataContext(IDataContext dataContext) {
		this.dataContext = dataContext;
	}

	@Override
	public Map<String, IDataOper> getSheetDataOpers() {
		return sheetDataOpers;
	}

	@Override
	public void setSheetDataOpers(Map<String, IDataOper> sheetDataOpers) {
		this.sheetDataOpers = sheetDataOpers;
	}

	@Override
	public Map<String, String> getBookProperties() {
		return bookProperties;
	}

	@Override
	public void setBookProperties(Map<String, String> bookExporterProps) {
		this.bookProperties = bookExporterProps;
		this.bookPropertyVars = new HashMap<String, VariableAppoint>();
		for (Map.Entry<String, String> entry : bookExporterProps.entrySet()) {
			String key = entry.getKey();
			String configStr = entry.getValue();

			VariableAppoint varAppoint = null;
			if (!AiStringUtil.isEmpty(configStr)) {
				varAppoint = new VariableAppoint(variablesMap);
				varAppoint.parserVariable(configStr);
				log.debug("setBookProps>> key=" + key + "; varAppoint="
						+ varAppoint.dumpStr());
			}

			this.bookPropertyVars.put(key, varAppoint);
		}
	}

	@Override
	public String getBookProperty(String propertyName) {
		String propertyValue = "";
		if (this.bookPropertyVars.containsKey(propertyName)) {
			VariableAppoint propertyDefine = this.bookPropertyVars
					.get(propertyName);
			propertyValue = propertyDefine.dataString(dataContext.getOutData())
					.toString();
		}
		return propertyValue;
	}

	@Override
	public void setBookProperty(String key, String value) {
		if (this.bookProperties == null) {
			this.bookProperties = new HashMap<String, String>();
		}
		if (this.bookPropertyVars == null) {
			this.bookPropertyVars = new HashMap<String, VariableAppoint>();
		}
		this.bookProperties.put(key, value);
		VariableAppoint variableAppoint = new VariableAppoint(this.variablesMap);
		variableAppoint.setConfigStr(value);
		this.bookPropertyVars.put(key, variableAppoint);
	}

	@Override
	public String getDefinePath() {
		return getBookProperty(IBookExporter.DEFINE_PATH);
	}

	@Override
	public void setDefinePath(String value) {
		setBookProperty(IBookExporter.DEFINE_PATH, value);
	}

	@Override
	public String getDefineShortPath() {
		return getBookProperty(IBookExporter.DEFINE_SHORT_PATH);
	}

	@Override
	public void setDefineShortPath(String value) {
		setBookProperty(IBookExporter.DEFINE_SHORT_PATH, value);
	}

	@Override
	public String getDefineFileName() {
		return getBookProperty(IBookExporter.DEFINE_FILE_NAME);
	}

	@Override
	public void setDefineFileName(String value) {
		setBookProperty(IBookExporter.DEFINE_FILE_NAME, value);
	}

	@Override
	public String getTplPath() {
		return getBookProperty(IBookExporter.TPL_PATH);
	}

	@Override
	public void setTplPath(String value) {
		setBookProperty(IBookExporter.TPL_PATH, value);
	}

	@Override
	public String getTplShortPath() {
		return getBookProperty(IBookExporter.TPL_SHORT_PATH);
	}

	@Override
	public void setTplShortPath(String value) {
		setBookProperty(IBookExporter.TPL_SHORT_PATH, value);
	}

	@Override
	public String getTplFileName() {
		return getBookProperty(IBookExporter.TPL_FILE_NAME);
	}

	@Override
	public void setTplFileName(String value) {
		setBookProperty(IBookExporter.TPL_FILE_NAME, value);
	}

	@Override
	public String getOutputPath() {
		return getBookProperty(IBookExporter.OUTPUT_PATH);
	}

	@Override
	public void setOutputPath(String value) {
		setBookProperty(IBookExporter.OUTPUT_PATH, value);
	}

	@Override
	public String getOutputShortPath() {
		return getBookProperty(IBookExporter.OUTPUT_SHORT_PATH);
	}

	@Override
	public void setOutputShortPath(String value) {
		setBookProperty(IBookExporter.OUTPUT_SHORT_PATH, value);
	}

	@Override
	public String getOutputFileName() {
		return getBookProperty(IBookExporter.OUTPUT_FILE_NAME);
	}

	@Override
	public void setOutputFileName(String value) {
		setBookProperty(IBookExporter.OUTPUT_FILE_NAME, value);
	}

	@Override
	public Map<String, Object> getOutData() {
		return this.getDataContext().getOutData();
	}

	@Override
	public void setOutData(Map<String, Object> outData) {
		if (this.dataContext == null) {
			this.dataContext = new DefaultDataContext();
		}
		this.dataContext.setOutData(outData);
	}

	@Override
	public void putOneOutData(String key, Object value) {
		if (this.dataContext == null) {
			this.dataContext = new DefaultDataContext();
		}
		this.dataContext.putOneOutData(key, value);
	}
}
