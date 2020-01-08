package org.com.example.dogal_sepeti;


import android.net.Uri;

import java.util.ArrayList;

public class Product {
    private String ProductId;
    private String SellerId;
    private String Title;
    private String Category;
    private String Description;
    private String Amount;
    private String FirstImageUrl;
    private String SecondImageUrl;
    private String ThirdImageUrl;
    private String FourthImageUrl;
    private ArrayList<String> images;

    public Product(String productId, String sellerId, String title, String category, String description, String amount, String firstImageUrl, String secondImageUrl, String thirdImageUrl, String fourthImageUrl, ArrayList<String> images) {
        ProductId = productId;
        SellerId = sellerId;
        Title = title;
        Category = category;
        Description = description;
        Amount = amount;
        FirstImageUrl = firstImageUrl;
        SecondImageUrl = secondImageUrl;
        ThirdImageUrl = thirdImageUrl;
        FourthImageUrl = fourthImageUrl;
        this.images = images;
    }

    public Product(){}

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getFirstImageUrl() {
        return FirstImageUrl;
    }

    public void setFirstImageUrl(String firstImageUrl) {
        FirstImageUrl = firstImageUrl;
    }

    public String getSecondImageUrl() {
        return SecondImageUrl;
    }

    public void setSecondImageUrl(String secondImageUrl) {
        SecondImageUrl = secondImageUrl;
    }

    public String getThirdImageUrl() {
        return ThirdImageUrl;
    }

    public void setThirdImageUrl(String thirdImageUrl) {
        ThirdImageUrl = thirdImageUrl;
    }

    public String getFourthImageUrl() {
        return FourthImageUrl;
    }

    public void setFourthImageUrl(String fourthImageUrl) {
        FourthImageUrl = fourthImageUrl;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getSellerId() {
        return SellerId;
    }

    public void setSellerId(String sellerId) {
        SellerId = sellerId;
    }
}

