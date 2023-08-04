package sr.unasat.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Timer {
    private Timer timer;
    private int elapsedSeconds;
    private ActionListener listener;

    public Timer(int delay, ActionListener listener) {
        elapsedSeconds = 0;
        this.listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedSeconds++;
            }
        };
    }

    public void start() {
        int delay = 1000; // Timer delay in milliseconds (1 second)
        timer = new Timer(delay, listener);
        timer.start();
    }

    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    public boolean isRunning() {
        return false;
    }

    public void reset() {
        elapsedSeconds = 0;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void setRepeats(boolean b) {
    }

    public void setInitialDelay(int initialDelay) {
    }
}
