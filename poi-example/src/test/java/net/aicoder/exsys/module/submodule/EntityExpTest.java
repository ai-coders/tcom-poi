package net.aicoder.exsys.module.submodule;

//import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import net.aicoder.exsys.module.TcomPoiExampleApplicationTests;
import net.aicoder.exsys.module.submodule.controller.EntityExpController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TcomPoiExampleApplicationTests.class)
@ContextConfiguration({
	//"classpath:spring-mvc-context.xml",
	"classpath:poi/spring-poi.xml"
	})
//public class EntityExpTest extends AbstractJUnit4SpringContextTests{
public class EntityExpTest{
	private static final Log log = LogFactory.getLog(EntityExpTest.class);
	
	@Autowired
	EntityExpController entityExpController;

	@Test
	public void testExpEntityList() {
		log.debug("TestCase Begin!");
		entityExpController.expEntityList();
		log.debug("TestCase End!");
	}
}
