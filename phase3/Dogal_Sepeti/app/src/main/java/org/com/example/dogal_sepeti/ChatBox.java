package org.com.example.dogal_sepeti;

public class ChatBox {
    private String productId,
            productName,
            productImage,
            productAvailability,
            productPrice;
    private String receiverId,
            receiverName,
            receiverImage,
            timeTextView;


    public ChatBox() {
        //needed
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductAvailability() {
        return productAvailability;
    }

    public void setProductAvailability(String productAvailability) {
        this.productAvailability = productAvailability;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public String getTimeTextView() {
        return timeTextView;
    }

    public void setTimeTextView(String timeTextView) {
        this.timeTextView = timeTextView;
    }

    public ChatBox(String productId, String productName, String productImage, String productAvailability, String productPrice, String receiverId, String receiverName, String receiverImage, String timeTextView) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productAvailability = productAvailability;
        this.productPrice = productPrice;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.timeTextView = timeTextView;
    }
}
