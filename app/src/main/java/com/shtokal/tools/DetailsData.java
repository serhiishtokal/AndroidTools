package com.shtokal.tools;


public class DetailsData {
    private String textTitle;
    private String textDescription;
    private int image;

    public DetailsData(String textTitle, String textDescription, int image) {
        this.textTitle = textTitle;
        this.textDescription = textDescription;
        this.image = image;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public String getTextDescription() {
        return textDescription;
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
