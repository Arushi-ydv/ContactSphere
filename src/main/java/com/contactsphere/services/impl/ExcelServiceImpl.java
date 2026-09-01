package com.contactsphere.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.contactsphere.entities.Contact;
import com.contactsphere.services.ExcelService;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Override
    public byte[] exportContactsToExcel(List<Contact> contacts) {

        try (XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                Sheet sheet = workbook.createSheet("Contacts");

                Row headerRow = sheet.createRow(0);

                headerRow.createCell(0).setCellValue("Name");
                headerRow.createCell(1).setCellValue("Email");
                headerRow.createCell(2).setCellValue("Phone Number");
                headerRow.createCell(3).setCellValue("Address");
                headerRow.createCell(4).setCellValue("Favourite");

                int rowNumber = 1;

                for(Contact contact : contacts) {

                    Row row = sheet.createRow(rowNumber++);

                    row.createCell(0).setCellValue(contact.getName());
                    row.createCell(1).setCellValue(contact.getEmail());
                    row.createCell(2).setCellValue(contact.getPhoneNumber());
                    row.createCell(3).setCellValue(contact.getAddress());
                    row.createCell(4).setCellValue(contact.isFavourite() ? "Yes" : "No");
                }
                workbook.write(outputStream);
                return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to export contacts to Excel");
        }
    }
}
