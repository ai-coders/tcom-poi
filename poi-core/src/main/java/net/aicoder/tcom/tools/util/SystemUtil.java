package net.aicoder.tcom.tools.util;

public class SystemUtil {
	
	public static String getClassPath()
	{
		String path = Thread.currentThread().getContextClassLoader().getResource("./").getPath();
		return path;
	}
	
	public static String getProjectPath()
	{
		String path = System.getProperty("user.dir").replace("\\", "/") + "/";
		return path;
	}

}
