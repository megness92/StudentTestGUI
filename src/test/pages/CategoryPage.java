package test.pages;

import test.Page;
import test.panels.*;
import javafx.scene.control.Control;
import javafx.scene.control.TreeItem;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CategoryPage
 *
 */
public class CategoryPage extends Page {

    public CategoryPage(String name, Page ... pages) {
        super(name);
        getChildren().addAll(Arrays.asList(pages));
    }

    List<Page> categoryChildren;
    @Override public Node createView() {
        
        categoryChildren = new ArrayList<Page>();
        for (TreeItem child:getChildren()) {
            Page page = (Page)child;
        }
        VBox main = new VBox(8) {
            @Override protected double computePrefHeight(double width) {
                return Math.max(
                        super.computePrefHeight(width),
                        getParent().getBoundsInLocal().getHeight()
                );
            }
        };
        main.getStyleClass().add("category-page");
        Label header = new Label(getName());
        header.setMaxWidth(Double.MAX_VALUE);
        header.setMinHeight(Control.USE_PREF_SIZE); // Workaround for RT-14251
        header.getStyleClass().add("page-header");
        main.getChildren().add(header);
        
        for(Page categoryPage:categoryChildren) {
            Label categoryHeader = new Label(categoryPage.getName());
            categoryHeader.setMaxWidth(Double.MAX_VALUE);
            categoryHeader.setMinHeight(Control.USE_PREF_SIZE); // Workaround for RT-14251
            categoryHeader.getStyleClass().add("category-header");
            main.getChildren().add(categoryHeader);
            TilePane directChildFlow = new TilePane(8,8);
            directChildFlow.setPrefColumns(1);
            main.getChildren().add(directChildFlow);
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("noborder-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(main);
        return scrollPane;
    }
  }
