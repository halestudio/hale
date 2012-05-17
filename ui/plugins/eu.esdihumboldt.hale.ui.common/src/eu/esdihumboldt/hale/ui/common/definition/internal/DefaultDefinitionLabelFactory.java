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

package eu.esdihumboldt.hale.ui.common.definition.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.ui.util.tip.BrowserTip;

/**
 * Default definition label factory
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefaultDefinitionLabelFactory implements DefinitionLabelFactory {
	
	private final BrowserTip browserTip = new BrowserTip(300, 200, true);

	/**
	 * @see DefinitionLabelFactory#createLabel(Composite, Definition, boolean)
	 */
	@Override
	public Control createLabel(Composite parent, Definition<?> definition, boolean verbose) {
		String name = definition.getDisplayName();
		
		String description = definition.getDescription();
		if (description != null && !description.isEmpty()) {
			// link for displaying documentation
			String linkText = "<a href=\"" + definition.getIdentifier() + "\">" + name + "</a>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			final Map<String, String> tips = new HashMap<String, String>();
			tips.put(definition.getIdentifier(), description);
			
			if (verbose && definition instanceof PropertyDefinition) {
				TypeDefinition parentType = ((PropertyDefinition) definition).getParentType();
				String typeDescription = parentType.getDescription();
				if (typeDescription != null) {
					tips.put(parentType.getIdentifier(), typeDescription);
					linkText = "<a href=\"" + parentType.getIdentifier() + "\">" +  //$NON-NLS-1$ //$NON-NLS-2$
						parentType.getDisplayName() + "</a>." + linkText; //$NON-NLS-1$
				}
				else {
					linkText = parentType.getDisplayName() + "." + linkText; //$NON-NLS-1$
				}
			}
			
			final Link link = new Link(parent, SWT.NONE);
			link.setText(linkText);
			
			link.addSelectionListener(new SelectionAdapter() {
				
				private Shell lastShell = null;

				@Override
				public void widgetSelected(SelectionEvent e) {
					// link target is in e.text - but not needed here
					String href = e.text;
					
					// show tip
					String tip = tips.get(href);
					
					if (tip != null) {
						BrowserTip.hideToolTip(lastShell);
						lastShell = browserTip.showToolTip(link, 0, link.getSize().y, tip);
					}
				}
				
			});
			
			return link;
		}
		else {
			Label label = new Label(parent, SWT.NONE);
			if (verbose && definition instanceof PropertyDefinition) {
				label.setText(((PropertyDefinition) definition).getParentType().getDisplayName() +
						"." + name); //$NON-NLS-1$
			}
			else {
				label.setText(name);
			}
			return label;
		}
	}

}
