package com.integracao.Views;

import com.integracao.Model.MainModel;
import javafx.scene.layout.VBox;

public class MainView {

    public VBox createMainView(){
        VBox mainModel = new MainModel().createViewModel("Linhas", "Selecione uma linha", "Modelos", "Lista de modelos");
        return mainModel;
    }

}