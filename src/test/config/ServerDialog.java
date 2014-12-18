/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.config;

import test.TSStudentMain;
import test.Pages;
import test.pages.CategoryPage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class ServerDialog extends VBox {
    private Button okBtn;
    private Pages pages;
    private final Window owner;
    private final ProxyPanel proxyPanel;
    private final Tab proxyTab;
    private final TabPane options;

    public ServerDialog(final Window owner, final Pages pages) {
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

        String text = "Changing of the server address is performed by the administrator. "
                + "Users receive notification about changes. "
                + "Without proper notification from the administrator it is not recommended to change the server address.";
        Text explanation = TextBuilder.create().text(text)
                .fill(Color.WHITE)
                .build();
        explanation.setWrappingWidth(400);
        explanation.setStyle("-fx-font-size: 1.1em;");
        BorderPane explPane = new BorderPane();
        VBox.setMargin(explPane, new Insets(5, 5, 5, 5));
        explPane.setCenter(explanation);
        BorderPane.setMargin(explanation, new Insets(5, 5, 5, 5));

        Label title = new Label("Server address");
        title.setId("title");
        title.setMinHeight(22);
        title.setPrefHeight(22);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        getChildren().add(title);

        proxyTab = new Tab("Enter server data");
        proxyPanel = new ProxyPanel();
        proxyTab.setContent(proxyPanel);

        options = new TabPane();
        options.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        options.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        options.getTabs().addAll(proxyTab);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setId("glass-grey");
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                TSStudentMain.getTSStudentMain().hideModalMessage();
            }
        });
        cancelBtn.setTooltip(new Tooltip("Cancel"));
        cancelBtn.setMinWidth(74);
        cancelBtn.setPrefWidth(74);
        okBtn = new Button("Save");
        okBtn.setId("glass-grey");
        okBtn.setDisable(true);
        okBtn.setDisable(true);
        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                if(!proxyPanel.hostNameBox.getText().equals("")) {
                    TSStudentMain.getTSStudentMain().hideModalMessage();
                    TSStudentMain.URL = proxyPanel.hostNameBox.getText();;
                }
            }
        });
        okBtn.setMinWidth(94);
        okBtn.setPrefWidth(94);
        okBtn.setTooltip(new Tooltip("Set server adress"));
        HBox.setMargin(okBtn, new Insets(0, 8, 0, 0));
        
        HBox bottomBar = new HBox(0);
        bottomBar.setAlignment(Pos.BASELINE_RIGHT);
        bottomBar.getChildren().addAll(okBtn, cancelBtn);
        VBox.setMargin(bottomBar, new Insets(20, 5, 5, 5));

        getChildren().addAll(explPane, options, bottomBar);
    }

    
    private class ProxyPanel extends GridPane {
        TextField hostNameBox;

        public ProxyPanel() {
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

            ChangeListener<String> textListener = new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                    okBtn.setDisable(
                            hostNameBox.getText() == null || hostNameBox.getText().isEmpty());
                }
            };
            hostNameBox.textProperty().addListener(textListener);

            getChildren().addAll(label2, hostNameBox);
        }

        public TextField getHostNameBox() {
            return hostNameBox;
        }
    }
}
