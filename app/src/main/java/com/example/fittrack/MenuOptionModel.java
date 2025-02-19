package com.example.fittrack;

public class MenuOptionModel {
    private String optionTitle;
    private int optionImage;

    public MenuOptionModel(String optionTitle, int optionImage) {
        this.optionTitle = optionTitle;
        this.optionImage = optionImage;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }

    public int getOptionImage() {
        return optionImage;
    }

    public void setOptionImage(int optionImage) {
        this.optionImage = optionImage;
    }
}
