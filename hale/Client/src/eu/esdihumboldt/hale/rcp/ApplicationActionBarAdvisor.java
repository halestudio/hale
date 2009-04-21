/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import eu.esdihumboldt.hale.rcp.actions.ChangeSLDAction;
import eu.esdihumboldt.hale.rcp.actions.EditSLDAction;
import eu.esdihumboldt.hale.rcp.actions.LoadAlignmentsAction;
import eu.esdihumboldt.hale.rcp.actions.LoadGMLFromFileAction;
import eu.esdihumboldt.hale.rcp.actions.LoadGeodataFromWFSAction;
import eu.esdihumboldt.hale.rcp.actions.LoadReferenceGMLAction;
import eu.esdihumboldt.hale.rcp.actions.LoadShapefileAction;
import eu.esdihumboldt.hale.rcp.actions.LoadTargetAppSchemaAction;
import eu.esdihumboldt.hale.rcp.actions.LoadTargetAppSchemaFromRepAction;
import eu.esdihumboldt.hale.rcp.actions.LoadTransformationRuleLocalAction;
import eu.esdihumboldt.hale.rcp.actions.PreferencesAction;
import eu.esdihumboldt.hale.rcp.actions.StartAutoAlignmentAction;
import eu.esdihumboldt.hale.rcp.actions.StartSourceSchemaExtractionAction;
import eu.esdihumboldt.hale.rcp.actions.StoreAlignmentsLocalAction;
import eu.esdihumboldt.hale.rcp.actions.StoreMappingInModelRepAction;
import eu.esdihumboldt.hale.rcp.actions.StoreTransformationRuleLocalAction;



/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private LoadShapefileAction loadShapefileAction;
	private LoadGMLFromFileAction loadGMLFromFileAction;
	private LoadGeodataFromWFSAction loadDataFromWFSAction;
	private LoadTargetAppSchemaAction loadTargetAppSchemaAction;
	private LoadTargetAppSchemaFromRepAction loadTargetAppSchemaFromRepAction;
	private LoadReferenceGMLAction loadReferenceGMLAction;
	private IWorkbenchAction exitAction;
	
	private LoadAlignmentsAction loadAlignmentsAction;
	private StoreAlignmentsLocalAction storeAlignmentsLocalAction;
	private LoadTransformationRuleLocalAction loadTransformationRuleLocalAction;
	private StoreTransformationRuleLocalAction storeTransformationRuleLocalAction;
	private StoreMappingInModelRepAction storeMappingInModelRepAction;
	private StartAutoAlignmentAction startAutoAlignmentAction;
	private StartSourceSchemaExtractionAction startSourceSchemaExtractionAction;
	
	private ChangeSLDAction changeSLDAAction;
	private ChangeSLDAction changeSLDBAction;
	private EditSLDAction editSLDAction;
	private PreferencesAction preferencesAction;
	private IWorkbenchAction aboutAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}
	
	
	@Override
	protected void makeActions(final IWorkbenchWindow _window) {
		
		makeImportExportActions(_window);
		
		makeExtractionAlignmentActions(_window);
        
        makeOtherActions(_window);
	}
	
	
	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		/*MenuManager importExportMenu = new MenuManager("Import/Export");
		MenuManager extractionMenu = new MenuManager("Extraction/Alignment");
		MenuManager othersMenu = new MenuManager("Other");
		
		MenuManager loadUserdataMenu = new MenuManager("Load User Data");
		MenuManager loadTargetdataMenu = new MenuManager("Target Data");
		MenuManager alignmentMenu = new MenuManager("Alignments");
		MenuManager transRulesMenu = new MenuManager("Transformation Rules");
		
		menuBar.add(importExportMenu);
		menuBar.add(extractionMenu);
		menuBar.add(othersMenu);
		
		importExportMenu.add(loadUserdataMenu);
		loadUserdataMenu.add(loadShapefileAction);
		loadUserdataMenu.add(loadGMLFromFileAction);
		loadUserdataMenu.add(loadDataFromWFSAction);
		
		importExportMenu.add(loadTargetdataMenu);
		loadTargetdataMenu.add(loadTargetAppSchemaAction);
		loadTargetdataMenu.add(loadTargetAppSchemaFromRepAction);
		loadTargetdataMenu.add(loadReferenceGMLAction);
		
		importExportMenu.add(new Separator());
		
		importExportMenu.add(exitAction);
		
		extractionMenu.add(alignmentMenu);
		alignmentMenu.add(loadAlignmentsAction);
		alignmentMenu.add(storeAlignmentsLocalAction);
		
		extractionMenu.add(transRulesMenu);
		transRulesMenu.add(loadTransformationRuleLocalAction);
		transRulesMenu.add(storeTransformationRuleLocalAction);
		
		
		extractionMenu.add(storeMappingInModelRepAction);
		extractionMenu.add(new Separator());
		extractionMenu.add(startAutoAlignmentAction);
		extractionMenu.add(startSourceSchemaExtractionAction);
		
		othersMenu.add(changeSLDAAction);
		othersMenu.add(changeSLDBAction);
		othersMenu.add(editSLDAction);
		othersMenu.add(new Separator());
		othersMenu.add(preferencesAction);
		othersMenu.add(new Separator());
		othersMenu.add(aboutAction); */
	}
	
	
	/**
	 * Inits and registers actions for importing data.
	 * @param _window
	 */
	protected void makeImportExportActions(final IWorkbenchWindow _window){
		/*loadShapefileAction = new LoadShapefileAction("from Shapefile", _window);
		register(loadShapefileAction);
		
		loadGMLFromFileAction = new LoadGMLFromFileAction("from GML File", _window);
		register(loadGMLFromFileAction);
		
		loadDataFromWFSAction = new LoadGeodataFromWFSAction("from WFS", _window);
		register(loadDataFromWFSAction);
		
		loadTargetAppSchemaAction = new LoadTargetAppSchemaAction("Load Schema",_window);
		register(loadTargetAppSchemaAction);
		
		loadTargetAppSchemaFromRepAction = new LoadTargetAppSchemaFromRepAction("Load Schema from Model Repository",_window);
		register(loadTargetAppSchemaFromRepAction);
		
		loadReferenceGMLAction = new LoadReferenceGMLAction("Load reference data as GML",_window);
		register(loadReferenceGMLAction);
		
		exitAction = ActionFactory.QUIT.create(_window);
        register(exitAction);*/
	}
	
	
	/**
	 * Inits and registers actions for storing data.
	 * @param _window
	 */
	protected void makeExtractionAlignmentActions(final IWorkbenchWindow _window){
		/*
		loadAlignmentsAction = new LoadAlignmentsAction("Load Alignment",_window);
		register(loadAlignmentsAction);
		
		storeAlignmentsLocalAction = new StoreAlignmentsLocalAction("Store alignment local",_window);
		register(storeAlignmentsLocalAction);
		
		loadTransformationRuleLocalAction = new LoadTransformationRuleLocalAction("Load Transformation Rule",_window);
		register(loadTransformationRuleLocalAction);
		
		storeTransformationRuleLocalAction = new StoreTransformationRuleLocalAction("Store Transformation Rule", _window);
		register(storeTransformationRuleLocalAction);
		
		storeMappingInModelRepAction = new StoreMappingInModelRepAction("Store Mapping in Model Repository",_window);
		register(storeMappingInModelRepAction);
		
		startAutoAlignmentAction = new StartAutoAlignmentAction("Start automatic alignment process",_window);
		register(startAutoAlignmentAction);
		
		startSourceSchemaExtractionAction = new StartSourceSchemaExtractionAction("Start Source Schema Extraction",_window);
		register(startSourceSchemaExtractionAction);*/
	}
	
	
	/**
	 * Inits and registers actions for Other.
	 * @param _window
	 */
	protected void makeOtherActions(final IWorkbenchWindow _window){
		/*
		changeSLDAAction = new ChangeSLDAction(_window,"Change SLD of User Data Map",1);
        register(changeSLDAAction);
        
        changeSLDBAction = new ChangeSLDAction(_window,"Change SLD of Target Data Map",2);
        register(changeSLDBAction);
        
        editSLDAction = new EditSLDAction("Edit a SLD",_window);
        register(editSLDAction);
        
        preferencesAction = new PreferencesAction("Preferencens...",_window);
        register(preferencesAction);
        
        aboutAction = ActionFactory.ABOUT.create(_window);
        register(aboutAction);*/
	}
}