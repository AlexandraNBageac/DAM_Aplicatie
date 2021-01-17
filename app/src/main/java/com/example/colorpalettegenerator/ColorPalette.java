package com.example.colorpalettegenerator;

import androidx.palette.graphics.Palette;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ColorPalette implements Serializable {
    private String Id;
    private String  pictureURL;
    private String  pictureName;
    private Date date;
    private ArrayList<Integer> RGBPalette;
    private ArrayList<Integer> RGBtextColor;
    private boolean marked;

    public ColorPalette(){

    }

    public ColorPalette( String pictureURL, Date date, ArrayList<Integer> RGBPalette) {
        this.pictureURL = pictureURL;
        this.date = date;
        this.RGBPalette = new ArrayList<>(RGBPalette);
        this.marked=false;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public ArrayList<Integer> getRGBtextColor() {
        return RGBtextColor;
    }

    public void setRGBtextColor(ArrayList<Integer> RGBtextColor) {
        this.RGBtextColor = RGBtextColor;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Integer> getRGBPalette() {
        return RGBPalette;
    }

    public void setRGBPalette(ArrayList<Integer> RGBPalette) {
        this.RGBPalette = RGBPalette ;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
