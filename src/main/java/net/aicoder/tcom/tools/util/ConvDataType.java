package net.aicoder.tcom.tools.util;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Stone
 * @version 1.0
 */
public class ConvDataType {
   	//Java的基本类型有八种：int, double, float, long, short, boolean, byte, char, void
    public static Long toLong(Object in) throws Exception {
        if (in == null){
        	long longRtn = 0;
        	return longRtn;
        }else if(in instanceof Long) {
            return (Long) in;
        }else if(in instanceof Double){
        	Double douIn = (Double)in;
        	return douIn.longValue();
        }
        
        Long rtn = null;
        String sIn = in.toString();
        if (sIn.trim().equals("")) {
            rtn = new Long(0);
        } else {
            rtn = new Long(sIn);
        }
        return rtn;
    }

    public static Integer toInteger(Object in) throws Exception {
        if (in == null){
        	int intRtn = 0;
        	return intRtn;
        }else if(in instanceof Integer) {
            return (Integer) in;
        }else if(in instanceof Double){
        	Double douIn = (Double)in;
        	return douIn.intValue();
        }
        
        Integer rtn = null;
        String sIn = in.toString();
        if (sIn.trim().equals("")) {
            rtn = new Integer(0);
        } else {
            rtn = new Integer(sIn);
        }
        return rtn;
    }

    public static BigDecimal toBigDecimal(Object in) throws Exception {
        if (in == null){
        	BigDecimal bigRtn = new BigDecimal(0);
        	return bigRtn;
        }else if(in instanceof BigDecimal) {
            return (BigDecimal) in;
        }
        BigDecimal rtn = null;
        String sIn = in.toString();
        if (sIn.trim().equals("")) {
            rtn = new BigDecimal(0);
        } else {
            rtn = new BigDecimal(sIn);
        }
        return rtn;
    }

    public static Date toDate(Object in) throws Exception {
        if (in == null || in instanceof Date) {
            return (Date) in;
        }
        Date rtn = null;
        String sIn = in.toString();
        if (!sIn.trim().equals("")) {
            rtn = DateUtil.toDate(sIn);
        }
        return rtn;
    }

    public static Boolean toBoolean(Object in) throws Exception {
        if (in == null || in instanceof Boolean) {
            return (Boolean) in;
        }
        Boolean rtn = null;
        String sIn = in.toString();
        if (!sIn.trim().equals("")) {
            rtn = new Boolean(sIn);
        }
        return rtn;
    }

    public static Double toDouble(Object in) throws Exception {
    	if (in == null){
    		double douRtn = 0.0;
        	return douRtn;
        }else if(in instanceof Double) {
            return (Double)in;
        }
        Double rtn=null;
        String sIn=in.toString();
        if (!sIn.trim().equals("")) {
            rtn = new Double(sIn);
        }
        return rtn;

    }

	@SuppressWarnings("rawtypes")
	public static Object toOtherType(Object in, Class toType) throws Exception {
		/**
		 * if (in == null || in.getClass().equals(toType)) { return in; }
		 **/
		if (in != null && in.getClass().equals(toType)) {
			return in;
		}

		if (toType.equals(Integer.class) || toType.equals(int.class)) {
			return toInteger(in);
		} else if (toType.equals(Long.class) || toType.equals(long.class)) {
			return toLong(in);
		} else if (toType.equals(BigDecimal.class)) {
			return toBigDecimal(in);
		} else if (toType.equals(Date.class)) {
			return toDate(in);
		} else if (toType.equals(Boolean.class) || toType.equals(boolean.class)) {
			return toBoolean(in);
		} else if (toType.equals(Double.class)) {
			return toDouble(in);
		} else if (java.util.Collection.class.isAssignableFrom(toType)
				&& in instanceof java.util.Collection) {
			return in;
		} else {
			if(in == null){
				return null;
			}else{
				return in.toString();
			}
		}
	}
}
