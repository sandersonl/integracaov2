package com.integracao.controllers;

import com.integracaobackend.models.LineModel;
import com.integracaobackend.models.CategoryModel;
import com.integracaobackend.models.MeterModel;
import com.integracaobackend.controllers.CategoryController;
import com.integracaobackend.controllers.LineController;
import com.integracaobackend.controllers.MeterController;
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

    private List<LineModel> lineList;
    private List<CategoryModel> categoryList;
    private List<MeterModel> meterList;

    public MainViewController() {
    }

    @FXML
    public void initialize() {

        LineController lineController = new LineController();
        CategoryController categoryController = new CategoryController();
        MeterController meterController = new MeterController();

        lineList = lineController.getAllLine();
        categoryList = categoryController.getAllCategory();
        meterList = meterController.getAllModel();

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
                .filter(categoryModel -> categoryModel.getLineName().equals(valueSelected))
                .forEach(categoryModel -> {
                    categorySelected = makeBranch(categoryModel.getName(), lineSelected);
                    meterList.stream()
                            .filter(meterModel -> meterModel.getCategoryName().equals(categoryModel.getName()))
                            .forEach(meterModel -> meterSelected = makeBranch(meterModel.getName(), categorySelected));
                });

        titledPaneModel.setDisable(false);
        titledPaneModel.setExpanded(true);
    }

    @FXML
    private void populateComboBox() {
        for (LineModel lineModel : lineList) {
            comboBox.getItems().add(lineModel.getName());
        }
    }

    private TreeItem<String> makeBranch(String name, TreeItem<String> parent) {
        TreeItem<String> newBranch = new TreeItem<>(name);
        newBranch.setExpanded(true);
        parent.getChildren().add(newBranch);
        return newBranch;
    }

}
