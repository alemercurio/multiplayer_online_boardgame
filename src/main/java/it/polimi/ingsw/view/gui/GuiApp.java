package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.controllers.ConnectionController;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GuiApp extends Application {
    public static Stage window;
    public static Scene mainMenu;
    private static Scene waitingScene;
    private static Client client;

    private static String gameChoice;
    private static int numPlayers;
    private static String soloMode;

    private GuiView view;

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 650;

    @Override
    public void start(Stage primaryStage) {
        this.view = GuiView.getGuiView();
        this.view.setGuiApp(this);

        window = primaryStage;

        try {
            Dialog<String> selectConnection = new Dialog<String>();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/connection.fxml"));
            try { selectConnection.setDialogPane(loader.load()); }
            catch (IOException ignored) { }
            selectConnection.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> selectConnection.setResult("esc"));
            loader.<ConnectionController>getController().setDialog(selectConnection);

            Optional<String> result = selectConnection.showAndWait();
            if (result.isPresent()) {
                GuiView.getGuiView().event(ViewEvent.CONNECTION_INFO,result.get());
                if(result.get().equals("esc")) {
                    Platform.exit();
                    return;
                }
            }
            else Platform.exit();
        } catch(Exception e) {
            e.printStackTrace();
            Platform.exit();
        }

        Scene scene = new Scene(createContent());
        mainMenu = scene;
        primaryStage.setTitle("Master of Renaissance");
        primaryStage.setScene(scene);
        primaryStage.show();

        window.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }

    private Pane root = new AnchorPane();
    private VBox mainMenuBox = new VBox(-5);
    public VBox newJoinBox = new VBox(-5);
    public VBox newGameMenuBox = new VBox(-5);
    private VBox numPlayersMenuBox = new VBox(-5);
    public VBox nicknameChoice = new VBox((-5));
    private TextField nicknameField;
    private Line line;

    private List<Pair<String, Runnable>> mainMenuFields = Arrays.asList(
            new Pair<String, Runnable>("Play", () -> {
                setMenu(mainMenuBox, nicknameChoice);
                showNicknameField();
            }),
            new Pair<String, Runnable>("Info", () -> showScene("/FXML/info.fxml")),
            new Pair<String, Runnable>("Rules", () -> showScene("/FXML/rules.fxml")),
            new Pair<String, Runnable>("Credits", () -> showScene("/FXML/credits.fxml")),
            new Pair<String, Runnable>("Exit", () -> {
                GuiView.getGuiView().event(ViewEvent.GAMEMODE, "esc");
                Platform.exit();
            })
    );

    private List<Pair<String, Runnable>> newJoin = Arrays.asList(
            new Pair<String, Runnable>("New Game", () -> {
                gameChoice = "new";
                setMenu(newJoinBox, newGameMenuBox);
            }),
            new Pair<String, Runnable>("Join Game", () -> {
                gameChoice = "join";
                startGame();
            })
    );

    private List<Pair<String, Runnable>> newGameMenuFields = Arrays.asList(
            new Pair<String, Runnable>("Single player", () -> {
                setSoloMode("on");
                GuiView.getGuiView().solo = true;
                setNumPlayers(1);
            }),
            new Pair<String, Runnable>("Multiplayer", () -> setMenu(newGameMenuBox, numPlayersMenuBox)),
            new Pair<String, Runnable>("Back", () -> setMenu(newGameMenuBox, newJoinBox))
    );

    private List<Pair<String, Runnable>> numPlayersMenuFields = Arrays.asList(
            new Pair<String, Runnable>("How many players?", () -> {}),
            new Pair<String, Runnable>("2", () -> setNumPlayers(2)),
            new Pair<String, Runnable>("3", () -> setNumPlayers(3)),
            new Pair<String, Runnable>("4", () -> setNumPlayers(4)),
            new Pair<String, Runnable>("Back", () -> setMenu(numPlayersMenuBox, newGameMenuBox))
    );

    private List<Pair<String, Runnable>> nicknameChoices = Arrays.asList(
            new Pair<String, Runnable>("Choose your nickname!", () -> {}),
            new Pair<String, Runnable>("", () -> {}),
            new Pair<String, Runnable>("Back", () -> {
                setMenu(nicknameChoice, mainMenuBox);
                removeFromRoot(nicknameField);
            })
    );

    private void startGame() {
        GuiView.getGuiView().event(ViewEvent.GAMEMODE, gameChoice);
        if(gameChoice.equals("new")) {
            GuiView.getGuiView().event(ViewEvent.NUMBER_OF_PLAYERS, numPlayers);
        }
        showScene("/FXML/waitingscreen.fxml");
    }

    private Parent createContent() {
        addBackground();
        addTitle();

        double lineX = WIDTH / 2 - 100;
        double lineY = HEIGHT / 3 + 50;

        addLine(lineX, lineY);
        createMainMenu(lineX + 5, lineY + 5);
        createMenu(lineX + 5, lineY + 5, newGameMenuBox, newGameMenuFields);
        createMenu(lineX + 5, lineY + 5, newJoinBox, newJoin);
        createMenu(lineX + 5, lineY + 5, numPlayersMenuBox, numPlayersMenuFields);
        createMenu(lineX + 5, lineY + 5, nicknameChoice, nicknameChoices);

        startAnimation();

        return root;
    }

    private void addBackground() {
        ImageView imageView = new ImageView(new Image(getClass().getResource("/PNG/board/athens.png").toExternalForm()));
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        root.getChildren().add(imageView);
    }

    private void addTitle() {
        Title title = new Title("MASTERS OF RENAISSANCE");
        title.setTranslateX(WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3);

        root.getChildren().add(title);
    }

    private void addLine(double x, double y) {
        line = new Line(x, y, x, y+280);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setEffect(new DropShadow(5, Color.GOLD));
        line.setScaleY(0);

        root.getChildren().add(line);
    }

    private void startAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), line);
        st.setToY(1);
        st.setOnFinished(e -> {
            for(int i = 0; i< mainMenuBox.getChildren().size(); i++) {
                Node node = mainMenuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.6 + i*0.15), node);
                tt.setToX(0);
                tt.setOnFinished(e2 -> node.setClip(null));
                tt.play();
            }
        });
        st.play();
    }

    private void createMainMenu(double x, double y) {
        mainMenuBox.setTranslateX(x);
        mainMenuBox.setTranslateY(y);
        mainMenuFields.forEach(data -> {
            StartMenuItem item = new StartMenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            mainMenuBox.getChildren().addAll(item);
        });

        root.getChildren().add(mainMenuBox);
    }

    public void createMenu(double x, double y, VBox menuBox, List<Pair<String, Runnable>> menuFields) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);
        menuFields.forEach(data -> {
            StartMenuItem item = new StartMenuItem(data.getKey());
            item.setOnAction(data.getValue());
            menuBox.getChildren().addAll(item);
        });
    }

    public void setMenu(VBox from, VBox to) {
        root.getChildren().remove(from);
        if(!root.getChildren().contains(to)) root.getChildren().add(to);
        removeFromRoot(nicknameField);
    }

    public void resetToMainMenu() {
        removeFromRoot(nicknameChoice);
        removeFromRoot(nicknameField);
        root.getChildren().add(mainMenuBox);
    }

    public void removeFromRoot(Object toRemove) {
        root.getChildren().remove(toRemove);
    }

    public void showNicknameField() {

        double lineX = WIDTH / 2 - 100;
        double lineY = HEIGHT / 3 + 50;

        TextField answer = new TextField();
        answer.setLayoutX(lineX + 5);
        answer.setLayoutY(lineY + 45);
        answer.setFont(Font.font("Copperplate Gothic Bold", 14));
        answer.setStyle("-fx-background-color: transparent");

        answer.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                GuiView.getGuiView().nickname = answer.getText();
                GuiView.getGuiView().event(ViewEvent.NICKNAME, GuiView.getGuiView().nickname);
                removeFromRoot(nicknameField);
                if(this.view.online) {
                    setMenu(nicknameChoice, newJoinBox);
                }
                else {
                    GuiView.getGuiView().event(ViewEvent.GAMEMODE, "new");
                    showScene("/FXML/waitingscreen.fxml");
                }
            }
        });

        nicknameField = answer;
        root.getChildren().add(answer);
    }

    private void setNumPlayers(int players) {
        numPlayers = players;
        startGame();
    }

    private void setSoloMode(String mode) {
        if(mode.equals("on") || mode.equals("off")) {
            soloMode = mode;
        }
    }

    public void showScene(String fxml) {
        try {
            Parent screen = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(screen);
            window.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
