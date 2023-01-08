import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        Menu menu = new Menu(cardLayout, mainPanel);
        mainPanel.add(menu, "menu");

        Game game = new Game(cardLayout, mainPanel);
        mainPanel.add(game, "game");

        Game.GameOver gameOver = new Game.GameOver(cardLayout, mainPanel, Game.score);
        mainPanel.add(gameOver, "gameOver");

        HowToPlay howToPlay = new HowToPlay(cardLayout, mainPanel);
        mainPanel.add(howToPlay, "howToPlay");

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
        Timer timer = new Timer(16, new ActionListener() { // 16 milliseconds = 60 FPS
            public void actionPerformed(ActionEvent e) {
                game.update();
            }

        });
        timer.start();
}


static class Menu extends JPanel implements ActionListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JPanel setContentPane;

    public Menu(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        JPanel backgroundPanel = new JPanel(new FlowLayout());
        setLayout(new FlowLayout(FlowLayout.CENTER, 60, 300));

        JButton normalButton = new JButton("Normal");
        normalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game game = new Game(cardLayout, mainPanel);
                game.CIRCLE_SPEED = 10;
                game.start();
                mainPanel.add(game, "game");
                cardLayout.show(mainPanel, "game");
            }
        });
        add(normalButton);

        JButton hardButton = new JButton("Hard");
        hardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game game = new Game(cardLayout, mainPanel);
                game.CIRCLE_SPEED = 20;
                game.start();
                mainPanel.add(game, "game");
                cardLayout.show(mainPanel, "game");
            }
        });
        add(hardButton);

        JButton howToPlayButton = new JButton("How to Play");
        howToPlayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "howToPlay");
            }
        });
        add(howToPlayButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(exitButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        g.drawImage(new ImageIcon("resources/menubetter.png").getImage(), 0, 0, this);
        // Draw the text "Beat Breaker" above the buttons
        g.setColor(Color.PINK);
        g.setFont(new Font("Arial", Font.BOLD, 46));
        g.drawString("Beat Breaker", getWidth() / 2 -110, 150);
    }

}

    static class HowToPlay extends JPanel {
        private CardLayout cardLayout;
        private JPanel mainPanel;
        public HowToPlay(CardLayout cardLayout, JPanel mainPanel) {
            this.cardLayout = cardLayout;
            this.mainPanel = mainPanel;
            setLayout(new BorderLayout());

            JLabel instructionsLabel = new JLabel("<html>Zasady gry:<br>Klikaj na spadajace kolka kiedy beda na zielonej lini.<br>Nie dopusc do tego aby kolka doszly poza zielona linie bo stracisz zycie<br>Gra trwa 40 sekund <br>Sprobuj zdobyc jak najwiecej punktow!</html>");
            instructionsLabel.setFont(new Font("Arial", Font.BOLD, 26));  // Set font size and style
            instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(instructionsLabel, BorderLayout.SOUTH);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton mainMenuButton = new JButton("Main Menu");
            mainMenuButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "menu");
                }
            });
            buttonPanel.add(mainMenuButton);

            add(buttonPanel, BorderLayout.NORTH);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // existing code
            // Draw the background image
            g.drawImage(new ImageIcon("resources/jakgrac.png").getImage(), 2, 80, this);
        }
    }


    static class Game extends JPanel implements ActionListener, MouseListener {
    private static final int CIRCLE_DIAMETER = 60;
    private static int CIRCLE_SPEED = 10;
    private static final int SPAWN_DELAY = 1000;
    private static final int NUM_LIVES = 3;
    private static final int GAME_DURATION = 40_000;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Timer spawnTimer;
    private Timer gameTimer;

    private Timer fps;
    private Random random;
    private List<Circle> circles;
    private static int score;
    private int lives;

    int FPS = 60;

    private Timer soundTimer;

    public Game(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        setPreferredSize(new Dimension(1024, 768));
        setBackground(Color.BLACK);
        addMouseListener(this);
        random = new Random();
        circles = new ArrayList<>();
        score = 0;
        lives = NUM_LIVES;

    }


    public void start() {
        reset();
        spawnTimer = new Timer(SPAWN_DELAY, this);
        spawnTimer.start();
        gameTimer = new Timer(GAME_DURATION, this);
        gameTimer.start();
        fps = new Timer(1000/FPS, this);
        fps.start();
    }
    public void reset() {
        circles.clear();
        score = 0;
        lives = NUM_LIVES;
        if (spawnTimer != null) {
            spawnTimer.stop();
        }
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == spawnTimer) {
            int x = random.nextInt(getWidth() - CIRCLE_DIAMETER);
            int y = 0 - CIRCLE_DIAMETER;
            Color color = Color.RED; // or any other color
            int dir = -1; // or -1 for downward direction
            circles.add(new Circle(x, y, CIRCLE_DIAMETER, CIRCLE_SPEED, color, dir));
        } else if (source == gameTimer) {
            GameOver gameOver = new GameOver(cardLayout, mainPanel, score);
            mainPanel.add(gameOver, "gameOver");
            cardLayout.show(mainPanel, "gameOver");
        }
        update();
        repaint();
    }

    public void update() {
        if (lives == 0) {
            gameTimer.stop();
            spawnTimer.stop();
            GameOver gameOver = new GameOver(cardLayout, mainPanel, score);
            mainPanel.add(gameOver, "gameOver");
            cardLayout.show(mainPanel, "gameOver");
        }
        for (Circle circle : circles) {
            circle.setY(circle.getY() + circle.getSpeed());
        }
        checkCollisions();
        removeDeadCircles();
    }



    public void checkCollisions() {
        for (int i = 0; i < circles.size(); i++) {
            Circle circle = circles.get(i);
            if (circle.getY() + CIRCLE_DIAMETER >= getHeight()) {
                lives--;
                circle.setAlive(false);
            }
        }
    }

    public void removeDeadCircles() {
        for (int i = circles.size() - 1; i >= 0; i--) {
            if (!circles.get(i).isAlive()) {
                circles.remove(i);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        for (int i = circles.size() - 1; i >= 0; i--) {
            Circle circle = circles.get(i);
            if (circle.contains(x, y)) {
                circles.remove(i);
                score++;
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        g.drawImage(new ImageIcon("resources/backgroundtest.png").getImage(), 0, -50, this); //image dla rozgrywki
        for (Circle c : circles) {
            g.setColor(c.getColor());
            g.fillOval(c.getX(), c.getY(), CIRCLE_DIAMETER, CIRCLE_DIAMETER);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, 10, 40);

    }

    public static class GameOver extends JPanel implements ActionListener {
        private CardLayout cardLayout;
        private JPanel mainPanel;
        private int score;

        public GameOver(CardLayout cardLayout, JPanel mainPanel, int score) {
            this.cardLayout = cardLayout;
            this.mainPanel = mainPanel;
            this.score = score;
            setLayout(new FlowLayout(FlowLayout.CENTER, 60, 300));

            JButton mainMenuButton = new JButton("Main Menu");
            mainMenuButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(mainPanel, "menu");
                }
            });
            add (mainMenuButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            add(exitButton);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);  // Set background color to black
            // Draw the background image
            g.drawImage(new ImageIcon("resources/gameover.png").getImage(), 150, 0, this);
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Score: " + score, getWidth() / 2-70, getHeight() / 2-95);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

}}
