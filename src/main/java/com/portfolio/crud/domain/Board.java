package com.portfolio.crud.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Board {
    private Long id;
    private String author;
    private String password;
    private String title;
    private String content;

    public Board(String author, String password, String title, String content) {
        this.author = author;
        this.password = password;
        this.title = title;
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }
}