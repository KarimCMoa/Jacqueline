package eu.karim.maze;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.util.Stack;

public class MazeSolver extends JPanel {
    private BufferedImage mazeImage;
    private int startX, startY;
    private boolean startSelected = false;

    public MazeSolver(String imagePath) {
        try {
            // Charger l'image à partir de n'importe quel format pris en charge (PNG, JPG, JPEG, etc.)
            mazeImage = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ajouter un écouteur de clic pour sélectionner le point de départ
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!startSelected) {
                    startX = e.getX();
                    startY = e.getY();
                    startSelected = true;
                    System.out.println("Spawn Point selected : " + startX + ", " + startY);
                    // Après avoir sélectionné, résoudre le labyrinthe
                    solveMaze();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(mazeImage, 0, 0, null);
    }

    // Algorithme pour résoudre le labyrinthe
    public void solveMaze() {
        // Appliquer DFS itératif
        dfsIterative(startX, startY);
    }

    // DFS itératif avec affichage lent des tracés
    public void dfsIterative(int startX, int startY) {
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(startX, startY));

        boolean[][] visited = new boolean[mazeImage.getWidth()][mazeImage.getHeight()];
        visited[startX][startY] = true;
        markVisited(startX, startY, Color.RED); // Marquer le point de départ en rouge
        repaint(); // Afficher la mise à jour graphique
        Toolkit.getDefaultToolkit().sync(); // Synchroniser l'affichage graphique

        while (!stack.isEmpty()) {
            Point current = stack.pop();
            int x = current.x;
            int y = current.y;

            // Délai pour permettre de voir chaque étape
            try {
                Thread.sleep(0); // 100 millisecondes entre chaque étape
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Parcourir les 4 directions (droite, gauche, haut, bas)
            // Droite
            if (canMove(x + 1, y, visited)) {
                System.out.println("Direction: Right, Coordinates: (" + (x + 1) + ", " + y + ")");
                stack.push(new Point(x + 1, y));
                visited[x + 1][y] = true;
                markVisited(x + 1, y, Color.RED);
                repaint(); // Mettre à jour l'affichage après avoir tracé
                Toolkit.getDefaultToolkit().sync(); // Synchroniser l'affichage graphique
            }
            // Gauche
            if (canMove(x - 1, y, visited)) {
                System.out.println("Direction: Left, Coordinates: (" + (x - 1) + ", " + y + ")");
                stack.push(new Point(x - 1, y));
                visited[x - 1][y] = true;
                markVisited(x - 1, y, Color.RED);
                repaint();
                Toolkit.getDefaultToolkit().sync();
            }
            // Bas
            if (canMove(x, y + 1, visited)) {
                System.out.println("Direction: Down, Coordinates: (" + x + ", " + (y + 1) + ")");
                stack.push(new Point(x, y + 1));
                visited[x][y + 1] = true;
                markVisited(x, y + 1, Color.RED);
                repaint();
                Toolkit.getDefaultToolkit().sync();
            }
            // Haut
            if (canMove(x, y - 1, visited)) {
                System.out.println("Direction: Top, Coordinates: (" + x + ", " + (y - 1) + ")");
                stack.push(new Point(x, y - 1));
                visited[x][y - 1] = true;
                markVisited(x, y - 1, Color.RED);
                repaint();
                Toolkit.getDefaultToolkit().sync();
            }
        }
    }

    // Vérifie si on peut bouger vers une direction
    private boolean canMove(int x, int y, boolean[][] visited) {
        if (x < 0 || y < 0 || x >= mazeImage.getWidth() || y >= mazeImage.getHeight()) {
            return false; // En dehors des limites de l'image
        }

        int pixelColor = mazeImage.getRGB(x, y);
        return !isWall(pixelColor) && !visited[x][y]; // Doit être un chemin non visité
    }

    // Marquer une zone comme visitée
    private void markVisited(int x, int y, Color color) {
        int currentColor = mazeImage.getRGB(x, y);

        if (currentColor == Color.RED.getRGB()) {
            // Si déjà rouge, passer en gris
            mazeImage.setRGB(x, y, Color.GRAY.getRGB());
        } else {
            // Marquer en rouge
            mazeImage.setRGB(x, y, color.getRGB());
        }
    }

    // Méthode pour détecter si un pixel est un mur
    private boolean isWall(int pixelColor) {
        // Par exemple, si le pixel est noir (pour les contours)
        return pixelColor == Color.BLACK.getRGB();
    }

    public static void main(String[] args) {
        // Modifier facilement l'image en changeant simplement le nom du fichier
        String imagePath = "maze2.jpg"; // Remplacez ce chemin par le nom de votre image (PNG, JPG, JPEG)

        JFrame frame = new JFrame("Maze Solver");
        MazeSolver mazePanel = new MazeSolver(imagePath);
        frame.add(mazePanel);
        frame.setSize(mazePanel.mazeImage.getWidth(), mazePanel.mazeImage.getHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
