public class GameLogicThread extends Thread {
    private SnakeGame snakeGame;
    private boolean running;

    public GameLogicThread(SnakeGame snakeGame) {
        this.snakeGame = snakeGame;
        this.running = true;
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            snakeGame.move();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
