package net.aicoder.tcom.poi.excel.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.aicoder.tcom.poi.config.BookDefine;
import net.aicoder.tcom.poi.config.PoiConfigHelper;
import net.aicoder.tcom.poi.config.SheetConfig;
import net.aicoder.tcom.poi.config.SheetDefine;
import net.aicoder.tcom.poi.config.VariableAppoint;
import net.aicoder.tcom.poi.config.VariableDefine;
import net.aicoder.tcom.poi.data.IDataContext;
import net.aicoder.tcom.poi.data.IDataOper;
import net.aicoder.tcom.poi.data.impl.DefaultDataContext;
import net.aicoder.tcom.poi.util.ExcelUtil;
import net.aicoder.tcom.tools.util.JaxbUtil;
import net.aicoder.tcom.tools.util.AiStringUtil;

public class BookImporter implements IBookImporter {
	private static final Log log = LogFactory.getLog(BookImporter.class);

	private IDataContext dataContext; // 数据上下文
	private Map<String, String> bookProperties; // 参数定义
	private Map<String, IDataOper> sheetDataOpers; // 工作表数据处理定义

	private Map<String, VariableAppoint> bookPropertyVars; // 参数定义的变量
	private Map<String, VariableDefine> variablesMap = new HashMap<String, VariableDefine>(); // 变量结构定义

	private BookDefine bookDefine = null; // 配置文件剖析后的定义
	private Workbook impWorkbook = null; // 导入工作簿

	private InputStream impInputStream; // 导入文件输入流

	public BookImporter() {
		super();
	}

	public void doImport() {
		// 开始时间
		Long begTime = System.currentTimeMillis();
		log.debug("Export Book begin:" + new Date(begTime));
		if (dataContext == null) {
			dataContext = new DefaultDataContext();
		} else {
			dataContext.produceData();
		}

		importInitialize();
		importSheets();
		importTerminate();

		// 结束时间
		Long endTime = System.currentTimeMillis();
		log.debug("Export book end:" + new Date(endTime) + ";Run time:"
				+ (endTime - begTime));
	}

	private void importSheets() {
		if (impWorkbook == null) {
			return;
		}
		int numSheets = impWorkbook.getNumberOfSheets();

		for (int sheetIdx = 0; sheetIdx < numSheets; sheetIdx++) {
			Sheet sheet = impWorkbook.getSheetAt(sheetIdx);
			String sheetName = sheet.getSheetName();
			boolean isImportSheet = bookDefine.isImportSheet(sheetName);
			log.debug("sheetName=" + sheetName + "; isImportSheet="
					+ isImportSheet);
			if (isImportSheet == false) {
				continue;
			}

			SheetConfig sheetConfig = new SheetConfig(sheet);
			String defineId = sheetConfig.getSheetDefineId();
			if (!AiStringUtil.isEmpty(defineId)) {
				SheetDefine sheetDefine = bookDefine.getSheetDefine(defineId);
				log.debug("sheetName=" + sheetName + ";defineId=" + defineId
						+ ";sheetDefine=" + sheetDefine.getId());
				
				if (sheetDefine != null) {
					
					IDataOper dataOper = getDataOper(sheetDefine.getId());
					if (dataOper != null) {
						SheetImporter sheetImporter = new SheetImporter(
								sheetDefine, sheetConfig);
						
						sheetImporter.setDataOper(dataOper);
						sheetImporter.setDataContext(dataContext);
						sheetImporter.doImport(sheet);
					}
				}
			}
			
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

	private void importInitialize() {
		loadBookDefine();
		openImpWorkbook();
	}

	private void importTerminate() {
		finishWorkbook();
	}

	private void openImpWorkbook() {
		String pathFullName = "";
		String fileName = getInputFileName();
		String fileFullName;

		if (AiStringUtil.isEmpty(fileName)) {
			return;
		}

		if (AiStringUtil.isEmpty(getInputPath())) {
			pathFullName = PoiConfigHelper.getPoiConfig().defaultOutputPath( //maybe change to InputPath
					getInputShortPath());
		} else {
			pathFullName = getInputPath();
		}

		fileFullName = pathFullName + "/" + fileName;
		try {
			impInputStream = new FileInputStream(fileFullName);

			String filePostfix = AiStringUtil.filePostfix(fileName);
			impWorkbook = ExcelUtil.openWorkbook(impInputStream, filePostfix);
			log.debug("Open import excel:" + fileFullName);
		} catch (FileNotFoundException e) {
			log.error(e.toString());
		}
	}

	private void finishWorkbook() {
		if (impInputStream != null) {
			try {
				impInputStream.close();
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

	public void dumpBookDefine() {
		String bookDefineXml = "";
		bookDefineXml = JaxbUtil.convertToXml(bookDefine);
		log.debug("----Begine Dump Book Define-----");
		log.debug("\n" + bookDefineXml + "\n");
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
		return getBookProperty(IBookImporter.DEFINE_PATH);
	}

	@Override
	public void setDefinePath(String value) {
		setBookProperty(IBookImporter.DEFINE_PATH, value);
	}

	@Override
	public String getDefineShortPath() {
		return getBookProperty(IBookImporter.DEFINE_SHORT_PATH);
	}

	@Override
	public void setDefineShortPath(String value) {
		setBookProperty(IBookImporter.DEFINE_SHORT_PATH, value);
	}

	@Override
	public String getDefineFileName() {
		return getBookProperty(IBookImporter.DEFINE_FILE_NAME);
	}

	@Override
	public void setDefineFileName(String value) {
		setBookProperty(IBookImporter.DEFINE_FILE_NAME, value);
	}

	@Override
	public String getInputPath() {
		return getBookProperty(IBookImporter.INPUT_PATH);
	}

	@Override
	public void setInputPath(String value) {
		setBookProperty(IBookImporter.INPUT_PATH, value);
	}

	@Override
	public String getInputShortPath() {
		return getBookProperty(IBookImporter.INPUT_SHORT_PATH);
	}

	@Override
	public void setInputShortPath(String value) {
		setBookProperty(IBookImporter.INPUT_SHORT_PATH, value);
	}

	@Override
	public String getInputFileName() {
		return getBookProperty(IBookImporter.INPUT_FILE_NAME);
	}

	@Override
	public void setInputFileName(String value) {
		setBookProperty(IBookImporter.INPUT_FILE_NAME, value);
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
