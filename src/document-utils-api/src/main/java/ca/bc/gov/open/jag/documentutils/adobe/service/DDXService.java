package ca.bc.gov.open.jag.documentutils.adobe.service;

import java.util.LinkedList;

import ca.bc.gov.open.jag.documentutils.adobe.models.MergeDoc;

public interface DDXService {

    String createMergeDDX(LinkedList<MergeDoc> pageList, boolean addToC);

}
