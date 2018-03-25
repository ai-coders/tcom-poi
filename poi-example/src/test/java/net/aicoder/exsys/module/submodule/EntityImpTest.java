package net.aicoder.exsys.module.submodule;

//import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import net.aicoder.exsys.module.TcomPoiExampleApplicationTests;
import net.aicoder.exsys.module.submodule.controller.EntityImpController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TcomPoiExampleApplicationTests.class)
@ContextConfiguration({
	//"classpath:spring-mvc-context.xml",
	"classpath:poi/spring-poi.xml"
	})
public class EntityImpTest extends AbstractJUnit4SpringContextTests{
	private static final Log log = LogFactory.getLog(EntityImpTest.class);
	
	@Autowired
	EntityImpController entityImpController = new EntityImpController();

	@Test
	public void testImpEntityList() {
		log.debug("TestCase Begin!");
		entityImpController.impEntityList();
		log.debug("TestCase End!");
	}

}
