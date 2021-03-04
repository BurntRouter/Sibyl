package com.sibyl.bot;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Authenticator {

    private JSONObject configJson;
    private JSONObject credentialsJson;

    private final String CONFIG_PATH = ("./config.json");

    public Authenticator() throws IOException {
        this.configJson = new JSONObject(FileUtils.readFileToString(new File(this.CONFIG_PATH), Charset.defaultCharset()));

        this.credentialsJson = this.configJson.getJSONObject("credentials");
    }

    public String getDatabaseName() throws IOException {
        return this.credentialsJson.getString("database_name");
    }

    public String getDatabaseHost() throws IOException {
        return this.credentialsJson.getString("database_host");
    }

    public String getDatabaseUser() throws IOException {
        return this.credentialsJson.getString("database_user");
    }
    public String getDatabasePassword() throws IOException {
        return this.credentialsJson.getString("database_password");
    }

    public String getToken() throws IOException {
        return this.credentialsJson.getString("token");
    }

}