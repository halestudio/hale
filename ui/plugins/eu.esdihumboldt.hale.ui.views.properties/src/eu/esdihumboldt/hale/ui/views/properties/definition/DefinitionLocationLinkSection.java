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

package eu.esdihumboldt.hale.ui.views.properties.definition;

import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.util.components.URILink;

/**
 * Properties section with a link to open the location in editor or browser
 * 
 * @author Patrick Lieb
 */
public class DefinitionLocationLinkSection extends DefaultDefinitionSection<Definition<?>> {

	private URILink location;

	private Link link;

	private Text linktext;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		location = new URILink(composite, 0, null, "<A>Open Location</A>");

		link = location.getLink();

		FormData data;

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		link.setLayoutData(data);

		link.setBackground(getWidgetFactory().getColors().getBackground());

		CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, "Location:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(link, 15);
		data.top = new FormAttachment(link, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);

		linktext = getWidgetFactory().createText(composite, "");
		linktext.setEditable(false);

		data = new FormData();
		data.width = 100;
//		data.height = 100;
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(link, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		linktext.setLayoutData(data);

		namespaceLabel = getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(linktext, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(linktext, 0, SWT.TOP);
		namespaceLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		URI loc = getDefinition().getLocation();
		if (loc == null) {
			location.setText("no Link available");
			linktext.setText("location not set");
		}
		else {
			location.refresh(loc);
			linktext.setText(loc.toASCIIString());
		}
		link = location.getLink();
	}
}
