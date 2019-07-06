package com.fbusers.tom.diploma.contacts;

import android.content.Context;
import android.content.Intent;

import com.fbusers.tom.diploma.MessagesActivity;

/**
 * Created by Tom on 22.04.2018.
 */

public class Contact {

    private  String nick;
    private String name;
    private String phone;
    private int image;

    public Contact(String nick, String name, String phone, int image) {
        this.nick = nick;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public String getNick() {
        return nick;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getImage() {
        return image;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
