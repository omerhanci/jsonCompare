package com.waes.jsonCompare.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Data {

    @Id
    private long id;

    @Column(length = 10000)
    private String leftPart;

    @Column(length = 10000)
    private String rightPart;

    public Data() {

    }

    public Data(long id, String leftPart, String rightPart) {
        this.id = id;
        this.leftPart = leftPart;
        this.rightPart = rightPart;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLeftPart() {
        return leftPart;
    }

    public void setLeftPart(String leftPart) {
        this.leftPart = leftPart;
    }

    public String getRightPart() {
        return rightPart;
    }

    public void setRightPart(String rightPart) {
        this.rightPart = rightPart;
    }
}
