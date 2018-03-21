package net.aicoder.tcom.tools.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AiStringUtil {
	
	/**
	 * 判断是否是空字符串 null和"" 都返回 true
	 * 
	 * @author Stone
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		if (s == null || s.equals("")) {
			return true;
		}else{
			return false;
		}
	}
	

	/**
	 * 自定义的分隔字符串函数 例如: 1,2,3 =>[1,2,3] 3个元素 ,2,3=>[,2,3] 3个元素 ,2,3,=>[,2,3,] 4个元素 ,,,=>[,,,] 4个元素
	 * 
	 * 5.22算法修改，为提高速度不用正则表达式 两个间隔符,,返回""元素
	 * 
	 * @param split
	 *            分割字符，默认为","
	 * @param src
	 *            输入字符串
	 * @return 分隔后的list
	 * @author Robin
	 */
	public static List<String> splitToList(String split, String src) {
		// 默认,
		String sp = ",";
		if (split != null && split.length() == 1) {
			sp = split;
		}
		List<String> r = new ArrayList<String>();
		int lastIndex = -1;
		int index = src.indexOf(sp);
		if (-1 == index && src != null) {
			r.add(src);
			return r;
		}
		while (index >= 0) {
			if (index > lastIndex) {
				r.add(src.substring(lastIndex + 1, index));
			} else {
				r.add("");
			}

			lastIndex = index;
			index = src.indexOf(sp, index + 1);
			if (index == -1) {
				r.add(src.substring(lastIndex + 1, src.length()));
			}
		}
		return r;
	}
	
	/**
	 * 依据左右分隔符，截取中间字符串，如：inStr="${myname.abc}",leftFlag="${",rightFlag="}",输出：myname.abc
	 * @param inStr
	 * @param leftFlag
	 * @param rightFlag
	 * @return
	 */
	public static List<String> splitByFlag(String inStr, String leftFlag, String rightFlag){
		List<String> paramsList = new ArrayList<String>();
		if (AiStringUtil.isEmpty(inStr) 
				|| AiStringUtil.isEmpty(leftFlag) 
				|| AiStringUtil.isEmpty(rightFlag)){
			return paramsList;
		}
		int iStrLen = inStr.length();
		int ileftFlagLen = leftFlag.length();
		int iRightFlagLen = rightFlag.length();
		int iPos = 0;
		int iBeg = 0;
		int iEnd = 0;
		
		String tmpStr;
		String eleStr;
		while(iPos < iStrLen){
			tmpStr = inStr.substring(iPos);
			iBeg = tmpStr.indexOf(leftFlag);
			iEnd = tmpStr.indexOf(rightFlag);
			if(iBeg>=0 && iEnd>iBeg){
				eleStr = tmpStr.substring(iBeg + ileftFlagLen, iEnd);
				paramsList.add(eleStr);
				iPos = iPos + iEnd + iRightFlagLen;
			}else{
				break;
			}
		}
		
		return paramsList;
	}
	
	/**
	 * 截取数字，如,输入：R45，输出：45
	 * @param content
	 * @return
	 */
	public static String getNumbers(String content) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			return matcher.group(0);
		}
		return "";
	}

	/**
	 * 截取非数字,如,输入：R45，输出：R
	 * @param content
	 * @return
	 */
	public static String splitNotNumber(String content) {
		Pattern pattern = Pattern.compile("\\D+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			return matcher.group(0);
		}
		return "";
	}
	
	/**
	 * 获取文件扩展名
	 * @param fileName 文件名
	 * @return
	 */
	public static String filePostfix(String fileName) {
		String postfix;
		
		if(AiStringUtil.isEmpty(fileName)){
			postfix =  "";
		}else{
			int pos = fileName.lastIndexOf(".");
			if (pos > 0) {
				postfix = fileName.substring(pos + 1);
			} else {
				postfix =  "";
			}
		}
		return postfix;
	}
	
	/**
	 * 数据库表格定义转化为类名，驼峰式命名
	 * @param sDbCode，单词间隔以"_"分隔
	 * @return
	 */
	public static String dbCode2Clazz(String sDbCode) {
		String sRtn = "";
		String[] strs = sDbCode.split("_");
		sRtn = "";
		int m = 0;
		for (int length = strs.length; m < length; m++) {
			if (m > 0) {
				String tempStr = strs[m].toLowerCase();
				tempStr = tempStr.substring(0, 1).toUpperCase()
						+ tempStr.substring(1, tempStr.length());
				sRtn = sRtn + tempStr;
			} else {
				sRtn = sRtn + strs[m].toLowerCase();
			}
		}
		return sRtn;
	}
	
	/**
	 * 数据库字段的定义转化为属性名，驼峰式命名且首字母小写
	 * @param sDbCode，单词间隔以"_"分隔
	 * @return
	 */
	public static String dbCode2Property(String sDbCode) {
		String sRtn = "";
		String tempStr = dbCode2Clazz(sDbCode);
		sRtn = tempStr.substring(0, 1).toLowerCase()
				+ tempStr.substring(1, tempStr.length());
		return sRtn;
	}
	
	/**
	 * 获取规范的包名，去掉重复的'.'以及最后字符'.'
	 * @param packageName
	 * @return
	 */
	public static String regulatePackage(String packageName){
		String outPackage;
		if(AiStringUtil.isEmpty(packageName)){
			outPackage = "";
		}else{
			//boolean isPoint = false;
			StringBuffer pkgSB = new StringBuffer();
			char preChar = '.';
			for(char c : packageName.toCharArray()){
				if(c == '.'){
					if(preChar == '.'){
						//Skip
					}else{
						pkgSB.append(c);
					}
				}else{
					if(c == ' '){
						//Skip
					}else{
						pkgSB.append(c);
					}
				}
				preChar = c;
			}
			int indexEnd = pkgSB.length() - 1;
			if(pkgSB.charAt(indexEnd) == '.'){
				pkgSB.deleteCharAt(indexEnd);
			}
			outPackage = pkgSB.toString();
		}
		
		return outPackage;
	}
	

	/**
	 * 检查输入字符串是否为所指定的模式，如：inStr="_TBL_", sameAs="_*_", 则返回为true
	 * @param inStr
	 * @param sameAs
	 * @return
	 */
	public static boolean checkStrSameAs( String inStr, String sameAs ){
		boolean isMatch = false;
		
		if(AiStringUtil.isEmpty(inStr)){
			if(AiStringUtil.isEmpty(sameAs)){
				return true;
			}else{
				return false;
			}
		}
		
		String regex = sameAs.replace("*", ".*");
		regex = "^" + regex + "$";
		
		try {
			Pattern pattern = Pattern.compile(regex);// 表达式
			Matcher matcher = pattern.matcher(inStr);// 要处理的内容
			
			if (matcher.find())// 如果匹配
			{
				isMatch = true;
			} else {// 如果不匹配
				isMatch = false;
			}
		} catch (Exception e) {
		}
		return isMatch;
	}
	
	/**
	 * 检查前缀是否相同，如：inStr="abcdefg",prefix="abc",返回为true
	 * @param inStr
	 * @param prefix
	 * @return
	 */
	public static boolean checkPrefix (String inStr, String prefix){
		boolean isMatch = false;
		
		if(AiStringUtil.isEmpty(inStr)){
			if(AiStringUtil.isEmpty(prefix)){
				return true;
			}else{
				return false;
			}
		}
		if(AiStringUtil.isEmpty(prefix)){
			return true;
		}
		
		int prefixLen = prefix.length();
		if(inStr.length()<prefixLen){
			return false;
		}
		if(prefix.equals(inStr.substring(0,prefixLen))){
			isMatch = true;
		}else{
			isMatch = false;
		}
		
		return isMatch;
	}
	
	/**
	 * 检查后缀是否相同，如：inStr="abcdefg",postfix="fg",返回为true
	 * @param inStr
	 * @param postfix
	 * @return
	 */
	public static boolean checkPostfix (String inStr, String postfix){
		boolean isMatch = false;
		
		if(AiStringUtil.isEmpty(inStr)){
			if(AiStringUtil.isEmpty(postfix)){
				return true;
			}else{
				return false;
			}
		}
		if(AiStringUtil.isEmpty(postfix)){
			return true;
		}
		
		int postfixLen = postfix.length();
		int inStrLen = inStr.length();
		if(inStr.length()<postfixLen){
			return false;
		}
		if(postfix.equals(inStr.substring(inStrLen-postfixLen,inStrLen))){
			isMatch = true;
		}else{
			isMatch = false;
		}
		
		return isMatch;
	}
	
}
