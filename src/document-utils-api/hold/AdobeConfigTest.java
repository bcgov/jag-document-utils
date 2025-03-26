package ca.bc.gov.open.jag.documentutils.adobe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import ca.bc.gov.open.jag.documentutils.adobe.service.AdobeAssemblerServiceClient;
import ca.bc.gov.open.jag.documentutils.adobe.service.AdobeDocConverterServiceClient;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdobeConfigTest {

//    ApplicationContextRunner context = new ApplicationContextRunner()
//            .withPropertyValues(
//                    "aem.endpoint=localhost",
//                    "aem.username=user",
//                    "aem.password=pwd",
//                    "aem.cxfLogging"
//            )
//            .withUserConfiguration(AdobeAssemblerServiceClient.class);
//    

//    @Test
//    public void testConfiguration() {
//
//        context.run(it -> {
//
//            assertThat(it).hasSingleBean(AdobeAssemblerServiceClient.class);
//            assertThat(it).hasSingleBean(AdobeDocConverterServiceClient.class);
//            assertThat(it).hasSingleBean(DocumentBuilderFactory.class);
//            assertThat(it).hasSingleBean(TransformerFactory.class);
//
//        });
//
//    }

}
