package com.zetcode;

public class Blog {

    private String name;
    private String url;
    private String lastUpdate;


    public Blog() {
    }

    public Blog(String name, String url, String lastUpdate) {
        this.url = url;
        this.name = name;
        this.lastUpdate = lastUpdate;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}