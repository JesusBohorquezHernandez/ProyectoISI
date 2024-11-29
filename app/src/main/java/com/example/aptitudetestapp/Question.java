package com.example.aptitudetestapp;

public class Question {
    private String question;
    private String[] options;

    public Question(String question, String[] options) {
        this.question = question;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }
}

