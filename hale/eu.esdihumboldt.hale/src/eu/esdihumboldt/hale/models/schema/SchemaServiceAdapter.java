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

package eu.esdihumboldt.hale.models.schema;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;

/**
 * Schema service listener adapter
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class SchemaServiceAdapter implements SchemaServiceListener {

	/**
	 * @see SchemaServiceListener#schemaChanged(SchemaType)
	 */
	@Override
	public void schemaChanged(SchemaType schema) {
		// override me
	}

	/**
	 * @see HaleServiceListener#update(UpdateMessage)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(UpdateMessage message) {
		// override me
	}

}
