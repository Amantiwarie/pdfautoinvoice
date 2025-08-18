package com.example.invoice.service;

import com.example.invoice.model.Dealer;
import com.example.invoice.model.Vehicle;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class InvoicePdfService {

    public static class InvoicePayload {
        public String invoiceNo;
        public String timestamp;
        public String transactionId;
        public Dealer dealer;
        public Vehicle vehicle;
        public String customerName;
        public BigDecimal base;
        public BigDecimal tax;
        public BigDecimal total;
    }

    public InvoicePayload buildPayload(Dealer dealer, Vehicle vehicle, String customerName) {
        InvoicePayload p = new InvoicePayload();
        String ts = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
        p.timestamp = ts;
        p.invoiceNo = "INV-" + ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        p.transactionId = UUID.randomUUID().toString();

        p.dealer = dealer; p.vehicle = vehicle; p.customerName = customerName;
        p.base = vehicle.price.setScale(2, RoundingMode.HALF_UP);
        p.tax  = p.base.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
        p.total= p.base.add(p.tax).setScale(2, RoundingMode.HALF_UP);
        return p;
    }

    public byte[] renderPdf(InvoicePayload p) throws Exception {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                float y = 750;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.newLineAtOffset(50, y);
                content.showText("Vehicle Sale Invoice");
                content.endText();

                y -= 30;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText("Invoice No: " + p.invoiceNo);
                content.endText();

                y -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText("Timestamp: " + p.timestamp);
                content.endText();

                y -= 25;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(50, y);
                content.showText("Dealer");
                content.endText();

                y -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText(p.dealer.name + " | " + p.dealer.phone);
                content.endText();

                y -= 16;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText(p.dealer.address);
                content.endText();

                y -= 22;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(50, y);
                content.showText("Bill To");
                content.endText();

                y -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText(p.customerName);
                content.endText();

                y -= 22;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(50, y);
                content.showText("Vehicle");
                content.endText();

                y -= 18;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText(p.vehicle.make + " " + p.vehicle.model + "  |  VIN: " + p.vehicle.vin);
                content.endText();

                y -= 30;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(50, y);
                content.showText("Base Price: INR" + p.base);
                content.endText();

                y -= 16;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, y);
                content.showText("Tax (10%): INR" + p.tax);
                content.endText();

                y -= 16;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(50, y);
                content.showText("Total: INR" + p.total);
                content.endText();

                // QR
                BufferedImage qr = makeQr(p.transactionId, 140);
                var pdImg = LosslessFactory.createFromImage(doc, qr);
                content.drawImage(pdImg, 420, 600, 140, 140);

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 9);
                content.newLineAtOffset(420, 585);
                content.showText("Txn ID: " + p.transactionId);
                content.endText();
            }

            doc.save(baos);
            return baos.toByteArray();
        }
    }

    private static BufferedImage makeQr(String data, int size) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size);
        BufferedImage img = MatrixToImageWriter.toBufferedImage(matrix);
        return img;
    }
}
