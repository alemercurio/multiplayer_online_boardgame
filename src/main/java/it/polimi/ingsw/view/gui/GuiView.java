package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.Client;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class GuiView extends Application {
    private static Stage window;
    private static Scene startMenu;
    private static Scene newGameMenu;

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("New Game", () -> newGameMenu()),
            new Pair<String, Runnable>("Join Game", () -> {}),
            new Pair<String, Runnable>("Options", () -> {}),
            new Pair<String, Runnable>("Bonus Content", () -> {}),
            new Pair<String, Runnable>("Rules", () -> {}),
            new Pair<String, Runnable>("Credits", () -> {}),
            new Pair<String, Runnable>("Exit", Platform::exit)
    );

    private List<Pair<String, Runnable>> menuData2 = Arrays.asList(
            new Pair<String, Runnable>("Single player", () -> {}),
            new Pair<String, Runnable>("Multiplayer", () -> {}),
            new Pair<String, Runnable>("Back", () -> setStartMenu())
    );

    private Pane root = new AnchorPane();
    private VBox menuBox = new VBox(-5);
    private VBox menuBox2 = new VBox(-5);
    private Line line;

    private Parent createContent() {
        addBackground();
        addTitle();

        double lineX = WIDTH / 2 - 100;
        double lineY = HEIGHT / 3 + 50;

        addLine(lineX, lineY);
        addMenu(lineX + 5, lineY + 5);
        addMenu2(lineX + 5, lineY + 5);

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
            for(int i=0; i<menuBox.getChildren().size(); i++) {
                Node node = menuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.6 + i*0.15), node);
                tt.setToX(0);
                tt.setOnFinished(e2 -> node.setClip(null));
                tt.play();
            }
        });
        st.play();
    }

    private void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);
        menuData.forEach(data -> {
            StartMenuItem item = new StartMenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        root.getChildren().add(menuBox);
    }

    private void addMenu2(double x, double y) {
        menuBox2.setTranslateX(x);
        menuBox2.setTranslateY(y);
        menuData2.forEach(data -> {
            StartMenuItem item = new StartMenuItem(data.getKey());
            item.setOnAction(data.getValue());
            menuBox2.getChildren().addAll(item);
        });
    }

    public void newGameMenu() {
        root.getChildren().remove(menuBox);
        root.getChildren().add(menuBox2);
    }

    public void setStartMenu() {
        root.getChildren().remove(menuBox2);
        root.getChildren().add(menuBox);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        try {
            Scene scene = new Scene(createContent());
            startMenu = scene;
            primaryStage.setTitle("Master of Renaissance");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
