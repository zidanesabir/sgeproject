package com.example.sgeproject.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class PDFGenerator {

    public byte[] generateExamenReport(String title, List<String> contentLines) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            document.add(new Paragraph(title));
            document.add(new Paragraph("\n")); // Add some space

            // Add content lines
            for (String line : contentLines) {
                document.add(new Paragraph(line));
            }

            document.close();
        } catch (DocumentException e) {
            // Log the exception
            throw new DocumentException("Error generating PDF: " + e.getMessage(), e);
        }
        return out.toByteArray();
    }

    // You can add more specific PDF generation methods here, e.g., for notes, reclamations, etc.
}