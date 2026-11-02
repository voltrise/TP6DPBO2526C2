import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class Logic implements ActionListener, KeyListener {
    private int frameWidth = 360;
    private int frameHeight = 640;

    private int playerStartPosX = frameWidth / 3;
    private int playerStartPosY = frameHeight / 2;
    private int playerWidth = 34;
    private int playerHeight = 24;

    private int pipeStartPosX = frameWidth;
    private int pipeStartPosY = 0;
    private int pipeWidth = 64;
    private int pipeHeight = 512;
    private int pipeVelocityX = -2;
    private int gravity = 1;

    private Player player;
    private ArrayList<Pipe> pipes;

    private Image birdImage;
    private Image upperPipeImage;
    private Image lowerPipeImage;

    private Timer gameLoop;
    private Timer pipesCooldown;
    private View view;

    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;
    private Runnable onReturnToMenu;

    public Logic() {
        loadAssets();

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        gameLoop = new Timer(1000 / 60, this);

        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    placePipes();
                }
            }
        });
    }

    private void loadAssets() {
        birdImage = loadImage("bird.png");
        upperPipeImage = loadImage("upperPipe.png");
        lowerPipeImage = loadImage("lowerPipe.png");
    }

    private Image loadImage(String fileName) {
        URL url = getClass().getResource("/assets/" + fileName);
        if (url == null) {
            url = getClass().getResource("assets/" + fileName);
        }
        if (url != null) {
            return new ImageIcon(url).getImage();
        }
        File file = new File("assets/" + fileName);
        if (!file.exists()) {
            file = new File("FlappyBird/assets/" + fileName);
        }
        if (file.exists()) {
            return new ImageIcon(file.getAbsolutePath()).getImage();
        }
        return null;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setOnReturnToMenu(Runnable onReturnToMenu) {
        this.onReturnToMenu = onReturnToMenu;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Pipe> getPipes() {
        return pipes;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void startGame() {
        resetState();
        gameLoop.start();
        pipesCooldown.start();
        SoundManager.playBgm();
        SoundManager.play("wing");
    }

    public void restartGame() {
        resetState();
        gameLoop.start();
        pipesCooldown.start();
        SoundManager.playBgm();
        SoundManager.play("wing");
    }

    private void resetState() {
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);
        pipes.clear();
        score = 0;
        gameOver = false;
        if (view != null) {
            view.updateScore(score);
            view.repaint();
        }
    }

    public void placePipes() {
        int randomPosY = (int) (pipeStartPosY - (pipeHeight / 4) - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage, true);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPosY + pipeHeight + openingSpace, pipeWidth, pipeHeight, lowerPipeImage, false);
        pipes.add(lowerPipe);
    }

    public void move() {
        if (gameOver) {
            return;
        }

        // Pergerakan vertikal dan gravitasi player
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        // Tabrakan batas bawah lantai
        if (player.getPosY() + player.getHeight() >= frameHeight - 20) {
            player.setPosY(frameHeight - 20 - player.getHeight());
            triggerGameOver();
            return;
        }

        // Pergerakan pipa, tabrakan, dan akumulasi skor
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);

            // Deteksi tabrakan AABB
            if (checkCollision(player, pipe)) {
                triggerGameOver();
                return;
            }

            // Skor bertambah +1 saat melewati pipa atas
            if (!pipe.isPassed() && pipe.isUpper() && player.getPosX() > pipe.getPosX() + pipe.getWidth()) {
                pipe.setPassed(true);
                score++;
                if (score > highScore) {
                    highScore = score;
                }
                SoundManager.play("point");
                if (view != null) {
                    view.updateScore(score);
                }
            }
        }

        // Hapus pipa yang sudah keluar dari layar kiri
        for (int i = pipes.size() - 1; i >= 0; i--) {
            if (pipes.get(i).getPosX() + pipes.get(i).getWidth() < 0) {
                pipes.remove(i);
            }
        }
    }

    public boolean checkCollision(Player a, Pipe b) {
        return a.getPosX() < b.getPosX() + b.getWidth() &&
               a.getPosX() + a.getWidth() > b.getPosX() &&
               a.getPosY() < b.getPosY() + b.getHeight() &&
               a.getPosY() + a.getHeight() > b.getPosY();
    }

    private void triggerGameOver() {
        if (gameOver) {
            return;
        }
        gameOver = true;
        gameLoop.stop();
        pipesCooldown.stop();
        SoundManager.stopBgm();
        SoundManager.play("hit");
        SoundManager.play("die");

        if (score > highScore) {
            highScore = score;
        }

        if (view != null) {
            view.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        if (view != null) {
            view.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            player.setVelocityY(-9);
            SoundManager.play("wing");
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame();
        } else if (e.getKeyCode() == KeyEvent.VK_M && gameOver) {
            if (onReturnToMenu != null) {
                onReturnToMenu.run();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
