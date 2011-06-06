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

import eu.esdihumboldt.hale.schema.io.SchemaReader;
import eu.esdihumboldt.hale.schema.io.SchemaReaderFactory;
import eu.esdihumboldt.hale.schema.model.Schema;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.ImportWizard;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Wizard for importing source or target schemas
 * @author Simon Templer
 */
public class SchemaImportWizard extends ImportWizard<SchemaReader, SchemaReaderFactory> {

	private final SchemaSpaceID spaceID;
	
	/**
	 * Create a schema import wizard
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE} 
	 *   or {@link SchemaSpaceID#TARGET}
	 */
	public SchemaImportWizard(SchemaSpaceID spaceID) {
		super(SchemaReaderFactory.class);
		
		this.spaceID = spaceID;
	}

	/**
	 * @see IOWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean success = super.performFinish();
		
		if (success) {
			// add loaded schema to schema space
			Schema schema = getProvider().getSchema();
			
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
			ss.addSchema(schema, spaceID);
		}
		
		return success;
	}

}
