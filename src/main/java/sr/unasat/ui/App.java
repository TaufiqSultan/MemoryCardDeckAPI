package sr.unasat.ui;

import sr.unasat.game.MemoryGame;

public class Main {
    public static void main(String[] args) {
        MemoryGameUI.getInstance().initializeUI(); // Start the game UI
        MemoryGame.getInstance().startGame(); // Start the game logic
    }
}
