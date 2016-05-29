/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.custom.pages.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.editors.AbstractEditor;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;

/**
 * TODO Type description
 * 
 * @author simon
 */
public class BindingOrTypeEditor extends AbstractEditor<BindingOrType> {

	private ComboViewer bindingSelect;

	private static enum PossibleBindings {
		NONE(null, "Schema type"), STRING(String.class, "String"), NUMBER(Number.class,
				"Number"), BOOLEAN(boolean.class, "Boolean");

		PossibleBindings(Class<?> binding, String name) {
			this.binding = binding;
			this.name = name;
		}

		public final Class<?> binding;
		public final String name;
	}

	private PropertyTypeSelector typeSelect;
	private Control mainControl;

	@SuppressWarnings("javadoc")
	public BindingOrTypeEditor(Composite parent, SchemaSpaceID ssid) {
		super();
//		GridLayoutFactory.fillDefaults().applyTo(page);
		Group group = new Group(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(group);

		GridDataFactory fieldData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false);

		// binding
		bindingSelect = new ComboViewer(group);
		fieldData.applyTo(bindingSelect.getControl());
		bindingSelect.setContentProvider(EnumContentProvider.getInstance());
		bindingSelect.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof PossibleBindings) {
					return ((PossibleBindings) element).name;
				}
				return super.getText(element);
			}

		});
		bindingSelect.setInput(PossibleBindings.class);
		bindingSelect.setSelection(new StructuredSelection(PossibleBindings.NONE));

		// type (through property selection)
		typeSelect = new PropertyTypeSelector(group, "Select a property type", ssid, null);
		fieldData.applyTo(typeSelect.getControl());

		// main control
		mainControl = group;
	}

	@Override
	public void setValue(BindingOrType value) {
		if (value.isUseBinding()) {
			bindingSelect.setSelection(
					new StructuredSelection(possibleBindingForClass(value.getBinding())));
		}
		else {
			bindingSelect.setSelection(new StructuredSelection(PossibleBindings.NONE));
		}
		if (value.getType() != null) {
			typeSelect.setSelection(new StructuredSelection(value.getType()));
		}
		else {
			typeSelect.setSelection(StructuredSelection.EMPTY);
		}
	}

	private PossibleBindings possibleBindingForClass(Class<?> binding) {
		if (binding == null) {
			return PossibleBindings.NONE;
		}
		if (Number.class.isAssignableFrom(binding)) {
			return PossibleBindings.NUMBER;
		}
		if (Boolean.class.isAssignableFrom(binding)) {
			return PossibleBindings.BOOLEAN;
		}
		return PossibleBindings.STRING;
	}

	@Override
	public BindingOrType getValue() {
		BindingOrType result = new BindingOrType();

		result.setType(typeSelect.getSelectedObject());

		ISelection bs = bindingSelect.getSelection();
		PossibleBindings binding = PossibleBindings.NONE;
		if (bs instanceof IStructuredSelection && !bs.isEmpty()) {
			binding = (PossibleBindings) ((IStructuredSelection) bs).getFirstElement();
		}
		result.setBinding(binding.binding);
		result.setUseBinding(binding.binding != null);

		return result;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Control getControl() {
		return mainControl;
	}

}
