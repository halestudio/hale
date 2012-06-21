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

package eu.esdihumboldt.hale.ui.style.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 */
@SuppressWarnings("all")
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.ui.style.internal.messages"; //$NON-NLS-1$

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
	public static String MarkEditor_FillLabel;
	public static String MarkEditor_MarkLabel;
	public static String MarkEditor_StrokeLabel;
	public static String PolygonSymbolizerEditor_FillLabel;
	public static String PolygonSymbolizerEditor_StrokeLabel;
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
	public static String PointGraphicEditor_UrlTextField;
	public static String PointGraphicEditor_FileDialogButton;
	public static String PointGraphicEditor_ErrorMessageFile;
	public static String PointGraphicEditor_ErrorMessageFormat;
	public static String PointGraphicEditor_SupportedTypes;
	public static String SaveStylesAction_SuperTitle;
	public static String SimpleLineStylePage_SuperTitle;
	public static String SimplePointStylePage_SuperTitle;
	public static String SimplePolygonStylePage_SuperTitle;
	public static String SimpleGraphicStylePage_SuperTitle;
	public static String StrokeEditor_ColorLabel;
	public static String StrokeEditor_OpacityLabel;
	public static String StrokeEditor_WidthLabel;
	public static String StyleDropdown_SuperTitle;
	public static String SymbolizerDialog_LabelText;
	public static String SymbolizerDialog_ShellSymbolizerText;
	public static String XMLStylePage3_SuperTitle;
	public static String XMLStylePage4_SuperTitle;
	public static String StylePreferencePage_0;
	public static String StylePreferencePage_1;
	public static String StylePreferencePage_2;
	public static String StylePreferencePage_3;
	public static String StylePreferencePage_4;
	public static String StylePreferencePage_5;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Hidden constructor
	 */
	private Messages() {
		// nothing to do here
	}

}
