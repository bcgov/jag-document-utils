package ca.bc.gov.open.jag.documentutils.adobe.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adobe.idp.services.DocConverterService;

import ca.bc.gov.open.jag.documentutils.adobe.AemProperties;

public class AdobeDocConverterServiceClientTest {

    @Mock
    private AemProperties mockProps;

    private AdobeDocConverterServiceClient adobeDocConverterServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockProps.getEndpoint()).thenReturn("http://example.com");
        when(mockProps.isCxfLogging()).thenReturn(false);
        when(mockProps.getUsername()).thenReturn("testUser");
        when(mockProps.getPassword()).thenReturn("testPassword");

        adobeDocConverterServiceClient = new AdobeDocConverterServiceClient(mockProps);
    }

    @Test
    void testGetDocConverterService() {
        DocConverterService docConverterService = adobeDocConverterServiceClient.getDocConverterService();
        assertNotNull(docConverterService, "The DocConverterService should not be null");
    }
}
