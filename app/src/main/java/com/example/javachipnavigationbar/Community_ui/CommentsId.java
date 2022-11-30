package com.example.javachipnavigationbar.Community_ui;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CommentsId {
    @Exclude
    public String ComentsId;

    public <T extends CommentsId>  T withId (@NonNull final String id){
        this.ComentsId = id;
        return (T) this;
    }
}
