package test.controls;

import java.util.ArrayList;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import java.util.Arrays;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * PropertySheet for editing groups of bean properties
 */
public class PropertySheet extends Accordion {

    private final List<PropertyGroup> propertyGroups;

    public PropertySheet(PropertyGroup ... propertyGroups) {
        getStyleClass().add("property-sheet");
        this.propertyGroups = Arrays.asList(propertyGroups);
        for(PropertyGroup pg: propertyGroups) {
            getPanes().add(pg.create());
        }
        if (propertyGroups.length > 0) getPanes().get(0).setExpanded(true);
    }

    public static PropertySheet.Property createProperty(String name) {
        return new PropertySheet.TextPropertyImpl(name);
    }
    public static PropertySheet.Property createProperty(String name, EventHandler<ActionEvent> action) {
        return new PropertySheet.ActionPropertyImpl(name, action);
    }
    public static PropertySheet.Property createEditProperty(String name) {
        return new PropertySheet.StringPropertyImpl(name);
    }
    public static PropertySheet.Property createDateProperty(String name) {
        return new PropertySheet.DatePropertyImpl(name);
    }
    
    public static class PropertyGroup {
        private final String title;
        private final Property[] properties;

        public PropertyGroup(String title,PropertySheet.Property ... properties) {
            this.title = title;
            this.properties = properties;
        }

        private TitledPane create() {
            GridPane grid = new GridPane();
            grid.setMaxWidth(Double.MAX_VALUE);
            TitledPane titledPane = new TitledPane(title, grid);
            grid.getColumnConstraints().addAll(
                    new ColumnConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE, Priority.ALWAYS, HPos.CENTER, true),
                    new ColumnConstraints(50, USE_COMPUTED_SIZE, Double.MAX_VALUE, Priority.ALWAYS, HPos.LEFT, true)
            );
            int row = 0;
            for(PropertySheet.Property prop: properties) {
                if (prop == null){
                    continue;
                } else if (prop instanceof PropertySheet.ActionPropertyImpl || prop instanceof PropertySheet.TextPropertyImpl) {
                    Label propLabel = new Label(prop.getName()+":");
                    propLabel.getStyleClass().add("property-label");
                    Node editor = prop.getEditor();
                    GridPane.setHalignment(editor, HPos.CENTER);
                    GridPane.setHgrow(editor, Priority.ALWAYS);
                    GridPane.setMargin(editor, new Insets(2,2,2,2));
                    GridPane.setColumnIndex(editor, 0);
                    GridPane.setColumnSpan(editor, 2);
                    GridPane.setRowIndex(editor, row);
                    grid.getChildren().add(editor);
                    row ++;
                } else if(prop instanceof PropertySheet.DatePropertyImpl) {
                    Label propLabel = new Label(prop.getName()+":");
                    propLabel.getStyleClass().add("property-label");
                    propLabel.setMaxWidth(Double.MAX_VALUE);
                    propLabel.setAlignment(Pos.CENTER_RIGHT);
                    GridPane.setHalignment(propLabel, HPos.RIGHT);
                    GridPane.setMargin(propLabel, new Insets(2,2,2,2));
                    GridPane.setColumnIndex(propLabel,0);
                    GridPane.setRowIndex(propLabel, row);
                    grid.getChildren().add(propLabel);
                    HBox hbox = new HBox();
                    Node editor = prop.getEditor();
                    hbox.getChildren().add(editor);
                    
                    GridPane.setHalignment(hbox, HPos.CENTER);
                    GridPane.setHgrow(hbox, Priority.ALWAYS);
                    GridPane.setMargin(hbox, new Insets(2,2,2,2));
                    GridPane.setColumnIndex(hbox, 1);
                    GridPane.setColumnSpan(hbox, 2);
                    GridPane.setRowIndex(hbox, row);
                    grid.getChildren().add(hbox);
                    row ++;
                }
                else {
                    Label propLabel = new Label(prop.getName()+":");
                    propLabel.setMaxWidth(Double.MAX_VALUE);
                    propLabel.setAlignment(Pos.CENTER_RIGHT);
                    GridPane.setHalignment(propLabel, HPos.RIGHT);
                    GridPane.setMargin(propLabel, new Insets(2,2,2,2));
                    GridPane.setColumnIndex(propLabel,0);
                    GridPane.setRowIndex(propLabel, row);
                    grid.getChildren().add(propLabel);
                    Node editor = prop.getEditor();
                    GridPane.setHgrow(editor, Priority.ALWAYS);
                    GridPane.setMargin(editor, new Insets(2,2,2,2));
                    GridPane.setColumnIndex(editor,1);
                    GridPane.setRowIndex(editor, row);
                    grid.getChildren().add(editor);
                    row ++;
                }
            }
            return titledPane;
        }
        
        public void changeEditor(String s) {
            for(PropertySheet.Property prop: properties) {
                if (prop == null){
                    continue;
                } else if (prop instanceof PropertySheet.ActionPropertyImpl || prop instanceof PropertySheet.TextPropertyImpl) {
                    prop.setEditor(s);
                }
            }
        }
    }

    public static abstract class Property {
        private String name;

        protected Property(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract Node getEditor();
        
        public abstract void setEditor(String s);
    }

    static class ActionPropertyImpl extends PropertySheet.Property {
        private Hyperlink editor = new Hyperlink();

        ActionPropertyImpl(String name, EventHandler<ActionEvent> action) {
            super(name);
            editor.setStyle("-fx-font-size: 1.2em;");
            editor.setText(name);
            editor.setOnAction(action);
        }

        @Override public Node getEditor() {
            return editor;
        }
        
        @Override public void setEditor(String s) {
             editor.setText(s);
        }
    }

    static class TextPropertyImpl extends PropertySheet.Property {
        private Text editor = new Text();

        TextPropertyImpl(String name) {
            super(name);
            editor.setStyle("-fx-font-size: 1.2em;");
            editor.setWrappingWidth(200);
            editor.setText(name);
            editor.setFill(Color.WHITE);
        }

        @Override public Node getEditor() {
            return editor;
        }
        
        @Override public void setEditor(String s) {
             editor.setText(s);
        }
    }
    
    static class StringPropertyImpl extends PropertySheet.Property {
        private TextField editor = new TextField();
        private String value = null;

        StringPropertyImpl(String name) {
            super(name);
            
            editor.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent t) {
                    String newValue = editor.getText();
                    editor.setStyle("-fx-font-size: 1.09em;");
                }
            });
        }

        @Override public Node getEditor() {
            return editor;
        }
        
        @Override public void setEditor(String s) {
             editor.setText(s);
        }
    }
    
    static class DatePropertyImpl extends PropertySheet.Property {
        private TextField editor = new TextField();
        private String value = null;

        DatePropertyImpl(String name) {
            super(name);
            editor.setStyle("-fx-font-size: 1.09em;");
        }
        
        @Override public Node getEditor() {
            return editor;
        }
        
        @Override public void setEditor(String s) {
             editor.setText(s);
        }
    }
}
