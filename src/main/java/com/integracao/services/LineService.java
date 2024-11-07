package com.integracao.services;

import com.integracao.controllers.LineController;
import com.integracao.models.LineModel;

import java.util.List;

public class LineService {

    LineController lineController = new LineController();

    public LineService() {
    }

    public List<LineModel> getAllLine() {
        return lineController.getAllLine();
    }

    public LineModel getLineById(int id) {
        return lineController.getLineById(id);
    }

}
