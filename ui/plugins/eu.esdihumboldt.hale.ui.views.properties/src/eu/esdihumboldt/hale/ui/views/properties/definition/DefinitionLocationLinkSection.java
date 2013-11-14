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
import eu.esdihumboldt.hale.ui.common.components.URILink;

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
