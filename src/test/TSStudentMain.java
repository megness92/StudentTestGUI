/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author Asus
 */

import test.config.*;
import test.controls.*;
import test.panels.*;
import com.caucho.hessian.client.HessianProxyFactory;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import javax.swing.Timer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import netscape.javascript.JSObject;

/**
 *
 * @author LUDMILA2
 */
public class TSStudentMain extends Application implements Runnable {
    static {
        System.setProperty("java.net.useSystemProxies", "true");
    }
    public static String URL = "http://mytomcatapp-testingservicesa.rhcloud.com/TestServer/";
    
    private static TSStudentMain main;
    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private ToolBar toolBar;
    private SplitPane splitPane;
    private Pane pageArea;
    private Pages pages;
    private Page currentPage;
    private Node currentPageView;
    private double mouseDragOffsetX = 0;
    private double mouseDragOffsetY = 0;
    private WindowResizeButton windowResizeButton;
    public boolean fromForwardOrBackButton = false;
    private StackPane modalDimmer;
    private ServerDialog serverDialog;
    private ToolBar pageToolBar;
    public EnterDialog enterDialog;
    public ResultDialog resultDialog;
    private Student currentStudent;
    private Teacher currentTeacher;
    private String currentLogin;
    private String currentPassword="";
    private Text text;
    
    private Test currentTest;
    
    public void setText(String t) {
        text.setText(t);
    }
    
    public static TSStudentMain getTSStudentMain() {
        return main;
    }
    
    public Student getCurrentStudent() {
        return currentStudent;
    }
    
    public Stage getStage() {
        return stage;
    }
    
    public void setCurrentStudent(Student currentStudent) {
         this.currentStudent = currentStudent;
    }
    
    public Teacher getCurrentTeacher() {
        return currentTeacher;
    }
    
    public void setCurrentTeacher(Teacher currentTeacher) {
         this.currentTeacher = currentTeacher;
    }
    
    public Test getCurrentTest() {
        return currentTest;
    }
    
    public void setCurrentTest(Test currentTest) {
         this.currentTest = currentTest;
    }
    
    public String getCurrentLogin() {
        return currentLogin;
    }
    
    public void setCurrentLogin(String currentLogin) {
         this.currentLogin = currentLogin;
    }
    
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
         this.currentPassword = currentPassword;
    }
    
    public ResultDialog getResultDialog() {
        return resultDialog;
    }
    
    @Override public void start(final Stage stage) {
        /*try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ipAddress = inetAddress.getHostAddress(); 
            URL = "http://" + ipAddress + ":8080/AuctionServer/";
        }
        catch (Exception e) {}*/
        main = this;
        stage.setTitle("Student Test System");
        StackPane layerPane = new StackPane();
        stage.initStyle(StageStyle.UNDECORATED);
        windowResizeButton = new WindowResizeButton(stage, 1035,700);
        root = new BorderPane() {
            @Override protected void layoutChildren() {
                super.layoutChildren();
                windowResizeButton.autosize();
                windowResizeButton.setLayoutX(getWidth() - windowResizeButton.getLayoutBounds().getWidth());
                windowResizeButton.setLayoutY(getHeight() - windowResizeButton.getLayoutBounds().getHeight());
            }
        };
        root.getStyleClass().add("application");
        
        root.setId("root");
        layerPane.setDepthTest(DepthTest.DISABLE);
        layerPane.getChildren().add(root);
        scene = new Scene(layerPane, 1035, 700);
        scene.getStylesheets().addAll(TSStudentMain.class.getResource("/test/main.css").toExternalForm());
        modalDimmer = new StackPane();
        modalDimmer.setId("ModalDimmer");
        modalDimmer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent t) {
                t.consume();
                hideModalMessage();
            }
        });
        modalDimmer.setVisible(false);
        layerPane.getChildren().add(modalDimmer);
        toolBar = new ToolBar();
        toolBar.setId("mainToolBar");
        
        Text title = TextBuilder.create().text("Student Test System")
                .x(0).y(0).font(Font.font("System", 26))
                .fill(Color.web("#e1fdff"))
                .stroke(Color.web("#ACACAC"))
                .effect(DropShadowBuilder.create().radius(15).color(Color.web("#ACACAC")).blurType(BlurType.ONE_PASS_BOX).build())
                .build();
        HBox.setMargin(title, new Insets(0,0,0,5));
        toolBar.getItems().add(title);
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        toolBar.getItems().add(spacer2);
        
        toolBar.setPrefHeight(66);
        toolBar.setMinHeight(66);
        toolBar.setMaxHeight(66);
        GridPane.setConstraints(toolBar, 0, 0);
        final WindowButtons windowButtons = new WindowButtons(stage);
        toolBar.getItems().add(windowButtons);
        toolBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    try {
                        windowButtons.toogleMaximized();
                    }
                    catch(Exception ex) {ex.printStackTrace();}
                }
            }
        });
        toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                mouseDragOffsetX = event.getSceneX();
                mouseDragOffsetY = event.getSceneY();
            }
        });
        toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if(!windowButtons.isMaximized()) {
                    stage.setX(event.getScreenX()-mouseDragOffsetX);
                    stage.setY(event.getScreenY()-mouseDragOffsetY);
                }
            }
        });
        
        
        pages = new Pages();
        serverDialog = new ServerDialog(stage, pages);
        pages.parseSamples();   

        pageArea = new Pane() {
            @Override protected void layoutChildren() {
                for (Node child:pageArea.getChildren()) {
                    child.resizeRelocate(0, 0, pageArea.getWidth(), pageArea.getHeight());
                }
            }
        };
        pageArea.setId("page-area");
        BorderPane rightSplitPane = new BorderPane();
        rightSplitPane.setCenter(pageArea);
        splitPane = new SplitPane();
        splitPane.setId("page-splitpane");
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setConstraints(splitPane, 0, 1);
        splitPane.getItems().addAll(rightSplitPane);
        splitPane.setDividerPosition(0, 0.25);

        this.root.setTop(toolBar);
        this.root.setCenter(splitPane);
        
        windowResizeButton.setManaged(false);
        this.root.getChildren().add(windowResizeButton);
        
        for (TreeItem child: pages.getRoot().getChildren()) {
            child.setExpanded(true);
            for (TreeItem child2: (ObservableList<TreeItem<String>>)child.getChildren()) {
                child2.setExpanded(true);
            }
        }
        
        stage.setScene(scene);
        stage.show();
        enterDialog = new EnterDialog(stage, pages);
//        resultDialog = new ResultDialog(stage, pages);
//        thisActivity = new Thread(this);
//        thisActivity.setDaemon(true);
        showEnterDialog();
//        //goToPage(TSStudentMain.getTSStudentMain().getPages().getInsPage());
        this.stage = stage;
    }
    
    // servlet to get test of the teacher
    TestServiceTeacher testServiceTeacher;


    public void getTestsForTeacher(final BigInteger id) {
        try {
            String url = "http://localhost:8085/TestServer/" + "TestServiceTeacherImpl";

            HessianProxyFactory factory = new HessianProxyFactory();
            testServiceTeacher = (TestServiceTeacher) factory.create(TestServiceTeacher.class, url);

            final Task<Test[]> task = new Task<Test[]>() {
                protected Test[] call() throws Exception {
                    Test[] newtests= testServiceTeacher.getTeacherTests(id);
   
                    return newtests;
                }
            };
            task.stateProperty().addListener(new ChangeListener<Worker.State>() {
                public void changed(ObservableValue<? extends Worker.State> source,
                                    Worker.State oldState, Worker.State newState) {
                    if (newState.equals(Worker.State.SUCCEEDED)) {
                        Test[] tests = task.getValue();
                        if (tests != null) {
                            //testsofteacher=tests;
                            System.out.println("SUCCESS in getting tests for teacher!");
                            System.out.println("length=" + tests.length);
                            for (int i = 0; i < tests.length; i++) {
                                System.out.println(tests[i].getTitle());
                                System.out.println(tests[i].getId());
                              //  System.out.println("questions="+tests[i].getQuestions());
                            //    System.out.println("questions = " + tests[i].getNumberOfQuestions());
                               /* for (int j = 0; j < tests[i].getNumberOfQuestions(); j++) {
                                    System.out.println(tests[i].getQuestion(j).getText());
                                }   */
                            }


                        }



                        //fx.add(createNewTest());
                    } else if (newState.equals(Worker.State.FAILED)) {
                        Throwable exception = task.getException();
                        exception.printStackTrace();
                    }
                }
            });
            new Thread(task).start();
        } catch (Exception ex) {
            System.out.println("getTests failed");
            ex.printStackTrace();
        }
    }

    
    public void startThread() {
        thisActivity.start(); 
    }
    
    public void showModalMessage(Node message) {
        modalDimmer.getChildren().add(message);
        modalDimmer.setOpacity(0);
        modalDimmer.setVisible(true);
        modalDimmer.setCache(true);
        TimelineBuilder.create().keyFrames(
            new KeyFrame(Duration.seconds(1), 
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        modalDimmer.setCache(false);
                    }
                },
                new KeyValue(modalDimmer.opacityProperty(),1, Interpolator.EASE_BOTH)
        )).build().play();
    }
    
    public void hideModalMessage() {
        modalDimmer.setCache(true);
        TimelineBuilder.create().keyFrames(
            new KeyFrame(Duration.seconds(1), 
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        modalDimmer.setCache(false);
                        modalDimmer.setVisible(false);
                        modalDimmer.getChildren().clear();
                    }
                },
                new KeyValue(modalDimmer.opacityProperty(),0, Interpolator.EASE_BOTH)
        )).build().play();
    }
    
    public void hideEnterModalMessage() {
        /*modalDimmer.setCache(false);
        modalDimmer.setVisible(false);
        modalDimmer.getChildren().clear();
        goToPage(pages.getInsPage());*/
        modalDimmer.setCache(true);
        TimelineBuilder.create().keyFrames(
            new KeyFrame(Duration.seconds(1), 
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        goToPage(pages.getInsPage());
                        modalDimmer.setCache(false);                       
                        modalDimmer.setVisible(false);
                        modalDimmer.getChildren().clear();
                    }
                },
                new KeyValue(modalDimmer.opacityProperty(),0, Interpolator.EASE_BOTH)
        )).build().play();
    }
        
    public Pages getPages() {
        return pages;
    }

    public void updateCurrentPage(Page page) {
        goToPage(page, true,false,false);
    }
    
    public void goToPage(String pagePath) {
        goToPage(pages.getPage(pagePath));
    }

    public void goToPage(String pagePath, boolean force) {
        goToPage(pages.getPage(pagePath),true,force,true);
    }

    public void goToPage(Page page) {
        goToPage(page, true, false, true);
    }
    
    private void goToPage(Page page, boolean addHistory, boolean force, boolean swapViews) {
        if(page==null) return;
        if(!force && page == currentPage) {
            return;
        }
        if (swapViews) {
            Node view = page.createView();
            if (view == null) view = new Region(); 
            if (force || view != currentPageView) {
                pageArea.getChildren().setAll(view);
                currentPageView = view;
            }
        }
        currentPage = page;
        
        Page p = page;
        while (p!=null) {
            p.setExpanded(true);
            p = (Page) p.getParent();
        }
    }
    
    public void showServerDialog() {
        showModalMessage(serverDialog);
    }
 
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    public void showEnterDialog() {
        showModalMessage(enterDialog);
    }
    
    public void showResultDialog() {
        showModalMessage(resultDialog);
    }
         
    private Student[] students;
    private Teacher[] teachers;
    private Test[] tests;
   
    public Student[] getStudents() {
        return students;
    }
    
    public Teacher[] getTeachers() {
        return teachers;
    }
    
    public void setStudents(Student[] students) {
        this.students = students;
    }
    
    public void setTeachers(Teacher[] teachers) {
        this.teachers = teachers;
    }
    
    public void setTests(Test[] tests) {
        this.tests = tests;
    }
    
    public Test[] getTests() {
        return tests;
    }
    
    private Thread thisActivity;    
    private volatile boolean looping = true;
   
    public void stop() {
        if(!getCurrentPassword().equals("") && remove(String.valueOf(getCurrentPassword().hashCode()))) {
            setLooping(false);
        }
    }
    
    public boolean remove(String user) {
        boolean temp = false;
        System.out.println("remove");
        try {
                URLConnection con = getServletConnection();
                OutputStream outstream = con.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(outstream);
                oos.writeObject("removeUser");
                oos.writeObject(user);
                oos.flush();
                oos.close();
                InputStream instr = con.getInputStream();
                ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
                if(inputFromServlet.readObject() != null) temp = true;
                inputFromServlet.close();
                instr.close();
        } 
        catch (SocketException sE) {
            String problem = "Can't establish a connection to host: \n" + sE.getMessage()+"\n";
            System.err.println(problem);
            try { Thread.sleep(5000); }
                catch (InterruptedException iE) { }
        }
        catch (FileNotFoundException fNfE) {
            String problem = "Resource not found in remove: " + fNfE.getMessage();
            System.err.println(problem);
            try { Thread.sleep(5000); }
                catch (InterruptedException iE) { }
        }
        catch (Exception e) {
            String problem = "Unknown exception in remove: " +
            e.getClass().getName() + ": " + e.getMessage();
            try { Thread.sleep(3000); }
                catch (InterruptedException iE) {}
            e.printStackTrace();
        }
        return temp; 
    }
       
    public synchronized void setLooping(boolean b) {
        System.out.println("setLooping");
        looping = b;
        if (looping)
            notify();
        
        System.out.println("setLopping looping1 = " + looping);
    }
    
    private URLConnection getServletConnection() throws MalformedURLException, IOException {
        URL urlServlet = new URL(TSStudentMain.URL + "StudentLoginServlet");
        URLConnection con = urlServlet.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestProperty("Content-Type", "application/x-java-serialized-object");
        return con;
    }
    
    public void run() {
        System.out.println("run");
        System.out.println("looping1 = " + looping);
        while (looping) {
             System.out.println("while looping1 = " + looping);
             try {
                    synchronized(this) {
                        while (!looping) {
                            wait(); 
                            System.out.println("wait looping2 = " + looping);
                        }
                        System.out.println("looping3 = " + looping);
                } 
            } 
            catch (InterruptedException e) {} 
        }
    }
    
    public boolean handleUserLogin(String login, String password) {
        boolean temp = false;
        System.out.println("handleUserLogin");
        try {
            String url = URL + "LoginServlet";
            URL u = new URL(url);
            URLConnection con = u.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-java-serialized-object");

            OutputStream outstream = con.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outstream);

            oos.writeObject("checkTeacher");
            oos.writeObject(login);
            oos.writeObject(password);

            oos.flush();
            oos.close();

            InputStream instr = con.getInputStream();
            ObjectInputStream inputFromServlet = new ObjectInputStream(instr);

            Object o = inputFromServlet.readObject();
            if (o != null) {
                currentTeacher = (Teacher) o;
                temp = true;
            }
            inputFromServlet.close();
            instr.close();
        } catch (SocketException sE) {
            String problem = "Can't establish a connection to host: \n" + sE.getMessage() + "\n";
            System.err.println(problem);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException iE) {
            }
        } catch (FileNotFoundException fNfE) {
            String problem = "Resource not found in handleWelcoming: " + fNfE.getMessage();
            System.err.println(problem);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException iE) {
            }
        } catch (Exception e) {
            String problem = "Unknown exception in handleWelcoming: " +
                    e.getClass().getName() + ": " + e.getMessage();
            System.err.println(problem);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException iE) {
            }
        }
        return temp;
    }
}
