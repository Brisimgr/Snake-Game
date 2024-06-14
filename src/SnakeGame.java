import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private GameLogicThread logicThread;
    public UIMainThread uiThread;

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;

    int velocityX;
    int velocityY;
    boolean gameOver = false;
    
    private JButton backToMenuButton;
    private App app;
    private int bestScore;
    private static final String SCORE_FILE = "best_score.txt";

    SnakeGame(int boardWidth, int boardHeight, App app) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.app = app;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        setLayout(null); // Umożliwia ręczne ustawienie pozycji komponentów
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        logicThread = new GameLogicThread(this);
        uiThread = new UIMainThread(this);
        logicThread.start();
        uiThread.start();

        bestScore = loadBestScore();
        initBackToMenuButton();
    }

    public void stopGame() {
        logicThread.stopThread();
    }
    
    public void stopUI() {
        uiThread.stopThread();
    }

    private void initBackToMenuButton() {
        backToMenuButton = new JButton("Back to Menu");
        backToMenuButton.setFont(new Font("Arial", Font.BOLD, 16));
        backToMenuButton.setForeground(Color.WHITE);
        backToMenuButton.setBackground(Color.RED);
        backToMenuButton.setFocusPainted(false);
        backToMenuButton.setBorderPainted(false);
        backToMenuButton.setOpaque(true);
        backToMenuButton.setBounds(boardWidth / 2 - 75, boardHeight / 2 - 20, 150, 40); // Środek planszy
        backToMenuButton.setVisible(false); // Ukryj przycisk początkowo

        backToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.showMenu();
            }
        });

        add(backToMenuButton);
    }

    private void showBackToMenuButton() {
        backToMenuButton.setVisible(true);
    }

    private void hideBackToMenuButton() {
        backToMenuButton.setVisible(false);
    }

    private int loadBestScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    private void saveBestScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw border
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, boardWidth - 1, boardHeight - 1);

        // Gradient for snake
        GradientPaint gp = new GradientPaint(0, 0, Color.GREEN, tileSize, tileSize, Color.GREEN.darker(), true);

        // Draw food with rounded corners
        g2d.setColor(Color.RED);
        g2d.fillRoundRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, tileSize / 2, tileSize / 2);

        // Draw snake head
        g2d.setPaint(gp);
        g2d.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        // Draw snake body
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g2d.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }

        // Draw score and best score with shadow
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 15, tileSize + 1);
        g2d.drawString("Best Score: " + bestScore, tileSize - 15, tileSize + 21);

        if (gameOver) {
            g2d.setColor(Color.RED);
            g2d.drawString("Game Over: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
            g2d.drawString("Best Score: " + bestScore, tileSize - 16, tileSize + 20);
            showBackToMenuButton();
        } else {
            g2d.setColor(Color.GREEN);
            g2d.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
            g2d.drawString("Best Score: " + bestScore, tileSize - 16, tileSize + 20);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt((boardHeight / tileSize));
    }

    public boolean collision(Tile t1, Tile t2) {
        return t1.x == t2.x && t1.y == t2.y;
    }

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize >= boardWidth || snakeHead.y * tileSize < 0
                || snakeHead.y * tileSize >= boardHeight) {
            gameOver = true;
        }

        if (gameOver) {
            if (snakeBody.size() > bestScore) {
                bestScore = snakeBody.size();
                saveBestScore(bestScore);
            }
            stopGame();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
