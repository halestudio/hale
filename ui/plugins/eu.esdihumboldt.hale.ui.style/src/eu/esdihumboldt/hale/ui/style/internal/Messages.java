/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
