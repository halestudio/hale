/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.utils.definition.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;

/**
 * Default definition label factory
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefaultDefinitionLabelFactory implements DefinitionLabelFactory {
	
	private final BrowserTip tip = new BrowserTip(300, 200, true);

	/**
	 * @see DefinitionLabelFactory#createLabel(Composite, Definition)
	 */
	@Override
	public Control createLabel(Composite parent, Definition definition) {
		String name = definition.getDisplayName();
		
		final String description = definition.getDescription();
		if (description != null && !description.isEmpty()) {
			// link for displaying documentation
			final Link link = new Link(parent, SWT.NONE);
			link.setText("<a href=\"" + definition.getIdentifier() + "\">" + name + "</a>");
			//final DefaultToolTip tt = new DefaultToolTip(link, ToolTip.NO_RECREATE, true);
			//tt.setText(description);
			link.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					// link target is in e.text - but not needed here
					// show tip
					tip.showToolTip(link, 0, link.getSize().y, description);
//					tt.show(new Point(0, link.getSize().y));
				}
				
			});
			return link;
		}
		else {
			Label label = new Label(parent, SWT.NONE);
			label.setText(name);
			return label;
		}
	}

}
