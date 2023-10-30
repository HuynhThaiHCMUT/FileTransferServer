package com.computernetwork.filetransferserver.Model;

import java.util.Date;

public class FileData {
    private String name;
    private Long size;
    private String description;
    private Date uploadedDate;
    private String owner;

    public FileData(String name, Long size, String description, String owner) {
        this.name = name;
        this.size = size;
        this.description = description;
        uploadedDate = new Date();
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
