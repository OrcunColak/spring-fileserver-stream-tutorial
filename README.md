# Serving Static Resources

See
https://naveen-metta.medium.com/mastering-file-uploads-and-downloads-with-spring-boot-e409d27daf0a

The general structure of code is as below

```
@Controller
public class FileDownloadController {

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        // Logic to retrieve the file from the storage location
        Resource fileResource = // Get resource representing the file
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
}
```

# Generating Dynamic File Downloads

See
https://naveen-metta.medium.com/mastering-file-uploads-and-downloads-with-spring-boot-e409d27daf0a

The general structure of code is as below

```
@Controller
public class DynamicFileDownloadController {

    @GetMapping("/download-report")
    public ResponseEntity<byte[]> downloadReport() {
        // Logic to generate the report dynamically
        byte[] reportBytes = // Generate report bytes
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.pdf\"")
                .body(reportBytes);
    }
}
```

# Download CSV

InputStreamResource is from  
https://medium.com/@souravdas08/download-large-files-over-rest-http-api-aa6a00a050cf

# Download Excel

The original idea is from  
https://medium.com/@jirapong21/spring-boot-generate-excel-with-react-fetch-blob-814938510b23

# Download zip

The original idea is from  
https://danielangel22.medium.com/how-to-generate-massive-pdfs-java-spring-a3f2b136ab9c
