package org.udg.pds.todoandroid.api;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class TaskDto {

    @SerializedName("id")
    public long id;

    @SerializedName("text")
    public String text;

    @SerializedName("completed")
    public boolean completed;

    @SerializedName("dateCreated")
    public ZonedDateTime dateCreated;

    @SerializedName("dateLimit")
    public ZonedDateTime dateLimit;

    @SerializedName("userId")
    public long userId;
}


