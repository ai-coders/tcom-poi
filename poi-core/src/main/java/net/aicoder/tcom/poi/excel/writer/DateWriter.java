package net.aicoder.tcom.poi.excel.writer;

import java.util.Calendar;
import java.util.Date;

public class DateWriter extends AbstractWriter {
	@Override
	public void writeValue(Object value) {
		Date date = getDate(value);
/**
		String dateStr = null;
		if (getFormat() != null) {
			dateStr = DateUtil.dateToString(date, getFormat());
		} else {
			dateStr = DateUtil.dateToString(date);
		}
**/
		cell.setCellValue(date);
	}

	protected Date getDate(Object value) {
		if (value instanceof Date)
			return (Date) value;
		else if (value instanceof Calendar)
			return ((Calendar) value).getTime();
		else
			throw new IllegalArgumentException("value:[" + value
					+ "] is not date object in DateWriter");
	}
}
