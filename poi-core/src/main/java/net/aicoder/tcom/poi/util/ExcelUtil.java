package net.aicoder.tcom.poi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
//import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.aicoder.tcom.tools.util.AiStringUtil;

public class ExcelUtil {
	private static final Log log = LogFactory.getLog(ExcelUtil.class);
	
	public static Workbook openWorkbook(){
		String filePostfix = null;
		return openWorkbook(null,filePostfix);
	}
	public static Workbook openWorkbook(String filePostfix){
		return openWorkbook(null,filePostfix);
	}
	
	public static Workbook openWorkbook(InputStream inputStream){
		String filePostfix = null;
		return openWorkbook(inputStream,filePostfix);
	}
	
	public static Workbook openWorkbook(InputStream inputStream,String filePostfix){
		Workbook workbook = null;
		
		if(AiStringUtil.isEmpty(filePostfix)){
			filePostfix = "xlsx";
		}

		try {
			if (("xls").equalsIgnoreCase(filePostfix)){
				if(inputStream == null){
					workbook =  new HSSFWorkbook();
				}else{
					workbook =  new HSSFWorkbook(inputStream); 
				}
			}else if(("xlsx").equalsIgnoreCase(filePostfix)){
				if(inputStream == null){
					workbook =  new XSSFWorkbook(); 
				}else{
					workbook =  new XSSFWorkbook(inputStream); 
				}
			}
		} catch (IOException e) {
			log.error(e.toString());
		}
		return workbook;
	}
	
    
    /** 
     * Sheet复制 
     * @param fromSheet 
     * @param toSheet 
     * @param copyValueFlag 
     */  
    public static void copySheet(Workbook wb,Sheet fromSheet, Sheet toSheet,  
            boolean copyValueFlag) {  
        //合并区域处理  
        mergerRegion(fromSheet, toSheet);  
        for (Iterator<Row> rowIt = fromSheet.rowIterator(); rowIt.hasNext();) {  
            Row tmpRow = (Row) rowIt.next();  
            Row newRow = toSheet.createRow(tmpRow.getRowNum());  
            //行复制  
            copyRow(wb,tmpRow,newRow,copyValueFlag);  
        }  
    }
    
    /** 
     * 行复制功能 
     * @param fromRow 
     * @param toRow 
     */  
    public static void copyRow(Workbook wb,Row fromRow,Row toRow,boolean copyValueFlag){
    	toRow.setHeight(fromRow.getHeight());
    	
        for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext();) {  
            Cell tmpCell = cellIt.next(); 
            //Cell newCell = toRow.createCell(tmpCell.getCellNum()); //无tmpCell.getCellNum()方法
            Cell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb,tmpCell, newCell, copyValueFlag);  
        }  
    }
    
    /** 
    * 复制原有sheet的合并单元格到新创建的sheet 
    *  
    * @param sheetCreat 新创建sheet 
    * @param sheet      原有的sheet 
    */  
    public static void mergerRegion(Sheet fromSheet, Sheet toSheet) {
       int sheetMergerCount = fromSheet.getNumMergedRegions();  
       for (int i = 0; i < sheetMergerCount; i++) {  
        //Region mergedRegionAt = fromSheet.getMergedRegionAt(i); 
    	CellRangeAddress mergedRegionAt = fromSheet.getMergedRegion(i);
        toSheet.addMergedRegion(mergedRegionAt);  
       }  
    }
    
    /** 
     * 复制单元格 
     *  
     * @param fromCell 
     * @param toCell 
     * @param copyValueFlag 
     *            true则连同cell的内容一起复制 
     */  
 	public static void copyCell(Workbook wb,Cell fromCell, Cell toCell,  
            boolean copyValueFlag) {
    	//样式
    	CellStyle fromCellStyle = fromCell.getCellStyle();
     	toCell.setCellStyle(fromCellStyle);
    	//copyCellStyle(fromCell,toCell);

        //评论
/**     	
        if (fromCell.getCellComment() != null) {  
            toCell.setCellComment(fromCell.getCellComment());  
        } 
**/
     	
/** POI 3.13
        // 不同数据类型处理  
        int srcCellType = srcCell.getCellType(); 
        distCell.setCellType(srcCellType);  
        
        if (copyValueFlag) {  
            if (srcCellType == Cell.CELL_TYPE_NUMERIC) {  
                if (DateUtil.isCellDateFormatted(srcCell)) {  
                    distCell.setCellValue(srcCell.getDateCellValue());  
                } else {  
                    distCell.setCellValue(srcCell.getNumericCellValue());  
                }  
            } else if (srcCellType == Cell.CELL_TYPE_STRING) {  
                distCell.setCellValue(srcCell.getRichStringCellValue());  
            } else if (srcCellType == Cell.CELL_TYPE_BLANK) {  
                // nothing21  
            } else if (srcCellType == Cell.CELL_TYPE_BOOLEAN) {  
                distCell.setCellValue(srcCell.getBooleanCellValue());  
            } else if (srcCellType == Cell.CELL_TYPE_ERROR) {  
                distCell.setCellErrorValue(srcCell.getErrorCellValue());  
            } else if (srcCellType == Cell.CELL_TYPE_FORMULA) {  
                distCell.setCellFormula(srcCell.getCellFormula());  
            } else { // nothing29  
            }  
        } 
**/
     	// POI 3.13
        int iSrcCellType = fromCell.getCellType();
        CellType srcCellType = CellType.forInt(iSrcCellType);
        toCell.setCellType(iSrcCellType); 

        // POI 3.17
        // 不同数据类型处理  
         
        //CellType srcCellType = fromCell.getCellTypeEnum();
        //toCell.setCellType(srcCellType);  
        if (copyValueFlag) {
        	switch(srcCellType){
        	case _NONE:
        		log.error("Unknown srcCellType!");
        		break;
        	case NUMERIC:
                if (DateUtil.isCellDateFormatted(fromCell)) {  
                    toCell.setCellValue(fromCell.getDateCellValue());  
                } else {  
                    toCell.setCellValue(fromCell.getNumericCellValue());  
                }  
                break;
        	case STRING:
        		toCell.setCellValue(fromCell.getRichStringCellValue());  
        		break;
        	case FORMULA:
        		toCell.setCellFormula(fromCell.getCellFormula());
        		break;
        	case BLANK:
        		//do nothing
        		break;
        	case BOOLEAN:
        		 toCell.setCellValue(fromCell.getBooleanCellValue()); 
        		break;
        	case ERROR:
        		toCell.setCellErrorValue(fromCell.getErrorCellValue());
        		break;
        	}
        }
    }
    
    /** 
     * 复制一个单元格样式到目的单元格样式 
     * @param fromStyle 
     * @param toStyle 
     */  
    //public static void copyCellStyle(CellStyle fromStyle, CellStyle toStyle) {
    public static void copyCellStyle(Cell fromCell, Cell toCell) {
    	CellStyle fromCellStyle = fromCell.getCellStyle();
       	CellStyle toCellStyle = toCell.getCellStyle();
       	
       	toCellStyle.cloneStyleFrom(fromCellStyle);

/**   For POI 3.17 	
    	toStyle.setAlignment(fromStyle.getAlignmentEnum());
    	toStyle.setBorderBottom(fromStyle.getBorderBottomEnum());
    	toStyle.setBorderLeft(fromStyle.getBorderLeftEnum());
    	toStyle.setBorderRight(fromStyle.getBorderRightEnum());
    	toStyle.setBorderTop(fromStyle.getBorderTopEnum());
    	toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
    	toStyle.setDataFormat(fromStyle.getDataFormat());
    	toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
    	toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());
    	toStyle.setFillPattern(fromStyle.getFillPatternEnum());
    	toStyle.setFont(arg0);
    	toStyle.setHidden(fromStyle.getHidden());
    	toStyle.setIndention(fromStyle.getIndention());
    	toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());
    	toStyle.setLocked(fromStyle.getLocked());
    	toStyle.setQuotePrefixed(fromStyle.getQuotePrefixed());
    	toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
    	toStyle.setRotation(fromStyle.getRotation());
    	toStyle.setShrinkToFit(fromStyle.getShrinkToFit());
    	toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
    	toStyle.setVerticalAlignment(fromStyle.getVerticalAlignmentEnum());
    	toStyle.setWrapText(fromStyle.getWrapText());
**/

 /**For POI 3.13
        toStyle.setAlignment(fromStyle.getAlignment()); 
        
        //边框和边框颜色  
        toStyle.setBorderBottom(fromStyle.getBorderBottom());  
        toStyle.setBorderLeft(fromStyle.getBorderLeft());  
        toStyle.setBorderRight(fromStyle.getBorderRight());  
        toStyle.setBorderTop(fromStyle.getBorderTop());  
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());  
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());  
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());  
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());  
          
        //背景和前景  
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());  
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());  
          
        toStyle.setDataFormat(fromStyle.getDataFormat());  
        toStyle.setFillPattern(fromStyle.getFillPattern());  
//      toStyle.setFont(fromStyle.getFont(null));  
        toStyle.setHidden(fromStyle.getHidden());  
        toStyle.setIndention(fromStyle.getIndention());//首行缩进  
        toStyle.setLocked(fromStyle.getLocked());  
        toStyle.setRotation(fromStyle.getRotation());//旋转  
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());  
        toStyle.setWrapText(fromStyle.getWrapText()); 
**/        
    }    
    public static Object readCellFormula(Cell cell){
    	Object cellValue = null;
    	
    	//POI 3.13
        int iCellType = cell.getCellType();
        CellType cellType = CellType.forInt(iCellType);
        
        //POI 3.17
        //CellType cellType = cell.getCellTypeEnum();
        if(cellType == CellType.FORMULA){
        	cellValue = cell.getCellFormula(); 
        }
  	
    	return cellValue;
   }
    
	public static Object readCellValue(Cell cell) {
		boolean bCellValue = true;
		return readCell(cell,bCellValue);
	}

	public static Object readCell(Cell cell) {
		boolean bCellValue = false;
		return readCell(cell,bCellValue);
	}
	
	private static Object readCell(Cell cell, boolean bCellValue) {
		Object cellValue = null;
		// 不同数据类型处理

    	//POI 3.13
        int iCellType = cell.getCellType();
        CellType cellType = CellType.forInt(iCellType);
        
        //POI 3.17
		//CellType cellType = cell.getCellTypeEnum();
		
		switch (cellType) {
    	case _NONE:
    		log.error("Unknown srcCellType!");
    		break;
    	case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				cellValue = cell.getDateCellValue();
			} else {
				cellValue = cell.getNumericCellValue();
			}
			break;
    	case STRING:
	   		if(!bCellValue){
	   			cellValue = cell.getRichStringCellValue();
	   		}else{
	   			cellValue =  String.valueOf(cell.getRichStringCellValue());
	   		}
    		break;
    	case FORMULA:
    		if(!bCellValue){
    			cellValue = cell.getCellFormula();
    		}else{
				try {
					if (DateUtil.isCellDateFormatted(cell)) {
						cellValue = cell.getDateCellValue();
						break;
					} else {
						cellValue = cell.getNumericCellValue();
					}
				} catch (IllegalStateException e) {
					cellValue = String.valueOf(cell.getRichStringCellValue());
				}
			}
			break;
    	case BLANK:
    		cellValue = null;
    		break;
    	case BOOLEAN:
    		cellValue = cell.getBooleanCellValue();
    		break;
    	case ERROR:
			cellValue = cell.getErrorCellValue();
    		break;
		default:
			cellValue = null;
			break;
		}
	
	   	return cellValue;
	}
	
/**
	public static Object readCell(Cell cell) {
		Object cellValue = null;

		// 不同数据类型处理
		CellType cellType = cell.getCellTypeEnum();
		switch (cellType) {
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				cellValue = cell.getDateCellValue();
			} else {
				cellValue = cell.getNumericCellValue();
			}
			break;
		case Cell.CELL_TYPE_STRING:
			cellValue = cell.getRichStringCellValue();
			if (cellValue != null) {
				cellValue = cell.getRichStringCellValue().getString();
			}
			break;
		case Cell.CELL_TYPE_FORMULA:
			cellValue = cell.getCellFormula();
			break;
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cellValue = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_ERROR:
			cellValue = cell.getErrorCellValue();
			break;
		default:
			break;
		}

		return cellValue;
	}
**/
	
}
