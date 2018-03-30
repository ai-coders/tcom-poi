package net.aicoder.exsys.module.submodule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import net.aicoder.exsys.module.submodule.controller.EntityExpController;

@SpringBootApplication
@ImportResource(locations={"classpath:poi/spring-poi.xml"})
public class EntityExpMain {
	private static final Log log = LogFactory.getLog(EntityExpMain.class);
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(EntityExpMain.class, args);
		EntityExpController ontroller = context.getBean(EntityExpController.class);
		log.debug("TestCase Begin!");
		ontroller.expEntityList();
		log.debug("TestCase End!");
	}
}
