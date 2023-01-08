import java.awt.*;

class Circle {
    private int x;
    private int y;
    private int diameter;
    private int speed;
    private Color color;
    private int dir;
    private boolean isAlive;

    public Circle(int x, int y, int diameter, int speed, Color color, int dir) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.speed = speed;
        this.color = color;
        this.dir = dir;
        this.isAlive = true;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval(x, y, diameter, diameter);
    }

    public boolean contains(int x, int y) {
        int xCenter = this.x + diameter / 2;
        int yCenter = this.y + diameter / 2;
        return (x >= this.x && x <= this.x + diameter) && (y >= this.y && y <= this.y + diameter);
    }
}
