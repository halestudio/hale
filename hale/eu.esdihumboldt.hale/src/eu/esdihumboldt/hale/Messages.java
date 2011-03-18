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

package eu.esdihumboldt.hale;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 * @author Michel Kraemer
 */
public class Messages {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public static String FeatureTypeList_LabelFilter;

	public static String FeatureTypeList_LabelFeature;

	public static String FeatureTypeSelection_LabelNamespace;

	public static String FeatureTypeSelection_LabelFeature;

	public static String FunctionWizard_SelectWizard;

	public static String FunctionWizard_FunctionWizard;

	public static String FunctionWizard_WindowTitle;

	public static String GetCapabilititiesRetriever_Retriever;

	public static String ImportDataStatusText;

	public static String InstanceDataImportWizard_MainPageLabel;

	public static String ImportGeodataText;

	public static String InstanceDataImportWizardMainPage_LoadGeoDescription1;

	public static String InstanceDataImportWizardMainPage_LoadGeoDescription2;

	public static String InstanceDataImportWizardMainPage_ReadGeodata;

	public static String InstanceDataImportWizardMainPage_File;

	public static String MappingExportWizard_ExportMenu1;

	public static String MappingExportWizard_WindowTitle;

	public static String MappingExportWizard_ExportMenu2;

	public static String MappingExportWizard_SaveFailed;

	public static String MappingExportWizardMainPage_MappingExportDescription;

	public static String MappingExportWizardMainPage_SelectionAreaText;

	public static String MappingExportWizardMainPage_File;

	public static String MappingImportWizard_ImportMappingTitle;

	public static String MappingImportWizard_ImportMappingDescription;

	public static String MappingImportWizard_WindowTitle;

	public static String MappingImportWizard_FileExistTitle;

	public static String MappingImportWizard_FileExistDescription;

	public static String MappingImportWizard_SourceExistTitle;

	public static String MappingImportWizard_SourceExistDescription;

	public static String MappingImportWizard_TargetExistTitle;

	public static String MappingImportWizard_TargetExistDescription;

	public static String MappingImportWizardMainPage_ImportDescription;

	public static String MappingImportWizardMainPage_FileSelect;

	public static String MappingImportWizardMainPage_FileSelectTitle;

	public static String MappingImportWizardMainPage_FileSelectDescription;

	public static String OGCFilterText_CreateFilter;

	public static String OpenAlignmentProjectWizard_OpenAlignmentProjectTitle;

	public static String OpenAlignmentProjectWizard_WindowTitle;

	public static String OpenAlignmentProjectWizard_OpenAlignmentProjectDescription;

	public static String OpenAlignmentProjectWizard_Failed;

	public static String OpenAlignmentProjectWizard_Failed2;

	public static String OpenAlignmentProjectWizardMainPage_SuperWindowDescription;

	public static String OpenAlignmentProjectWizardMainPage_SelectProjectText;

	public static String OpenAlignmentProjectWizardMainPage_FileSelectTitle;

	public static String OpenAlignmentProjectWizardMainPage_File;

	public static String SaveAlignmentProjectWizard_SaveAlignmentTitle;

	public static String SaveAlignmentProjectWizard_WindowTitle;

	public static String SaveAlignmentProjectWizard_SaveAlignmentDescription;

	public static String SaveAlignmentProjectWizard_SaveFaild;

	public static String SaveAlignmentProjectWizardMainPage_ProjectNameText;

	public static String SaveAlignmentProjectWizardMainPage_Description;

	public static String SaveAlignmentProjectWizardMainPage_FileSelectTitle;

	public static String SaveAlignmentProjectWizardMainPage_File;

	public static String SaveAlignmentProjectWizardMainPage_ProjectSavingText;

	public static String SchemaImportWizard_ImportSchemaTitle;

	public static String SchemaImportWizard_ImportSchemaDescription;

	public static String SchemaImportWizard_WindowTitle;

	public static String SchemaImportWizard_SchemaImport;

	public static String SchemaImportWizard_ErrorMessage1;

	public static String SchemaImportWizard_JobError;

	public static String SchemaImportWizardMainPage_SchemaImportDescription;

	public static String SchemaImportWizardMainPage_SchemaImportDescription2;

	public static String SchemaImportWizardMainPage_SchemaImportReadSchema;

	public static String SchemaImportWizardMainPage_FileSelect;

	public static String SchemaImportWizardMainPage_File;

	public static String SchemaImportWizardMainPage_ImportDestination;

	public static String SchemaImportWizardMainPage_ImportSource;

	public static String SchemaImportWizardMainPage_ImportTarget;

	public static String WFSDataReaderDialog_UrlDefinitionText;

	public static String WFSDataReaderDialog_HostPortLabel;

	public static String WFSDataReaderDialog_HostPortToolTipText;

	public static String WFSDataReaderDialog_HostPortToolTipText2;

	public static String WFSDataReaderDialog_URLValidationText;

	public static String WFSDataReaderDialog_TestURLText;

	public static String WFSDataReaderDialog_CurrentStatusText;

	public static String WFSDataReaderDialog_FilterText;

	public static String WFSDataReaderDialog_FilterTitle;

	public static String WFSDataReaderDialog_FilterDescription;

	public static String WFSDataReaderDialog_CancelText;

	public static String WFSDataReaderDialog_ValidationFailedText;

	public static String WFSDataReaderDialog_ValidationOKText;

	public static String WFSDataReaderDialog_FeatureTypesText;

	public static String WFSDataReaderDialog_ValidationFailedText2;

	public static String WFSFeatureTypesReaderDialog_UrlEnterText;

	public static String WFSFeatureTypesReaderDialog_HostPortLabelText;

	public static String WFSFeatureTypesReaderDialog_HostPortTooltip1;

	public static String WFSFeatureTypesReaderDialog_HostPortTooltip2;

	public static String WFSFeatureTypesReaderDialog_ValidateText;

	public static String WFSFeatureTypesReaderDialog_TestUrlText;

	public static String WFSFeatureTypesReaderDialog_StatusText;

	public static String WFSFeatureTypesReaderDialog_CancelText;

	public static String WFSFeatureTypesReaderDialog_ValidationFailedText;

	public static String AbstractTilePainter_ZoomIn;
	public static String AbstractTilePainter_ZoomOut;
	public static String FeatureStyleDialog_ApplyButtonText;
	public static String FeatureStyleDialog_ErrorMessageDescription;
	public static String FeatureStyleDialog_ErrorMessageTitle;
	public static String FeatureStyleDialog_SwitchStyleDescription;
	public static String FeatureStyleDialog_SwitchStyleDescription2;
	public static String FeatureStyleDialog_SwitchStyleTitle;
	public static String FeatureStyleDialog_SwitchStyleTitle2;
	public static String FeatureStyleDialog_Title;
	public static String FillEditor_ColorLabelText;
	public static String FillEditor_OpacityText;
	public static String LoadStylesAction_SuperTitle;
	public static String MapView_ChangeBackgroundText;
	public static String MarkEditor_FillLabel;
	public static String MarkEditor_MarkLabel;
	public static String MarkEditor_StrokeLabel;
	public static String PolygonSymbolizerEditor_FillLabel;
	public static String PolygonSymbolizerEditor_StrokeLabel;
	public static String RuleEditor_ExceptionText;
	public static String RuleEditor_FilterLabel;
	public static String RuleStylePage_AddRuleButtonToolTippText;
	public static String RuleStylePage_DownRuleButtonToolTippText;
	public static String RuleStylePage_InputDialogDescription;
	public static String RuleStylePage_InputDialogTitle;
	public static String RuleStylePage_RemoveRuleButtonToolTippText;
	public static String RuleStylePage_RenameRuleButtonToolTippText;
	public static String RuleStylePage_Rule;
	public static String RuleStylePage_RuleLabelText;
	public static String RuleStylePage_SuperTitle;
	public static String RuleStylePage_UpRuleButtonToolTippText;
	public static String SaveStylesAction_SuperTitle;
	public static String SelectCRSDialog_ContentTitle;
	public static String SelectCRSDialog_GroupText;
	public static String SelectCRSDialog_RadioCRSText;
	public static String SelectCRSDialog_RadioWKTText;
	public static String SelectCRSDialog_ShellTitle;
	public static String SimpleLineStylePage_SuperTitle;
	public static String SimplePointStylePage_SuperTitle;
	public static String SimplePolygonStylePage_SuperTitle;
	public static String SplitStyle_ComboBoxText1;
	public static String SplitStyle_ComboBoxText2;
	public static String SplitStyle_ComboBoxText3;
	public static String SplitStyle_ComboBoxText4;
	public static String SplitStyle_ComboBoxText5;
	public static String SplitStyle_ComboBoxText6;
	public static String SplitStyle_ComboBoxText7;
	public static String StrokeEditor_ColorLabel;
	public static String StrokeEditor_OpacityLabel;
	public static String StrokeEditor_WidthLabel;
	public static String StyleDropdown_SuperTitle;
	public static String SymbolizerDialog_LabelText;
	public static String SymbolizerDialog_ShellSymbolizerText;
	public static String TileCache_JobLoadTitle;
	public static String XMLStylePage3_SuperTitle;
	public static String XMLStylePage4_SuperTitle;
	
	public static String CellDetails_AugmentationTitle;
	public static String CellDetails_Entity1Title;
	public static String CellDetails_Entity2Title;
	public static String CellDetails_FilterTitle;
	public static String CellDetails_NameText;
	public static String CellDetails_TransformationTitle;
	public static String CellDetails_ValueText;
	public static String CellSelector_ConfirmCellText;
	public static String CellSelector_ConfirmCellTitle;
	public static String CellSelector_DeleteButtonToolTipText;
	public static String CellSelector_EditButtonToolTipText;
	public static String CellSelector_NextButtonToolTipText;
	public static String CellSelector_PrevButtonToolTipText;
	public static String CellSelector_SynchButtonToolTipText;
	
	public static String ModelNavigationView_ActionText;
	public static String ModelNavigationView_FunctionButtonToolTipText;
	public static String ModelNavigationView_GeometryHide;
	public static String ModelNavigationView_GeometryShow;
	public static String ModelNavigationView_NumericHide;
	public static String ModelNavigationView_NumericShow;
	public static String ModelNavigationView_PropertyHide;
	public static String ModelNavigationView_PropertyShow;
	public static String ModelNavigationView_StringHide;
	public static String ModelNavigationView_StringShow;
	public static String PropertiesAction_PropertiesText;
	public static String PropertiesDialog_col12Text;
	public static String PropertiesDialog_col1Text;
	public static String PropertiesDialog_ShellTitle;
	public static String PropertiesDialog_Title;
	public static String PropertiesDialog_TreeNodeIndentifier;
	public static String PropertiesDialog_TreeNodeTitleAttributes;
	public static String PropertiesDialog_TreeNodeTitleBinding;
	public static String PropertiesDialog_TreeNodeTitleCardinality;
	public static String PropertiesDialog_TreeNodeTitleEnumeration;
	public static String PropertiesDialog_TreeNodeTitleLocalpart;
	public static String PropertiesDialog_TreeNodeTitleName;
	public static String PropertiesDialog_TreeNodeTitleNamespace;
	public static String PropertiesDialog_TreeNodeTitleNillable;
	public static String PropertiesDialog_TreeNodeTitleType;
	public static String UseAggregationHierarchyAction_PropertyAggregationToolTipText;
	public static String UseFlatHierarchyAction_OrganizeTooltipText;
	public static String UseInheritanceHierarchyAction_ShowInheritedTooltipText;
	
	public static String InstanceServiceFeatureSelector_defaultReturnText;
	public static String InstanceServiceFeatureSelector_SourceReturnText;
	public static String InstanceServiceFeatureSelector_TargetReturnText;
	public static String TransformedTableView_SynchToolTipText;
	
	public static String TaskTreeView_CommentText;
	public static String TaskTreeView_description_tooltip;
	public static String TaskTreeView_NumberText;
	public static String TaskTreeView_SourceNodeTitle;
	public static String TaskTreeView_StatusText;
	public static String TaskTreeView_TargetNodeTitle;
	public static String TaskTreeView_TitleDescriptionText;
	public static String TaskTreeView_value_tooltip;

	public static String SchemaImportWizard_SchemaQuestion0;
	public static String SchemaImportWizard_SchemaQuestion1;

	public static String WFSFeatureTypesReaderDialog_ValdationOKText;

	public static String SaveAlignmentProjectWizardMainPage_LocationText;

	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	/**
	 * Hidden constructor
	 */
	private Messages() {
		//nothing to do here
	}

	/**
	 * Get externalized string
	 * @param key the string's id
	 * @return the externalized string
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
