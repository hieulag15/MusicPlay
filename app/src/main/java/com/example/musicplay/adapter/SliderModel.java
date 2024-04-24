package com.example.musicplay.adapter;

public class SliderModel {
    private int Image;
    private String slidename;

    public SliderModel(int image, String slidename) {
        Image = image;
        this.slidename = slidename;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getSlidename() {
        return slidename;
    }

    public void setSlidename(String slidename) {
        this.slidename = slidename;
    }
}
