package test.panels;

import com.caucho.hessian.client.HessianProxyFactory;
import impl.org.controlsfx.i18n.Localization;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import test.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextBuilder;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 * Created by Asus on 08.11.2014.
 */
public class PassTestPage extends Page {

    public PassTestPage(String name) {
        super(name);
        Localization.setLocale(new Locale("en", "CH"));
    }

    private Test test;
    private Pagination pagination;

    private Timeline timeline;
    Text currentRateText;
    int page;
    BorderPane borderPane;
    Answer answers[];
    ArrayList<AnswersStudent> finalAnswers;

    long timeSeconds;

    VBox main;
    
    @Override
    public Node createView() {

        main = new VBox(8) {
            @Override
            protected double computePrefHeight(double width) {
                return Math.max(
                        super.computePrefHeight(width),
                        getParent().getBoundsInLocal().getHeight()
                );
            }
        };
        main.getStyleClass().add("category-page");

        test = TSStudentMain.getTSStudentMain().getCurrentTest();
        answers = new Answer[test.getNumberOfQuestions()];

        ToolBar pageToolBar = new ToolBar();
        pageToolBar.setId("page-toolbar");
        pageToolBar.setMinHeight(29);
        pageToolBar.setMaxWidth(Double.MAX_VALUE);

        Button submitButton = new Button("Submit");
        submitButton.setGraphic(new ImageView(new Image(TSStudentMain.class.getResource("images/finish2.png").toString())));
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String unanswered = "";
                for (int i = 0; i < test.getNumberOfQuestions(); i++) {
                    if(answers[i] == null || answers[i].getText().equals("")) {
                        if(unanswered.equals(""))
                            unanswered += (" №" + (i + 1));
                        else unanswered += (", №" + (i + 1));
                    }
                }
                Action response = Dialogs.create()
                        .owner(TSStudentMain.getTSStudentMain().getStage())
                        .title("Confirm Dialog")
                        .message("You have not answered questions: " + unanswered + "! You still have the opportunity to answer the questions! To continue the test choose Cancel, to finish choose OK")
                        .actions(Dialog.Actions.OK, Dialog.Actions.CANCEL)
                        .showConfirm();

                if (response == Dialog.Actions.OK) {
                    addAnswer();
                    main.setDisable(true);
                   //submitTest(); 
                }   
            }
        });
        submitButton.setMaxHeight(Double.MAX_VALUE);
        submitButton.setFont(new Font("Verdana", 13));
        submitButton.setTooltip(new Tooltip("Submit test answers"));

        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);

        Text t = TextBuilder.create().text("")
                .x(0).y(0).font(Font.font("System", 12))
                .fill(Color.web("black"))
                .effect(DropShadowBuilder.create().radius(15).color(Color.web("#ACACAC")).blurType(BlurType.ONE_PASS_BOX).build())
                .build();
        HBox.setMargin(t, new Insets(0, 0, 0, 5));

        currentRateText = new Text();
        currentRateText.setFill(Color.WHITE);
        currentRateText.setFont(new Font("Verdana", 13));

        if (test.getTimeToPass() != 0) {
            timeSeconds = 3;//test.getTimeToPass();
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent t) {
                            timeSeconds--;
                            int hours = (int) timeSeconds / 3600;
                            int remainder = (int) timeSeconds - hours * 3600;
                            int mins = remainder / 60;
                            remainder = remainder - mins * 60;
                            int secs = remainder;
                            String timeString = String.format("%02d:%02d:%02d", hours, mins, secs);
                            currentRateText.setText("   " + timeString + "   ");
                            if (timeSeconds <= 0) {
                                timeline.stop();
                                main.setDisable(true);
//                                Action response = Dialogs.create()
//                                    .owner(TSStudentMain.getTSStudentMain().getStage())
//                                    .title("User message")
//                                    .message("Time is over, the test is finished!")
//                                    .actions(Dialog.Actions.OK)
//                                    .showConfirm();
//                                if (response == Dialog.Actions.OK) {
                                    addAnswer();
                                    submitTest();
//                                }
                            }
                        }
                    }
                    ));
           timeline.playFromStart();
        }

        pageToolBar.getItems().addAll(submitButton, spacer3, t, currentRateText);

        pagination = new Pagination(test.getNumberOfQuestions(), 0);
       // pagination.setId("pag");
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                addAnswer();
                page = pageIndex;
                radioButtons = new ArrayList<RadioButton>();
                checkBoxes = new ArrayList<CheckBox>();
                return createQuestionPage();
            }
        });
        pagination.setPrefHeight(700);
        pagination.getStyleClass().add("pagination");

        main.getChildren().add(pagination);
        borderPane = new BorderPane();
        borderPane.setTop(pageToolBar);
        borderPane.setCenter(main);

        return borderPane;
    }

    private ScrollPane createQuestionPage() {
        if (test.getQuestion(page).getQuestionType().getName().equals("open")) {
            return createOpenQuestionPage();
        } else if (test.getQuestion(page).getQuestionType().getName().equals("single")) {
            return createSimpleQuestionPage();
        } else {
            return createMultipleQuestionPage();
        }
    }

    ToggleGroup radioGroup;
    ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>();
    ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();
    FlowPane tileSingle;
    FlowPane tileMultiple;

    private ScrollPane createSimpleQuestionPage() {

        VBox main = new VBox(20) {
            @Override
            protected double computePrefHeight(double width) {
                return Math.max(
                        super.computePrefHeight(width),
                        getParent().getBoundsInLocal().getHeight()
                );
            }
        };
        main.getStyleClass().add("category-page");
        main.setPadding(new Insets(20));

        Label categoryHeader = new Label("Question #" + (page + 1));
        categoryHeader.setMaxWidth(Double.MAX_VALUE);
        categoryHeader.setMinHeight(Control.USE_PREF_SIZE);
        categoryHeader.setId("category-header");

        Text q = new Text(test.getQuestion(page).getText());
        q.setFont(new Font("Verdana", 14));
        q.setWrappingWidth(950);
        q.setFill(Color.WHITE);

        main.getChildren().addAll(categoryHeader, q);

        tileSingle = new FlowPane(Orientation.VERTICAL);
        tileSingle.setVgap(20);
        tileSingle.setHgap(4);
        tileSingle.setPadding(new Insets(30, 10, 10, 10));
        
        radioGroup = new ToggleGroup();
        Separator[] separator1 = new Separator[test.getQuestion(page).getAnswers().size()];
        for (int i = 0; i < test.getQuestion(page).getAnswers().size(); i++) {
            VBox box = new VBox(15);
            box.setAlignment(Pos.CENTER_LEFT);

            RadioButton but = new RadioButton(test.getQuestion(page).getAnswers().get(i).getText());
            but.setToggleGroup(radioGroup);
            radioButtons.add(but);
            separator1[i] = new Separator();
            box.getChildren().addAll(separator1[i], but);  
            
            tileSingle.getChildren().add(box);
        }

        if(answers[page] != null && Integer.valueOf(answers[page].getText()) >= 0) 
                radioButtons.get(Integer.valueOf(answers[page].getText())).setSelected(true);
        
        main.getChildren().add(tileSingle);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("noborder-scroll-pane");
        scrollPane.setContent(main);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMinWidth(685);

        return scrollPane;
    }

    TextArea text;

    private ScrollPane createOpenQuestionPage() {

        VBox main = new VBox(20) {
            @Override
            protected double computePrefHeight(double width) {
                return Math.max(
                        super.computePrefHeight(width),
                        getParent().getBoundsInLocal().getHeight()
                );
            }
        };
        main.getStyleClass().add("category-page");
        main.setPadding(new Insets(20));

        Label categoryHeader = new Label("Question #" + (page + 1));
        categoryHeader.setMaxWidth(Double.MAX_VALUE);
        categoryHeader.setMinHeight(Control.USE_PREF_SIZE);
        categoryHeader.setId("category-header");

        Text q = new Text(test.getQuestion(page).getText());
        q.setFont(new Font("Verdana", 14));
        q.setWrappingWidth(950);
        q.setFill(Color.WHITE);

        text = new TextArea("") {
            @Override
            protected double computePrefHeight(double width) {
                return Math.max(
                        super.computePrefHeight(width),
                        getParent().getBoundsInLocal().getHeight()
                );
            }
        };
        text.setMinHeight(300);
        text.setPromptText("Print your answer here, be as detailed and precise as possible");
        text.setWrapText(true);

        if (answers[page] != null) {
            text.setText(answers[page].getText());
        }

        main.getChildren().addAll(categoryHeader, q, text);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("noborder-scroll-pane");
        scrollPane.setContent(main);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMinWidth(685);

        return scrollPane;
    }

    private ScrollPane createMultipleQuestionPage() {
        VBox main = new VBox(20) {
            @Override
            protected double computePrefHeight(double width) {
                return Math.max(
                        super.computePrefHeight(width),
                        getParent().getBoundsInLocal().getHeight()
                );
            }
        };
        main.getStyleClass().add("category-page");
        main.setPadding(new Insets(20));

        Label categoryHeader = new Label("Question #" + (page + 1));
        categoryHeader.setMaxWidth(Double.MAX_VALUE);
        categoryHeader.setMinHeight(Control.USE_PREF_SIZE);
        categoryHeader.setId("category-header");

        Text q = new Text(test.getQuestion(page).getText());
        q.setFont(new Font("Verdana", 14));
        q.setWrappingWidth(950);
        q.setFill(Color.WHITE);

        main.getChildren().addAll(categoryHeader, q);

        tileMultiple = new FlowPane(Orientation.VERTICAL);
        tileMultiple.setVgap(20);
        tileMultiple.setHgap(4);
        tileMultiple.setPadding(new Insets(30, 10, 10, 10));
        
        Separator[] separator1 = new Separator[test.getQuestion(page).getAnswers().size()];
        for (int i = 0; i < test.getQuestion(page).getAnswers().size(); i++) {
            VBox box = new VBox(15);
            box.setAlignment(Pos.CENTER_LEFT);

            CheckBox but = new CheckBox(test.getQuestion(page).getAnswers().get(i).getText());
            checkBoxes.add(but);
            separator1[i] = new Separator();
            box.getChildren().addAll(separator1[i], but);  
            
            if (answers[page] != null && answers[page].getText().contains(Integer.toString(i))) {
                checkBoxes.get(i).setSelected(true);
            }
            
            tileMultiple.getChildren().add(box);
        }
    
        main.getChildren().add(tileMultiple);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("noborder-scroll-pane");
        scrollPane.setContent(main);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMinWidth(685);

        return scrollPane;
    }

    public void addAnswer() {
        if(radioGroup != null){
            if(test.getQuestion(page).getQuestionType().getName().equals("open")) {   
                if(answers[page] == null)
                    answers[page] = new Answer(); 
                answers[page].setText(text.getText());
            }

            else if(test.getQuestion(page).getQuestionType().getName().equals("single")) {
                if(radioGroup.getToggles().indexOf(radioGroup.getSelectedToggle()) != -1) {
                    if(answers[page] == null)
                        answers[page] = new Answer(); 
                    answers[page].setText(Integer.toString(radioGroup.getToggles().indexOf(radioGroup.getSelectedToggle())));
                }
            }
            else {
                String ans = "";
                for(int i = 0; i < checkBoxes.size(); i++) {
                    if(checkBoxes.get(i).isSelected())
                        ans += Integer.toString(i);
                }
                if(answers[page] == null)
                    answers[page] = new Answer();
                answers[page].setText(ans);
            }
        }
    }

    public void submitTest() {

        String pass = "";
        int score = 0;
        String grade = "";
        int correct = 0;
        int wrong = 0;
        int open = 0;

        finalAnswers = new ArrayList<AnswersStudent>();

        //----------------------------
        for (int i = 0; i < test.getNumberOfQuestions(); i++) {

            //AnswersStudent fa = new AnswersStudent();
            // fa.setStudentID(TSStudentMain.getTSStudentMain().getCurrentStudent().getId());
            // fa.setQuestionID(test.getQuestion(i).getId());
            // fa.setTestID(test.getId());
            if (test.getQuestion(i).getQuestionType().getName().equals("open")) {
                AnswersStudent fa = new AnswersStudent();
                // fa.setStudentID(TSStudentMain.getTSStudentMain().getCurrentStudent().getId());
                // fa.setQuestionID(test.getQuestion(i).getId());
                // fa.setTestID(test.getId());
                //fa.setAnswerID(test.getQuestion(i).getAnswers().get(0).getId());
                finalAnswers.add(fa);
                open++;
            } else if (test.getQuestion(i).getQuestionType().getName().equals("single")) {

                AnswersStudent fa = new AnswersStudent();
                // fa.setStudentID(TSStudentMain.getTSStudentMain().getCurrentStudent().getId());
                // fa.setQuestionID(test.getQuestion(i).getId());
                // fa.setTestID(test.getId());
                //if(answers[i] == null)
                wrong++;
                //else {
                int ans = radioGroup.getToggles().indexOf(radioGroup.getSelectedToggle());
                    //fa.setAnswerID(test.getQuestion(i).getAnswers().get(ans).getId());
//                    if(ans == Integer.valueOf(test.getQuestion(i).getCorrect())) {
//                        correct++;
//                        score += test.getQuestion(i).getValue();
//                    }
                //else
                wrong++;
                //}
                finalAnswers.add(fa);
            } else {
                // if(answers[i] == null)
                wrong++;
                //else {
                String ans = "";
                for (int j = 0; j < 10; j++) {
//                    if (checkButtons[i].isSelected()) {
//                        ans += Integer.toString(i) + ",";
//                        AnswersStudent fa = new AnswersStudent();
//                            // fa.setStudentID(TSStudentMain.getTSStudentMain().getCurrentStudent().getId());
//                        // fa.setQuestionID(test.getQuestion(i).getId());
//                        // fa.setTestID(test.getId());
//                        //fa.setAnswerID(test.getQuestion(i).getAnswers().get(j).getId());
//                        finalAnswers.add(fa);
//                    }

//                    if(test.getQuestion(i).getCorrect().equals(ans)) {
//                        correct++;
//                        score += test.getQuestion(i).getValue();
//                    }
//                    else
                    wrong++;
                    // }
                }
            }
        }

        if (open > 0) {
            pass = "Open question present";
        } else if (score >= test.getPassScore()) {
            pass = "Passed";
        } else {
            pass = "Failed";
        }

        if (score <= test.getPoor()) {
            grade = "Poor";
        } else if (score <= test.getUnsat()) {
            grade = "Unsatisfactory";
        } else if (score <= test.getSat()) {
            grade = "Satisfactory";
        } else if (score <= test.getGood()) {
            grade = "Good";
        } else if (score <= test.getExc()) {
            grade = "Excellent";
        }

        //AnsweredTest ansTest = new AnsweredTest(score, grade, correct, wrong, open);        
        borderPane.setVisible(false);

        TSStudentMain.getTSStudentMain().getResultDialog().setPass(pass);
        TSStudentMain.getTSStudentMain().getResultDialog().setGrade(grade);
        TSStudentMain.getTSStudentMain().getResultDialog().setScore(score);
        TSStudentMain.getTSStudentMain().getResultDialog().setOpen(open);
        TSStudentMain.getTSStudentMain().getResultDialog().setCorrect(correct);
        TSStudentMain.getTSStudentMain().getResultDialog().setWrong(wrong);

        TSStudentMain.getTSStudentMain().showResultDialog();
    }

//    public void add(final Auction a) {
//        try { 
//        String url = AuctionClientFX.URL + "AuctionServiceImpl";
//        HessianProxyFactory factory = new HessianProxyFactory();
//        auctionService = (AuctionService) factory.create(AuctionService.class, url);
//        final Task<Boolean> task = new Task<Boolean>()
//        {
//            protected Boolean call() throws Exception
//            {
//                return auctionService.addAuction(a);
//            }
//        };
//        task.stateProperty().addListener(new ChangeListener<Worker.State>()
//        {
//            public void changed(ObservableValue<? extends Worker.State> source,
//                                Worker.State oldState, Worker.State newState)
//            {
//                if (newState.equals(Worker.State.SUCCEEDED))
//                {
//                    if(task.getValue()) {
//                        AuctionClientFX.getAuctionClientFX().sendAuction();
//                        labelNameInf.setText("");
//                        labelPlaceInf.setText("");
//                        labelRDateInf.setText("");
//                        labelRTimeInf.setText("");
//                        labelSTimeInf.setText("");
//                        labelSDateInf.setText("");
//                        labelSubjectInf.setText("");
//                        labelPriceInf.setText("");
//                        labelProductInf.setText("");
//                        labelQuantityInf.setText("");
//                        labelSectorInf.setText("");
//                        link1.setText("Прикрепить");
//                        Dialog.setTextAndTitle("Сообщение для пользователя", "Новый аукцион для закупки " + a.getSubject() + " создан");
//                        Dialog d = new Dialog();
//                    }
//                    else {
//                        Dialog.setTextAndTitle("Сообщение для пользователя", "Не удалось создать данный аукцион");
//                        Dialog d = new Dialog();
//                    }
//                }
//                else if (newState.equals(Worker.State.FAILED))
//                {
//                   Dialog.setTextAndTitle("Сообщение для пользователя", "Возникла проблема с передачей данных с сервера. Проверьте подключение к серверу");
//                    Dialog d = new Dialog();
//                    Throwable exception = task.getException();
//                    exception.printStackTrace();
//                }
//            }
//        });
//        
//        new Thread(task).start();
//      } catch(Exception e) {
//            Dialog.setTextAndTitle("Сообщение для пользователя", "Возникла проблема с передачей данных с сервера. Проверьте подключение к серверу");
//            Dialog d = new Dialog();
//            System.out.println("FFFFFFFFFFFFFFFFFFFFFF");}  
//    }
}
