package sr.unasat.api;

import com.google.gson.annotations.SerializedName;

public class Deck {
    @SerializedName("deck_id")
    private String deckId;

    public String getDeckId() {
        return deckId;
    }
}
