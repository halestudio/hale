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

package eu.esdihumboldt.hale.io.xsd.ui.properties;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;

/**
 * Properties Section with XmlElements
 * 
 * @author Patrick Lieb
 */
public class TypeDefinitionXmlElementsSection extends DefaultDefinitionSection<TypeDefinition> {

	private Text[] textarray;

	private Text text;

	private Composite parent;

	private Composite composite;

	private TabbedPropertySheetPage aTabbedPropertySheetPage;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		this.parent = parent;
		this.aTabbedPropertySheetPage = aTabbedPropertySheetPage;
	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if (composite != null)
			composite.dispose();
		Collection<? extends XmlElement> elements = getDefinition()
				.getConstraint(XmlElements.class).getElements();
		int size = elements.size();
		XmlElement elm[] = elements.toArray(new XmlElement[size]);
		textarray = new Text[size];

		super.createControls(parent, aTabbedPropertySheetPage);
		composite = getWidgetFactory().createFlatFormComposite(parent);

		FormData data;

		text = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		text.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		text.setLayoutData(data);

		CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, "XML-Elements:"); //$NON-NLS-1$

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(text, 10);
		data.top = new FormAttachment(text, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);

		text.setText(elm[0].getName().toString());

		textarray[0] = text;

		for (int pos = 1; pos < size; pos++) {

			text = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
			text.setEditable(false);
			data = new FormData();
			data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(textarray[pos - 1], ITabbedPropertyConstants.VSPACE);
			text.setLayoutData(data);

			namespaceLabel = getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

			data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(text, 10);
			data.top = new FormAttachment(text, 0, SWT.CENTER);
			namespaceLabel.setLayoutData(data);

			text.setText(elm[pos].getName().getNamespaceURI().toString());

			textarray[pos] = text;
		}
		parent.layout();
		parent.getParent().layout();
	}
}
