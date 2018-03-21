package net.aicoder.tcom.tools.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.aicoder.tcom.tools.util.BeanUtil;

//@RunWith(Suite.class)
//@SuiteClasses({})
@SuppressWarnings("unused")
public class BeanUtilTest {
	
	@SuppressWarnings("rawtypes")
	@Test
	public void getParameterizedTypeOfListTest(){
		List<String> list = new ArrayList<String>();
		
		Class clazz = BeanUtil.getParameterizedTypeOfList(list);
		String className = clazz.getName();
		
		assertEquals("DateUtil",className);
		className = BeanUtil.getParameterizedTypeNameOfList(list);
		assertEquals("DateUtil",className);
	}

}
