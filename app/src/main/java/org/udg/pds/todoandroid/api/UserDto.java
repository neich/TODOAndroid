package org.udg.pds.todoandroid.api;

import com.google.gson.annotations.SerializedName;

public class UserDto {

    @SerializedName("id")
    public int id;

    @SerializedName("username")
    public String username;

    @SerializedName("email")
    public String email;
}
