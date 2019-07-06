package com.fbusers.tom.diploma;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.fbusers.tom.diploma.contacts.Contact;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    private static String _nickname;
    private static String _name;

    private static String _phone;
    private static List<Contact> _contacts;

    public static void setProfile(String nickname, String fullName, String phone)
    {
        _nickname = nickname;
        _name = fullName;
        _phone = phone;

        _contacts = new ArrayList<>();
    }

    public static void updateProfile(String nickname, String fullName)
    {
        _nickname = nickname;
        _name = fullName;
    }

    public static String getNickname() {
        return _nickname;
    }

    public static String getName() {
        return _name;
    }

    public static String getPhone() {
        return _phone;
    }

    public  static void addContact(Contact contact)
    {
        _contacts.add(contact);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void removeContact(String phoneNumber) {
        _contacts.remove(_contacts.stream().filter(con -> con.getPhone().equals(phoneNumber)));
    }

    public static Contact getContact(String phoneNumber) {
        for (Contact contact : _contacts) {
            if (contact.getPhone().equals(phoneNumber)) return contact;
        }
        return null;
    }

    public static List<Contact> getContacts() {
        return _contacts;
    }
}
