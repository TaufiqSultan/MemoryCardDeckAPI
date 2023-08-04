package sr.unasat.ui;

import sr.unasat.api.Card;

public interface Observer {
    void onCardFlipped(Card card);
    void onGameEnded();
}
