## Document Merge Service

A RESTful Document Merge Service. 

## Configuration

| Name | type | Required | Description |
| --- | --- | --- | --- |
| SERVER_PORT | integer | false | Default is 8080 |
| DOCMERGE_AEM_ENDPOINT | string | true | the AEM server endpoint |
| DOCMERGE_AEM_USER | string | true | the AEM server user |
| DOCMERGE_AEM_PASSWORD | string | true | the AEM server password |
| DOCMERGE_CXF_LOGGING | bool | false | Enables CXF logging |

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

| Name | type | Required | Description |
| --- | --- | --- | --- |
| forcePDFAOnLoad | String bool | false | Default is 8080 |
| createToC | String bool | false | Create ToC entry from title |
| forceEvenPageCount | String bool | false | Force even page number in output |

__Note__: data is base64 PDF. 

## Build and run locally 

```
mvn clean  
mvn generate-sources
mvn spring-boot:run
```
  