package com.zetcode;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;


@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String location;
    private List<String> dirs = new ArrayList<>();


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getDirs() {
        return dirs;
    }


}