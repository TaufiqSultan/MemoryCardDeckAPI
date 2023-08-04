package sr.unasat.game;

import retrofit2.converter.gson.GsonConverterFactory;
import sr.unasat.api.Card;
import sr.unasat.api.Deck;
import sr.unasat.api.DeckOfCardsAPI;
import sr.unasat.api.DrawnCards;
import sr.unasat.ui.Observer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import retrofit2.*;
import sr.unasat.util.Timer;


public class MemoryGame {
    private static MemoryGame instance;
    private List<Card> cardsInPlay;
    private Map<String, Card> flippedCards;
    private boolean gameEnded;
    private Card firstFlippedCard;
    private Card secondFlippedCard;
    private Timer gameTimer;
    private List<Observer> observers;

    private MemoryGame() {
        cardsInPlay = new ArrayList<Card>();
        flippedCards = new HashMap<String, Card>();
        gameEnded = false;
        observers = new ArrayList<Observer>();
    }

    public static MemoryGame getInstance() {
        if (instance == null) {
            instance = new MemoryGame();
        }
        return instance;
    }

    public void startGame() {
        // Fetch deck from the API, shuffle cards, and draw initial cards
        String DECK_API_BASE_URL = "https://deckofcardsapi.com/api/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DECK_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeckOfCardsAPI deckOfCardsAPI = retrofit.create(DeckOfCardsAPI.class);
        Call<Deck> call = deckOfCardsAPI.getNewShuffledDeck();
        try {
            Response<Deck> response = call.execute();
            if (response.isSuccessful()) {
                Deck deck = response.body();
                if (deck != null) {
                    drawInitialCards(deck.getDeckId());
                    startTimer();
                }
            } else {
                System.out.println("Error fetching the deck: " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawInitialCards(String deckId) {
        int numberOfCardsToDraw = 16; // Adjust the number of cards as per the game requirements
        String DECK_API_BASE_URL = "https://deckofcardsapi.com/api/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DECK_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeckOfCardsAPI deckOfCardsAPI = retrofit.create(DeckOfCardsAPI.class);
        Call<DrawnCards> call = deckOfCardsAPI.drawCards(deckId, numberOfCardsToDraw);
        try {
            Response<DrawnCards> response = call.execute();
            if (response.isSuccessful()) {
                DrawnCards drawnCards = response.body();
                if (drawnCards != null) {
                    for (Card card : drawnCards.getCards()) {
                        cardsInPlay.add(card);
                    }
                }
            } else {
                System.out.println("Error drawing cards: " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flipCard(Card card) {
        // Handle the logic of flipping a card
        if (gameEnded || card == null || flippedCards.containsKey(card.getValue())) {
            // Invalid move, do nothing
            return;
        }

        if (flippedCards.size() < 2) {
            // Add the card to the flippedCards map
            flippedCards.put(card.getValue(), card);

            // Check if it's the first or second card flipped
            if (flippedCards.size() == 1) {
                firstFlippedCard = card;
            } else if (flippedCards.size() == 2) {
                secondFlippedCard = card;

                // Delay showing cards for a short period before flipping them back
                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        checkForMatch();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void checkForMatch() {
        if (firstFlippedCard.getValue().equals(secondFlippedCard.getValue())) {
            // Cards matched
            cardsInPlay.remove(firstFlippedCard);
            cardsInPlay.remove(secondFlippedCard);
            flippedCards.clear();
            firstFlippedCard = null;
            secondFlippedCard = null;

            if (cardsInPlay.isEmpty()) {
                gameEnded = true;
                handleGameEnd();
            }
        } else {
            // Cards did not match
            flippedCards.remove(firstFlippedCard.getValue());
            flippedCards.remove(secondFlippedCard.getValue());
            firstFlippedCard = null;
            secondFlippedCard = null;
        }

        // Notify UI that a card has been flipped and check for match
        notifyCardFlipped();
    }

    private void startTimer() {
        int initialDelay = 0;
        int delay = 1000; // Timer delay in milliseconds (1 second)
        ActionListener timerAction = new ActionListener() {
            private int seconds = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                // Notify UI with the elapsed time
                notifyElapsedTime(seconds);
            }
        };
        gameTimer = new Timer(delay, timerAction);
        gameTimer.setInitialDelay(initialDelay);
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    private void handleGameEnd() {
        stopTimer();
        notifyGameEnded();
    }

    // Implement the following methods to register and notify the UI
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    private void notifyCardFlipped() {
        for (Observer observer : observers) {
            observer.onCardFlipped((Card) flippedCards);
        }
    }

    private void notifyElapsedTime(int elapsedSeconds) {
        for (Observer observer : observers) {
            observer.onElapsedTimeUpdate(elapsedSeconds);
        }
    }

    private void notifyGameEnded() {
        for (Observer observer : observers) {
            observer.onGameEnded();
        }
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public boolean isCardFlipped(Card card) {
        return flippedCards.containsKey(card.getValue());
    }

    public List<Card> getCardsInPlay() {
        return cardsInPlay;
    }

    public boolean isMatchMade() {
        return flippedCards.size() == 2 && firstFlippedCard.getValue().equals(secondFlippedCard.getValue());
    }
}
