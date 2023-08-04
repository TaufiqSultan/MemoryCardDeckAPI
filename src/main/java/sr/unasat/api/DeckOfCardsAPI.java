package sr.unasat.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeckOfCardsAPI {
    @GET("deck/new/shuffle/")
    Call<Deck> getNewShuffledDeck();

    @GET("deck/{deck_id}/draw/")
    Call<DrawnCards> drawCards(@Path("deck_id") String deckId, @Query("count") int count);
}
