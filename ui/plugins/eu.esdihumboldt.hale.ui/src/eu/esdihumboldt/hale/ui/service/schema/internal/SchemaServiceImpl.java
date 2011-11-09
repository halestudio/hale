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

package eu.esdihumboldt.hale.ui.service.schema.internal;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Default {@link SchemaService} implementation
 * @author Simon Templer
 */
public class SchemaServiceImpl extends AbstractSchemaService {
	
	/**
	 * Maps schema space IDs to schema spaces
	 */
	private static final Map<SchemaSpaceID, DefaultSchemaSpace> spaces = new HashMap<SchemaSpaceID, DefaultSchemaSpace>();

	/**
	 * @see AbstractSchemaService#AbstractSchemaService(ProjectService)
	 */
	public SchemaServiceImpl(ProjectService projectService) {
		super(projectService);
	}

	/**
	 * @see SchemaService#getSchemas(SchemaSpaceID)
	 */
	@Override
	public SchemaSpace getSchemas(SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);
		
		synchronized (spaces) {
			DefaultSchemaSpace space = spaces.get(spaceID);
			if (space == null) {
				space = new DefaultSchemaSpace();
				spaces.put(spaceID, space);
			}
			return space;
		}
	}

	/**
	 * @see SchemaService#addSchema(Schema, SchemaSpaceID)
	 */
	@Override
	public void addSchema(Schema schema, SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);
		
		DefaultSchemaSpace space = (DefaultSchemaSpace) getSchemas(spaceID);
		space.addSchema(schema);
		
		notifySchemaAdded(spaceID, schema);
	}

	/**
	 * @see SchemaService#clearSchemas(SchemaSpaceID)
	 */
	@Override
	public void clearSchemas(SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);
		
		synchronized (spaces) {
			spaces.remove(spaceID);
		}
		
		notifySchemasCleared(spaceID);
	}

}
