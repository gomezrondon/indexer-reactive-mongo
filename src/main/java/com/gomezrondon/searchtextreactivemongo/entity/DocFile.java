package com.gomezrondon.searchtextreactivemongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Document
public class DocFile {

    @Id
    private String id;

    private String name;
    private String type;
    private String path;
    private List<String> content;

    public DocFile() {
    }

    public DocFile(String name, String type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public DocFile(String name, String type, String path, List<String> content) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DocFile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
