import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class View extends JPanel {
    private int width = 360;
    private int height = 640;
    private Logic logic;
    private Image backgroundImage;
    private JLabel scoreLabel;

    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());
        setFocusable(true);
        addKeyListener(logic);

        loadAssets();
        buildHUD();
    }

    private void loadAssets() {
        try {
            backgroundImage = loadImage("background.png");
        } catch (Exception e) {
            // Fallback to solid background
        }
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

    private void buildHUD() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel scorePill = new JPanel();
        scorePill.setBackground(new Color(0, 0, 0, 140));
        scorePill.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 160), 1),
            new EmptyBorder(4, 16, 4, 16)
        ));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setForeground(Color.WHITE);

        scorePill.add(scoreLabel);
        topPanel.add(scorePill);
        add(topPanel, BorderLayout.NORTH);
    }

    public void updateScore(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // 1. Gambar latar belakang
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        } else {
            g.setColor(new Color(112, 197, 206));
            g.fillRect(0, 0, width, height);
        }

        // 2. Gambar rintangan pipa
        ArrayList<Pipe> pipes = logic.getPipes();
        if (pipes != null) {
            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                if (pipe != null && pipe.getImage() != null) {
                    g.drawImage(
                        pipe.getImage(),
                        pipe.getPosX(),
                        pipe.getPosY(),
                        pipe.getWidth(),
                        pipe.getHeight(),
                        null
                    );
                }
            }
        }

        // 3. Gambar karakter player (burung)
        Player player = logic.getPlayer();
        if (player != null && player.getImage() != null) {
            g.drawImage(
                player.getImage(),
                player.getPosX(),
                player.getPosY(),
                player.getWidth(),
                player.getHeight(),
                null
            );
        }

        // 4. Gambar batas tanah bawah
        g.setColor(new Color(115, 190, 46));
        g.fillRect(0, height - 20, width, 20);

        // 5. Tampilan Game Over
        if (logic.isGameOver()) {
            drawGameOverOverlay(g);
        }
    }

    private void drawGameOverOverlay(Graphics g) {
        // Layar redup semi-transparan
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, width, height);

        // Banner Game Over
        g.setColor(new Color(230, 74, 25));
        g.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fmTitle = g.getFontMetrics();
        String title = "GAME OVER";
        int titleX = (width - fmTitle.stringWidth(title)) / 2;
        g.drawString(title, titleX, 220);

        // Panel Skor Akhir & High Score
        int boxW = 240;
        int boxH = 120;
        int boxX = (width - boxW) / 2;
        int boxY = 250;

        g.setColor(new Color(20, 20, 20, 210));
        g.fillRect(boxX, boxY, boxW, boxH);
        g.setColor(new Color(255, 255, 255, 180));
        g.drawRect(boxX, boxY, boxW, boxH);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String currentScoreText = "Skor Akhir: " + logic.getScore();
        g.drawString(currentScoreText, boxX + 25, boxY + 45);

        g.setColor(new Color(255, 215, 0));
        String highScoreText = "Skor Tertinggi: " + logic.getHighScore();
        g.drawString(highScoreText, boxX + 25, boxY + 85);

        // Panduan tombol interaktif
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fmPrompt = g.getFontMetrics();
        String promptRestart = "Tekan [R] untuk Main Lagi";
        int promptX = (width - fmPrompt.stringWidth(promptRestart)) / 2;
        g.drawString(promptRestart, promptX, 420);

        g.setColor(new Color(200, 200, 200));
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        FontMetrics fmMenu = g.getFontMetrics();
        String promptMenu = "Tekan [M] untuk Kembali ke Menu";
        int menuX = (width - fmMenu.stringWidth(promptMenu)) / 2;
        g.drawString(promptMenu, menuX, 450);
    }
}
