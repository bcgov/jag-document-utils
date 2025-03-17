package ca.bc.gov.open.jag.documentutils.adobe;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.bc.gov.open.jag.documentutils.adobe.models.MergeDoc;
import ca.bc.gov.open.jag.documentutils.adobe.service.DDXServiceImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DDXServiceImplTest {

    @InjectMocks
    private DDXServiceImpl ddxService;

    @Mock
    private MergeDoc mergeDoc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMergeDDX_shouldGenerateValidDDXString() {
    	
        // Arrange
        LinkedList<MergeDoc> pageList = new LinkedList<>();
        MergeDoc doc1 = new MergeDoc("data1".getBytes(), "First Title");
        MergeDoc doc2 = new MergeDoc("data2".getBytes(), "Second Title");

        pageList.add(doc1);
        pageList.add(doc2);

        // Act
        String ddxResult = ddxService.createMergeDDX(pageList, true);

        // Assert
        assertNotNull(ddxResult);
        assertTrue(ddxResult.contains("out.pdf"));
        assertTrue(ddxResult.contains("TableOfContents"));
        assertTrue(ddxResult.contains("bookmarkTitle"));
    }

    @Test
    void createMergeDDX_shouldThrowNullPointerForEmptyPageList() {

        // Empty page list
        assertThrows(NullPointerException.class, () -> {
            ddxService.createMergeDDX(null, true);
        });
    }

}
