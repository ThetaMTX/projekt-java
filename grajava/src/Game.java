import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//poczatek gry
public class Game extends JFrame {

    private static final float LATE_TIME = 1.0f;


    // Guziki w menu głównym
    private JButton playButton;
    private JButton howToPlayButton;
    private JButton exitButton;

    // Etykieta tekstowa z informacją o liczbie punktów
    private JLabel pointsLabel;
    private JPanel menuPanel;

    private JPanel gamePanel;
    private static final float MAX_CIRCLES =5 ;
    // Szerokość i wysokość okna gry
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    // Czas trwania gry (w sekundach)
    private static final int GAME_DURATION = 30;

    // Liczba punktów za kliknięcie kółka na czas
    private static final int POINTS_ON_TIME = 300;

    // Liczba punktów za kliknięcie kółka za późno
    private static final int POINTS_LATE = 100;

    // Liczba missed, po której gra się kończy
    private static final int MISSED_LIMIT = 3;

    // Panel z grą


    // Lista kółek
    private List<Circle> circles;

    // Losowy generator liczb
    private Random random;

    // Czas rozpoczęcia gry
    private long startTime;

    // Liczba punktów gracza
    private int points;

    // Liczba missed
    private int missed;

    // Czy gra jest aktywna
    private boolean gameActive;

    //Gra
    public Game() {
        menuPanel = new JPanel();
        gamePanel = new JPanel();
        pointsLabel = new JLabel("Liczba punktów: " + points);
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        setSize(WIDTH, HEIGHT);
        setTitle("Beat Circles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dodanie etykiety tekstowej do panelu gry
        add(pointsLabel, BorderLayout.NORTH);
        // Ustawienie layoutu na null, aby móc ręcznie ustawić położenie elementów
        setLayout(null);

        // Stworzenie obiektu JLabel z obrazkiem tła
        JLabel backgroundLabel = new JLabel(new ImageIcon("resources/czerwony.png"));
        add(backgroundLabel, BorderLayout.CENTER);
        // Dodanie przycisku "Graj"
        JButton playButton = new JButton("Graj");
        playButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 - 25, 100, 50);
        playButton.addActionListener(e -> startGame());
        add(playButton);

        // Dodanie przycisku "Jak grać"
        JButton howToPlayButton = new JButton("Jak grać");
        howToPlayButton.setBounds(WIDTH / 2 - 75, HEIGHT / 2 + 50, 150, 50);
        howToPlayButton.addActionListener(e -> showHowToPlay());
        add(howToPlayButton);

        // Dodanie przycisku "Wyjście"
        JButton exitButton = new JButton("Wyjście");
        exitButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 + 125, 100, 50);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);
        // Stworzenie panelu z grą
        gamePanel = new GamePanel();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBounds(0, 0, WIDTH, HEIGHT);
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Jeśli gra jest aktywna, sprawdź, czy kliknięto w kółko
                if (gameActive) {
                    checkCircleClicked(e.getX(), e.getY());
                }
            }
        });
        add(gamePanel);

        // Stworzenie pustej listy kółek
        circles = new ArrayList<>();

        // Stworzenie obiektu losowego
        random = new Random();

        // Ustawienie flagi, że gra jest nieaktywna
        gameActive = false;
    }

    private void startGame() {
        JFrame gameFrame = new JFrame();
        JPanel startGamePanel = new JPanel();
        startGamePanel.setLayout(new BorderLayout());
        // Dodanie tła
        startGamePanel.add(new JLabel(new ImageIcon("resources/zielony.png")), BorderLayout.CENTER);
        // Ustawienie początkowych wartości
        gameFrame.setTitle("Gra");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.setSize(1024, 768);
        gameFrame.add(gamePanel, BorderLayout.CENTER);
        gameFrame.setLocationRelativeTo(null);
        points = 0;
        missed = 0;
        startTime = System.currentTimeMillis();
        gameActive = true;

        // Wyświetlenie okna z grą
        gameFrame.setVisible(true);
        // Stworzenie nowej listy kółek
        circles.clear();

        // Dodanie pierwszego kółka
        addCircle();

        // Uruchomienie timera
        Timer timer = new Timer(1000 / 60, e -> {
            long currentTime = System.currentTimeMillis();
            float elapsedTime = (currentTime - startTime) / 1000.0f;

            // Sprawdzenie, czy gra się skończyła
            if (elapsedTime > GAME_DURATION || missed >= MISSED_LIMIT) {
                gameActive = false;
                showGameOver();
                return;
            }

            // Aktualizacja położenia kółek i sprawdzenie, czy należy dodać nowe kółko
            updateCircles();

            // Odświeżenie panelu z grą
            gamePanel.repaint();
        });
        timer.start();
    }

    // Metoda, która jest wywoływana co określony interwał czasu podczas gry
    private void updateGame(float deltaTime) {
        // Sprawdzenie, czy gra jest aktywna
        if (gameActive) {
            // Ukrycie guzików
            playButton.setVisible(false);
            howToPlayButton.setVisible(false);
            exitButton.setVisible(false);

            // ...
        }
    }

    private void showHowToPlay() {
        // Stworzenie panelu z instrukcją
        JPanel howToPlayPanel = new JPanel();
        howToPlayPanel.setLayout(new BorderLayout());

        // Dodanie tła
        howToPlayPanel.add(new JLabel(new ImageIcon("resources/jakgrac.png")), BorderLayout.CENTER);

        // Stworzenie panelu z tekstem
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // Dodanie tekstu do panelu
        JLabel textLabel1 = new JLabel("<html>Celem gry jest kliknięcie jak największej liczby kółek<br>przed ich zniknięciem.</html>");
        textLabel1.setForeground(Color.BLACK);
        textLabel1.setFont(new Font("Arial", Font.PLAIN, 24));
        textPanel.add(textLabel1);
        textPanel.add(Box.createVerticalStrut(10));

        JLabel textLabel2 = new JLabel("<html>Za kliknięcie kółka na czas otrzymasz " + POINTS_ON_TIME +" po "+ MISSED_LIMIT + " chybionych kliknięciach gra się kończy.</html>");
        textLabel2.setForeground(Color.BLACK);
        textLabel2.setFont(new Font("Arial", Font.PLAIN, 24));
        textPanel.add(textLabel2);
        textPanel.add(Box.createVerticalStrut(10));

        JLabel textLabel3 = new JLabel("<html>Naciśnij przycisk Powrót, aby wrócić do menu głównego.</html>");
        textLabel3.setForeground(Color.BLACK);
        textLabel3.setFont(new Font("Arial", Font.PLAIN, 24));
        textPanel.add(textLabel3);

        // Dodanie panelu z tekstem do panelu z instrukcją
        howToPlayPanel.add(textPanel, BorderLayout.SOUTH);

        // Stworzenie okna z instrukcją
        JFrame howToPlayFrame = new JFrame("Jak grać");
        howToPlayFrame.setSize(WIDTH, HEIGHT);
        howToPlayFrame.setResizable(false);
        howToPlayFrame.setLayout(new BorderLayout());
        howToPlayFrame.add(howToPlayPanel, BorderLayout.CENTER);

        // Stworzenie i dodanie przycisku "Powrót"
        JButton backButton = new JButton("Powrót");
        backButton.addActionListener(e -> howToPlayFrame.dispose());
        howToPlayFrame.add(backButton, BorderLayout.SOUTH);

// Wyświetlenie okna z instrukcją
        howToPlayFrame.setVisible(true);
    }
    private boolean gameOverScreenShown = false;

    private void showGameOver() {
        if (gameOverScreenShown) {
            return;
        }
        gameOverScreenShown = true;

        // Stworzenie panelu z informacją o zakończeniu gry
        JPanel gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BorderLayout());

        // Dodanie tła
        gameOverPanel.add(new JLabel(new ImageIcon("resouces/backgroundgameover.png")), BorderLayout.CENTER);

        // Stworzenie panelu z tekstem
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // Dodanie tekstu do panelu
        JLabel textLabel1 = new JLabel("KONIEC GRY");
        textLabel1.setForeground(Color.RED);
        textLabel1.setFont(new Font("Arial", Font.PLAIN, 36));
        textPanel.add(textLabel1);
        JLabel textLabel2 = new JLabel("Liczba punktów: " + points);
        textLabel2.setForeground(Color.BLACK);
        textLabel2.setFont(new Font("Arial", Font.PLAIN, 24));
        textPanel.add(textLabel2);

        // Dodanie panelu z tekstem do panelu z informacją o zakończeniu gry
        gameOverPanel.add(textPanel, BorderLayout.NORTH);

        // Stworzenie okna z informacją o zakończeniu gry
        JFrame gameOverFrame = new JFrame();
        gameOverFrame.setTitle("KONIEC GRY");
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverFrame.setResizable(false);
        gameOverFrame.setSize(300, 200);
        gameOverFrame.setLocationRelativeTo(null);
        gameOverFrame.add(gameOverPanel, BorderLayout.CENTER);

        // Stworzenie i dodanie przycisku "Powrót do menu"
        JButton backButton = new JButton("Powrót do menu");
        backButton.addActionListener(e -> gameOverFrame.dispose());
        gameOverFrame.add(backButton, BorderLayout.SOUTH);

        // Wyświetlenie okna z informacją o zakończeniu gry
        gameOverFrame.setVisible(true);
    }




    private void addCircle() {
        // Losowanie położenia kółka
        int x = random.nextInt(WIDTH - 100) + 50;
        int y = random.nextInt(HEIGHT - 100) + 50;

        // Losowanie promienia kółka
        int radius = random.nextInt(35) + 25;

        // Losowanie czasu, po którym kółko zniknie
        float time = random.nextInt(10) + 15;

        // Losowanie liczby punktów za kliknięcie kółka
        int points = random.nextInt(3) * 50 + 100;

        // Dodanie kółka do listy
        circles.add(new Circle(x, y, radius, time, points));
    }

    private void updateCircles() {
        // Pobranie aktualnego czasu
        long currentTime = System.currentTimeMillis();

        // Przeszukanie listy kółek
        for (int i = circles.size() - 1; i >= 0; i--) {
            Circle circle = circles.get(i);

            // Obliczenie upływającego czasu dla kółka
            float elapsedTime = (currentTime - startTime) / 1000.0f;

            // Jeśli czas upłynął, usunięcie kółka z listy
            if (elapsedTime > circle.time + LATE_TIME) {
                circles.remove(i);
                missed++;
            }
        }

        // Dodanie nowego kółka, jeśli lista jest mniejsza niż maksymalna liczba kółek
        if (circles.size() < MAX_CIRCLES) {
            addCircle();
        }
    }

    private void checkCircleClicked(int x, int y) {

        // Przeszukanie listy kółek
        for (int i = circles.size() - 1; i >= 0; i--) {
            Circle circle = circles.get(i);

            // Obliczenie odległości pomiędzy kliknięciem a środkiem kółka
            double distance = Math.sqrt(Math.pow(x - circle.x, 2) + Math.pow(y - circle.y, 2));

            // Jeśli odległość jest mniejsza niż promień kółka, kliknięto w kółko
            if (distance < circle.radius) {
                // Sprawdzenie, czy kliknięto na czas
                long currentTime = System.currentTimeMillis();
                float elapsedTime = (currentTime - startTime) / 1000.0f;
                if (elapsedTime < circle.time) {
                    points += POINTS_ON_TIME;
                } else if (elapsedTime < circle.time + LATE_TIME) {
                    points += POINTS_LATE;
                } else {
                    missed++;
                }

                // Usunięcie kółka z listy
                circles.remove(i);

                // Dodanie nowego kółka
                addCircle();
                break;
            }
        }
    }


    // Klasa wewnętrzna odpowiedzialna za rysowanie gry
    private class GamePanel extends JPanel {

        private Image backgroundImage;

        public GamePanel() {

            // Stworzenie obrazka tła
            backgroundImage = new ImageIcon("resources/background1.png").getImage();

            // Ustawienie panelu jako nieprzezroczystego
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Stworzenie obrazka tła
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            // Rysowanie tła
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            // Rysowanie kółek
            for (Circle circle : circles) {
                circle.draw(g);
            }

            // Rysowanie liczby punktów
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Punkty: " + points, 10, 30);
        }
    }

    // Klasa reprezentująca kółko
    private class Circle {
        // Położenie kółka
        int x;
        int y;

        // Promień kółka
        int radius;

        // Czas, po którym kółko zniknie
        float time;

        // Liczba punktów za kliknięcie kółka
        int points;

        Circle(int x, int y, int radius, float time, int points) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.time = time;
            this.points = points;
        }

        // Rysowanie kółka
        void draw(Graphics g) {
            // Obliczenie aktualnego stanu obwódki czasowej
            float progress = 1.0f - ((System.currentTimeMillis() - startTime) / 1000.0f) / time;

            // Rysowanie obwódki czasowej
            g.setColor(Color.GREEN);
            g.drawArc(x - radius, y - radius, radius * 2 , radius * 2, 90, (int) (360 * progress));

            // Rysowanie kółka
            g.setColor(Color.RED);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setVisible(true);
    }
}
