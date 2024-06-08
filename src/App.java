import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int windowWidth = 600;
        int windowHeight = windowWidth;

        JFrame window = new JFrame("Snake");
        window.setVisible(true);
        window.setSize(windowWidth, windowHeight);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SnakeGame snakeGame = new SnakeGame(windowWidth, windowHeight);
        window.add(snakeGame);
        window.pack();
    }
}
