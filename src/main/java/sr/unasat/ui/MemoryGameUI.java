package sr.unasat.ui;

import sr.unasat.api.Card;
import sr.unasat.game.MemoryGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MemoryGameUI implements Observer {
    private static MemoryGameUI instance;
    private JFrame frame;
    private JButton[] cardButtons;
    private MemoryGame memoryGame;
    private List<Card> cardsInPlay;
    private int elapsedSeconds = 0;

    private MemoryGameUI() {
        memoryGame = MemoryGame.getInstance();
        memoryGame.registerObserver(this);
        cardsInPlay = memoryGame.getCardsInPlay();
    }

    public static MemoryGameUI getInstance() {
        if (instance == null) {
            instance = new MemoryGameUI();
        }
        return instance;
    }

    public void initializeUI() {
        frame = new JFrame("Memory Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new GridLayout(4, 4));

        cardButtons = new JButton[16];
        for (int i = 0; i < cardButtons.length; i++) {
            cardButtons[i] = new JButton("Card " + (i + 1));
            cardButtons[i].addActionListener(new CardButtonListener(i));
            frame.add(cardButtons[i]);
        }

        frame.setVisible(true);
    }

    private void updateUI() {
        for (int i = 0; i < cardButtons.length; i++) {
            Card card = cardsInPlay.get(i);
            if (memoryGame.isCardFlipped(card)) {
                cardButtons[i].setText(card.getValue());
            } else {
                cardButtons[i].setText("Card " + (i + 1));
            }
        }
    }

    @Override
    public void onCardFlipped(Card card) {
        SwingUtilities.invokeLater(() -> {
            updateUI();

            if (memoryGame.isGameEnded()) {
                // Handle the game end, e.g., show a message with the number of matches made
                showGameEndMessage();
            }
        });
    }

    @Override
    public void onGameEnded() {
        // Show a message indicating that the game has ended and the elapsed time
        int elapsedSeconds = gameTimer.getDelay() / 1000;
        showGameEndMessage(elapsedSeconds);
    }

    private class CardButtonListener implements ActionListener {
        private int index;

        public CardButtonListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!memoryGame.isGameEnded() && !memoryGame.isCardFlipped(cardsInPlay.get(index))) {
                memoryGame.flipCard(cardsInPlay.get(index));

                // Check if the player has made a match and update the UI accordingly
                if (memoryGame.isMatchMade()) {
                    updateMatchesMade();
                }
            }
        }
    }

    private void showGameEndMessage(int elapsedSeconds) {
        // Show a message indicating the game has ended and the number of matches made
        JOptionPane.showMessageDialog(frame, "Game Over! Matches Made: " + matchesMade + " Elapsed Time: " + elapsedSeconds + " seconds");
    }

    private void updateElapsedTime() {
        elapsedSeconds++;
        // Update the UI to display the elapsed time, e.g., on a label or status bar
    }

    private void updateMatchesMade() {
        matchesMade++;
        // Update the UI to display the number of matches made
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MemoryGameUI.getInstance().initializeUI(); // Start the game UI
                MemoryGame.getInstance().startGame(); // Start the game logic
            }
        });
    }
}

