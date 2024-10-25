package com.integracao.Model;

import com.integracao.Controllers.MainController;
import com.integracao.Controllers.TreeViewController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainModel {

    TitledPane titledPaneLine, titledPaneModel, t1 , t2;
    ComboBox<String> comboBoxController;
    VBox treeViewVbox;

    public VBox createViewModel(String titleT1, String descriptionT1, String titleT2, String descriptionT2) {

        MainController mainController = new MainController();
        TreeViewController treeViewController = new TreeViewController();
        Separator separator = new Separator();

        comboBoxController = mainController.createComboBoxController();
        treeViewVbox = treeViewController.createTreeView("");

        // t1 e t2 sao os titledpane que estão no accordion.
        t1 = TitledPaneLine(titleT1, descriptionT1, comboBoxController);
        t2 = TitledPaneModel(titleT2, descriptionT2, treeViewVbox);

        comboBoxController.setOnAction( e -> {
            treeViewVbox.getChildren().clear();
            treeViewVbox.getChildren().add(treeViewController.createTreeView(comboBoxController.getValue()));
            titledPaneModel.setExpanded(true);
        });

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(t1, t2);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.getChildren().addAll(new Label("Autor: Lucas"), separator, accordion);

        return layout;
    }

    public TitledPane TitledPaneLine(String title, String description, ComboBox<String> comboBox){
        titledPaneLine = new TitledPane(title, new HBox(new Label(description), comboBox));
        return titledPaneLine;
    }

    public TitledPane TitledPaneModel(String title, String description, VBox treeView){
        titledPaneModel = new TitledPane(title, new HBox(new Label(description), treeView));
        return titledPaneModel;
    }

}
