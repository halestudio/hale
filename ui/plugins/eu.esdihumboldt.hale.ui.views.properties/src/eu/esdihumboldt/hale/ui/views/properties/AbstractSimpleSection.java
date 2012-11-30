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

package eu.esdihumboldt.hale.ui.views.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * Simple property section featuring one label and a control.
 * 
 * @author Simon Templer
 * @param <T> the control type
 */
public abstract class AbstractSimpleSection<T extends Control> extends AbstractSingleObjectSection {

	private T control;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.AbstractSingleObjectSection#setInput(java.lang.Object)
	 */
	@Override
	protected void setInput(Object input) {
		updateControl(input, control);
	}

	/**
	 * Update the control with the new object.
	 * 
	 * @param input the object to display properties on
	 * @param control the control
	 */
	protected abstract void updateControl(Object input, T control);

	/**
	 * Create and configure the control that should display the property.
	 * 
	 * @param parent the parent composite
	 * @param widgetFactory the widget factory to use for creating the control
	 * @return the control
	 */
	protected abstract T createControl(Composite parent,
			TabbedPropertySheetWidgetFactory widgetFactory);

	/**
	 * Get the property name to display in the section label.
	 * 
	 * @return the name of the property displayed in the section
	 */
	protected abstract String getPropertyName();

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		control = createControl(composite, getWidgetFactory());
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		control.setLayoutData(data);

		CLabel label = getWidgetFactory().createCLabel(composite, getPropertyName());
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(control, 10);
		data.top = new FormAttachment(control, 0, SWT.CENTER);
		label.setLayoutData(data);
	}

}
