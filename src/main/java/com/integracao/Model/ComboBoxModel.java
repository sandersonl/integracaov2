package com.integracao.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class ComboBoxModel {

    ComboBox<String> comboBox;
    ObservableList<String> items = FXCollections.observableArrayList("Cronos", "Ares");

    public ComboBox<String> getComboBox(){
        comboBox = new ComboBox<>(items);
        return comboBox;
    }

}
