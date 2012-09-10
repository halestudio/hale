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

package eu.esdihumboldt.hale.io.xsd.ui.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;

/**
 * Properties section with XmlAttributeFlag
 * 
 * @author Patrick Lieb
 */
public class PropertyTypeXmlAttributeFlagSection extends
		DefaultDefinitionSection<PropertyDefinition> {

	private Text xmlattributeflag;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		xmlattributeflag = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		xmlattributeflag.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		xmlattributeflag.setLayoutData(data);

		CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, "XML-Attribute-Flag:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(xmlattributeflag, 10);
		data.top = new FormAttachment(xmlattributeflag, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);

	}

	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		if (getDefinition().getConstraint(XmlAttributeFlag.class).isEnabled()) {
			xmlattributeflag.setText("true");
		}
		else {
			xmlattributeflag.setText("false");
		}
	}
}
