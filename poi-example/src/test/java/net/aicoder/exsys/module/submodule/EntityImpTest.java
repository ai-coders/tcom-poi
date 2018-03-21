package net.aicoder.exsys.module.submodule;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.aicoder.exsys.module.submodule.controller.EntityExpController;
import net.aicoder.exsys.module.submodule.controller.EntityImpController;
import net.aicoder.exsys.module.submodule.service.IEntityImpService;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:devp/spring-mvc-devp.xml")
@ContextConfiguration({
	"classpath:spring-mvc-context.xml",
	//"classpath:spring-mvc-aop.xml",
	"classpath:hibernate4hib.xml",
	//"classpath:spring-minidao.xml",
	//"classpath:spring-mvc.xml",
	"classpath:poi/spring-mvc-poi.xml"
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
