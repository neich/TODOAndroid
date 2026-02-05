package org.udg.pds.todoandroid.api;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class CreateTaskRequest {

    @SerializedName("text")
    public String text;

    @SerializedName("dateCreated")
    public ZonedDateTime dateCreated;

    @SerializedName("dateLimit")
    public ZonedDateTime dateLimit;

    public CreateTaskRequest(String text, ZonedDateTime dateCreated, ZonedDateTime dateLimit) {
        this.text = text;
        this.dateCreated = dateCreated;
        this.dateLimit = dateLimit;
    }
}
