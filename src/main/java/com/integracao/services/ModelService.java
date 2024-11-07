package com.integracao.services;

import com.integracao.controllers.ModelController;
import com.integracao.models.MeterModel;

import java.util.List;

public class ModelService {

    ModelController modelController = new ModelController();

    public ModelService() {
    }

    public List<MeterModel> getAllModel() {
        return modelController.getAllModel();
    }

    public List<MeterModel> getModelByCategoryId(int id) {
        return modelController.getModelByCategoryId(id);
    }

}
