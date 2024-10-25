package com.integracao.Controllers;

import javafx.scene.layout.VBox;


public class TreeViewController {

    VBox selectedTreeView;

    public VBox createTreeView(String valueSelected) {
        selectedTreeView = new ComboBoxController().comboBoxValueSelected(valueSelected);
        return selectedTreeView;
    }

}