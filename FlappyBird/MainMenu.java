import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

public class MainMenu extends JPanel {
    private int width = 360;
    private int height = 640;
    private Image backgroundImage;
    private Image birdImage;
    private Runnable onStartGame;

    public MainMenu(Runnable onStartGame) {
        this.onStartGame = onStartGame;
        setPreferredSize(new Dimension(width, height));
        setLayout(new BorderLayout());

        loadAssets();
        buildUI();
    }

    private void loadAssets() {
        try {
            backgroundImage = loadImage("background.png");
            birdImage = loadImage("bird.png");
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

    private void buildUI() {
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(70, 30, 40, 30));

        JLabel titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(245, 166, 35));

        JLabel subLabel = new JLabel("DPBO 2026 - Swing GameDev", SwingConstants.CENTER);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setFont(new Font("Arial", Font.BOLD, 13));
        subLabel.setForeground(Color.WHITE);

        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(new Color(0, 0, 0, 130));
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        infoCard.setMaximumSize(new Dimension(280, 75));
        infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Devindra Azra Dwi Putra");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nimLabel = new JLabel("NIM: 2509003 - Kelas C2");
        nimLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        nimLabel.setForeground(new Color(220, 220, 220));
        nimLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hintLabel = new JLabel("Kontrol: [SPASI] Melompat | [R] Restart");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        hintLabel.setForeground(new Color(255, 215, 0));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoCard.add(nameLabel);
        infoCard.add(Box.createVerticalStrut(3));
        infoCard.add(nimLabel);
        infoCard.add(Box.createVerticalStrut(4));
        infoCard.add(hintLabel);

        JButton btnStart = createStyledButton("Mulai Permainan", new Color(46, 125, 50), new Color(56, 142, 60));
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onStartGame != null) {
                    onStartGame.run();
                }
            }
        });

        JButton btnExit = createStyledButton("Keluar", new Color(198, 40, 40), new Color(211, 47, 47));
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(subLabel);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(infoCard);
        centerPanel.add(Box.createVerticalStrut(45));
        centerPanel.add(btnStart);
        centerPanel.add(Box.createVerticalStrut(14));
        centerPanel.add(btnExit);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, final Color normalColor, final Color hoverColor) {
        final JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 46));
        button.setPreferredSize(new Dimension(240, 46));
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(normalColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 180), 2),
            new EmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, width, height, null);
        } else {
            g.setColor(new Color(112, 197, 206));
            g.fillRect(0, 0, width, height);
        }

        if (birdImage != null) {
            int birdX = (width / 2) - 17;
            int birdY = 160;
            g.drawImage(birdImage, birdX, birdY, 34, 24, null);
        }
    }
}
