package com.example.javachipnavigationbar;

import java.util.HashMap;

public class User{


    public String Email;
    public String Uid;
    public String ProfileUrl;
    public String Name;
    public String Weight;
    public String Height;

    public String image;


    public HashMap<String, Object> usermap = new HashMap<>();


    public User() {

    }

    public User(String name, String Email, String Uid, String ProfileUrl, String Weight, String Height){
        this.Name = name;
        this.Email = Email;
        this.Uid = Uid;
        this.ProfileUrl = ProfileUrl;
        this.Weight = Weight;
        this.Height = Height;
        //this.Walking = Walking;
    }

    public HashMap<String, Object> usertomap(){
        HashMap<String, Object> user_result = new HashMap<>();

        user_result.put("Name", Name);
        user_result.put("Email", Email);
        user_result.put("Uid", Uid);
        user_result.put("ProfileUrl", ProfileUrl);
        user_result.put("Weight",Weight);
        user_result.put("Height",Height);

        return user_result;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return Name;
    }

}