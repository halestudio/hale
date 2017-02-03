/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io.align;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advisor for storing an alignment in the alignment service
 * 
 * @author Simon Templer
 */
public class AlignmentImportAdvisor extends DefaultIOAdvisor<AlignmentReader> {

	/**
	 * @see AbstractIOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(AlignmentReader provider) {
		super.prepareProvider(provider);

		SchemaService ss = getService(SchemaService.class);
		provider.setSourceSchema(ss.getSchemas(SchemaSpaceID.SOURCE));
		provider.setTargetSchema(ss.getSchemas(SchemaSpaceID.TARGET));

		ProjectService ps = getService(ProjectService.class);
		// XXX uses the same path updater as the project
		// If someone edited the project file and referenced an alignment file,
		// which isn't in the project directory this won't work.
		provider.setPathUpdater(ps.getLocationUpdater());
	}

	/**
	 * @see AbstractIOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(AlignmentReader provider) {
		super.handleResults(provider);

		AlignmentService as = getService(AlignmentService.class);
		// XXX clear old mapping?
		// FIXME merging alignments not supported yet
		as.addOrUpdateAlignment(provider.getAlignment());
	}

}
