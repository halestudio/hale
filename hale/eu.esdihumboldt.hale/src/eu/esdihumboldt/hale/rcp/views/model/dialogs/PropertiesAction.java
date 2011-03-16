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

package eu.esdihumboldt.hale.rcp.views.model.dialogs;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;

/**
 * Action that launches a {@link PropertiesDialog}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PropertiesAction extends Action {

	private final SchemaItem item;
	
	/**
	 * Create an action to show a properties dialog
	 * 
	 * @param item the schema item to inspect
	 */
	public PropertiesAction(SchemaItem item) {
		super();
		
		this.item = item;
		
		setText(Messages.PropertiesAction_PropertiesText);
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		final Display display = Display.getCurrent();
		
		PropertiesDialog dialog = new PropertiesDialog(display.getActiveShell(), item);
		dialog.open();
	}

}
