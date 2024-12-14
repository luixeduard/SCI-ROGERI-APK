/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Sides;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.pdfbox.printing.PDFPageable;



/**
 *
 * @author luix_
 */
public class PDF {
    
    PDFont font = PDType1Font.HELVETICA;
    ByteArrayOutputStream document;
    
    public PDF(BufferedImage qr, String nombre, String uso) throws IOException{
        try (PDDocument doc = new PDDocument()) {
            PDRectangle PDPer = new PDRectangle(800, 400);
            PDPage page = new PDPage(PDPer);
            doc.addPage(page);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qr, "jpg", baos);
            try(PDPageContentStream contentStream = new PDPageContentStream(doc, page)){
                PDImageXObject image = PDImageXObject.createFromByteArray(doc, baos.toByteArray(), "qr");
                contentStream.drawImage(image, 0, 0);
                contentStream.beginText();
                contentStream.newLineAtOffset(400, 300);
                contentStream.setFont(font, 24);
                contentStream.setLeading(17);
                contentStream.showText(nombre);
                contentStream.newLine();
                contentStream.showText(uso);
                contentStream.endText();
                contentStream.close();
            }
            print(doc);
        } catch (PrinterException ex) {
            Logger.getLogger(PDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void print(PDDocument document) throws PrinterException{
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        attr.add(new PageRanges(1, 1));
        PDViewerPreferences vp = document.getDocumentCatalog().getViewerPreferences();
        if (vp != null && vp.getDuplex() != null){
            String dp = vp.getDuplex();
            if (PDViewerPreferences.DUPLEX.DuplexFlipLongEdge.toString().equals(dp)){
                attr.add(Sides.TWO_SIDED_LONG_EDGE);
            }else if (PDViewerPreferences.DUPLEX.DuplexFlipShortEdge.toString().equals(dp)){
                attr.add(Sides.TWO_SIDED_SHORT_EDGE);
            }else if (PDViewerPreferences.DUPLEX.Simplex.toString().equals(dp)){
                attr.add(Sides.ONE_SIDED);
            }
        }
        if (job.printDialog(attr)){
            job.print(attr);
        }
    }
}
