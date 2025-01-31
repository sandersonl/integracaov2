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

        //
        assertEquals(mvc.root, mvc.treeView.getRoot());

        verify(mvc, times(1)).populateComboBox();
        verify(mvc, times(1)).titlePanedModelDisable();
        verify(mvc, times(1)).setupUi();
    }

    @Test(expected = RuntimeException.class)
    public void initializeTest02() throws Exception{
        // Given
        mvc.apiLineService = PowerMockito.mock(ApiLineService.class);
        mvc.apiCategoryService = PowerMockito.mock(ApiCategoryService.class);
        mvc.apiModelService = PowerMockito.mock(ApiModelService.class);

        doThrow(RuntimeException.class).when(mvc.apiLineService).getLines("lines");
        doThrow(RuntimeException.class).when(mvc.apiCategoryService).getCategories("categories");
        doThrow(RuntimeException.class).when(mvc.apiModelService).getModels("models");

        //When
        try {
            mvc.initialize();
            fail("Expected");
        } catch (RuntimeException e) {
            //Then
            assertEquals(RuntimeException.class, e.getCause().getClass());

            verify(mvc.apiLineService, times(1)).getLines("lines");
            verify(mvc.apiCategoryService, never()).getCategories("categories");
            verify(mvc.apiModelService, never()).getModels("models");

            throw e;
        }
    }

    @Test
    public void populateComboBoxTest01() {
        // Given
        // The linelist is already mocked in the beforeTest.
        // The populateComboBox uses the linelist to populate the fields

        // When
        mvc.populateComboBox();

        // Then
        //Confirm if the values of the combobox items are the same as the values they should have.
        assertEquals("[Cronos, Ares]", mvc.comboBox.getItems().toString());
    }

    @Test
    public void titlePaneModelDisableTest01() {
        // Given
        // The initial state of the application where no item is selected in the first select (TitledPaneLine).

        // When
        mvc.titlePanedModelDisable();

        // Then
        assertNull( "Verify that no item is selected in the combobox",
                mvc.comboBox.getSelectionModel().getSelectedItem());
        assertTrue("TitledPaneModel should be disabled when no value is selected in the combo box.",
                mvc.titledPaneModel.isDisabled());
    }

    @Test
    public void titledPaneModelDisableTest02() {
        // Given
        // Insert an item into the combo box to enable selection
        mvc.comboBox.getItems().add("Cronos");


        // When
        // Simulate selecting an item in the combo box to verify if the function under test changes the state of the TitledPaneModel.
        mvc.comboBox.getSelectionModel().selectFirst();
        mvc.titlePanedModelDisable();

        //Then
        assertFalse("The state should be enabled after an item is selected in the combobox.",
                mvc.titledPaneModel.isDisabled());
        assertTrue("The TitledPaneModel must be expanded after an item is selected in the combo box.",
                mvc.titledPaneModel.isExpanded());
    }

    @Test
    public void setupUiTest01() {
        // When
        mvc.setupUi();
        // Then
        // Get the HBox that contains the content and is inside the TitledPaneLine.
        HBox hboxTitledPaneLine = (HBox)mvc.titledPaneLine.getContent();

        assertEquals("Check if the label's text is as expected.",
                "Selecione uma linha",
                ((Label) hboxTitledPaneLine.getChildren().get(0)).getText());
        assertTrue("Verify that there is an instance of a ComboBox inside the HBox.",
                hboxTitledPaneLine.getChildren().get(1) instanceof ComboBox);

        // Get the HBox that contains the content and is inside the TitledPaneModel.
        HBox hboxTitledPaneModel = (HBox) mvc.titledPaneModel.getContent();

        assertEquals("Check if the label's text is as expected.",
                "Lista de Modelos",
                ((Label) hboxTitledPaneModel.getChildren().get(0)).getText());

        assertTrue("Verify that there is an instance of a TreeView inside the HBox.",
                hboxTitledPaneModel.getChildren().get(1) instanceof TreeView);
    }

    @Test
    public void populateTreeViewTest01() {
        // Given
        // Insert an item and simulate selection to invoke the function under test.
        mvc.comboBox.getSelectionModel().select("Cronos");

        // When
        mvc.populateTreeView();

        // Then
        assertEquals("Verify that the Cronos categories have the expected values.",
                "[TreeItem [ value: Cronos Old ], TreeItem [ value: Cronos L ], TreeItem [ value: Cronos NG ]]",
                mvc.root.getChildren().get(0).getChildren().toString());
    }

    @Test
    public void populateTreeViewTest02() {
        // Given
        mvc.comboBox.getSelectionModel().select("Ares");

        // When
        mvc.populateTreeView();

        // Then
        assertEquals("Verify that the Ares categories have the expected values.",
                "[TreeItem [ value: Ares TB ], TreeItem [ value: Ares THS ]]",
                mvc.root.getChildren().get(0).getChildren().toString());
    }

    @Test
    public void populateTreeViewTest03() {
        // Given
        mvc.comboBox.getSelectionModel().select("Cronos");

        // Then
        mvc.populateTreeView();

        // When
        assertEquals("Verify that the models in the Cronos Old category have the expected values.",
                "[TreeItem [ value: Cronos 6001-A ], TreeItem [ value: Cronos 6003 ], TreeItem [ value: Cronos 7023 ]]",
                mvc.root.getChildren().get(0).getChildren().get(0).getChildren().toString());
    }

    @Test
    public void populateTreeViewTest04() {
        // Given
        mvc.comboBox.getSelectionModel().select("Cronos");

        // When
        mvc.populateTreeView();

        // Then
        assertEquals("Verify that the models in the Cronos L category have the expected values.",
                "[TreeItem [ value: Cronos 6021 ], TreeItem [ value: Cronos 6021L ], TreeItem [ value: Cronos 7023L ]]",
                mvc.root.getChildren().get(0).getChildren().get(1).getChildren().toString());
    }

    @Test
    public void populateTreeViewTest05() {
        // Given
        mvc.comboBox.getSelectionModel().select("Cronos");

        // When
        mvc.populateTreeView();

        // Then
        assertEquals("Verify that the models in the Cronos NG category have the expected values.",
                "[TreeItem [ value: Cronos 6001-NG ], TreeItem [ value: Cronos 6003-NG ], TreeItem [ value: Cronos 6021-NG ], TreeItem [ value: Cronos 6031-NG ], TreeItem [ value: Cronos 7021-NG ], TreeItem [ value: Cronos 7023-NG ]]",
                mvc.root.getChildren().get(0).getChildren().get(2).getChildren().toString());
    }

    @Test
    public void populateTreeViewTest06() {
        // Given
        mvc.comboBox.getSelectionModel().select("Ares");

        // Then
        mvc.populateTreeView();

        // When
        assertEquals("Verify that the models in the Ares TB category have the expected values.",
                "[TreeItem [ value: ARES 7021 ], TreeItem [ value: ARES 7023 ], TreeItem [ value: ARES 7031 ]]",
                mvc.root.getChildren().get(0).getChildren().get(0).getChildren().toString());
    }

    @Test
    public void populateTreeViewTest07() {
        // Given
        mvc.comboBox.getSelectionModel().select("Ares");

        // Then
        mvc.populateTreeView();

        // When
        assertEquals("Verify that the models in the Ares THS category have the expected values.",
                "[TreeItem [ value: ARES 8023 15 ], TreeItem [ value: ARES 8023 200 ], TreeItem [ value: ARES 8023 2,5 ]]",
                mvc.root.getChildren().get(0).getChildren().get(1).getChildren().toString());
    }

    @Test
    public void makeBranchTest01() {
        // Given
        TreeItem<String> parent = new TreeItem<>("Main Branch");

        // When
        TreeItem<String> newBranch = mvc.makeBranch("Secondary Branch", parent);

        // Then
        assertEquals("Check if the new branch has been successfully added to the parent.",
                "[TreeItem [ value: Secondary Branch ]]",
                parent.getChildren().toString());
        assertTrue("Ensure that the branch is expanded when added to a parent.",
                newBranch.isExpanded());
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
