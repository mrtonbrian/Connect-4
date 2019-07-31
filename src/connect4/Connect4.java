package connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class Connect4 extends Application {
    private static final int ITEM_WIDTH = 100;
    private static final int ITEM_HEIGHT = 100;
    Player[] players;
    private Button[] buttons;
    private Label[][] labels;
    private BBBoard board = new BBBoard();
    private Label turnLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ArrayList<String> choices = new ArrayList<>();
        choices.add("1");
        choices.add("2");
        ChoiceDialog<String> playerAmount = new ChoiceDialog<>("How Many Players? ", choices);
        playerAmount.setTitle("Player Amount");
        Optional<String> amountResult = playerAmount.showAndWait();
        if (amountResult.isPresent()) {
            if (amountResult.get().equals("2")) {
                players = new Player[2];
                TextInputDialog name = new TextInputDialog("Player A");
                name.setTitle("Name");
                name.setHeaderText("Player A");
                name.setContentText("Please enter your name:");

                Optional<String> nameResult = name.showAndWait();
                if (nameResult.isPresent()) {
                    players[0] = new Player(nameResult.get(), Utils.PLAYERA);
                } else {
                    System.exit(0);
                }

                name = new TextInputDialog("Player B");
                name.setTitle("Name");
                name.setHeaderText("Player B");
                name.setContentText("Please enter your name:");

                nameResult = name.showAndWait();
                if (nameResult.isPresent()) {
                    players[1] = new Player(nameResult.get(), Utils.PLAYERB);
                } else {
                    System.exit(0);
                }
            } else if (amountResult.get().equals("1")) {
                players = new Player[2];
                TextInputDialog name = new TextInputDialog("Player");
                name.setTitle("Name");
                name.setHeaderText("Player");
                name.setContentText("Please enter your name:");

                Optional<String> nameResult = name.showAndWait();
                if (nameResult.isPresent()) {
                    players[0] = new Player(nameResult.get(), Utils.PLAYERA);
                } else {
                    System.exit(0);
                }

                players[1] = new Computer(Utils.PLAYERB, board);
            } else {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }

        primaryStage.setTitle("Connect 4 Game");
        VBox box = new VBox();

        turnLabel = new Label("");
        turnLabel.setOpacity(100);
        turnLabel.setFont(new Font(30));

        final Text testText = new Text("Test");
        testText.setFont(new Font(30));
        new Scene(new Group(testText));
        testText.applyCss();
        double fontHeight = testText.getLayoutBounds().getHeight();

        box.getChildren().addAll(turnLabel, initItems());
        box.setAlignment(Pos.TOP_CENTER);
        primaryStage.setScene(new Scene(box, Utils.WIDTH * ITEM_WIDTH, fontHeight + 1 + Utils.HEIGHT * ITEM_HEIGHT));
        primaryStage.setResizable(false);
        updateBoard();
        primaryStage.show();
    }

    private GridPane initItems() {
        labels = new Label[Utils.HEIGHT - 1][Utils.WIDTH];
        buttons = new Button[Utils.WIDTH];
        GridPane gridPane = new GridPane();
        // Initializes Buttons
        for (int i = 0; i < Utils.WIDTH; i++) {
            Button button = new Button(i + 1 + "");
            button.setMinSize(ITEM_WIDTH, ITEM_HEIGHT);
            button.setFont(new Font(20));
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Button button = (Button) actionEvent.getSource();

                    makeMove(Integer.parseInt(button.getText()));
                }
            });
            GridPane.setRowIndex(button, 0);
            GridPane.setColumnIndex(button, i);
            gridPane.getChildren().add(button);
            buttons[i] = button;
        }

        for (int row = 0; row < Utils.HEIGHT - 1; row++) {
            for (int col = 0; col < Utils.WIDTH; col++) {
                Label l = new Label();
                l.setMinSize(ITEM_WIDTH, ITEM_HEIGHT);
                l.setMaxSize(ITEM_WIDTH, ITEM_HEIGHT);
                labels[row][col] = l;

                l.setOpacity(0);

                int[] squares = Utils.mirrorSquares(row, col);
                GridPane.setRowIndex(l, squares[0]);
                GridPane.setColumnIndex(l, squares[1]);
                gridPane.getChildren().add(l);
            }
        }
        gridPane.setGridLinesVisible(true);
        return gridPane;
    }

    private void makeMove(int i) {
        try {
            board.addToColumn(i - 1);
            updateBoard();

            checkResult();

            if (players[board.getTurn()] instanceof Computer) {
                for (Button b : buttons) {
                    b.setDisable(true);
                }
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() {
                        board.addToColumn(((Computer) players[board.getTurn()]).searchPosition());

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                updateBoard();
                                checkResult();

                                for (Button b : buttons) {
                                    b.setDisable(false);
                                }
                            }
                        });
                        return null;
                    }
                };
                new Thread(task).start();
            }
        } catch (IllegalArgumentException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Column Full!");
            a.setContentText(null);
            a.showAndWait();
        }
    }

    private void checkResult() {

        int gameResult = board.checkWinner();

        if (gameResult != Utils.NO_RESULT) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);

            if (gameResult == Utils.PLAYERA) {
                a.setHeaderText(String.format("%s Wins!", players[Utils.PLAYERA].getName()));
                a.setContentText(null);
            } else if (gameResult == Utils.PLAYERB) {
                a.setHeaderText(String.format("%s Wins!", players[Utils.PLAYERB].getName()));
                a.setContentText(null);
            } else if (gameResult == Utils.TIE) {
                a.setHeaderText("It's A Tie!");
                a.setContentText(null);
            }
            a.showAndWait();

            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setHeaderText("Another Game?");
            a.setContentText(null);

            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");
            a.getButtonTypes().setAll(yes, no);
            Optional<ButtonType> result = a.showAndWait();
            if (result.isPresent() && result.get().equals(yes)) {
                board.resetBoard();
                updateBoard();
            } else {
                System.exit(0);
            }
        }
    }

    private void updateBoard() {
        turnLabel.setText("It Is " + players[board.getTurn()].getName() + "'s Turn");
        for (int row = 0; row < Utils.HEIGHT - 1; row++) {
            for (int col = 0; col < Utils.WIDTH; col++) {
                Label l = labels[row][col];
                if (board.getPlayer(row, col) == Utils.PLAYERA) {
                    l.setStyle("-fx-background-color:#ff5461");
                    l.setOpacity(100);
                } else if (board.getPlayer(row, col) == Utils.PLAYERB) {
                    l.setStyle("-fx-background-color:#fffb21");
                    l.setOpacity(100);
                } else {
                    l.setOpacity(0);
                }
            }
        }
    }
}
