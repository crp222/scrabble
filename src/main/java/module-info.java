module hu.simontamas.scrabble {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.boot.starter;
    requires spring.aop;
    requires static lombok;
    requires java.management;
    requires org.slf4j;

    opens hu.simontamas.scrabble to javafx.fxml;
    opens hu.simontamas.scrabble.view to javafx.fxml;
    opens hu.simontamas.scrabble.service to spring.core;
    exports hu.simontamas.scrabble;
    exports hu.simontamas.scrabble.view;
    exports hu.simontamas.scrabble.service;
    exports hu.simontamas.scrabble.model;
    exports hu.simontamas.scrabble.enums;
    exports hu.simontamas.scrabble.threads;
    exports hu.simontamas.scrabble.utils;
    exports hu.simontamas.scrabble.service.wordService;
    opens hu.simontamas.scrabble.service.wordService to spring.core;
}