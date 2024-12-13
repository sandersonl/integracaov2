package com.integracao.controllers;

import com.integracaobackend.controllers.CategoryController;
import com.integracaobackend.controllers.LineController;
import com.integracaobackend.controllers.ModelController;
import com.integracaobackend.entity.Line;
import com.integracaobackend.entity.Category;
import com.integracaobackend.entity.Model;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


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
    private TreeItem<String> root, lineSelected, categorySelected, meterSelected;

    private List<Line> lineList;
    private List<Category> categoryList;
    private List<Model> modelList;

    public MainViewController() {
    }

    @FXML
    public void initialize() {

        LineController lineController = new LineController();
        CategoryController categoryController = new CategoryController();
        ModelController meterController = new ModelController();

        lineList = lineController.getAllLine();
        categoryList = categoryController.getAllCategory();
        modelList = meterController.getAllModel();

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
                .filter(categoryModel -> categoryModel.getLine().getName().equals(valueSelected))
                .forEach(categoryModel -> {
                    categorySelected = makeBranch(categoryModel.getName(), lineSelected);
                    modelList.stream()
                            .filter(meterModel -> meterModel.getCategory().getName().equals(categoryModel.getName()))
                            .forEach(meterModel -> meterSelected = makeBranch(meterModel.getName(), categorySelected));
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
