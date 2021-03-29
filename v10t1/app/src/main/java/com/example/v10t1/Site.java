package com.example.v10t1;

public class Site {
    private int siteId;
    private String siteName;
    public Site(int id, String name){
        siteId = id;
        siteName = name;
    }

    public int getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }
}
