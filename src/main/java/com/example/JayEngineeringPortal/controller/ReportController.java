package com.example.JayEngineeringPortal.controller;

import com.example.JayEngineeringPortal.dao.ReportService;
import com.example.JayEngineeringPortal.model.Product;
import com.example.JayEngineeringPortal.model.ProductDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;



@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // ✅ Download Excel Template (unchanged)
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadExcelTemplate() throws IOException {
        ClassPathResource template = new ClassPathResource("templates/inspection_template.xlsx");
        FileInputStream fis = new FileInputStream(template.getFile());
        byte[] bytes = new byte[(int) template.getFile().length()];
        fis.read(bytes);
        fis.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inspection_template.xlsx");

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    // ✅ Download Product-specific Excel (safe version)
    @GetMapping("/excel/{productId}")
    public ResponseEntity<byte[]> downloadProductExcel(@PathVariable Long productId) {
        try {
            byte[] excelData = reportService.generateExcelReport(productId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=InspectionReport_" + productId + ".xlsx");

            return ResponseEntity.ok().headers(headers).body(excelData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ Download Product-specific PDF (unchanged)
    @GetMapping("/pdf/{productId}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long productId) {
        try {
            byte[] pdfData = reportService.generatePdf(productId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=InspectionReport_" + productId + ".pdf");

            return ResponseEntity.ok().headers(headers).body(pdfData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
