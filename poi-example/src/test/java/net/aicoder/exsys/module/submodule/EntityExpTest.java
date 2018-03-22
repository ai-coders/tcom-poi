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
import net.aicoder.exsys.module.submodule.service.IEntityExpService;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	//"classpath:spring-mvc-context.xml",
	"classpath:poi/spring-mvc-poi.xml"
	})
public class EntityExpTest extends AbstractJUnit4SpringContextTests{
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
