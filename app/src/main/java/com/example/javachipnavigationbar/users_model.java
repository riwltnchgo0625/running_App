package com.example.javachipnavigationbar;

public class users_model {
    public static String useremail;
    public static String usernm;
    public static String userphoto;
    public static String uid;
    public static String userweight;
    public static String userheight;

    public users_model() {
    }

    public users_model(String useremail, String usernm, String userphoto, String uid, String userweight, String userheight) {
        this.useremail = useremail;
        this.usernm = usernm;
        this.userphoto = userphoto;
        this.uid = uid;
        this.userweight = userweight;
        this.userheight = userheight;
    }
}