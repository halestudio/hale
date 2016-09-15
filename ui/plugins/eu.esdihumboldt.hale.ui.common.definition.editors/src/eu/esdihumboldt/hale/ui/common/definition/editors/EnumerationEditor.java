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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.common.editors.AbstractAttributeEditor;

/**
 * Editor for function parameters with an enumeration.
 * 
 * @author Kai Schwierczek
 */
public class EnumerationEditor extends AbstractAttributeEditor<String> {

	private final Collection<String> enumerationValues;
	private final ComboViewer viewer;

	/**
	 * Creates an editor that only allows values from the given enumeration.
	 * 
	 * @param parent the parent composite
	 * @param enumerationValues the enumeration values
	 */
	public EnumerationEditor(Composite parent, Collection<String> enumerationValues) {
		this.enumerationValues = enumerationValues;
		viewer = new ComboViewer(parent, SWT.READ_ONLY | SWT.BORDER);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(enumerationValues);
		viewer.setSelection(new StructuredSelection(enumerationValues.iterator().next()));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return viewer.getControl();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String value) {
		setAsText(value);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValue()
	 */
	@Override
	public String getValue() {
		return getAsText();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		if (enumerationValues.contains(text)) {
			viewer.setSelection(new StructuredSelection(text));
		}
		// else simply ignore
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getAsText()
	 */
	@Override
	public String getAsText() {
		// auto select the first value, so selection can never be empty.
//		if (isValid())
		return ((IStructuredSelection) viewer.getSelection()).getFirstElement().toString();
//		else
//			throw new IllegalStateException("no value selected");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		// auto select the first value, so selection can never be empty.
		return true;
//		return !viewer.getSelection().isEmpty();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValueType()
	 */
	@Override
	public String getValueType() {
		return ParameterValue.DEFAULT_TYPE;
	}
}
