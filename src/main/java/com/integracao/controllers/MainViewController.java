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


import java.util.List;

public class MainViewController {

    @FXML
    public ComboBox<String> comboBox;
    @FXML
    public TreeView<String> treeView;
    @FXML
    public TitledPane titledPaneLine;
    @FXML
    public TitledPane titledPaneModel;
    @FXML
    public TreeItem<String> root, lineSelected, categorySelected, modelSelected;

    public List<Category> categoryList;
    public List<Line> lineList;
    public List<Model> modelList;

    public ApiLineService apiLineService;
    public ApiCategoryService apiCategoryService;
    public ApiModelService apiModelService;

    public MainViewController() {
        this.apiLineService = new ApiLineService();
        this.apiCategoryService = new ApiCategoryService();
        this.apiModelService = new ApiModelService();
    }

    @FXML
    public void initialize() {

        try {
            lineList = apiLineService.getLines("lines");
            categoryList = apiCategoryService.getCategories("categories");
            modelList = apiModelService.getModels("models");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        populateComboBox();

        titlePanedModelDisable();

        setupUi();

    }

    public void titlePanedModelDisable() {
        if (comboBox.getSelectionModel().getSelectedItem() == null) titledPaneModel.setDisable(true);
    }

    public void setupUi() {
        titledPaneLine.setContent(new HBox(new Label("Selecione uma linha"), comboBox));
        titledPaneModel.setContent(new HBox(new Label("Lista de Modelos"), treeView));
    }

    @FXML
    public void populateTreeView() {
        titledPaneModel.setDisable(false);
        titledPaneModel.setExpanded(true);

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
    }

    @FXML
    public void populateComboBox() {
        for (Line line : lineList) {
            comboBox.getItems().add(line.getName());
        }
    }

    public TreeItem<String> makeBranch(String name, TreeItem<String> parent) {
        TreeItem<String> newBranch = new TreeItem<>(name);
        newBranch.setExpanded(true);
        parent.getChildren().add(newBranch);
        return newBranch;
    }

}
