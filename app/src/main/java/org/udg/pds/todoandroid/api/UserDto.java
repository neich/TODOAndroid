package org.udg.pds.todoandroid.api;

import com.google.gson.annotations.SerializedName;

public class UserDto {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;
}
