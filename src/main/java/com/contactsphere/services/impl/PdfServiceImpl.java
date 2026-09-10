package com.contactsphere.services.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.openpdf.text.Document;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import com.contactsphere.entities.Contact;
import com.contactsphere.services.PdfService;

@Service
public class PdfServiceImpl implements PdfService{

    @Override
    public byte[] exportContactsToPdf(List<Contact> contacts){

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, outputStream);
            document.open();

            //Title
            document.add(new Paragraph("My Contacts"));

            // Table with 5 columns
            PdfPTable table = new PdfPTable(5);

            // Table headers
            table.addCell("Name");
            table.addCell("Email");
            table.addCell("Phone Number");
            table.addCell("Address");
            table.addCell("Favourite");

            // Add contact data
            for(Contact contact : contacts) {
                table.addCell(contact.getName());
                table.addCell(contact.getEmail());
                table.addCell(contact.getPhoneNumber());
                table.addCell(contact.getAddress());
                table.addCell(contact.isFavourite() ? "Yes" : "No");
            }
            document.add(table);
            document.close();
            return outputStream.toByteArray();

        } catch(Exception e) {
            throw new IllegalStateException("Failed to export contacts to PDF", e);
        }
    }
}
