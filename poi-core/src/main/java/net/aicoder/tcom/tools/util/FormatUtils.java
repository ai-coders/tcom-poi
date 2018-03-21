package net.aicoder.tcom.tools.util;

import java.util.Calendar;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import org.apache.oro.text.perl.Perl5Util;
//import org.apache.oro.text.perl.*;
import java.text.SimpleDateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;

public final class FormatUtils {
    public static final String YYMM = "yymm";
    public static final String YYYYMM = "yyyymm";
    public static final String YYMMDD = "yymmdd";
    public static final String YYYYMMDD = "yyyymmdd";
    
	private static final Log log = LogFactory.getLog(FormatUtils.class);
    
    /* synthetic field */

    private FormatUtils() {
        //log = Logger.getLogger(this.getClass());
    }

    public static boolean checkDateSys(String inputdate) {
        int year = 0;
        int month = 0;
        int date = 0;
        if (inputdate == null) {
            return false;
        }
        if (inputdate.equals("")) {
            return false;
        }
        try {
            year = Integer.parseInt(inputdate.substring(0, 2));
            month = Integer.parseInt(inputdate.substring(2, 4));
            if (6 == inputdate.length()) {
                date = Integer.parseInt(inputdate.substring(4, 6));
            } else {
                date = 1;
            }
        } catch (NumberFormatException e) {
            log.debug("Error: " + e.toString());
            throw e;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.set(year, month, date);
        @SuppressWarnings("unused")
		java.util.Date checkdate; // maybe check something
        try {
            checkdate = calendar.getTime();
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public static String addComma(String input) {
        try {
            if (input == null || input.length() == 0) {
                throw new Exception("invalid argument[" + input + "]");
            }
            String unsigned = null;
            boolean inputisnegative = false;
            if (input.startsWith("-")) {
                unsigned = input.substring(1);
                inputisnegative = true;
            } else {
                unsigned = input;
            }
            StringTokenizer st = new StringTokenizer(unsigned, ".");
            int counttokens = st.countTokens();
            if (counttokens != 1 && counttokens != 2) {
                throw new Exception("illegal argument[" + input + "]");
            }
            String seisu = st.nextToken();
            int length = seisu.length();
            int commacount = (length - 1) / 3;
            StringBuffer buf = new StringBuffer();
            buf.append(seisu);
            for (int counts = 0; counts < commacount; counts++) {
                buf.insert(seisu.length() - (counts + 1) * 3, ",");
            }

            if (inputisnegative) {
                buf.insert(0, "-");
            }
            if (counttokens == 2) {
                String syosu = st.nextToken();
                buf.append(".");
                buf.append(syosu);
            }
            return buf.toString();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public static String unFormatMoneySys(String inputmoney) {
        String unformatmoney = null;
        if (inputmoney == null || "".equals(inputmoney)) {
            return inputmoney;
        } else {
            unformatmoney = inputmoney.replaceAll(",", "");
            return unformatmoney;
        }
    }
/*
    public static String formatCancel(String input) {
        String returnstr = null;
        Perl5Util util = new Perl5Util();
        if (util.match("/,/", input)) {
            returnstr = unFormatMoneySys(input);
        } else
        if (util.match("/\\//", input)) {
            returnstr = unformatDate(input);
        } else {
            return input;
        }
        return returnstr;
    }
*/
    public static String getDefaultNumberValue(String format) {
        try {
            String decimalPart;
            if (format == null) {
                throw new Exception("illegal argument[" + format + "]");
            }
            int periodIndex = format.lastIndexOf(46);
            if (periodIndex == -1) {
                return "0";
            }
            decimalPart = format.substring(periodIndex + 1);
            StringBuffer buf;
            int decimal = Integer.parseInt(decimalPart);
            buf = new StringBuffer();
            buf.append("0");
            if (decimal > 0) {
                buf.append(".");
                for (int i = 0; i < decimal; i++) {
                    buf.append("0");
                }

            }
            return buf.toString();
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
    }

    public static String formatNumber(String input, String fmt) {
        try {
            int fmtnumbers[] = divideFmt(fmt);
            @SuppressWarnings("unused")
			double testdouble; // maybe check something
            try {
                testdouble = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                throw e;
            }
            String unsigned = "";
            boolean inputisnegative = false;
            if (input.startsWith("-")) {
                unsigned = input.substring(1);
                inputisnegative = true;
            } else {
                unsigned = input;
            }
            @SuppressWarnings("unused")
			String number[] = new String[2]; // maybe check something
            StringTokenizer numtoken = new StringTokenizer(unsigned, ".");
            int counttokens = numtoken.countTokens();
            if (counttokens != 1 && counttokens != 2) {
                throw new Exception("counttokens != 1 && counttokens != 2");
            }
            String seisu = numtoken.nextToken();
            if (seisu.length() > fmtnumbers[0]) {
                throw new Exception("seisu.length() > fmtnumbers[0]");
            }
            String syousu = "";
            if (counttokens == 2) {
                syousu = numtoken.nextToken();
                if (syousu.length() > fmtnumbers[1]) {
                    throw new Exception("syousu.length() > fmtnumbers[1]");
                }
            }
            StringBuffer buf = new StringBuffer();
            if (inputisnegative) {
                buf.append("-");
            }
            buf.append(seisu);
            if (fmtnumbers[1] != 0) {
                buf.append(".");
            }
            buf.append(syousu);
            for (int i = 0; i < fmtnumbers[1] - syousu.length(); i++) {
                buf.append("0");
            }

            return buf.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("unused")
	private static String createExceptionMessage(String input, String fmt) {
        StringBuffer buf = new StringBuffer();
        buf.append("illegal argument. input[");
        buf.append(input);
        buf.append("], fmt[");
        buf.append(fmt);
        buf.append("]");
        return buf.toString();
    }

    private static int[] divideFmt(String fmt) {
        try {
            int ret[];
            ret = new int[2];
            if (fmt == null) {
                throw new Exception("illegal argument fmt[" + fmt + "]");
            }
            StringTokenizer st = new StringTokenizer(fmt, ".");
            int tokensCount = st.countTokens();
            if (tokensCount == 1) {
                ret[0] = Integer.parseInt(st.nextToken());
                ret[1] = 0;
            } else
            if (tokensCount == 2) {
                ret[0] = Integer.parseInt(st.nextToken());
                ret[1] = Integer.parseInt(st.nextToken());
            } else {
                throw new Exception("illegal argument fmt[" + fmt + "]");
            }
            return ret;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public static String formatDate(java.util.Date date, String datetype) {
        String value = "";
        SimpleDateFormat sdf = null;
        if (datetype != null) {
            if (datetype.equals("dateyymm")) {
                sdf = new SimpleDateFormat("yyMM");
            } else if (datetype.equals("dateyymmdd")) {
                sdf = new SimpleDateFormat("yyMMdd");
            } else if (datetype.equals("dateyyyymmdd")) {
                sdf = new SimpleDateFormat("yyyyMMdd");
            } else if (datetype.equals("dateyyyymm")) {
                sdf = new SimpleDateFormat("yyyyMM");
            } else if (datetype.equals("datemmdd")) {
                sdf = new SimpleDateFormat("MMdd");
            } else if (datetype.equals("timehhmmss")) {
                sdf = new SimpleDateFormat("HHmmss");
            } else if (datetype.equals("timehhmm")) {
                sdf = new SimpleDateFormat("HHmm");
            }
            if (sdf != null) {
                value = sdf.format(date, new StringBuffer(),
                                   new FieldPosition(0)).toString();
            }
        }
        return value;
    }

    @SuppressWarnings("unused")
	private static String formatDateYYYYMMDDtoYYMMDD(String value) {
        try {
            if (value == null || "".equalsIgnoreCase(value)) {
                return value;
            }
/** mark by stone.shi        
            Perl5Util util = new Perl5Util();
            if (util.match("/\\//", value)) {
                return value;
            }
**/
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.error("Format error : " + value);
                throw new Exception("Format error : " + value);
            }
            if (value.length() != 8) {
                return value;
            } else {
                return value.substring(2, 8);
            }
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
    }

    @SuppressWarnings("unused")
	private static String formatDateYYYYMMtoYYMM(String value) {
        try {
            if (value == null || "".equalsIgnoreCase(value)) {
                return value;
            }
            
/** mark by stone.shi        
            Perl5Util util = new Perl5Util();
            if (util.match("/\\//", value)) {
                return value;
            }
**/
            
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.error("Format error : " + value);
                throw new Exception("Format error : " + value);
            }
            if (value.length() != 6) {
                return value;
            } else {
                return value.substring(2, 6);
            }
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
    }

    public static String formatDateMMDD(String inputdate) {
        try {
            if (inputdate == null || "".equalsIgnoreCase(inputdate)) {
                return inputdate;
            }
            checkInputDate(inputdate);
            if (inputdate.length() != 4) {
                throw new Exception("the argument [" + inputdate +
                                    "] is illegal.");
            } else {
                StringBuffer buf = new StringBuffer();
                buf.append(inputdate.substring(0, 2));
                buf.append("/");
                buf.append(inputdate.substring(2));
                return buf.toString();
            }
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
    }

    public static String formatDateYYYYMMDD(String inputdate) {
        if (inputdate == null || "".equals(inputdate)) {
            return inputdate;
        } else {
            String date = changeDateLengthYYYYMMDD(inputdate, "yyyymmdd");
            StringBuffer buf = new StringBuffer();
            buf.append(date.substring(0, 4));
            buf.append("/");
            buf.append(date.substring(4, 6));
            buf.append("/");
            buf.append(date.substring(6));
            return buf.toString();
        }
    }

    public static String formatDateYYMMDD(String inputdate) {
        if (inputdate == null || "".equals(inputdate)) {
            return inputdate;
        } else {
            String date = changeDateLengthYYYYMMDD(inputdate, "yymmdd");
            StringBuffer buf = new StringBuffer();
            buf.append(date.substring(0, 2));
            buf.append("/");
            buf.append(date.substring(2, 4));
            buf.append("/");
            buf.append(date.substring(4));
            return buf.toString();
        }
    }

    public static String formatDateYYYYMM(String inputdate) {
        if (inputdate == null || "".equals(inputdate)) {
            return inputdate;
        } else {
            String date = changeDateLengthYYYYMM(inputdate, "yyyymm");
            StringBuffer buf = new StringBuffer();
            buf.append(date.substring(0, 4));
            buf.append("/");
            buf.append(date.substring(4));
            return buf.toString();
        }
    }

    public static String formatDateYYMM(String inputdate) {
        if (inputdate == null || "".equals(inputdate)) {
            return inputdate;
        } else {
            String date = changeDateLengthYYYYMM(inputdate, "yymm");
            StringBuffer buf = new StringBuffer();
            buf.append(date.substring(0, 2));
            buf.append("/");
            buf.append(date.substring(2));
            return buf.toString();
        }
    }

    private static String changeDateLengthYYYYMMDD(String inputdate,
            String format) {
        try {
            if (inputdate == null || "".equalsIgnoreCase(inputdate)) {
                return inputdate;
            }
            //checkInputDate(inputdate);
            if (inputdate.length() != 8 && inputdate.length() != 10) {
                throw new Exception("the argument [" + inputdate +
                                    "] is illegal.");
            }
            if (inputdate.length() == 8) {
                if (format.equalsIgnoreCase("yyyymmdd")) {
                    return inputdate;
                }
                if (format.equalsIgnoreCase("yymmdd")) {
                    return inputdate.substring(2, 8);
                } else {
                    throw new Exception("the argument [" + format +
                                        "] is illegal.");
                }
            } else {
                boolean isFormatSuccess = false;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.parse(inputdate, new ParsePosition(0));
                    isFormatSuccess = true;
                } catch (Exception ex1) {
                }

                if (!isFormatSuccess) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy/MM/dd");
                        sdf.parse(inputdate, new ParsePosition(0));
                        isFormatSuccess = true;
                    } catch (Exception ex1) {
                    }
                }

                if (!isFormatSuccess) {
                    throw new Exception("inputdate [" + inputdate +
                                        "] not match format [" + format + "]");
                }

                String sInputdate = inputdate.substring(0, 4) +
                                    inputdate.substring(5, 7) +
                                    inputdate.substring(8);

                if (format.equalsIgnoreCase("yyyymmdd")) {
                    return sInputdate;
                }
                if (format.equalsIgnoreCase("yymmdd")) {
                    return sInputdate.substring(2, 8);
                }
            }
        } catch (Exception ex) {
            log.error(ex);
        }
        return null;
    }

    private static String changeDateLengthYYYYMM(String inputdate,
                                                 String format) {
        try {
            if (inputdate == null || "".equalsIgnoreCase(inputdate)) {
                return inputdate;
            }
            checkInputDate(inputdate);
            if (inputdate.length() != 6) {
                throw new Exception("the argument [" + inputdate +
                                    "] is illegal.");
            }
            if (format.equalsIgnoreCase("yyyymm")) {
                return inputdate;
            }
            if (format.equalsIgnoreCase("yymm")) {
                return inputdate.substring(2, 6);
            } else {
                throw new Exception("the argument [" + format + "] is illegal.");
            }
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
    }

    private static void checkInputDate(String inputdate) {
        try {
            if (inputdate != null && !"".equalsIgnoreCase(inputdate)) {
/** mark by stone.shi        
                Perl5Util util = new Perl5Util();
                if (util.match("/\\//", inputdate)) {
                    throw new Exception("the argument [" + inputdate +
                                        "] is illegal.");
                }
**/
                try {
                    Integer.parseInt(inputdate);
                } catch (NumberFormatException e) {
                    throw new Exception("the argument [" + inputdate +
                                        "] is illegal.");
                }
            }
       } catch (Exception ex) {
            log.error(ex);
        }
    }

    public static String unFormatDateSys(String inputdate) {
        String unformatdate = null;
        if (inputdate == null) {
            return inputdate;
        }
        if (inputdate.equals("")) {
            return inputdate;
        }
        if (inputdate.length() != 8 && inputdate.length() != 5) {
            return inputdate;
        } else {
            unformatdate = inputdate.replaceAll("/", "");
            return unformatdate;
        }
    }

    public static String unformatDate(String inputdate) {
        if (inputdate == null || "".equals(inputdate)) {
            return inputdate;
        } else {
            return inputdate.replaceAll("/", "");
        }
    }

    public static String formatTimehhmmss(String inputtime) {
        try {
            if (inputtime == null || "".equals(inputtime)) {
                return inputtime;
            }
            if (inputtime.length() != 6) {
                throw new Exception("the argument [" + inputtime +
                                    "] is illegal.");
            }
            @SuppressWarnings("unused")
			int num;
            try {
                num = Integer.parseInt(inputtime);
            } catch (NumberFormatException e) {
                throw new Exception("the argument [" + inputtime +
                                    "] is illegal.");
            }
/** mark by stone.shi        
            Perl5Util util = new Perl5Util();
            if (util.match("/:/", inputtime)) {
                throw new Exception("the argument [" + inputtime +
                                    "] is illegal.");
            } else 
**/            
            {
                StringBuffer sbuf = new StringBuffer();
                sbuf.append(inputtime.substring(0, 2));
                sbuf.append(":");
                sbuf.append(inputtime.substring(2, 4));
                sbuf.append(":");
                sbuf.append(inputtime.substring(4, 6));
                return sbuf.toString();
            }
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
    }

    public static String formatTimehhmm(String inputtime) {
        try {
            if (inputtime == null || "".equals(inputtime)) {
                return inputtime;
            }
            if (inputtime.length() != 4) {
                throw new Exception("the argument [" + inputtime +
                                    "] is illegal.");
            }
            @SuppressWarnings("unused")
			int num;
            try {
                num = Integer.parseInt(inputtime);
            } catch (NumberFormatException e) {
                throw new Exception("the argument [" + inputtime +
                                    "] is illegal.");
            }
/** mark by stone.shi        
            Perl5Util util = new Perl5Util();
            if (util.match("/:/", inputtime)) {
                throw new Exception("the argument [" + inputtime +
                                    "] is illegal.");
            } else 
**/            
            {
                StringBuffer sbuf = new StringBuffer();
                sbuf.append(inputtime.substring(0, 2));
                sbuf.append(":");
                sbuf.append(inputtime.substring(2, 4));
                return sbuf.toString();
            }
        } catch (Exception ex) {
            log.error(ex);
            return null;
        }
    }
}
