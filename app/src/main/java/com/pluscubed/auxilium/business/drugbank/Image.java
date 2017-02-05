package com.pluscubed.auxilium.business.drugbank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("ndc_id")
    @Expose
    private String ndcId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image_url_original")
    @Expose
    private String imageUrlOriginal;
    @SerializedName("image_url_tiny")
    @Expose
    private String imageUrlTiny;
    @SerializedName("image_url_thumb")
    @Expose
    private String imageUrlThumb;
    @SerializedName("image_url_medium")
    @Expose
    private String imageUrlMedium;

    public String getNdcId() {
        return ndcId;
    }

    public void setNdcId(String ndcId) {
        this.ndcId = ndcId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrlOriginal() {
        return imageUrlOriginal;
    }

    public void setImageUrlOriginal(String imageUrlOriginal) {
        this.imageUrlOriginal = imageUrlOriginal;
    }

    public String getImageUrlTiny() {
        return imageUrlTiny;
    }

    public void setImageUrlTiny(String imageUrlTiny) {
        this.imageUrlTiny = imageUrlTiny;
    }

    public String getImageUrlThumb() {
        return imageUrlThumb;
    }

    public void setImageUrlThumb(String imageUrlThumb) {
        this.imageUrlThumb = imageUrlThumb;
    }

    public String getImageUrlMedium() {
        return imageUrlMedium;
    }

    public void setImageUrlMedium(String imageUrlMedium) {
        this.imageUrlMedium = imageUrlMedium;
    }
}
