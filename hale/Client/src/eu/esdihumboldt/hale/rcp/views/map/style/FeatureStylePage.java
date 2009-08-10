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
package eu.esdihumboldt.hale.rcp.views.map.style;

import org.eclipse.jface.dialogs.DialogPage;
import org.geotools.styling.Style;

/**
 * Dialog page for the {@link FeatureStyleDialog}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class FeatureStylePage extends DialogPage {
	
	private final FeatureStyleDialog parent;

	/**
	 * Creates a new dialog page
	 * 
	 * @param parent the parent dialog
	 * @param title the page title
	 */
	public FeatureStylePage(FeatureStyleDialog parent, String title) {
		super(title);
		
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public FeatureStyleDialog getParent() {
		return parent;
	}
	
	/**
	 * Get the edited style
	 * 
	 * @return the style
	 * @throws Exception if the style could not be retrieved
	 */
	public abstract Style getStyle() throws Exception;
	
}
