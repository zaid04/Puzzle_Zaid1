package com.example.demo;

import javafx.scene.image.Image;

import java.io.Serializable;

public class Tuile implements Serializable {
    private final int number;
    private final Image image;

    /**
     * constructeur de la tuile
     * @param number
     * @param image
     */
    public Tuile(int number, Image image) {
        this.number = number;
        this.image = image;
    }

    /**
     * getter du numéro de la tuile
     * @return numéro de la tuile
     */
    public int getNumber() {
        return number;
    }

    /**
     * getter de l'image contenant dans la tuile
     * @return image
     */
    public Image getImage() {
        return image;
    }

}
