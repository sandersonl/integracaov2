package com.integracao.Controllers;

import com.integracao.Model.ComboBoxModel;
import javafx.scene.control.ComboBox;


public class MainController {

    ComboBox<String> comboBox;
    ComboBoxModel comboBoxModel = new ComboBoxModel();

    public ComboBox<String> createComboBoxController(){
        comboBox = comboBoxModel.getComboBox();
        return comboBox;
    }
}