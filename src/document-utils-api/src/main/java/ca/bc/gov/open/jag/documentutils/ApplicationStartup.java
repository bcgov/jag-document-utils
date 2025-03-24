package ca.bc.gov.open.jag.documentutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * 
 * Reports application version at startup.
 * 
 */
@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
	
	static final Logger log = LoggerFactory.getLogger(ApplicationStartup.class);
	
	
	@Value("${app.version}")
	private String appVersion;

	/**
	 * This event is executed as late as conceivably possible to indicate that the
	 * application is ready to service requests.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		
		log.info("Document Utils, version {} starting up.", appVersion);
		return;
	}
}