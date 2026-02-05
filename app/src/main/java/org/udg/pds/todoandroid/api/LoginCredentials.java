package org.udg.pds.todoandroid.api;

import com.google.gson.annotations.SerializedName;

public class LoginCredentials {

    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    public LoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
