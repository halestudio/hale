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

package eu.esdihumboldt.hale.ui.io.schema;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advisor for schema import to the {@link SchemaService}
 * 
 * @author Simon Templer
 * @since 2.5
 */
public class SchemaImportAdvisor extends DefaultIOAdvisor<SchemaReader> {

	private final SchemaSpaceID spaceID;

	/**
	 * Create a schema import advisor
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 */
	public SchemaImportAdvisor(SchemaSpaceID spaceID) {
		super();
		this.spaceID = spaceID;
	}

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(SchemaReader provider) {
		super.prepareProvider(provider);

		// set shared types XXX this is not fixed yet
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
		provider.setSharedTypes(ss.getSchemas(spaceID));
	}

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(SchemaReader provider) {
		// add loaded schema to schema space
		Schema schema = provider.getSchema();

		SchemaService ss = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
		ss.addSchema(schema, spaceID);

		if (ss.getSchemas(spaceID).getMappingRelevantTypes().isEmpty()) {
			// if no types are present after loading, open editor for mapping
			// relevant types
			ss.editMappableTypes(spaceID);
		}

		super.handleResults(provider);
	}

}
