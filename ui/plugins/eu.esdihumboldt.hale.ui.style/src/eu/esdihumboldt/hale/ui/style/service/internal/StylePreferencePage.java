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

package eu.esdihumboldt.hale.ui.style.service.internal;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;
import eu.esdihumboldt.hale.ui.style.internal.Messages;

/**
 * Style preference page
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StylePreferencePage extends FieldEditorPreferencePage implements
		StylePreferenceConstants, IWorkbenchPreferencePage {

	/**
	 * Default constructor
	 */
	public StylePreferencePage() {
		super(GRID);

		setPreferenceStore(InstanceStylePlugin.getDefault().getPreferenceStore());
		setDescription(Messages.StylePreferencePage_0); //$NON-NLS-1$
	}

	/**
	 * @see FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		addField(new ColorFieldEditor(KEY_DEFAULT_BACKGROUND, Messages.StylePreferencePage_1,
				getFieldEditorParent())); //$NON-NLS-1$

		addField(new ColorFieldEditor(KEY_SOURCE_DEFAULT_COLOR, "Default color for source data",
				getFieldEditorParent()));
		addField(new ColorFieldEditor(KEY_TRANSFORMED_DEFAULT_COLOR,
				"Default color for transformed data", getFieldEditorParent()));

		addField(new IntegerFieldEditor(KEY_DEFAULT_WIDTH, Messages.StylePreferencePage_3,
				getFieldEditorParent())); //$NON-NLS-1$

		addField(new ColorFieldEditor(KEY_SELECTION_COLOR, Messages.StylePreferencePage_4,
				getFieldEditorParent())); //$NON-NLS-1$
		addField(new IntegerFieldEditor(KEY_SELECTION_WIDTH, Messages.StylePreferencePage_5,
				getFieldEditorParent())); //$NON-NLS-1$
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

		return true;
	}

	/**
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// do nothing
	}

}
