## Document Merge Service

A RESTful Document Merge Service. 

## Configuration

| Name | type | Required | Description |
| --- | --- | --- | --- |
| SERVER_PORT | integer | false | Default is 8080 |
| DOCMERGE_AEM_ENDPOINT | string | true | the AEM server endpoint |
| DOCMERGE_AEM_USER | string | true | the AEM server user |
| DOCMERGE_AEM_PASSWORD | string | true | the AEM server password |


## Swagger-UI endpoint
http://localhost:8082/docmerge/swagger-ui.html

## API Endpoints

Basic Merge
http://localhost:8082/docmerge/merge/{correlationId}

## Building AEM tools with Maven
https://helpx.adobe.com/ca/experience-manager/6-3/sites/developing/using/ht-projects-maven.html
https://helpx.adobe.com/aem-forms/6/aem-livecycle-connector.html#AdobeLiveCycleAssemblerClientbundle

## AEM engine
AEM version : Sarcee, v6.2.0, GM

## Request Example
```json
{
   "options":{
      "forcePDFAOnLoad": "true",  
      "createToC": "true"
   },
   "documents":[
      {
         "id":"optional",
         "docType":"pdf",
         "order":1, 
         "data":"SGVsbG9Xb3JsZA=="
      },
      {
         "id":"optional",
         "docType":"pdf",
         "order":2,
         "data":"SGVsbG9Xb3JsZA=="
      }
   ]
}

```
