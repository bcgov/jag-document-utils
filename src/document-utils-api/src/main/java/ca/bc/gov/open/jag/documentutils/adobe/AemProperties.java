package ca.bc.gov.open.jag.documentutils.adobe;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aem")
public class AemProperties {

    private String endpoint;
    private String username;
    private String password;
    private boolean cxfLogging; 

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public boolean isCxfLogging() {
		return cxfLogging;
	}

	public void setCxfLogging(boolean cxfLogging) {
		this.cxfLogging = cxfLogging;
	}
}
