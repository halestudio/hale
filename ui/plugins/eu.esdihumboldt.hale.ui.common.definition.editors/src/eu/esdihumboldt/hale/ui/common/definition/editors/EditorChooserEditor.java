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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.definition.editors;

import java.util.Collection;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.editors.AbstractEditor;

/**
 * Editor that provides a drop down to select from a list of available editors.
 * 
 * @author Kai Schwierczek
 * @param <T> the attribute value type/binding
 */
public class EditorChooserEditor<T> extends AbstractEditor<T> {

	// TODO new property specifying which editor is selected
	private final Composite composite;

	/**
	 * Constructs the editor chooser.
	 * 
	 * @param parent the parent composite
	 */
	public EditorChooserEditor(Composite parent) {
		composite = new Composite(parent, SWT.NONE);

		ComboViewer comboViewer = new ComboViewer(composite, SWT.BORDER | SWT.READ_ONLY);
		// TODO add choices, select default
		Collection<Editor<T>> availableEditors;

		// for binding class Boolean add BooleanEditor

		// otherwise add Default*Editor

		// add other matching editors
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return composite;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(T value) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getValue()
	 */
	@Override
	public T getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getAsText()
	 */
	@Override
	public String getAsText() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#isValid()
	 */
	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
