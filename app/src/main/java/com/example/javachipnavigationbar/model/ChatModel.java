package com.example.javachipnavigationbar.model;

import com.example.javachipnavigationbar.User;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public Map<String,Boolean> User = new HashMap<>(); //채팅방의 유저들
    public Map<String,Comment> comments = new HashMap<>();//채팅방의 대화내용


    public static class Comment {

        public String Uid;
        public String message;
        public Object timestamp;
    }
}