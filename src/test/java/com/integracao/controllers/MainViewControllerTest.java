package com.integracao.controllers;

import com.integracao.dto.Category;
import com.integracao.dto.Line;
import com.integracao.dto.Model;
import com.integracao.service.ApiCategoryService;
import com.integracao.service.ApiLineService;
import com.integracao.service.ApiModelService;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MainViewControllerTest extends ApplicationTest {

    private MainViewController mvc;

    @Before
    public void setUp(){
        mvc = spy(MainViewController.class);
        mvc.titledPaneLine = new TitledPane();
        mvc.titledPaneModel = new TitledPane();
        mvc.comboBox = new ComboBox<>();
        mvc.treeView = new TreeView<>();
        mvc.root = new TreeItem<>();
        mvc.lineList = mockLines();
        mvc.categoryList = mockCategories();
        mvc.modelList = mockModel();

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void finish() {
        mvc.modelList = null;
        mvc.categoryList = null;
        mvc.lineList = null;
        mvc.root = null;
        mvc.treeView = null;
        mvc.comboBox = null;
        mvc.titledPaneModel = null;
        mvc.titledPaneLine = null;
        mvc = null;
    }

    @Test
    public void initializeTest01() throws Exception {
        // Given
        mvc.apiLineService = PowerMockito.mock(ApiLineService.class);
        mvc.apiCategoryService = PowerMockito.mock(ApiCategoryService.class);
        mvc.apiModelService = PowerMockito.mock(ApiModelService.class);

        when(mvc.apiLineService.getLines("lines")).thenReturn(mockLines());
        when(mvc.apiCategoryService.getCategories("categories")).thenReturn(mockCategories());
        when(mvc.apiModelService.getModels("models")).thenReturn(mockModel());
        // When
        mvc.initialize();
        // Then
        verify(mvc.apiLineService, times(1)).getLines("lines");
        verify(mvc.apiCategoryService, times(1)).getCategories("categories");
        verify(mvc.apiModelService, times(1)).getModels("models");

        assertNull(mvc.root.getValue());

        verify(mvc, times(1)).populateComboBox();
        verify(mvc, times(1)).titlePanedModelDisable();
        verify(mvc, times(1)).setupUi();
    }

    @Test
    public void initializeTest02() throws Exception {
        // Given
        mvc.apiLineService = PowerMockito.mock(ApiLineService.class);
        mvc.apiCategoryService = PowerMockito.mock(ApiCategoryService.class);
        mvc.apiModelService = PowerMockito.mock(ApiModelService.class);

        when(mvc.apiLineService.getLines("lines")).thenThrow(new RuntimeException("API error"));
        // When
        try {
            mvc.initialize();
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            // Then
            assertTrue("The cause of the exception is not an instance of Exception",e.getCause() instanceof Exception);
            assertEquals("API error", e.getCause().getMessage());
        }

        verify(mvc.apiCategoryService, never()).getCategories(anyString());
        verify(mvc.apiModelService, never()).getModels(anyString());
    }

    @Test
    public void populateComboBoxTest01() {
        // Given
        // mvc.lineList = mock in setup of the test
        // When
        mvc.populateComboBox();
        // Then
        assertEquals(2, mvc.comboBox.getItems().size());
        assertTrue("The item Cronos was not found in the ComboBox",mvc.comboBox.getItems().contains("Cronos"));
        assertTrue("The item Ares was not found in the ComboBox",mvc.comboBox.getItems().contains("Ares"));
    }

    @Test
    public void titlePaneModelDisableTest01() {
        // When
        mvc.titlePanedModelDisable();
        // Then
        assertNull(mvc.comboBox.getSelectionModel().getSelectedItem());
        assertTrue("The titledPaneModel is not disable",mvc.titledPaneModel.isDisabled());
    }

    @Test
    public void titledPaneModelDisableTest02() {
        // Given
        mvc.comboBox.getItems().add("Cronos");
        mvc.comboBox.getItems().add("Ares");
        // When
        mvc.comboBox.getSelectionModel().selectFirst();
        mvc.titlePanedModelDisable();
        //Then
        assertFalse("The titledPaneModel is disable", mvc.titledPaneModel.isDisabled());
        assertTrue("The titledPaneModel is not expanded",mvc.titledPaneModel.isExpanded());
    }

    @Test
    public void setupUiTest01() {
        // When
        mvc.setupUi();
        // Then
        assertNotNull(mvc.titledPaneLine);
        assertNotNull(mvc.titledPaneModel);

        assertTrue("The content of the TitledPaneLine is not an instance of HBox", mvc.titledPaneLine.getContent() instanceof HBox);
        HBox hboxTitledPaneLine = (HBox) mvc.titledPaneLine.getContent();

        assertEquals(2, hboxTitledPaneLine.getChildren().size());
        assertTrue("The content of the TitledPaneLine is not an instance of Label", hboxTitledPaneLine.getChildren().get(0) instanceof Label);
        assertEquals("Selecione uma linha", ((Label) hboxTitledPaneLine.getChildren().get(0)).getText());
        assertTrue("The content of the TitledPaneModel is not an instance of ComboBox", hboxTitledPaneLine.getChildren().get(1) instanceof ComboBox);

        assertTrue("The content of the TitledPaneModel is not an instance of HBox", mvc.titledPaneModel.getContent() instanceof HBox);
        HBox hboxTitledPaneModel = (HBox) mvc.titledPaneModel.getContent();

        assertEquals(2, hboxTitledPaneModel.getChildren().size());
        assertEquals("Lista de Modelos", ((Label) hboxTitledPaneModel.getChildren().get(0)).getText());
        assertTrue("The content of the TitledPaneModel is not an instance of TreeView", hboxTitledPaneModel.getChildren().get(1) instanceof TreeView);
    }

    @Test
    public void populateTreeViewTest01() {
        // Given
        mvc.comboBox.getSelectionModel().select("Cronos");
        // When
        mvc.populateTreeView();
        // Then
        assertEquals(1, mvc.root.getChildren().size());
        assertEquals(3, mvc.root.getChildren().get(0).getChildren().size());
        assertEquals("Cronos", mvc.root.getChildren().get(0).getValue());
        assertEquals("Cronos Old", mvc.root.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("Cronos L", mvc.root.getChildren().get(0).getChildren().get(1).getValue());
        assertEquals("Cronos NG", mvc.root.getChildren().get(0).getChildren().get(2).getValue());
    }

    @Test
    public void populateTreeViewTest02() {
        // Given
        mvc.comboBox.getSelectionModel().select("Ares");
        // When
        mvc.populateTreeView();
        // Then
        assertEquals(1, mvc.root.getChildren().size());
        assertEquals(2, mvc.root.getChildren().get(0).getChildren().size());
        assertEquals("Ares", mvc.root.getChildren().get(0).getValue());
        assertEquals("Ares TB", mvc.root.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("Ares THS", mvc.root.getChildren().get(0).getChildren().get(1).getValue());
    }

    @Test
    public void populateTreeViewTest03() {
        // Given
        mvc.comboBox.getSelectionModel().select("Cronos");
        // Then
        mvc.populateTreeView();
        // When
        assertEquals(3, mvc.root.getChildren().get(0).getChildren().get(0).getChildren().size());
        assertEquals("Cronos 6001-A", mvc.root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("Cronos 6003", mvc.root.getChildren().get(0).getChildren().get(0).getChildren().get(1).getValue());
        assertEquals("Cronos 7023", mvc.root.getChildren().get(0).getChildren().get(0).getChildren().get(2).getValue());
    }

    @Test
    public void populateTreeViewTest04() {
        // Given
        mvc.comboBox.getSelectionModel().select("Cronos");
        // When
        mvc.populateTreeView();
        // Then
        assertEquals(3, mvc.root.getChildren().get(0).getChildren().get(1).getChildren().size());
        assertEquals("Cronos L", mvc.root.getChildren().get(0).getChildren().get(1).getValue());
        assertEquals("Cronos 6021", mvc.root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getValue());
        assertEquals("Cronos 6021L", mvc.root.getChildren().get(0).getChildren().get(1).getChildren().get(1).getValue());
        assertEquals("Cronos 7023L", mvc.root.getChildren().get(0).getChildren().get(1).getChildren().get(2).getValue());
    }

    @Test
    public void populateTreeViewTest05() {
        // Given
        mvc.comboBox.getSelectionModel().select("Cronos");
        // When
        mvc.populateTreeView();
        // Then
        assertEquals(6, mvc.root.getChildren().get(0).getChildren().get(2).getChildren().size());
        assertEquals("Cronos NG", mvc.root.getChildren().get(0).getChildren().get(2).getValue());
        assertEquals("Cronos 6001-NG", mvc.root.getChildren().get(0).getChildren().get(2).getChildren().get(0).getValue());
        assertEquals("Cronos 6003-NG", mvc.root.getChildren().get(0).getChildren().get(2).getChildren().get(1).getValue());
        assertEquals("Cronos 6021-NG", mvc.root.getChildren().get(0).getChildren().get(2).getChildren().get(2).getValue());
        assertEquals("Cronos 6031-NG", mvc.root.getChildren().get(0).getChildren().get(2).getChildren().get(3).getValue());
        assertEquals("Cronos 7021-NG", mvc.root.getChildren().get(0).getChildren().get(2).getChildren().get(4).getValue());
        assertEquals("Cronos 7023-NG", mvc.root.getChildren().get(0).getChildren().get(2).getChildren().get(5).getValue());
    }

    @Test
    public void populateTreeViewTest06() {
        // Given
        mvc.comboBox.getSelectionModel().select("Ares");
        // Then
        mvc.populateTreeView();
        // When
        assertEquals(3, mvc.root.getChildren().get(0).getChildren().get(0).getChildren().size());
        assertEquals("Ares TB", mvc.root.getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("ARES 7021", mvc.root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getValue());
        assertEquals("ARES 7023", mvc.root.getChildren().get(0).getChildren().get(0).getChildren().get(1).getValue());
        assertEquals("ARES 7031", mvc.root.getChildren().get(0).getChildren().get(0).getChildren().get(2).getValue());
    }

    @Test
    public void populateTreeViewTest07() {
        // Given
        mvc.comboBox.getSelectionModel().select("Ares");
        // Then
        mvc.populateTreeView();
        // When
        assertEquals(3, mvc.root.getChildren().get(0).getChildren().get(1).getChildren().size());
        assertEquals("Ares THS", mvc.root.getChildren().get(0).getChildren().get(1).getValue());
        assertEquals("ARES 8023 15", mvc.root.getChildren().get(0).getChildren().get(1).getChildren().get(0).getValue());
        assertEquals("ARES 8023 200", mvc.root.getChildren().get(0).getChildren().get(1).getChildren().get(1).getValue());
        assertEquals("ARES 8023 2,5", mvc.root.getChildren().get(0).getChildren().get(1).getChildren().get(2).getValue());
    }

    @Test
    public void makeBranchTest01() {
        // Given
        TreeItem<String> parent = new TreeItem<>("Parent");
        // When
        TreeItem<String> newBranch = mvc.makeBranch("New Branch", parent);
        // Then
        assertNotNull(newBranch);
        assertEquals("New Branch", newBranch.getValue());
        assertTrue(newBranch.isExpanded());
        assertTrue("Teste 11",parent.getChildren().contains(newBranch));
        assertEquals(1, parent.getChildren().size());
    }

    private List<Line> mockLines() {
        List<Line> lines = new ArrayList<>();
        lines.add(new Line(1, "Cronos"));
        lines.add(new Line(2, "Ares"));
        return lines;
    }

    private List<Category> mockCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Cronos Old", mockLines().get(0)));
        categories.add(new Category(2, "Cronos L", mockLines().get(0)));
        categories.add(new Category(3, "Cronos NG", mockLines().get(0)));
        categories.add(new Category(4, "Ares TB", mockLines().get(1)));
        categories.add(new Category(5, "Ares THS", mockLines().get(1)));
        return categories;
    }

    private List<Model> mockModel() {
        List<Model> models = new ArrayList<>();
        models.add(new Model(1, "Cronos 6001-A", mockCategories().get(0)));
        models.add(new Model(2, "Cronos 6003", mockCategories().get(0)));
        models.add(new Model(3, "Cronos 7023", mockCategories().get(0)));
        models.add(new Model(4, "Cronos 6021", mockCategories().get(1)));
        models.add(new Model(5, "Cronos 6021L", mockCategories().get(1)));
        models.add(new Model(6, "Cronos 7023L", mockCategories().get(1)));
        models.add(new Model(7, "Cronos 6001-NG", mockCategories().get(2)));
        models.add(new Model(8, "Cronos 6003-NG", mockCategories().get(2)));
        models.add(new Model(9, "Cronos 6021-NG", mockCategories().get(2)));
        models.add(new Model(10, "Cronos 6031-NG", mockCategories().get(2)));
        models.add(new Model(11, "Cronos 7021-NG", mockCategories().get(2)));
        models.add(new Model(12, "Cronos 7023-NG", mockCategories().get(2)));
        models.add(new Model(13, "ARES 7021", mockCategories().get(3)));
        models.add(new Model(14, "ARES 7023", mockCategories().get(3)));
        models.add(new Model(15, "ARES 7031", mockCategories().get(3)));
        models.add(new Model(16, "ARES 8023 15", mockCategories().get(4)));
        models.add(new Model(17, "ARES 8023 200", mockCategories().get(4)));
        models.add(new Model(18, "ARES 8023 2,5", mockCategories().get(4)));
        return models;
    }

}
