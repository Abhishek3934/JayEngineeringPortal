package com.example.JayEngineeringPortal.dao;

import com.example.JayEngineeringPortal.model.Product;
import com.example.JayEngineeringPortal.model.ProductDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductDimensionDao productDimensionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ---------------------------
    // PDF generation (cleaned)
    // ---------------------------
    public byte[] generatePdf(Long productId) throws Exception {
        Product product = getProductById(productId);
        List<ProductDimension> dimensions = productDimensionDao.findByProductId(productId);

        Document document = new Document(PageSize.A4.rotate(), 16, 16, 16, 16);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Fonts
        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7);
        Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        // Title (centered above boxes)
        Paragraph titleLine = new Paragraph("STANDARD INSPECTION REPORT", titleFont);
        titleLine.setAlignment(Element.ALIGN_CENTER);
        titleLine.setSpacingAfter(6f);
        document.add(titleLine);

        // ===== HEADER BLOCK (Company | Customer | Insp Details) =====
        PdfPTable top = new PdfPTable(3);
        top.setWidthPercentage(100f);
        top.setWidths(new float[]{45f, 30f, 25f});

        // LEFT: COMPANY DETAILS
        PdfPCell compBox = new PdfPCell();
        compBox.setBorder(Rectangle.BOX);
        compBox.setPadding(6f);

        Paragraph compP = new Paragraph();
        compP.add(new Chunk("JAY ENGINEERING INDUSTRIES\n", companyFont));
        compP.add(new Chunk("Works: E-25, MIDC, Ambad, Nashik - 422010\n", valueFont));
        compP.add(new Chunk("Off.: X-29, MIDC, Ambad, Nashik - 422010", valueFont));
        compP.setAlignment(Element.ALIGN_CENTER);

        compBox.addElement(compP);
        top.addCell(compBox);

        // CENTER: CUSTOMER NAME IN MIDDLE
        PdfPCell custBox = new PdfPCell();
        custBox.setBorder(Rectangle.BOX);
        custBox.setPadding(6f);

        Paragraph cust = new Paragraph();
        cust.add(new Chunk("CUSTOMER : ", labelFont));
        cust.add(new Chunk(safeString(product.getCustomer()), valueFont));
        cust.setAlignment(Element.ALIGN_CENTER);

        custBox.addElement(cust);
        top.addCell(custBox);

        // RIGHT: INSP DETAILS
        PdfPCell inspBox = new PdfPCell();
        inspBox.setBorder(Rectangle.BOX);
        inspBox.setPadding(6f);

        PdfPTable inspInner = new PdfPTable(2);
        inspInner.setWidthPercentage(100f);

        inspInner.addCell(makeCell("INSP. REPORT NO.:", labelFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
        inspInner.addCell(makeCell("", valueFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
        inspInner.addCell(makeCell("INSP. DATE:", labelFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
        inspInner.addCell(makeCell("", valueFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));

        inspBox.addElement(inspInner);
        top.addCell(inspBox);

        document.add(top);

        // PRODUCT DETAILS area (6-column-like layout)
        PdfPTable details = new PdfPTable(6);
        details.setWidthPercentage(100f);
        details.setWidths(new float[]{20f, 30f, 20f, 30f, 20f, 30f});

        // ROW 1: PROJECT | ORDER QTY | OTHER DETAILS (OTHER DETAILS spans 2 rows)
        details.addCell(makeInlineCell("PROJECT :", safeString(product.getProjectNo()), labelFont, valueFont, 2));
        details.addCell(makeInlineCell("ORDER QTY.:", safeString(product.getOrderQty()), labelFont, valueFont, 2));
        PdfPCell otherDetails = makeInlineCell("OTHER DETAILS:", safeString(product.getOtherDetails()), labelFont, valueFont, 2);
        otherDetails.setRowspan(2);
        details.addCell(otherDetails);

        // ROW 2: PART NAME | MATERIAL
        details.addCell(makeInlineCell("PART NAME :", safeString(product.getPartName()), labelFont, valueFont, 2));
        details.addCell(makeInlineCell("MATERIAL :", safeString(product.getMaterial()), labelFont, valueFont, 2));
        // (otherDetails covers the last two columns via rowspan)

        // ROW 3: DRAWING NO full width across 6 columns
        details.addCell(makeInlineCell("DRAWING NO.:", safeString(product.getDrawingNo()), labelFont, valueFont, 6));

        document.add(details);

        // ===== DIMENSION TABLE (14 columns) =====
        PdfPTable dimTable = new PdfPTable(14);
        dimTable.setWidthPercentage(100f);

        float[] widths = {1.2f, 3.2f, 4.2f, 2.0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
        dimTable.setWidths(widths);

        // Header row 1 (first four headers rowspan 2, observed dims colspan 10)
        PdfPCell h;

        h = new PdfPCell(new Phrase("SR. NO", tableHeaderFont));
        h.setHorizontalAlignment(Element.ALIGN_CENTER);
        h.setRowspan(2);
        h.setPadding(6f);
        h.setBorder(Rectangle.BOX);
        dimTable.addCell(h);

        h = new PdfPCell(new Phrase("DIMENSION TYPE", tableHeaderFont));
        h.setHorizontalAlignment(Element.ALIGN_CENTER);
        h.setRowspan(2);
        h.setPadding(6f);
        h.setBorder(Rectangle.BOX);
        dimTable.addCell(h);

        h = new PdfPCell(new Phrase("SPECIFIED DIMENSION", tableHeaderFont));
        h.setHorizontalAlignment(Element.ALIGN_CENTER);
        h.setRowspan(2);
        h.setPadding(6f);
        h.setBorder(Rectangle.BOX);
        dimTable.addCell(h);

        h = new PdfPCell(new Phrase("TOLERANCE", tableHeaderFont));
        h.setHorizontalAlignment(Element.ALIGN_CENTER);
        h.setRowspan(2);
        h.setPadding(6f);
        h.setBorder(Rectangle.BOX);
        dimTable.addCell(h);

        PdfPCell obs = new PdfPCell(new Phrase("OBSERVED DIMENSIONS", tableHeaderFont));
        obs.setHorizontalAlignment(Element.ALIGN_CENTER);
        obs.setColspan(10);
        obs.setPadding(6f);
        obs.setBorder(Rectangle.BOX);
        dimTable.addCell(obs);

        // Header row 2: numbers 1..10
        for (int i = 1; i <= 10; i++) {
            PdfPCell c = new PdfPCell(new Phrase(String.valueOf(i), tableHeaderFont));
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setPadding(6f);
            c.setBorder(Rectangle.BOX);
            dimTable.addCell(c);
        }

        dimTable.setHeaderRows(2);

        // Data rows
        int sr = 1;
        if (dimensions == null || dimensions.isEmpty()) {
            // print one blank row
            for (int c = 0; c < 14; c++) {
                dimTable.addCell(makeCell(" ", tableCellFont, Element.ALIGN_LEFT, Rectangle.BOX));
            }
        } else {
            for (ProductDimension d : dimensions) {
                dimTable.addCell(makeCell(String.valueOf(sr++), tableCellFont, Element.ALIGN_CENTER, Rectangle.BOX));
                dimTable.addCell(makeCell(safeString(d.getDimensionType()), tableCellFont, Element.ALIGN_LEFT, Rectangle.BOX));
                dimTable.addCell(makeCell(safeString(d.getSpecifiedDimension()), tableCellFont, Element.ALIGN_LEFT, Rectangle.BOX));
                dimTable.addCell(makeCell(safeString(d.getTolerance()), tableCellFont, Element.ALIGN_CENTER, Rectangle.BOX));
                // 10 observed blank cells
                for (int k = 0; k < 10; k++) {
                    dimTable.addCell(makeCell(" ", tableCellFont, Element.ALIGN_CENTER, Rectangle.BOX));
                }
            }
        }

        document.add(dimTable);

        // ===== FINISHING + REFERENCE (single combined bottom area) =====
        PdfPTable bottom = new PdfPTable(9);
        bottom.setWidthPercentage(100f);
        bottom.setSpacingBefore(4f);
        bottom.setWidths(new float[]{1.8f, 1.8f, 1f, 1f, 1f, 1f, 1f, 1f, 1f});

        // Row1: Buffing | Anodising | Refer title (colspan 7)
        bottom.addCell(makeCell("BUFFING", valueFont, Element.ALIGN_LEFT, Rectangle.BOX));
        bottom.addCell(makeCell("ANODISING", valueFont, Element.ALIGN_LEFT, Rectangle.BOX));
        PdfPCell refTitleCell = new PdfPCell(new Phrase("Refer table for unspecified dimensions", labelFont));
        refTitleCell.setColspan(7);
        refTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        refTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        refTitleCell.setPadding(6f);
        refTitleCell.setBackgroundColor(new BaseColor(255, 255, 255));
        refTitleCell.getPhrase().getFont().setColor(BaseColor.BLACK);
        bottom.addCell(refTitleCell);

        // Row2: Mirror buffing | Hardchrome | dim values (7 columns)
        bottom.addCell(makeCell("MIRROR BUFFING", valueFont, Element.ALIGN_LEFT, Rectangle.BOX));
        bottom.addCell(makeCell("HARDCHROME", valueFont, Element.ALIGN_LEFT, Rectangle.BOX));
        String[] dimValues = {"Dim. in mm", "6", "6-30", "30-120", "120-315", "315-1000", "1000-2000"};
        for (String v : dimValues) bottom.addCell(makeCell(v, valueFont, Element.ALIGN_CENTER, Rectangle.BOX));

        // Row3: Blackodising | Other | tolerance values
        bottom.addCell(makeCell("BLACKODISING", valueFont, Element.ALIGN_LEFT, Rectangle.BOX));
        bottom.addCell(makeCell("OTHER:", valueFont, Element.ALIGN_LEFT, Rectangle.BOX));
        String[] tolValues = {"Tolerance", "±0.1", "±0.2", "±0.3", "±0.5", "±0.8", "±1.2"};
        for (String v : tolValues) bottom.addCell(makeCell(v, valueFont, Element.ALIGN_CENTER, Rectangle.BOX));

        document.add(bottom);

        // Remarks + Signatures
        Paragraph remarksLine = new Paragraph("Remarks : " + safeString(""), valueFont);
        remarksLine.setAlignment(Element.ALIGN_LEFT);
        remarksLine.setSpacingAfter(6f);
        document.add(remarksLine);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);


        PdfPTable sig = new PdfPTable(2);
        sig.setWidthPercentage(100f);
        sig.addCell(makeCell("Inspected by: ____________________", valueFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
        sig.addCell(makeCell("Approved by: ____________________", valueFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
        document.add(sig);

        document.close();
        return out.toByteArray();
    }

    // ---------------------------
    // Excel generation (cleaned)
    // ---------------------------
    public byte[] generateExcelReport(Long productId) throws Exception {
        Product product = getProductById(productId);
        List<ProductDimension> dims = productDimensionDao.findByProductId(productId);

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sh = wb.createSheet("Inspection Report");

        // ---------- Styles ----------
        XSSFFont titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);

        XSSFFont companyFont = wb.createFont();
        companyFont.setBold(true);
        companyFont.setFontHeightInPoints((short) 11);

        XSSFFont labelFont = wb.createFont();
        labelFont.setBold(true);
        labelFont.setFontHeightInPoints((short) 9);

        XSSFFont valueFont = wb.createFont();
        valueFont.setFontHeightInPoints((short) 9);

        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle companyStyle = wb.createCellStyle();
        companyStyle.setFont(companyFont);
        companyStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle labelStyle = wb.createCellStyle();
        labelStyle.setFont(labelFont);
        labelStyle.setVerticalAlignment(VerticalAlignment.TOP);

        CellStyle valueStyle = wb.createCellStyle();
        valueStyle.setFont(valueFont);

        CellStyle boxedLabelStyle = wb.createCellStyle();
        boxedLabelStyle.cloneStyleFrom(labelStyle);
        boxedLabelStyle.setBorderTop(BorderStyle.THIN);
        boxedLabelStyle.setBorderBottom(BorderStyle.THIN);
        boxedLabelStyle.setBorderLeft(BorderStyle.THIN);
        boxedLabelStyle.setBorderRight(BorderStyle.THIN);
        boxedLabelStyle.setAlignment(HorizontalAlignment.LEFT);
        boxedLabelStyle.setWrapText(true);

        CellStyle boxedValueStyle = wb.createCellStyle();
        boxedValueStyle.cloneStyleFrom(valueStyle);
        boxedValueStyle.setBorderTop(BorderStyle.THIN);
        boxedValueStyle.setBorderBottom(BorderStyle.THIN);
        boxedValueStyle.setBorderLeft(BorderStyle.THIN);
        boxedValueStyle.setBorderRight(BorderStyle.THIN);
        boxedValueStyle.setAlignment(HorizontalAlignment.LEFT);
        boxedValueStyle.setWrapText(true);

        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.cloneStyleFrom(labelStyle);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle centerCell = wb.createCellStyle();
        centerCell.cloneStyleFrom(valueStyle);
        centerCell.setAlignment(HorizontalAlignment.CENTER);
        centerCell.setBorderTop(BorderStyle.THIN);
        centerCell.setBorderBottom(BorderStyle.THIN);
        centerCell.setBorderLeft(BorderStyle.THIN);
        centerCell.setBorderRight(BorderStyle.THIN);

        // ---------- Layout rows/cols ----------
        int row = 0;

        // Title (centered across wide area)
        Row r = sh.createRow(row++);
        createCell(r, 0, "STANDARD INSPECTION REPORT", titleStyle);
        sh.addMergedRegion(new CellRangeAddress(r.getRowNum(), r.getRowNum(), 0, 13));


     // Company block on left (two rows)
     // Company block on left (two rows)
     // ====== HEADER BLOCK ======

        int startCol = 0;

        /* -------------------- ROW 1 -------------------- */
        /* Company (center), Customer (center), Insp Report No (right) */

        Row row1 = sh.createRow(row++);
        createCell(row1, startCol, "JAY ENGINEERING INDUSTRIES", companyStyle);
        createCell(row1, 5, "CUSTOMER : " + safeString(product.getCustomer()), valueStyle);
        createCell(row1, 10, "INSP. REPORT NO.:", labelStyle);
        createCell(row1, 12, safeString(""), valueStyle);

        // Merges - 14 columns (0 to 13)
        sh.addMergedRegion(new CellRangeAddress(row1.getRowNum(), row1.getRowNum(), 0, 4)); // Company
        sh.addMergedRegion(new CellRangeAddress(row1.getRowNum(), row1.getRowNum(), 5, 9)); // Customer
        sh.addMergedRegion(new CellRangeAddress(row1.getRowNum(), row1.getRowNum(), 10, 11)); // Label
        sh.addMergedRegion(new CellRangeAddress(row1.getRowNum(), row1.getRowNum(), 12, 13)); // Value


        /* -------------------- ROW 2 -------------------- */
        /* Works (center-left), Insp Date (right) */

        Row row2 = sh.createRow(row++);
        createCell(row2, startCol, "Works: E-25, MIDC, Ambad, Nashik - 422010", valueStyle);
        createCell(row2, 10, "INSP. DATE:", labelStyle);
        createCell(row2, 12, safeString(product.getInspectionDate() != null ? product.getInspectionDate().toString() : ""), valueStyle);

        sh.addMergedRegion(new CellRangeAddress(row2.getRowNum(), row2.getRowNum(), 0, 9));  // Works
        sh.addMergedRegion(new CellRangeAddress(row2.getRowNum(), row2.getRowNum(), 10, 11)); // Label
        sh.addMergedRegion(new CellRangeAddress(row2.getRowNum(), row2.getRowNum(), 12, 13)); // Value


        /* -------------------- ROW 3 -------------------- */
        /* Office Address (left) + Drawing No at far-left like PDF */

        Row row3 = sh.createRow(row++);
        createCell(row3, startCol, "Off.: X-29, MIDC, Ambad, Nashik - 422010", valueStyle);

     
        

        /* -------------------- ROW 4 -------------------- */

        Row row4 = sh.createRow(row++);
        createCell(row4, startCol, "PROJECT:                " + safeString(product.getProjectNo()), valueStyle);
        createCell(row4, 5, "Order Qty:            " + safeString(product.getOrderQty()), valueStyle);
        createCell(row4, 10, "OTHER DETAILS:          ", labelStyle);
        createCell(row4, 12, safeString(""), valueStyle);

        // Merges - 14 columns (0 to 13)
        sh.addMergedRegion(new CellRangeAddress(row4.getRowNum(), row4.getRowNum(), 0, 4)); // Company
        sh.addMergedRegion(new CellRangeAddress(row4.getRowNum(), row4.getRowNum(), 5, 9)); // Customer
        sh.addMergedRegion(new CellRangeAddress(row4.getRowNum(), row4.getRowNum(), 10, 11)); // Label
        sh.addMergedRegion(new CellRangeAddress(row4.getRowNum(), row4.getRowNum(), 12, 13)); // Value



        /* -------------------- ROW 5 -------------------- */

        Row row5 = sh.createRow(row++);
        createCell(row5, startCol, "PART NAME -               " + safeString(product.getPartName()), valueStyle);
        createCell(row5, 5, "MATERIAL            " + safeString(product.getMaterial()), valueStyle);
        createCell(row5, 10, "", labelStyle);
        createCell(row5, 12, safeString(""), valueStyle);

        // Merges - 14 columns (0 to 13)
        sh.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 0, 4)); // Company
        sh.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 5, 9)); // Customer
        sh.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 10, 11)); // Label
        sh.addMergedRegion(new CellRangeAddress(row5.getRowNum(), row5.getRowNum(), 12, 13)); // Value


        /* -------------------- ROW 6 -------------------- */

        Row row6 = sh.createRow(row++);
        createCell(row6, startCol, "DRAWING No. -              " + safeString(product.getDrawingNo()), valueStyle);
        createCell(row6, 10, "", labelStyle);
        createCell(row6, 12, safeString(""), valueStyle);

        // Merges - 14 columns (0 to 13)
        sh.addMergedRegion(new CellRangeAddress(row6.getRowNum(), row6.getRowNum(), 0, 4)); // Company
        sh.addMergedRegion(new CellRangeAddress(row6.getRowNum(), row6.getRowNum(), 5, 9)); // Customer
        sh.addMergedRegion(new CellRangeAddress(row6.getRowNum(), row6.getRowNum(), 10, 11)); // Label
        sh.addMergedRegion(new CellRangeAddress(row6.getRowNum(), row6.getRowNum(), 12, 13)); // Value



        // Dimension headers
        Row hdr = sh.createRow(row++);
        String[] headers = {"SR.NO", "DIMENSION TYPE", "SPECIFIED DIMENSION", "TOLERANCE",
                "OBS 1", "OBS 2", "OBS 3", "OBS 4", "OBS 5", "OBS 6", "OBS 7", "OBS 8", "OBS 9", "OBS 10"};
        for (int c = 0; c < headers.length; c++) {
            createCell(hdr, c, headers[c], headerCellStyle);
        }

        int minRows = 12;
        int sr = 1;
        for (int i = 0; i < Math.max(minRows, (dims == null ? 0 : dims.size())); i++) {
            Row dr = sh.createRow(row++);
            if (dims != null && i < dims.size()) {
                ProductDimension d = dims.get(i);
                createCell(dr, 0, String.valueOf(sr++), centerCell);
                createCell(dr, 1, safeString(d.getDimensionType()), boxedValueStyle);
                createCell(dr, 2, safeString(d.getSpecifiedDimension()), boxedValueStyle);
                createCell(dr, 3, safeString(d.getTolerance()), centerCell);
            } else {
                createCell(dr, 0, "", centerCell);
                createCell(dr, 1, "", boxedValueStyle);
                createCell(dr, 2, "", boxedValueStyle);
                createCell(dr, 3, "", centerCell);
            }
            for (int oc = 4; oc < 14; oc++) {
                createCell(dr, oc, "", centerCell);
            }
        }


     // === 3-Row Finishing + Refer Table Layout (L2: 0-6 left, 7-13 right) ===

        
        
        CellStyle leftAlignedStyle = wb.createCellStyle();  // <-- change workbook to wb
        leftAlignedStyle.cloneStyleFrom(boxedValueStyle);
        leftAlignedStyle.setAlignment(HorizontalAlignment.LEFT);

     // ---------- ROW 1 ----------
     Row fin1 = sh.createRow(row++);
     createCell(fin1, 0, "BUFFING", boxedValueStyle);
     sh.addMergedRegion(new CellRangeAddress(fin1.getRowNum(), fin1.getRowNum(), 0, 1));

     createCell(fin1, 2, "", boxedValueStyle); // blank

     createCell(fin1, 2, "ANODISING", leftAlignedStyle);
     sh.addMergedRegion(new CellRangeAddress(fin1.getRowNum(), fin1.getRowNum(), 3, 4));


     // Leave col6 for future if needed
     createCell(fin1, 7, "REFER TABLE FOR UNSPECIFIED DIMENSIONS", boxedLabelStyle);
     sh.addMergedRegion(new CellRangeAddress(fin1.getRowNum(), fin1.getRowNum(), 7, 13));


     // ---------- ROW 2 ----------
     Row fin2 = sh.createRow(row++);
     createCell(fin2, 0, "MIRROR BUFFING", boxedValueStyle);
     sh.addMergedRegion(new CellRangeAddress(fin2.getRowNum(), fin2.getRowNum(), 0, 1));

     createCell(fin2, 2, "", boxedValueStyle); // blank

     createCell(fin2, 2, "HARDCHROME", leftAlignedStyle);
     sh.addMergedRegion(new CellRangeAddress(fin2.getRowNum(), fin2.getRowNum(), 3, 4));


     // Right side: Dim ranges
     String[] dimRanges = {"Dim (mm)", "6", "6-30", "30-120", "120-315", "315-1000", "1000-2000"};
     for (int i = 0; i < dimRanges.length; i++) {
         createCell(fin2, 7 + i, dimRanges[i], boxedValueStyle);
     }


     // ---------- ROW 3 ----------
     Row fin3 = sh.createRow(row++);
     createCell(fin3, 0, "BLACKODISING", boxedValueStyle);
     sh.addMergedRegion(new CellRangeAddress(fin3.getRowNum(), fin3.getRowNum(), 0, 1));

     createCell(fin3, 2, "OTHER: ", boxedValueStyle);
     sh.addMergedRegion(new CellRangeAddress(fin3.getRowNum(), fin3.getRowNum(), 2, 4));


     // Right side: Tolerance row
     String[] tolVals = {"Tolerance", "±0.1", "±0.2", "±0.3", "±0.5", "±0.8", "±1.2"};
     for (int i = 0; i < tolVals.length; i++) {
         createCell(fin3, 7 + i, tolVals[i], boxedValueStyle);
     }



        row++; // gap
        Row remarks = sh.createRow(row++);
        createCell(remarks, 0, "Remarks:", labelStyle);
        sh.addMergedRegion(new CellRangeAddress(remarks.getRowNum(), remarks.getRowNum(), 1, 13));

        row++;
        row++;
        row++;
        Row sig = sh.createRow(row++);
        createCell(sig, 0, "Inspected by: ____________________", valueStyle);
        createCell(sig, 7, "Approved by: ____________________", valueStyle);
        sh.addMergedRegion(new CellRangeAddress(sig.getRowNum(), sig.getRowNum(), 0, 6));
        sh.addMergedRegion(new CellRangeAddress(sig.getRowNum(), sig.getRowNum(), 7, 13));

        // Auto-size a few important columns
        for (int c = 0; c <= 13; c++) {
            if (c <= 3 || c == 7) sh.autoSizeColumn(c);
        }

        // Write workbook to byte[]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }

    // ---------- helpers ----------
    private PdfPCell makeCell(String text, Font font, int alignment, int border) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(border);
        cell.setPadding(5f);
        return cell;
    }

    private PdfPCell makeInlineCell(String label, String value, Font labelFont, Font valueFont, int colspan) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        if (value != null && !value.trim().isEmpty()) {
            p.add(new Chunk(value, valueFont));
        }

        PdfPCell cell = new PdfPCell(p);
        cell.setBorder(Rectangle.BOX);
        cell.setColspan(colspan);
        cell.setPadding(5f);
        return cell;
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    // ---------------------------
    // Database mapping
    // ---------------------------
    public Product getProductById(Long productId) {
        String sql = "SELECT * FROM products WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{productId}, (rs, rowNum) -> {
            Product p = new Product();
            p.setId(rs.getLong("ID"));
            // map expected columns (adjust names if your DB uses different column names)
            try { p.setProjectNo(rs.getString("project_no")); } catch (Exception ignored) {}
            try { p.setInspectionReportNo(rs.getString("inspection_report_no")); } catch (Exception ignored) {}
            try { p.setInspectionDate(rs.getString("inspection_date")); } catch (Exception ignored) {}
            try { p.setOtherDetails(rs.getString("other_details")); } catch (Exception ignored) {}
            p.setPartName(rs.getString("part_name"));
            p.setDrawingNo(rs.getString("drawing_no"));
            p.setCustomer(rs.getString("customer"));
            p.setMaterial(rs.getString("material"));
            try { p.setOrderQty(rs.getInt("order_qty")); } catch (Exception ignored) {}
            return p;
        });
    }

    // ---------- Excel helper ----------
    private void createCell(org.apache.poi.ss.usermodel.Row row, int col, String text, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(col);
        cell.setCellValue(text == null ? "" : text);
        if (style != null) cell.setCellStyle(style);
    }
}
