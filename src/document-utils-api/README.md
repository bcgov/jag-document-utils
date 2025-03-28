## Document Utils Merge Service Quick Start

A RESTful Document Merge Service.  
Uses Adobe Experience Manager server to merge PDF documents via SOAP (MTOM).   

## Configuration

| Name | type | Required | Description |
| --- | --- | --- | --- |
| SERVER_PORT | integer | false | Default is 8080 |
| DOCMERGE_AEM_ENDPOINT | string | true | AEM server endpoint |
| DOCMERGE_AEM_USER | string | true | AEM server user |
| DOCMERGE_AEM_PASSWORD | string | true | AEM server password |
| DOCMERGE_CXF_LOGGING | bool | false | Enables CXF logging for req/resp troubleshooting |
| KC_AUTH_RESOURCE_ID | string | true | Keycloak resource ID (clientID) |
| KC_AUTH_SERVER_URL| string | true | Keycloak instance endpoint |

## API Endpoints

__Basic Merge__  
POST: http://localhost:8080/docmerge/merge

__Headers__  
X-Correlation-ID: correlation Id  
X-Client-ID:  client name  

## AEM engine
AEM version compatibility: v6.5.9

## Request Example
```json
{
   "options":{
      "forcePDFAOnLoad": "true",  
      "createToC": "true",
	  "forceEvenPageCount": "false"
   },
   "documents":[
      {
         "index": 1,
         "title": "The Quick Brown Fox" 
         "data":"SGVsbG9Xb3JsZA=="
      },
      {
         "index": 2,
         "title": "Jumped over the lazy fence",
         "data":"SGVsbG9Xb3JsZA=="
      }
   ]
}

```  

## Request Attributes

|| Name | type | Required | Description |
|| --- | --- | --- | --- |
|options| | | | |
|| forcePDFAOnLoad | String bool | false | Default is 8080 |
|| createToC | String bool | false | Create ToC entry from title |
|| forceEvenPageCount | String bool | false | Force even page number in output |
|documents| | | | |
|| index | number | true | Specifies output order index of page |
|| title | string | false | Specifies title of content when toc option used |
|| data | string | true | Base64 PDF document |


## Build and run locally (after enviro variables set)

```
mvn clean  
mvn generate-sources
mvn spring-boot:run
```
  