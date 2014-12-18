/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.config;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Window;
import test.Pages;
import test.TSStudentMain;

/**
 *
 * @author Asus
 */
public class ResultDialog extends VBox {
    private Button okBtn;
    private Pages pages;
    private Window owner;
    private AuthorizationPanel authorizationPanel;
    private Tab authorizationTab;
    private TabPane options;
    private Text explanation;
    Label title;
    
    public ResultDialog(final Window owner, final Pages pages) {
        this.owner = owner;
        this.pages = pages;
        setId("ProxyDialog");
        setSpacing(10);
        setMaxSize(350, USE_PREF_SIZE);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent t) {
                t.consume();
            }
        });

        String text = "You have finished the test  ";
        explanation = TextBuilder.create().text(text)
                .fill(Color.WHITE)
                .build();
        explanation.setWrappingWidth(400);
        explanation.setStyle("-fx-font-size: 1.1em;");
        
        BorderPane explPane = new BorderPane();
        VBox.setMargin(explPane, new Insets(5, 5, 5, 5));
        explPane.setCenter(explanation);
        BorderPane.setMargin(explanation, new Insets(5, 5, 5, 5));

        // create title
        title = new Label("Test Results");
        title.setId("title");
        title.setMinHeight(22);
        title.setPrefHeight(22);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        getChildren().add(title);

        authorizationTab = new Tab("Results");
        
        authorizationPanel = new AuthorizationPanel();
        authorizationTab.setContent(authorizationPanel);

        options = new TabPane();
        options.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        options.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        options.getTabs().add(authorizationTab);
        
        VBox main = new VBox(8) {
            @Override protected double computePrefHeight(double width) {
                return Math.max(
                        super.computePrefHeight(width),
                        getParent().getBoundsInLocal().getHeight()
                );
            }
        };
        main.setId("results");
        main.getChildren().add(authorizationPanel);

        okBtn = new Button("OK");
        okBtn.setId("glass-grey");
        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
               Platform.exit();
            }
        });
        okBtn.setTooltip(new Tooltip("Login to the system"));
        okBtn.setMinWidth(94);
        okBtn.setPrefWidth(94);

        HBox bottomBar = new HBox(0);
        bottomBar.setAlignment(Pos.BASELINE_RIGHT);
        bottomBar.getChildren().addAll(okBtn);
        VBox.setMargin(bottomBar, new Insets(20, 5, 5, 5));

        getChildren().addAll(explPane, main, bottomBar);        
    }    
    
    private class AuthorizationPanel extends GridPane {

        public AuthorizationPanel() {
            setPadding(new Insets(8));
            setHgap(5.0F);
            setVgap(5.0F);

            int rowIndex = 0;

            Label labelA = new Label("Result: ");
            labelA.setId("proxy-dialog-label");
            GridPane.setHalignment(labelA, HPos.RIGHT);
            GridPane.setConstraints(labelA, 0, rowIndex);
            
            Text passText = new Text(pass);
            passText.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            GridPane.setConstraints(passText, 1, rowIndex++);
            
            Label labelA1 = new Label("Final score: ");
            labelA1.setId("proxy-dialog-label");
            GridPane.setHalignment(labelA1, HPos.RIGHT);
            GridPane.setConstraints(labelA1, 0, rowIndex);
            
            Text scoreText = new Text(Integer.toString(score));
            scoreText.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            GridPane.setConstraints(scoreText, 1, rowIndex++);

            Label labelA2 = new Label("Grade: ");
            labelA2.setId("proxy-dialog-label");
            GridPane.setHalignment(labelA2, HPos.RIGHT);
            GridPane.setConstraints(labelA2, 0, rowIndex);
            
            Text gradeText = new Text(grade);
            gradeText.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            GridPane.setConstraints(gradeText, 1, rowIndex++);
            
            Label labelA3 = new Label("Correct answers: ");
            labelA3.setId("proxy-dialog-label");
            GridPane.setHalignment(labelA3, HPos.RIGHT);
            GridPane.setConstraints(labelA3, 0, rowIndex);
            
            Text correctText = new Text(Integer.toString(correct));
            correctText.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            GridPane.setConstraints(correctText, 1, rowIndex++);
            
            Label labelA4 = new Label("Wrong answers: ");
            labelA4.setId("proxy-dialog-label");
            GridPane.setHalignment(labelA4, HPos.RIGHT);
            GridPane.setConstraints(labelA4, 0, rowIndex);
            
            Text wrongText = new Text(Integer.toString(wrong));
            wrongText.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            GridPane.setConstraints(wrongText, 1, rowIndex++);
            
            Label labelA5 = new Label("Open questions to be evaluated: ");
            labelA5.setId("proxy-dialog-label");
            GridPane.setHalignment(labelA5, HPos.RIGHT);
            GridPane.setConstraints(labelA5, 0, rowIndex);
            
            Text openText = new Text(Integer.toString(open));
            openText.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            GridPane.setConstraints(openText, 1, rowIndex++);
            
            getChildren().addAll(labelA, passText, labelA1, scoreText, labelA2, gradeText, 
                    labelA3, correctText, labelA4, wrongText, labelA5, openText);
        }
    }    
    
    private String pass = "";
    private int score;
    private String grade;
    private int correct;
    private int wrong;
    private int open;
    
    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public String getPass() {
        return pass;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    public String getGrade() {
        return grade;
    }
        
    public void setCorrect(int correct) {
        this.correct = correct;
    }
    
    public int getCorrect() {
        return correct;
    }
    
    public void setWrong(int wrong) {
        this.wrong = wrong;
    }
    
    public int getWrong() {
        return wrong;
    }
    
    public void setOpen(int open) {
        this.open = open;
    }
    
    public int getOpen() {
        return open;
    }
}


