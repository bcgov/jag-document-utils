package ca.bc.gov.open.jag.documentutils.adobe.service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.adobe.idp.services.DocConverterService;

import ca.bc.gov.open.jag.documentutils.adobe.AemProperties;
import jakarta.xml.ws.BindingProvider;

@Configuration
public class AdobeDocConverterServiceClient {
	
	private AemProperties props; 	
	
	public AdobeDocConverterServiceClient(AemProperties props) {
		this.props = props; 
	}
	
	private final static String AEM_SERVICE_FRAG = "/soap/services/DocConverterService?blob=mtom";
	
	/**
     * Creates a CXF Adobe Doc Converter Service client with MTOM enabled.
     */
	@Bean
    DocConverterService getDocConverterService() {
    	
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(DocConverterService.class);
        factory.setAddress(props.getEndpoint() + AEM_SERVICE_FRAG);
        
        // Add in and out logging interceptors (good for debugging). 
        if (props.isCxfLogging()) {
        	factory.getInInterceptors().add(new LoggingInInterceptor());  
        	factory.getOutInterceptors().add(new LoggingOutInterceptor());
        }

        DocConverterService service = (DocConverterService) factory.create();

        // Configure Authentication
        BindingProvider bp = (BindingProvider) service;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, props.getEndpoint() + AEM_SERVICE_FRAG);
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, props.getUsername());
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, props.getPassword());
        
        // Enable MTOM
        Client client = ClientProxy.getClient(service);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        
        // MTOM requires chunking
        httpClientPolicy.setAllowChunking(true); 
        http.setClient(httpClientPolicy);

        return service;
    }

}
