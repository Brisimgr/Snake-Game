public class UIMainThread extends Thread {
    private SnakeGame snakeGame;
    private boolean running;

    public UIMainThread(SnakeGame snakeGame) {
        this.snakeGame = snakeGame;
        this.running = true;
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            snakeGame.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
