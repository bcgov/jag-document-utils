package ca.bc.gov.open.jag.documentutils.adobe.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adobe.idp.services.AssemblerService;

import ca.bc.gov.open.jag.documentutils.adobe.AemProperties;

public class AdobeAssemblerServiceClientTest {

    @Mock
    private AemProperties mockProps;

    private AdobeAssemblerServiceClient adobeAssemblerServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockProps.getEndpoint()).thenReturn("http://example.com");
        when(mockProps.isCxfLogging()).thenReturn(false);
        when(mockProps.getUsername()).thenReturn("testUser");
        when(mockProps.getPassword()).thenReturn("testPassword");

        adobeAssemblerServiceClient = new AdobeAssemblerServiceClient(mockProps);
    }

    @Test
    void testGetAssemblerService() {
        AssemblerService assemblerService = adobeAssemblerServiceClient.getAssemblerService();
        assertNotNull(assemblerService, "The AssemblerService should not be null");
    }
}
