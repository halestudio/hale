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

package eu.esdihumboldt.hale.models.style;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.map.MapView;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
/**
 * Style preference page
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StylePreferencePage extends FieldEditorPreferencePage
	implements StylePreferenceConstants, IWorkbenchPreferencePage {

	/**
	 * Default constructor
	 */
	public StylePreferencePage() {
		super(GRID);
		
		setPreferenceStore(HALEActivator.getDefault().getPreferenceStore());
		setDescription(Messages.StylePreferencePage_0);  //$NON-NLS-1$
	}

	/**
	 * @see FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		addField(new ColorFieldEditor(KEY_DEFAULT_BACKGROUND, Messages.StylePreferencePage_1, getFieldEditorParent())); //$NON-NLS-1$
		
		addField(new ColorFieldEditor(KEY_DEFAULT_COLOR, Messages.StylePreferencePage_2, getFieldEditorParent())); //$NON-NLS-1$
		addField(new IntegerFieldEditor(KEY_DEFAULT_WIDTH, Messages.StylePreferencePage_3, getFieldEditorParent())); //$NON-NLS-1$
		
		addField(new ColorFieldEditor(KEY_SELECTION_COLOR, Messages.StylePreferencePage_4, getFieldEditorParent())); //$NON-NLS-1$
		addField(new IntegerFieldEditor(KEY_SELECTION_WIDTH, Messages.StylePreferencePage_5, getFieldEditorParent())); //$NON-NLS-1$
	}

	/**
	 * @see FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		if (!result) {
			return false;
		}
		
		// map update (e.g for selection style change)
		MapView map = (MapView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MapView.ID);
		if (map != null) {
			map.getPainter().updateMap(); //updateSelection();
		}
		// refresh schema explorer (for legend images change)
		ModelNavigationView model = (ModelNavigationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelNavigationView.ID);
		if (model != null) {
			model.refresh();
		}
		
		return true;
	}

	/**
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// do nothing
	}
	
}
