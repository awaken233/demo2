package com.example.demo2;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo2Application {

    public static void main(String[] args) {
        Demo2Application app = new Demo2Application();
        app.export(new Circle());
    }

    public void export(Shape shape) {
        Exporter exporter = new Exporter();
        exporter.export(shape);
    }
}
