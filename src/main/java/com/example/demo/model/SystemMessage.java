package com.example.demo.model;

import java.io.Serializable;

public class SystemMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String source;

    public void setSource(String source) {
        this.source = source;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getSource() {
        return source;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SystemMessage{" +
                "source='" + source + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
