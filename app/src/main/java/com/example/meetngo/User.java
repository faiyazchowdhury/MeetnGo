package com.example.meetngo;

import java.util.HashMap;
import java.util.Vector;

public class User {
    public String emailID, message, phone;
    public int distance, duration, settings, groups, freeness;


    public User(){

    }

    public User(String emailID, String phone, int freeness, String message, int distance, int duration, int settings, int groups){
        this.emailID = emailID;
        this.phone = phone;
        this.freeness = freeness;
        this.message = message;
        this.distance = distance;
        this.duration = duration;
        this.settings = settings;
        this.groups = groups;
    }
}
