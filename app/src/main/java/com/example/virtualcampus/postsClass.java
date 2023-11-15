package com.example.virtualcampus;

public class postsClass {
    String content,subject,topic,picuri;
    Integer postType;

    public postsClass(String content, String subject, String topic, String picuri,Integer postType) {
        this.content = content;
        this.subject = subject;
        this.topic = topic;
        this.picuri = picuri;
        this.postType=postType;
    }
}
