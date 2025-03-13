package ca.bc.gov.open.jag.documentutils.adobe.service;

import java.io.StringWriter;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ca.bc.gov.open.jag.documentutils.adobe.AdobeKeys;
import ca.bc.gov.open.jag.documentutils.adobe.models.MergeDoc;
import ca.bc.gov.open.jag.documentutils.exception.DocumentParserException;
import ca.bc.gov.open.jag.documentutils.exception.MergeException;


/**
 * A set of Adobe DDX utils.
 *
 * @author shaunmillargov
 */
@Service
public class DDXServiceImpl implements DDXService {

    private final static Logger logger = LoggerFactory.getLogger(DDXServiceImpl.class);

    /**
     * Creates a Merge DDX document for Assembler using an org.w3c.dom.Document object
     *
     * @return
     * @throws Exception
     */
    @Override
    public String createMergeDDX(LinkedList<MergeDoc> pageList, boolean addToC)  {
    	
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
        // Create a new Document object
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {

            logger.error("Error Creating document Builder", e);
            throw new DocumentParserException("Error Creating document Builder", e);
        }
        Document document = documentBuilder.newDocument();

        // Create the root element and append it to the XML DOM
        Element root = (Element) document.createElement(AdobeKeys.DDX_ELEMENT_DDX);
        root.setAttribute(AdobeKeys.DDX_NAMESPACE_ATTRIBUTE, AdobeKeys.DDX_NAMESPACE);
        document.appendChild(root);

        // Create the output element
        Element PDFs = (Element) document.createElement(AdobeKeys.DDX_ELEMENT_PDF);
        PDFs.setAttribute(AdobeKeys.DDX_OUTPUT_ATTRIBUTE, AdobeKeys.DDX_OUTPUT_NAME);
        root.appendChild(PDFs);

        // Add ToC?
        if (addToC) {
            logger.info("Adding table of contents...");
            Element ToC = (Element) document.createElement("TableOfContents");
            ToC.setAttribute("bookmarkTitle", "Table Of Contents");
            PDFs.appendChild(ToC);
        }

        // Add each pageId element to the DDX
        for( MergeDoc element: pageList ) {
        	
            Element PDF = (Element) document.createElement(AdobeKeys.DDX_ELEMENT_PDF);
            PDF.setAttribute(AdobeKeys.DDX_SOURCE_ATTRIBUTE, element.getId());
            
            // from JustinCJB-1684: If there's a title, add a bookmark. 
            if (null != element.getTitle()) { 
            	PDF.setAttribute("bookmarkTitle", element.getTitle());
            	// if ToC is indicated, add the bookmark to the ToC 
            	if (addToC) {
            		PDF.setAttribute("includeInTOC", "true");
            	}
            }
            
            PDFs.appendChild(PDF);
        }

        return convertDocumentToString(document);

    }
    
    /**
     * 
     * Converts an org.w3.dom.Document to String. 
     * 
     * @param doc
     * @return
     */
    public static String convertDocumentToString(Document doc) {
    	
        try {
            // Create a Transformer instance
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Prepare the DOMSource and StreamResult
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);

            // Transform the document to a String
            transformer.transform(domSource, streamResult);

            return writer.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new MergeException("Error converting a DDX Document object to a string.", e);
        }
    }
}
