package com.my.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DiscussPost {
    private int id;
    private String userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
