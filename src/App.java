import javax.swing.*;

public class App {
    private JFrame window;
    private MenuPanel menuPanel;
    private SnakeGame snakeGame;
    private int windowWidth = 600;
    private int windowHeight = windowWidth;

    public static void main(String[] args) throws Exception {
        new App().createAndShowGUI();
    }

    private void createAndShowGUI() {
        window = new JFrame("Snake");
        window.setSize(windowWidth, windowHeight);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuPanel = new MenuPanel(this);
        window.add(menuPanel);
        window.setVisible(true);
    }

    public void startGame() {
        window.remove(menuPanel);
        snakeGame = new SnakeGame(windowWidth, windowHeight);
        window.add(snakeGame);
        window.revalidate();
        window.repaint();
        snakeGame.requestFocus();
    }
}
