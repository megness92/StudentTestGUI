/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.panels;

import com.caucho.hessian.client.HessianProxyFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.controlsfx.dialog.Dialogs;
import test.Page;
import test.TSStudentMain;
import test.controls.NumberTextField;

/**
 *
 * @author Asus
 */
public class InstructionPage extends Page {

    public InstructionPage(String name) {
        super(name);
    }
    
    Test curTest;
    ScrollPane scrollPane;
    Text labelNameStud = new Text("Full name:");
    TextField nameStud;
    Text labelEmailStud = new Text("Email:      ");
    TextField emailStud;
    Text labelCourseStud = new Text("Course number:  ");
    NumberTextField courseStud;
    Text labelGroupStud = new Text("Group number:  ");
    NumberTextField groupStud;
    Text labelFacultyStud = new Text("Faculty:    ");
    TextField facultyStud;
    
    Text testText;
    
    @Override public Node createView() {
        
        VBox main = new VBox(8) {
            @Override protected double computePrefHeight(double width) {
                return Math.max(
                        super.computePrefHeight(width),
                        getParent().getBoundsInLocal().getHeight()
                );
            }
        };
        
        curTest = TSStudentMain.getTSStudentMain().getCurrentTest();
        
        main.getStyleClass().add("category-page");
        //Label header = new Label(getName());
        Label header = new Label(getName() + ": " + curTest.getTitle());
        header.setMaxWidth(Double.MAX_VALUE);
        header.setMinHeight(Control.USE_PREF_SIZE); 
        header.getStyleClass().add("page-header");
        main.getChildren().add(header);
        
        Label categoryHeader = new Label("Login");
        categoryHeader.setMaxWidth(Double.MAX_VALUE);
        categoryHeader.setMinHeight(Control.USE_PREF_SIZE); 
        categoryHeader.setId("category-header");
        main.getChildren().add(categoryHeader);
        
        BorderPane reg = new BorderPane();    
        Text textReg = new Text("To access a Test page you must first enter the following data: " + "\n"); 
        textReg.setFont(Font.font ("Verdana", 14));
        textReg.setFill(Color.WHITE);
        reg.setTop(textReg);  
        
//        labelLogin2.setFont(Font.font ("Verdana", 13));
//        labelLogin2.setFill(Color.WHITE);
//        loginStud2 = new TextField("");
//        loginStud2.setPromptText("Enter your login"); 
//        loginStud2.setPrefColumnCount(14);
//        HBox hbox = new HBox(10);
//        hbox.setPadding(new Insets(5, 0, 5, 0));
//        hbox.setAlignment(Pos.BASELINE_LEFT);
//        
//        labelPass2.setFont(Font.font ("Verdana", 13));
//        labelPass2.setFill(Color.WHITE);
//        passwordStud2 = new TextField();
//        passwordStud2.setPromptText("Enter your password"); 
//        passwordStud2.setPrefColumnCount(13);
//        hbox.getChildren().addAll(labelLogin2, loginStud2, labelPass2, passwordStud2);
                
        labelNameStud.setFont(Font.font ("Verdana", 14));
        labelNameStud.setFill(Color.WHITE);
        nameStud = new TextField("ghjgj");
//        nameStud.setId("text-input");     
        nameStud.setPromptText("Enter your full name"); 
        nameStud.setPrefColumnCount(36);
        HBox hbox2 = new HBox(10);
        hbox2.setPadding(new Insets(5, 10, 5, 0));
        hbox2.setAlignment(Pos.BASELINE_LEFT);
        hbox2.getChildren().addAll(labelNameStud, nameStud);
        
        labelEmailStud.setFont(Font.font ("Verdana", 14));
        labelEmailStud.setFill(Color.WHITE);
        emailStud = new TextField("ghjgj");
        emailStud.setPromptText("Enter your email address"); 
        emailStud.setPrefColumnCount(18);
        HBox hbox3 = new HBox(10);
        hbox3.setPadding(new Insets(5, 0, 5, 0));
        hbox3.setAlignment(Pos.BASELINE_LEFT);
        hbox3.getChildren().addAll(labelEmailStud, emailStud);
        
        labelCourseStud.setFont(Font.font ("Verdana", 14));
        labelCourseStud.setFill(Color.WHITE);
        courseStud = new NumberTextField();
        courseStud.addEventFilter(KeyEvent.KEY_TYPED, numFilter()); 
        courseStud.setPrefColumnCount(5);
        
        labelGroupStud.setFont(Font.font ("Verdana", 14));
        labelGroupStud.setFill(Color.WHITE);
        groupStud = new NumberTextField();
        groupStud.setPrefColumnCount(5);
        HBox hbox4 = new HBox(10);
        hbox4.setPadding(new Insets(5, 0, 5, 0));
        hbox4.setAlignment(Pos.BASELINE_LEFT);
        hbox4.getChildren().addAll(labelCourseStud, courseStud, labelGroupStud, groupStud);
        
        labelFacultyStud.setFont(Font.font ("Verdana", 14));
        labelFacultyStud.setFill(Color.WHITE);
        facultyStud = new TextField("ghjgj");
        facultyStud.setPromptText("Enter your faculty name"); 
        facultyStud.setPrefColumnCount(36);
        HBox hbox5 = new HBox(10);
        hbox5.setPadding(new Insets(5, 0, 5, 0));
        hbox5.setAlignment(Pos.BASELINE_LEFT);
        hbox5.getChildren().addAll(labelFacultyStud, facultyStud);
        
        VBox box = new VBox(10);
        box.getChildren().addAll(hbox2, hbox5, hbox3, hbox4);
        
        reg.setLeft(box);        
        main.getChildren().add(reg);
        
        Label categoryHeader1 = new Label("Current Test ");
        categoryHeader1.setMaxWidth(Double.MAX_VALUE);
        categoryHeader1.setMinHeight(Control.USE_PREF_SIZE); 
        categoryHeader1.setId("category-header");
        main.getChildren().add(categoryHeader1);
        
        TilePane directChildFlow1 = new TilePane(8,8);
        directChildFlow1.setPrefColumns(1);
        
        Text curTText = new Text();
        curTText.setFont(Font.font ("Verdana", 14));
        curTText.setWrappingWidth(1000);
        curTText.setText("Theme: " + curTest.getTheme() + "\n\n"
                + "Instructions: " + curTest.getInstructions() + "\n\n"
                + "Required score to pass: " + curTest.getPassScore() + "\n\n"
                + "Time assigned: " + curTest.getTimeToPass() + "\n");
        curTText.setFill(Color.WHITE);
        
        directChildFlow1.getChildren().add(curTText);
        main.getChildren().add(directChildFlow1);
        
        Label categoryHeader2 = new Label("Taking the Test ");
        categoryHeader2.setMaxWidth(Double.MAX_VALUE);
        categoryHeader2.setMinHeight(Control.USE_PREF_SIZE); 
        categoryHeader2.setId("category-header");
        main.getChildren().add(categoryHeader2);
        
        TilePane directChildFlow2 = new TilePane(8,8);
        directChildFlow2.setPrefColumns(1);
        
        testText = new Text();
        testText.setFont(Font.font ("Verdana", 14));
        testText.setWrappingWidth(1000);
        testText.setFill(Color.WHITE);
        testText.setText(
"1. Select an answer for every question. Unanswered questions will be scored as incorrect.\n" +
"\n" + "2. There are three possible question types:\n" +
"\n" + "    - Multiple Choice: click the checkbox button to indicate your choice. Several answers can be selected for a multiple choice question.\n" +
"\n" + "    - Signle Choice: click the radio button to indicate your choice. Only one answer can be selected for a single choice question.\n" +
"\n" + "    - Open: enter your answer, no available choices.\n" +
"\n" + "3. Click on the Submit button at the bottom of the page to have your answers graded.\n" +
"\n" + "4. If the time assigned for the test is over, the test finishes automatically.\n" +
"\n" + "5. You will be shown your results, including your score and number of correct and wrong answers.\n");        
        
        directChildFlow2.getChildren().add(testText);
        main.getChildren().add(directChildFlow2);
        
        Label categoryHeader3 = new Label("Checking Your Answers ");
        categoryHeader3.setMaxWidth(Double.MAX_VALUE);
        categoryHeader3.setMinHeight(Control.USE_PREF_SIZE); 
        categoryHeader3.setId("category-header");
        main.getChildren().add(categoryHeader3);
        
        TilePane directChildFlow3 = new TilePane(8,8);
        directChildFlow3.setPrefColumns(1);
        
        Text answerText = new Text();
        answerText.setFill(Color.WHITE);
        answerText.setFont(Font.font ("Verdana", 14));
        answerText.setWrappingWidth(1000);
        answerText.setText("1. You are allowed to return to questions and check the answers until the test is finished.\n" +
"\n" + "2. When the test is finished you will see the score based on single and multiple choice questions, open questions will be evaluated later by the teacher. \n" +
               "\n\n\nTo start the test press the button: ");
        
        directChildFlow3.getChildren().add(answerText);   
        main.getChildren().add(directChildFlow3);
        
        BorderPane pan = new BorderPane();        
        Button tile = new Button("", getIcon());
        tile.setTooltip(new Tooltip("Start the test"));
        tile.setMinSize(140,140);
        tile.setPrefSize(140,140);
        tile.setMaxSize(140,140);
        tile.setContentDisplay(ContentDisplay.TOP);
        tile.getStyleClass().clear();
        tile.getStyleClass().add("sample-tile");
        tile.setOnAction(new EventHandler() {
            public void handle(Event event) {
                if(!nameStud.getText().equals("")
                     && !emailStud.getText().equals("") && !courseStud.getText().equals("") &&!groupStud.getText().equals("")
                        && !facultyStud.getText().equals("")) {
                    
                    Student student = new Student(nameStud.getText(), emailStud.getText(), Integer.valueOf(courseStud.getText()),
                                                        groupStud.getText(), facultyStud.getText());
                    addStudent(student);
                    if(TSStudentMain.getTSStudentMain().getCurrentStudent() != null) {
                        TSStudentMain.getTSStudentMain().goToPage(TSStudentMain.getTSStudentMain().getPages().getPTPage());
                    }
                    else {
                        Dialogs.create()
                        .owner(TSStudentMain.getTSStudentMain().getStage())
                        .title("User message")
                        .message("Your data wasn't stored! Check your server settings and try again!")
                        .showError();
                    }
                }
                else {
                    Dialogs.create()
                        .owner(TSStudentMain.getTSStudentMain().getStage())
                        .title("User message")
                        .message("All fields are obligatory to fill! \nCheck and edit your entered data!")
                        .showWarning();
                }
                    
            }
        });
        pan.setCenter(tile);        
        main.getChildren().addAll(pan);        
        
        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("noborder-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(main);
        
        return scrollPane;
    }
    
    public static EventHandler<KeyEvent> numFilter() {
        EventHandler<KeyEvent> aux = new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {
                if (!"0123456789".contains(keyEvent.getCharacter())) {
                    keyEvent.consume();
                }
            }
        };
        return aux;
    }
    
    private Node getIcon() {
            Rectangle overlayHighlight = new Rectangle(-8,-8,130,130);
            overlayHighlight.setFill(new LinearGradient(0,0.5,0,1,true, CycleMethod.NO_CYCLE, new Stop[]{ new Stop(0,Color.BLACK), new Stop(1,Color.web("#444444"))}));
            overlayHighlight.setOpacity(0.8);
            overlayHighlight.setMouseTransparent(true);
            overlayHighlight.setBlendMode(BlendMode.ADD);
            Rectangle background = new Rectangle(-8,-8,130,130);
            background.setFill(Color.web("#b9c0c5"));
            Group group = new Group(background);
            Rectangle clipRect = new Rectangle(114,114);
            clipRect.setArcWidth(38);
            clipRect.setArcHeight(38);
            group.setClip(clipRect);
            Node content = createIconContent();
            if (content != null) {
                content.setTranslateX((int)((114-content.getBoundsInParent().getWidth())/2)-(int)content.getBoundsInParent().getMinX());
                content.setTranslateY((int)((114-content.getBoundsInParent().getHeight())/2)-(int)content.getBoundsInParent().getMinY());
                group.getChildren().add(content);
            }
            group.getChildren().addAll(overlayHighlight);
            return new Group(group);
    }
    
    public Node createIconContent() {
        ImageView imageView = new ImageView(new Image(TSStudentMain.class.getResource("images/start.png").toString()));
        imageView.setMouseTransparent(true);
        return imageView;
    }    
    
    
    TestStudentService testStudentService;
    
    public void addStudent(final Student student) {
      try { 
        String url = TSStudentMain.URL + "TestStudentServiceImpl";
        HessianProxyFactory factory = new HessianProxyFactory();
        testStudentService = (TestStudentService) factory.create(TestStudentService.class, url);
        final Task<Student> task = new Task<Student>()
        {
            protected Student call() throws Exception
            {
                return testStudentService.addStudent(student);
            }
        };
        task.stateProperty().addListener(new ChangeListener<Worker.State>()
        {
            public void changed(ObservableValue<? extends Worker.State> source,
                                Worker.State oldState, Worker.State newState)
            {
                if (newState.equals(Worker.State.SUCCEEDED))
                {
                    TSStudentMain.getTSStudentMain().setCurrentStudent(task.getValue());
                }
                else if (newState.equals(Worker.State.FAILED))
                {
                    Throwable exception = task.getException();
                    exception.printStackTrace();
                }
            }
        });
        new Thread(task).start();
        } catch(Exception e) {
              e.printStackTrace();
        }  
    }
}
