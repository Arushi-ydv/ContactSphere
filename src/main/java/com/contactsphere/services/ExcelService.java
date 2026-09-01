package com.contactsphere.services;

import java.util.List;

import com.contactsphere.entities.Contact;

public interface ExcelService {

    byte[] exportContactsToExcel(List<Contact> contacts);
}
