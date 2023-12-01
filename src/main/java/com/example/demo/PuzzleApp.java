package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;

public class PuzzleApp extends Application implements Serializable {

    private PuzzleBoard puzzleBoard;
    private GridPane gridPane;
    private int moveCount = 0;
    private Label movesLabel;
    private Label timeLabel;
    private int secondsElapsed = 0;
    private Timeline timeline;
    private BooleanProperty gameStartedProperty = new SimpleBooleanProperty(false);
    private int tileNumber = 1;

    public static void main(String[] args) {
        launch(args);
    }
    /**
     * Initialise et lance l'application du jeu de taquin.
     * @param primaryStage La scène principale de l'application.
     */
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        gridPane = new GridPane();
        movesLabel = new Label("Nombre de déplacements: 0");
        timeLabel = new Label("Temps ecoulé: 0s");
        Label titleLabel = new Label("Taquin 4*4");
        titleLabel.getStyleClass().add("title-label");
        StackPane titlePane = new StackPane(titleLabel);
        titlePane.getStyleClass().add("title-pane");

        root.getChildren().add(titlePane);
        root.getChildren().addAll(createControlPane(primaryStage), gridPane, movesLabel, timeLabel);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 300, 350);

        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("jeu de taquin");

        // timeline pour le suivi du temps
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    secondsElapsed++;
                    timeLabel.setText("Temps ecoulé: " + secondsElapsed + "s");
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);

        // après 2 secondes
        Timeline initialShuffle = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> shuffleBoard())
        );
        initialShuffle.play();
        primaryStage.show();
    }
    /**
     * Crée et retourne le panneau de contrôle de l'interface utilisateur.
     * Le panneau contient des boutons pour choisir une image et débuter le jeu et pour mettre le en pause et quitter le jeu.
     * @param primaryStage La scène principale.
     * @return Un objet VBox qui représente le panneau de contrôle.
     */
    private VBox createControlPane(Stage primaryStage) {
        VBox controlPane = new VBox();
        controlPane.setSpacing(10);

        // Bouton choisir l'image
        Button chooseImageButton = new Button("Choisir une image");
        chooseImageButton.setOnAction(event -> chooseImage(primaryStage));
        controlPane.getChildren().add(chooseImageButton);

        // Bouton  commencer le jeu
        Button startGameButton = new Button("Débuter le jeu");
        startGameButton.managedProperty().bind(startGameButton.visibleProperty());
        startGameButton.setOnAction(event -> startGame());
        controlPane.getChildren().add(startGameButton);

        // Bouton mettre en pause le jeu
        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(event -> pauseGame());
        controlPane.getChildren().add(pauseButton);
        startGameButton.visibleProperty().bind(gameStartedProperty.not());
        // button quitter
        Button quitButton = new Button("Quitter");
        quitButton.setOnAction(event -> {

            try {
                quitGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        controlPane.getChildren().add(quitButton);

        return controlPane;
    }

    /**
     * fonction pour quitter le jeu
     * @throws IOException
     */
    private void quitGame() throws IOException {
        Platform.exit();
    }

    /**
     * Permet au joueur de choisir une image.
     * Une boîte de dialogue de sélection de fichier s'ouvre pour permettre à l'utilisateur de choisir une image
     * parmi les fichiers de type PNG, JPG, GIF ou BMP. Une fois l'image sélectionnée, elle est chargée
     * et utilisée pour initialiser le plateau de jeu avec la classe PuzzleBoard.
     *
     * @param primaryStage La scène principale.
     */
    private void chooseImage(Stage primaryStage) {
        // Créer un FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir l'image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image", "*.png", "*.jpg", "*.gif", "*.bmp")
        );
        // Afficher la boîte de dialogue de sélection de fichier
        File selectedFile = fileChooser.showOpenDialog(null);
        // Charger l'image sélectionnée et mettre à jour le jeu
        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            puzzleBoard = new PuzzleBoard(4, divideImage(selectedImage, 4, 4));
            updateGridPane();
            primaryStage.setWidth(selectedImage.getWidth()-750);
            primaryStage.setHeight(selectedImage.getHeight()+100);
            primaryStage.centerOnScreen();
        }
    }

    /**
     * Démarre le jeu.
     * Vérifie d'abord si le plateau de jeu est initialisé. Si c'est oui,il mélange le plateau , démarre le chronomètre.
     */
    private void startGame() {
        if (puzzleBoard != null) {
            shuffleBoard();
            // Démarrer le chronomètre après le mélange initial
            timeline.play();
            // Réinitialiser le numéro des tuiles
            tileNumber = 1;
        }
    }

    /**
     * fonction qui fait pause au jeu
     */
    private void pauseGame() {
        // Mettez en pause le chronomètre
        timeline.pause();
    }

    /**
     * fonction qui mélange le puzzle et debute le jeu
     */
    private void shuffleBoard() {
        // Réinitialiser le nombre de mouvements et le temps écoulé
        moveCount = 0;
        secondsElapsed = 0;
        movesLabel.setText("Nombre de déplacements: 0");
        timeLabel.setText("Temps ecoulé: 0s");

        // Arrêter le chronomètre s'il est en cours
        timeline.stop();

        // Mélanger le tableau
        puzzleBoard.shuffle();
        updateGridPane();
    }

    /**
     * fonction qui gére le déplacement selon la touche
     * touches « q » pour gauche, « s » pour bas, « d » pour droite, « z » pour haut
     * @param keyCode
     */
    private void handleKeyPress(KeyCode keyCode) {
        if (puzzleBoard != null) {
            int emptyRow = puzzleBoard.getEmptyRow();
            int emptyCol = puzzleBoard.getEmptyCol();

            switch (keyCode) {
                case D:
                    if (emptyCol > 0) {
                        puzzleBoard.moveTile(emptyRow, emptyCol - 1);
                        moveCount++;
                    }
                    break;
                case Q:
                    if (emptyCol < puzzleBoard.getSize() - 1) {
                        puzzleBoard.moveTile(emptyRow, emptyCol + 1);
                        moveCount++;
                    }
                    break;
                case S:
                    if (emptyRow > 0) {
                        puzzleBoard.moveTile(emptyRow - 1, emptyCol);
                        moveCount++;
                    }
                    break;
                case Z:
                    if (emptyRow < puzzleBoard.getSize() - 1) {
                        puzzleBoard.moveTile(emptyRow + 1, emptyCol);
                        moveCount++;
                    }
                    break;
            }
            updateGridPane();
            checkForWin();
            // Mettre à jour le label des mouvements
            movesLabel.setText("Nombre de deplacements: " + moveCount);
            // Démarrer le chronomètre si le jeu a commencé
            if (!timeline.getStatus().equals(Animation.Status.RUNNING)) {
                timeline.play();
            }
        }
    }

    /**
     * fonction qui actualise le contenu de plateau par exemple aprés un déplacement
     */
    private void updateGridPane() {
        gridPane.getChildren().clear();

        Tuile[][] tuiles = puzzleBoard.getTiles();
        int size = puzzleBoard.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tuile tile = tuiles[row][col];
                if (tile != null) {
                    ImageView imageView = new ImageView(tile.getImage());
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);

                    // Ajouter le numéro à l'image
                    Label numberLabel = new Label(Integer.toString(tile.getNumber()));
                    numberLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
                    StackPane stackPane = new StackPane();
                    stackPane.setMargin(imageView, new Insets(5));
                    stackPane.getChildren().addAll(imageView, numberLabel);
                    gridPane.add(stackPane, col, row);
                }
            }
        }
    }

    /**
     * fonction qui verifie si on a gagné la partie
     */
    private void checkForWin() {
        Tuile[][] tuiles = puzzleBoard.getTiles();
        int size = puzzleBoard.getSize();
        int expectedNumber = 1;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tuile tuile = tuiles[row][col];
                if (tuile != null) {
                    if (tuile.getNumber() != expectedNumber) {
                        // La partie n'est pas encore gagnée
                        return;
                    }
                    expectedNumber++;
                }
            }
        }

        // Arrêter le chronomètre lorsque le jeu est gagné
        timeline.stop();

        // ecrit dans la console
        System.out.println("Félicitations ! Vous avez gagné !");
        //fonction qui affiche sur l'interface
        Platform.runLater(() -> showWinDialog());
    }
    /**
     * Affiche une petite fenetre informant l'utilisateur qu'il a gagné la partie.
     */
    private void showWinDialog() {
        // Crée une boîte de dialogue (Alert) pour informer l'utilisateur qu'il a gagné
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Félicitations !");
        alert.setHeaderText("Vous avez gagné !");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        dialogPane.getStyleClass().add("win-dialog");
        alert.initStyle(StageStyle.UNDECORATED); // Remove default window decorations
        alert.showAndWait();
    }
    /**
     * Divise une image en un tableau bidimensionnel d'images.
     * Chaque image résultante représente une tuile du puzzle.
     *
     * @param image L'image à diviser.
     * @param rows Le nombre de lignes pour la division.
     * @param cols Le nombre de colonnes pour la division.
     * @return Un tableau  d'images représentant les tuiles .
     */
    private Image[][] divideImage(Image image, int rows, int cols) {
        int width = (int) image.getWidth() / cols;
        int height = (int) image.getHeight() / rows;

        Image[][] tuiles = new Image[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * width;
                int y = i * height;
                tuiles[i][j] = new WritableImage(image.getPixelReader(), x, y, width, height);
            }
        }

        return tuiles;
    }
}
