import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class App {
    private JFrame window;
    public SnakeGame snakeGame;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 600;

    public static void main(String[] args) throws Exception {
        new App().showMenu();
    }

    public void showMenu() {
        if (window == null) {
            window = new JFrame("Snake");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            window.setLocationRelativeTo(null);
            window.setResizable(false);
        } else {
            window.getContentPane().removeAll();
        }

        MenuPanel menuPanel = new MenuPanel(this);
        window.add(menuPanel);
        window.setVisible(true);

        // Adding window listener
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopGame();
                window.dispose(); // Closing the JFrame window
            }
        });
    }

    public void startGame() {
        window.getContentPane().removeAll();
        snakeGame = new SnakeGame(WINDOW_WIDTH, WINDOW_HEIGHT, this);
        window.add(snakeGame);
        window.pack();
        snakeGame.requestFocus();
        window.setVisible(true);
    }

    public void stopGame() {
        if (snakeGame != null) {
            snakeGame.stopGame();
            if (snakeGame.uiThread != null) {
                snakeGame.stopUI();
            }
        }
    }
}
