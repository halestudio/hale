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

import eu.esdihumboldt.hale.models.AbstractUpdateService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.UpdateMessage;

/**
 * Notification handling for {@link SchemaService}s that support
 * {@link SchemaServiceListener}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractSchemaService extends AbstractUpdateService
		implements SchemaService {

	/**
	 * The default update message
	 */
	private static final UpdateMessage<?> DEF_MESSAGE = new UpdateMessage<Object>(SchemaService.class, null);
	
	/**
	 * @see AbstractUpdateService#notifyListeners(UpdateMessage)
	 * @deprecated use {@link #notifySchemaChanged(SchemaType)} instead
	 */
	@Deprecated
	@Override
	protected void notifyListeners(UpdateMessage<?> message) {
		notifySchemaChanged(null);
	}

	/**
	 * Call when a schema has changed
	 * 
	 * @param schema the schema type, <code>null</code> if both schemas have changed
	 */
	protected void notifySchemaChanged(SchemaType schema) {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof SchemaServiceListener) {
				if (schema == null) {
					((SchemaServiceListener) listener).schemaChanged(SchemaType.SOURCE);
					((SchemaServiceListener) listener).schemaChanged(SchemaType.TARGET);
				} else {
					((SchemaServiceListener) listener).schemaChanged(schema);
				}
			}
			
			listener.update(DEF_MESSAGE);
		}
	}

}
