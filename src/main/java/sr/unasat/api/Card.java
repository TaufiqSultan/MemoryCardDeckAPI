package sr.unasat.api;

import com.google.gson.annotations.SerializedName;

public class Card {
    private String value;
    private String suit;
    @SerializedName("image")
    private String imageUrl;

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
// Constructors, getters, and setters

    // toString() method for better debugging
    @Override
    public String toString() {
        return value + " of " + suit;
    }
}
