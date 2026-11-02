import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("FlappyBird - DPBO 2026");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);

                final CardLayout cardLayout = new CardLayout();
                final JPanel rootContainer = new JPanel(cardLayout);

                final Logic logic = new Logic();
                final View gameView = new View(logic);
                logic.setView(gameView);

                Runnable onStartGame = new Runnable() {
                    @Override
                    public void run() {
                        cardLayout.show(rootContainer, "GAME");
                        gameView.requestFocusInWindow();
                        logic.startGame();
                    }
                };

                Runnable onReturnToMenu = new Runnable() {
                    @Override
                    public void run() {
                        cardLayout.show(rootContainer, "MENU");
                    }
                };
                logic.setOnReturnToMenu(onReturnToMenu);

                MainMenu mainMenu = new MainMenu(onStartGame);

                rootContainer.add(mainMenu, "MENU");
                rootContainer.add(gameView, "GAME");

                cardLayout.show(rootContainer, "MENU");

                frame.add(rootContainer);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
