package net.aicoder.cd.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import net.aicoder.cd.report.controller.PrjWeeklyReportExpController;

@SpringBootApplication
@ImportResource(locations={"classpath:poi/spring-cd-report.xml"})
public class PrjWeeklyReportExpMain {
	private static final Log log = LogFactory.getLog(PrjWeeklyReportExpMain.class);
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(PrjWeeklyReportExpMain.class, args);
		PrjWeeklyReportExpController controller = context.getBean(PrjWeeklyReportExpController.class);
		log.debug("expPrjWeeklyReport Begin!");
		controller.expPrjWeeklyReport();
		log.debug("expPrjWeeklyReport End!");
	}
}
