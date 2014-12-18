/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.config;

import com.caucho.hessian.client.HessianProxyFactory;
import test.TSStudentMain;
import test.Pages;
import test.panels.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author LUDMILA2
 */
public class EnterDialog extends VBox {
    private Button okBtn;
    private Pages pages;
    private Window owner;
    private AuthorizationPanel authorizationPanel;
    private ServerPanel serverPanel;
    private TestPanel testPanel;
    private Tab serverTab;
    private Tab authorizationTab;
    private TabPane options;
    private Text explanation;
    Label title;
    
    public EnterDialog(final Window owner, final Pages pages) {
        this.owner = owner;
        this.pages = pages;
        setId("ProxyDialog");
        setSpacing(10);
        setMaxSize(430, USE_PREF_SIZE);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent t) {
                t.consume();
            }
        });

        String text = "To login to the system enter your name and password. "
                + "Also you can set address and port of the server. "
                + "Without proper notification from the administrator it is not recommended to change the server address.";
        explanation = TextBuilder.create().text(text)
                .fill(Color.WHITE)
                .build();
        explanation.setWrappingWidth(400);
        explanation.setStyle("-fx-font-size: 1.1em;");
        
        BorderPane explPane = new BorderPane();
        VBox.setMargin(explPane, new Insets(5, 5, 5, 5));
        explPane.setCenter(explanation);
        BorderPane.setMargin(explanation, new Insets(5, 5, 5, 5));

        title = new Label("Login");
        title.setId("title");
        title.setMinHeight(22);
        title.setPrefHeight(22);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        getChildren().add(title);

        serverTab = new Tab("Server");
        serverPanel = new ServerPanel();
        serverTab.setContent(serverPanel);

        authorizationTab = new Tab("Authorization");
        authorizationPanel = new AuthorizationPanel();
        authorizationTab.setContent(authorizationPanel);

        options = new TabPane();
        options.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        options.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        options.getTabs().addAll(authorizationTab, serverTab);

        okBtn = new Button("Login");
        okBtn.setId("glass-grey");
        okBtn.setDisable(true);
        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                if(!serverPanel.hostNameBox.getText().equals("")) {
                    TSStudentMain.URL = serverPanel.hostNameBox.getText();
                }                
                if(okBtn.getText().equals("Login")) {
                    if(TSStudentMain.getTSStudentMain().handleUserLogin(authorizationPanel.loginBox.getText(), authorizationPanel.passwordBox.getText())){
                        TSStudentMain.getTSStudentMain().setCurrentLogin(authorizationPanel.loginBox.getText());
                        TSStudentMain.getTSStudentMain().setCurrentPassword(authorizationPanel.passwordBox.getText());
                        getAllTests();                        
                        authorizationTab.setText("Test for students");
                        title.setText("Choose test");
                        okBtn.setText("OK");
                        testPanel = new TestPanel();
                        authorizationTab.setContent(testPanel);                
                        String text = "To choose the test - set test id or select the test from the list of availbale ones according to the title";
                        explanation.setText(text);
                    }
                    else {
                        Dialogs.create()
                            .owner(TSStudentMain.getTSStudentMain().getStage())
                            .title("Error message")
                            .message("There was a problem while connecting to the server! Check your username and password!")
                            .showError();                    
                    }
                }
                else {
                    if(!testPanel.idBox.getText().equals("")) {
                        getTestById(BigInteger.valueOf(Long.valueOf(testPanel.idBox.getText())));
                        if (TSStudentMain.getTSStudentMain().getCurrentTest() == null) {
                            Dialogs.create()
                                .owner(TSStudentMain.getTSStudentMain().getStage())
                                .title("Error message")
                                .message("No test was found with such id!")
                                .showError();
                        }
                        else {
                            TSStudentMain.getTSStudentMain().goToPage(TSStudentMain.getTSStudentMain().getPages().getInsPage());
                        }
                    }  
                    else {
                       int index = list.getSelectionModel().getSelectedIndex();
                       if(index < 0) {
                           Dialogs.create()
                            .owner(TSStudentMain.getTSStudentMain().getStage())
                            .title("Warning message")
                            .message("Test id is not specified and no test is selected from the list!")
                            .showWarning();
                       }
                       else {
                           TSStudentMain.getTSStudentMain().setCurrentTest(
                                TSStudentMain.getTSStudentMain().getTests()[index]);
                           TSStudentMain.getTSStudentMain().goToPage(TSStudentMain.getTSStudentMain().getPages().getInsPage());
                       }
                    }
                    TSStudentMain.getTSStudentMain().hideEnterModalMessage();                    
                    //TSStudentMain.getTSStudentMain().startThread();
                }
            }
        });
        okBtn.setTooltip(new Tooltip("Login to the system"));
        okBtn.setMinWidth(94);
        okBtn.setPrefWidth(94);

        HBox bottomBar = new HBox(0);
        bottomBar.setAlignment(Pos.BASELINE_RIGHT);
        bottomBar.getChildren().addAll(okBtn);
        VBox.setMargin(bottomBar, new Insets(20, 5, 5, 5));

        getChildren().addAll(explPane, options, bottomBar);
    }
    
    private class ServerPanel extends GridPane {
        private TextField hostNameBox;
        
        public ServerPanel() {
            setPadding(new Insets(8));
            setHgap(5.0F);
            setVgap(5.0F);

            int rowIndex = 0;

            Label label2 = new Label("Address");
            label2.setId("proxy-dialog-label");
            GridPane.setConstraints(label2, 0, rowIndex);

            rowIndex++;
            hostNameBox = new TextField("");
            hostNameBox.setPromptText("Enter server adress");
            hostNameBox.setTooltip(new Tooltip("Field for server address"));
            hostNameBox.setPrefColumnCount(40);
            GridPane.setConstraints(hostNameBox, 0, rowIndex);

            getChildren().addAll(label2, hostNameBox);
        }

        public TextField getHostNameBox() {
            return hostNameBox;
        }
    }   
    
    private class AuthorizationPanel extends GridPane {
        private TextField loginBox;
        private PasswordField passwordBox;

        public AuthorizationPanel() {
            setPadding(new Insets(8));
            setHgap(5.0F);
            setVgap(5.0F);

            int rowIndex = 0;

            Label labelA1 = new Label("User name");
            labelA1.setId("proxy-dialog-label");
            GridPane.setConstraints(labelA1, 0, rowIndex);

            Label labelA2 = new Label("Password");
            labelA2.setId("proxy-dialog-label");
            GridPane.setConstraints(labelA2, 1, rowIndex);
            getChildren().addAll(labelA1, labelA2);

            rowIndex++;
            loginBox = new TextField();
            loginBox.setPrefColumnCount(20);
            loginBox.setTooltip(new Tooltip("Field for use name"));
            GridPane.setConstraints(loginBox, 0, rowIndex);

            passwordBox = new PasswordField();
            passwordBox.setPrefColumnCount(15);
            passwordBox.setTooltip(new Tooltip("Field for password"));
            GridPane.setConstraints(passwordBox, 1, rowIndex);

            ChangeListener<String> textListener = new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                    okBtn.setDisable(
                            loginBox.getText() == null || loginBox.getText().isEmpty()
                            || passwordBox.getText() == null || passwordBox.getText().isEmpty());
                }
            };
            loginBox.textProperty().addListener(textListener);
            passwordBox.textProperty().addListener(textListener);

            getChildren().addAll(loginBox, passwordBox);
        }
    }
    
    private ListView<String> list;
    
    private class TestPanel extends GridPane {        

        private TextField idBox;
        
        public TestPanel() {
            setPadding(new Insets(8));
            setHgap(5.0F);
            setVgap(5.0F);
            
            int rowIndex = 0;

            Label label2 = new Label("Test id: ");
            label2.setId("proxy-dialog-label");
            GridPane.setConstraints(label2, 0, rowIndex);

            rowIndex++;
            idBox = new TextField("");
            idBox.setPromptText("Enter test id");
            idBox.setTooltip(new Tooltip("Field for test id"));
            idBox.setPrefColumnCount(40);
            GridPane.setConstraints(idBox, 0, rowIndex);

            getChildren().addAll(label2, idBox);
            
            rowIndex++;
            
            Label labelT2 = new Label("Or choose from the list:");
            labelT2.setId("proxy-dialog-label");
            GridPane.setConstraints(labelT2, 0, rowIndex, 2, 1);
            
            rowIndex++;
            list = new ListView<String>();
            Test[] tlist = TSStudentMain.getTSStudentMain().getTests();
            String[] titles = new String[tlist.length];
            for(int i = 0; i < tlist.length; i++) {
                titles[i] = tlist[i].getTitle();
            }
            ObservableList<String> items = FXCollections.observableArrayList (titles);
            list.setItems(items);
            list.setPrefWidth(500);
            list.setPrefHeight(150);
            GridPane.setConstraints(list, 0, rowIndex, 2, 1);

            getChildren().addAll(labelT2, list);
        }
    }
    
    private TestServiceTeacher testServiceTeacher;
    
    public void getAllTests() {
      try { 
        String url = TSStudentMain.URL + "TestServiceTeacherImpl";
        HessianProxyFactory factory = new HessianProxyFactory();
        testServiceTeacher = (TestServiceTeacher) factory.create(TestServiceTeacher.class, url);
        final Task<Test[]> task = new Task<Test[]>()
        {
            protected Test[] call() throws Exception
            {
                return testServiceTeacher.getTeacherTests(TSStudentMain.getTSStudentMain().getCurrentTeacher().getId());
            }
        };
        task.stateProperty().addListener(new ChangeListener<Worker.State>()
        {
            public void changed(ObservableValue<? extends Worker.State> source,
                                Worker.State oldState, Worker.State newState)
            {
                if (newState.equals(Worker.State.SUCCEEDED))
                {
                    TSStudentMain.getTSStudentMain().setTests(task.getValue());
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
    
    public void getTestById(final BigInteger id) {
      try { 
        String url = TSStudentMain.URL + "TestServiceTeacherImpl";
        HessianProxyFactory factory = new HessianProxyFactory();
        testServiceTeacher = (TestServiceTeacher) factory.create(TestServiceTeacher.class, url);
        final Task<Test> task = new Task<Test>()
        {
            protected Test call() throws Exception
            {
                return testServiceTeacher.getTeacherTestById(id);
            }
        };
        
        
        task.stateProperty().addListener(new ChangeListener<Worker.State>()
        {
            public void changed(ObservableValue<? extends Worker.State> source,
                                Worker.State oldState, Worker.State newState)
            {
                if (newState.equals(Worker.State.SUCCEEDED))
                {
                    TSStudentMain.getTSStudentMain().setCurrentTest(task.getValue());
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

