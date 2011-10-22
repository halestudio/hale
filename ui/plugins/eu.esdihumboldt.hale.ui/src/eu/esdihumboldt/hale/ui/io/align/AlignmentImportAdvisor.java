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

package eu.esdihumboldt.hale.ui.io.align;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Advisor for storing an alignment in the alignment service
 * @author Simon Templer
 */
public class AlignmentImportAdvisor extends AbstractIOAdvisor<AlignmentReader> {

	/**
	 * @see AbstractIOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(AlignmentReader provider) {
		super.prepareProvider(provider);
		
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		provider.setSourceSchema(ss.getSchemas(SchemaSpaceID.SOURCE));
		provider.setTargetSchema(ss.getSchemas(SchemaSpaceID.TARGET));
	}

	/**
	 * @see AbstractIOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(AlignmentReader provider) {
		super.handleResults(provider);
		
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		//XXX clear old mapping?
		//FIXME merging alignments not supported yet
		as.addOrUpdateAlignment(provider.getAlignment());
	}

}
