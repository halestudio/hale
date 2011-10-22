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

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceAdapter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Provides UI variables related to the {@link SchemaService}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class SchemaServiceSource extends AbstractSourceProvider {

	/**
	 * The name of the variable which value is <code>true</code> if there is a
	 * source schema present in the {@link SchemaService}
	 */
	public static final String HAS_SOURCE_SCHEMA = "hale.schemas.has_source";
	
	/**
	 * The name of the variable which value is <code>true</code> if there is a
	 * target schema present in the {@link SchemaService}
	 */
	public static final String HAS_TARGET_SCHEMA = "hale.schemas.has_target";
	
	private SchemaServiceListener schemaListener;
	
	/**
	 * Default constructor
	 */
	public SchemaServiceSource() {
		super();
		
		final SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		ss.addSchemaServiceListener(schemaListener = new SchemaServiceAdapter() {

			/**
			 * @see SchemaServiceAdapter#schemaAdded(SchemaSpaceID, Schema)
			 */
			@Override
			public void schemaAdded(SchemaSpaceID spaceID, Schema schema) {
				schemasCleared(spaceID);
			}

			/**
			 * @see SchemaServiceAdapter#schemasCleared(SchemaSpaceID)
			 */
			@Override
			public void schemasCleared(SchemaSpaceID spaceID) {
				switch (spaceID) {
				case SOURCE:
					fireSourceChanged(ISources.WORKBENCH, HAS_SOURCE_SCHEMA, hasSchema(ss, spaceID));
					break;
				case TARGET:
					fireSourceChanged(ISources.WORKBENCH, HAS_TARGET_SCHEMA, hasSchema(ss, spaceID));
					break;
				}
			}

		});
	}

	/**
	 * @see ISourceProvider#dispose()
	 */
	@Override
	public void dispose() {
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		ss.removeSchemaServiceListener(schemaListener);
	}

	/**
	 * @see ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState() {
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(HAS_SOURCE_SCHEMA, hasSchema(ss, SchemaSpaceID.SOURCE));
		result.put(HAS_TARGET_SCHEMA, hasSchema(ss, SchemaSpaceID.TARGET));
		
		return result;
	}

	private static boolean hasSchema(SchemaService ss, SchemaSpaceID spaceID) {
		SchemaSpace schemas = ss.getSchemas(spaceID);
		return schemas != null && !schemas.getMappableTypes().isEmpty();
	}

	/**
	 * @see ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames() {
		return new String[]{
				HAS_SOURCE_SCHEMA, 
				HAS_TARGET_SCHEMA};
	}

}
