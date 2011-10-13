/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.properties.typedefinition;

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
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;
import eu.esdihumboldt.hale.ui.views.properties.DefaultDefinitionSection;

/**
 * Properties section with Enumeration information
 * 
 * @author Patrick Lieb
 */
public class TypeDefinitionEnumeration extends
		DefaultDefinitionSection<TypeDefinition> {

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
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
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
		@SuppressWarnings("unchecked")
		Collection<? extends TypeDefinition> elements = getDefinition()
				.getConstraint(Enumeration.class).getValues();
		int size = elements.size();
		TypeDefinition type[] = elements.toArray(new TypeDefinition[size]);
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

		CLabel namespaceLabel = getWidgetFactory().createCLabel(composite,
				"Enumeration:"); //$NON-NLS-1$

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(text, 10);
		data.top = new FormAttachment(text, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);

		text.setText(type[0].getName().getNamespaceURI().toString());

		textarray[0] = text;

		for (int pos = 1; pos < size; pos++) {

			text = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
			text.setEditable(false);
			data = new FormData();
			data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
			data.right = new FormAttachment(100, 0);
			data.top = new FormAttachment(textarray[pos - 1],
					ITabbedPropertyConstants.VSPACE);
			text.setLayoutData(data);

			namespaceLabel = getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

			data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(text, 10);
			data.top = new FormAttachment(text, 0, SWT.CENTER);
			namespaceLabel.setLayoutData(data);

			text.setText(type[pos].getName().getNamespaceURI().toString());

			textarray[pos] = text;
		}
		parent.layout();
		parent.getParent().layout();
	}
}
