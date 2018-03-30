package net.aicoder.exsys.module.submodule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import net.aicoder.exsys.module.submodule.controller.EntityImpController;

@SpringBootApplication
@ImportResource(locations={"classpath:poi/spring-poi.xml"})
public class EntityImpMain {
	private static final Log log = LogFactory.getLog(EntityImpMain.class);
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(EntityImpMain.class, args);
		EntityImpController ontroller = context.getBean(EntityImpController.class);
		log.debug("TestCase Begin!");
		ontroller.impEntityList();
		log.debug("TestCase End!");
	}
}
