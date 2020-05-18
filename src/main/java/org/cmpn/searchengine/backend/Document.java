package org.cmpn.searchengine.backend;

public class Document {
    private String title;
    private String url;
    private String textToShow;

    public Document(String title, String url, String textToShow) {
        this.title = title;
        this.url = url;
        this.textToShow = textToShow;
    }

    public String getTitle() {
        return title;
    }


    public String getUrl() {
        return url;
    }

    public String getTextToShow() {
        return textToShow;
    }

}
