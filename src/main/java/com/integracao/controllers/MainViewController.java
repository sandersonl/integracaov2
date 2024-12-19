package com.integracao.controllers;

import com.integracao.service.ApiCategoryService;
import com.integracao.service.ApiLineService;
import com.integracao.service.ApiModelService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import com.integracao.dto.Line;
import com.integracao.dto.Category;
import com.integracao.dto.Model;


import java.io.IOException;
import java.util.List;

public class MainViewController {

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private TitledPane titledPaneLine;
    @FXML
    private TitledPane titledPaneModel;
    @FXML
    private TreeItem<String> root, lineSelected, categorySelected, modelSelected;

    private List<Category> categoryList;
    private List<Line> lineList;
    private List<Model> modelList;

    public MainViewController() {
    }

    @FXML
    public void initialize() throws IOException {


        try {
            lineList = new ApiLineService().getLines("lines");
            categoryList = new ApiCategoryService().getCategories("categories");
            modelList = new ApiModelService().getModels("models");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        populateComboBox();

        treeView.setRoot(root);

        if (comboBox.getValue() == null) titledPaneModel.setDisable(true);

        titledPaneLine.setContent(new HBox(new Label("Selecione uma linha"), comboBox));
        titledPaneModel.setContent(new HBox(new Label("Lista de Modelos"), treeView));

    }

    @FXML
    private void populateTreeView() {
        String valueSelected = comboBox.getValue();
        root.getChildren().clear();

        lineSelected = makeBranch(valueSelected, root);

        categoryList.stream()
                .filter(category -> category.getLine().getName().equals(valueSelected))
                .forEach(category -> {
                    categorySelected = makeBranch(category.getName(), lineSelected);
                    modelList.stream()
                            .filter(model -> model.getCategory().getName().equals(category.getName()))
                            .forEach(model -> modelSelected = makeBranch(model.getName(), categorySelected));
                });

        titledPaneModel.setDisable(false);
        titledPaneModel.setExpanded(true);
    }

    @FXML
    private void populateComboBox() {
        for (Line line : lineList) {
            comboBox.getItems().add(line.getName());
        }
    }

    private TreeItem<String> makeBranch(String name, TreeItem<String> parent) {
        TreeItem<String> newBranch = new TreeItem<>(name);
        newBranch.setExpanded(true);
        parent.getChildren().add(newBranch);
        return newBranch;
    }

}
