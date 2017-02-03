/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io.align;

import eu.esdihumboldt.hale.common.align.io.BaseAlignmentReader;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advisor for base alignment import. This actually does not give the reader
 * enough information to work. The {@link MutableAlignment} is missing.
 * 
 * @author Kai Schwierczek
 */
public class BaseAlignmentImportAdvisor extends DefaultIOAdvisor<BaseAlignmentReader> {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor#prepareProvider(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public void prepareProvider(BaseAlignmentReader provider) {
		super.prepareProvider(provider);

		// XXX reader still needs MutableAlignment!

		SchemaService ss = getService(SchemaService.class);
		provider.setSourceSchema(ss.getSchemas(SchemaSpaceID.SOURCE));
		provider.setTargetSchema(ss.getSchemas(SchemaSpaceID.TARGET));

		ProjectService ps = getService(ProjectService.class);
		provider.setProjectLocation(ps.getLoadLocation());
	}
}
