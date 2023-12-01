package com.example.demo;

import com.example.demo.Tuile;
import javafx.scene.image.Image;

import java.io.Serializable;

public class PuzzleBoard implements Serializable {
    private final int size;
    private Tuile[][] tuiles;
    private int emptyRow;
    private int emptyCol;

    /**
     * constructeur du plateau du puzzle
     * @param size
     * @param images
     */

    public PuzzleBoard(int size, Image[][] images) {
        this.size = size;
        this.tuiles = new Tuile[size][size];

        // Initialiser les tuiles avec les images
        int number = 1;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                tuiles[row][col] = new Tuile(number++, images[row][col]);
            }
        }

        // Positionner l'emplacement vide au coin inférieur droit
        emptyRow = size - 1;
        emptyCol = size - 1;
        tuiles[emptyRow][emptyCol] = null;
    }

    /**
     * getter qui retourne les tuiles
     * @return les tuiles
     */
    public Tuile[][] getTiles() {
        return tuiles;
    }

    /**
     * getter qui retourne la taille du puzzle
     * @return taille
     */
    public int getSize() {
        return size;
    }

    /**
     * getter la ligne vide
     * @return la ligne pour identifier la case vide
     */
    public int getEmptyRow() {
        return emptyRow;
    }

    /**
     * getter la column vide
     * @return la column vide pour identifier la case vide
     */
    public int getEmptyCol() {
        return emptyCol;
    }

    /**
     * pour deplacer la case vide
     * @param newRow
     * @param newCol
     */
    public void moveTile(int newRow, int newCol) {
        // Échanger la tuile avec l'emplacement vide
        Tuile temp = tuiles[newRow][newCol];
        tuiles[newRow][newCol] = null;
        tuiles[emptyRow][emptyCol] = temp;
        emptyRow = newRow;
        emptyCol = newCol;
    }

    /**
     * cette fonction permet de mélanger le puzzle pour qu'il soit en desordre
     *
     */
    public void shuffle() {
        // Effectuez un certain nombre de mouvements aléatoires pour mélanger les tuiles
        int shuffleCount = size * size * 10; // aj
        for (int i = 0; i < shuffleCount; i++) {
            int randomRow = (int) (Math.random() * size);
            int randomCol = (int) (Math.random() * size);

            // Vérifiez si le mouvement aléatoire est valide (non nul)
            if (tuiles[randomRow][randomCol] != null) {
                moveTile(randomRow, randomCol);
            }
        }
    }
}
