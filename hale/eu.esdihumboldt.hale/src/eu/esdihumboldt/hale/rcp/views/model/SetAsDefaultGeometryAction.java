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

package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.jface.action.Action;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.DefaultGeometries;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Action that sets a default geometry property for a type 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SetAsDefaultGeometryAction extends Action {

	private final SchemaItem item;
	
	/**
	 * Constructor
	 * 
	 * @param item the geometry property schema item
	 */
	public SetAsDefaultGeometryAction(SchemaItem item) {
		this.item = item;
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		SchemaItem parent = item.getParent(); // parent must be a feature type
		
		String propertyName = item.getName().getLocalPart();
		Definition def = parent.getDefinition();
		Name typeName = (def instanceof TypeDefinition)?(((TypeDefinition) def).getName()):(((SchemaElement) def).getTypeName());
		
		DefaultGeometries.setDefaultGeometryName(typeName, propertyName);
		
		//TODO reload schema and instances
	}

}
