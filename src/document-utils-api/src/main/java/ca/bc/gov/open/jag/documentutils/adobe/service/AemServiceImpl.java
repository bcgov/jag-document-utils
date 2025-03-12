package ca.bc.gov.open.jag.documentutils.adobe.service;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.adobe.idp.services.AssemblerOptionSpec;
import com.adobe.idp.services.AssemblerResult;
import com.adobe.idp.services.AssemblerService;
import com.adobe.idp.services.BLOB;
import com.adobe.idp.services.MyMapOfXsdStringToXsdAnyType;
import com.adobe.idp.services.MyMapOfXsdStringToXsdAnyTypeItem;
import com.adobe.idp.services.PDFAConversionOptionSpec;
import com.adobe.idp.services.PDFAConversionResult;

import ca.bc.gov.open.jag.documentutils.adobe.models.MergeDoc;
import ca.bc.gov.open.jag.documentutils.api.models.DocMergeRequest;
import ca.bc.gov.open.jag.documentutils.api.models.DocMergeResponse;
import ca.bc.gov.open.jag.documentutils.exception.MergeException;
import ca.bc.gov.open.jag.documentutils.utils.MediaTypes;
import ca.bc.gov.open.jag.documentutils.utils.PDFBoxUtilities;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

@Service
//TODO - Does this need to be request scope??
//@Scope("request")
public class AemServiceImpl implements AemService {

	private final Logger logger = LoggerFactory.getLogger(AemServiceImpl.class);

	private final AdobeAssemblerServiceClient assemblerClient;
	private final AdobeDocConverterServiceClient docConverterClient;
	private final DDXService ddxService;
	
	public AemServiceImpl(AdobeAssemblerServiceClient assemblerClient,
			AdobeDocConverterServiceClient docConverterClient,
			DDXService ddxService) {
		this.assemblerClient = assemblerClient;
		this.docConverterClient = docConverterClient;
		this.ddxService = ddxService;
	}

	@Override
	public DocMergeResponse mergePDFDocuments(DocMergeRequest request, String correlationId) {

		DocMergeResponse resp = new DocMergeResponse();
		resp.setMimeType(MediaTypes.APPLICATION_PDF);

		logger.info("Calling mergePDFDocuments...");

		try {

			// Sort the documents based on placement id in the event they are mixed. lowest to highest
			LinkedList<MergeDoc> pageList = convertToLinkedList(request);

			// CJB-1685
			if (request.getOptions().isForceEvenPageCount()) {
				logger.info("Forcing Even Page count");
				FixPageCount(pageList);
			}

			// Use DDXService to Dynamically generate the DDX file.
			BLOB inDDX = new BLOB();
			inDDX.setBinaryData(ddxService.createMergeDDX(pageList, request.getOptions().isCreateToC()).getBytes());
			inDDX.setContentType("text/xml");

			// Create a list of MyMapOfXsdStringToXsdAnyType to send to AEM
			MyMapOfXsdStringToXsdAnyType myMap = new MyMapOfXsdStringToXsdAnyType();
			for (MergeDoc page : pageList) {
				DataHandler dh = createDataHandler(page.getFile());
				BLOB inDoc = new BLOB();
				inDoc.setMTOM(dh);
				MyMapOfXsdStringToXsdAnyTypeItem at = new MyMapOfXsdStringToXsdAnyTypeItem();
				at.setKey(page.getId());
				at.setValue(inDoc);
				myMap.getItem().add(at);
			}

			// Create the spec
			AssemblerOptionSpec assemblerSpec = new AssemblerOptionSpec();
			assemblerSpec.setFailOnError(false);

			// Merge and capture the resulting doc.
			AssemblerService aService = assemblerClient.getAssemblerService();
			AssemblerResult	result = aService.invoke(inDDX, myMap, assemblerSpec);
				
			if (result.getDocuments() != null && result.getDocuments().getItem().size() > 0) {
				BLOB bresult = (BLOB) result.getDocuments().getItem().get(0).getValue();
				DataHandler dh = bresult.getMTOM();
				byte[] mergedPdf = IOUtils.toByteArray(dh.getInputStream());
				resp.setDocument(encodeToBase64(mergedPdf));
			} else {
				throw new Exception("AEM error detected. Likely a problem with one or more of the input PDFs. For "
						+ "a full description of the problem, see " + getAEMErrorFileLocation(result));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			String err = "Error caught at Calling AemServiceImpl.mergePDFDocuments. " + ex.getMessage();
			logger.error(err);
			throw new MergeException(err, ex);
		}

		return resp;

	}

	private DataHandler createDataHandler(byte[] file) {
		DataSource dataSource = new ByteArrayDataSource(file, MediaType.APPLICATION_PDF_VALUE);
	    return new DataHandler(dataSource);
	}

	private void FixPageCount(LinkedList<MergeDoc> pageList) {
		pageList.forEach((element) -> MakeEvenPages(element));
	}

	/**
	 * MakeEvenPages - Scan the page count. If odd number of pages, add another page.
	 * 
	 * @param MergeDoc
	 * @return
	 */
	private void MakeEvenPages(MergeDoc pdfDoc) {

		if (null != pdfDoc.getFile()) {
			int n = PDFBoxUtilities.getPages(pdfDoc.getFile());
			if (n % 2 == 0)
				logger.debug("PDF Document has even number of pages.");
			else {
				logger.debug("PDF Document has odd number of pages. Adding blank page");
				try {
					PDFBoxUtilities.addBlankPage(pdfDoc);
				} catch (IOException e) {
					logger.error(
							"MakeEvenPages: Failure to add a new page to document with an odd number of pages. Detail: "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * Creates a base64 encoded string from a byte array.  
	 * 
	 * @param byteArray
	 * @return
	 */
    public static String encodeToBase64(byte[] byteArray) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(byteArray);
    }

	private LinkedList<MergeDoc> convertToLinkedList(DocMergeRequest request) {
		request.getDocuments()
				.sort(Comparator.comparing(ca.bc.gov.open.jag.documentutils.api.models.Document::getIndex));

		return request.getDocuments().stream().map(doc -> buildMergeDoc(doc, request))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * 
	 * 
	 * 
	 * @param doc
	 * @param request
	 * @return
	 */
	private MergeDoc buildMergeDoc(ca.bc.gov.open.jag.documentutils.api.models.Document doc, DocMergeRequest request) {

		byte[] docBytes = Base64.getDecoder().decode(doc.getData().getBytes());

		if (request.getOptions().isForcePDFAOnLoad() && PDFBoxUtilities.isPDFXfa(docBytes)) {
			logger.info("forcePDFA is on and document, order {}, is XFA. Converting to PDF/A...", doc.getIndex());
			return new MergeDoc(createPDFADocument(docBytes), doc.getTitle());
		}

		logger.info("Loaded page {}", doc.getIndex());
		return new MergeDoc(docBytes, doc.getTitle());
	}
	
	private String getAEMErrorFileLocation(AssemblerResult result) {
		MyMapOfXsdStringToXsdAnyType attribs =  result.getJobLog().getAttributes();
		for (MyMapOfXsdStringToXsdAnyTypeItem element: attribs.getItem()) {
			if (element.getKey().equalsIgnoreCase("file")) {
				return String.valueOf(element.getValue());
			}
		}
		return "Error log location not returned";
	}

	/**
	 * 
	 * Call AEM DocConverterService to convert to PDF/A. 
	 * 
	 * @param inputFile
	 * @return
	 */
	private byte[] createPDFADocument(byte[] inputFile) {
		
		PDFAConversionOptionSpec convOptSpec = new PDFAConversionOptionSpec();
		
		//TODO - set me off for prod. 
		convOptSpec.setLogLevel("FINE");
		
		try {
			
			DataHandler dh = createDataHandler(inputFile);
			BLOB inDoc = new BLOB();
			inDoc.setMTOM(dh);
			
			// Convert to PDF/A
			PDFAConversionResult cServiceResult = docConverterClient.getDocConverterService().toPDFA(inDoc, convOptSpec);
			BLOB pdfADoc = cServiceResult.getPDFADocument();
			
			return IOUtils.toByteArray(pdfADoc.getMTOM().getInputStream());
		
		} catch (Exception ex) {
			logger.error("Error while converting document tp PDF/A", ex);
			throw new MergeException("Error while converting document tp PDF/A", ex);
		}
	}
}
