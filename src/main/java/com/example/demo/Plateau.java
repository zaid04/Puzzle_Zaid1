package com.example.demo;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Plateau implements Serializable {
    private Tuilel[][] plateau;
    private int emptyRow;
    private int emptyCol;

    public Plateau() {
        this.plateau = new Tuilel[4][4];
        char lettre = 'a';

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.plateau[i][j] = new Tuilel(lettre++, i, j);
            }
        }
        this.emptyRow = 3;
        this.emptyCol = 3;
        this.plateau[emptyRow][emptyCol] = new Tuilel(' ', emptyRow, emptyCol);
        melangerPlateau();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Plateau p;

        File file = new File("partie.ser");
        if (file.exists()) {
            FileInputStream fileIn = new FileInputStream("partie.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);


            p = (Plateau) in.readObject();

            in.close();
            fileIn.close();

            System.out.println("vous etes de retour");
            p.afficherPlateau();
        } else {
            p = new Plateau();
        }

        ObjectOutputStream out = null;

        try {
            do {
                Scanner sc = new Scanner(System.in);
                System.out.println("   ");
                String d = sc.nextLine();
                if (d.equals("d")) {
                    p.deplacer(Plateau.Direction.LEFT);
                    System.out.println("Après le déplacement :");
                    p.afficherPlateau();
                } else if (d.equals("q")) {
                    p.deplacer(Plateau.Direction.RIGHT);
                    System.out.println("Après le déplacement :");
                    p.afficherPlateau();
                } else if (d.equals("s")) {
                    p.deplacer(Plateau.Direction.UP);
                    System.out.println("Après le déplacement :");
                    p.afficherPlateau();
                } else if (d.equals("z")) {
                    p.deplacer(Plateau.Direction.DOWN);
                    System.out.println("Après le déplacement :");
                    p.afficherPlateau();
                } else {
                    break;
                }

                out = new ObjectOutputStream(new FileOutputStream("partie.ser"));
                out.writeObject(p);

            } while (!p.ordre());
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    private void afficherPlateau() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(this.plateau[i][j].getLettre() + " ");
            }
            System.out.println();
        }
    }

    private void melangerPlateau() {
        Random rand = new Random();

        for (int i = 3; i > 0; i--) {
            for (int j = 3; j > 0; j--) {
                int m = rand.nextInt(i + 1);
                int n = rand.nextInt(j + 1);

                Tuilel temp = this.plateau[i][j];
                this.plateau[i][j] = this.plateau[m][n];
                this.plateau[m][n] = temp;

                if (this.plateau[i][j].getLettre() == ' ') {
                    this.emptyRow = i;
                    this.emptyCol = j;
                } else if (this.plateau[m][n].getLettre() == ' ') {
                    this.emptyRow = m;
                    this.emptyCol = n;
                }
            }
        }
    }

    public void deplacer(Direction direction) {
        int newRow = emptyRow;
        int newCol = emptyCol;

        switch (direction) {
            case UP:
                newRow--;
                break;
            case DOWN:
                newRow++;
                break;
            case LEFT:
                newCol--;
                break;
            case RIGHT:
                newCol++;
                break;
        }

        if (estDeplacementValide(newRow, newCol)) {
            plateau[emptyRow][emptyCol] = plateau[newRow][newCol];
            plateau[newRow][newCol] = new Tuilel(' ', newRow, newCol);
            emptyRow = newRow;
            emptyCol = newCol;
        }
    }

    private boolean estDeplacementValide(int row, int col) {
        return row >= 0 && row < 4 && col >= 0 && col < 4;
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public boolean ordre() {
        char lettre = 'a';

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (plateau[i][j].getLettre() != lettre) {
                    return false;
                }
                lettre++;
            }
        }

        return true;
    }
}

class Tuilel implements Serializable {
    private char lettre;
    private int row;
    private int col;

    public Tuilel(char lettre, int row, int col) {
        this.lettre = lettre;
        this.row = row;
        this.col = col;
    }

    public char getLettre() {
        return lettre;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
