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

package eu.esdihumboldt.hale.server.templates.impl;

import java.io.File;
import java.io.IOException;

import eu.esdihumboldt.hale.common.headless.scavenger.ProjectReference;
import eu.esdihumboldt.hale.server.templates.TemplateScavenger;
import eu.esdihumboldt.util.scavenger.AbstractResourceScavenger;

/**
 * Scavenger for (template) projects.
 * 
 * @author Simon Templer
 */
public class TemplateScavengerImpl extends AbstractResourceScavenger<ProjectReference<Void>>
		implements TemplateScavenger {

	/**
	 * Create a template project scavenger.
	 * 
	 * @param scavengeLocation the location to scan, if the location does not
	 *            exist or is not accessible, a default location inside the
	 *            platform instance location is used
	 */
	public TemplateScavengerImpl(File scavengeLocation) {
		super(scavengeLocation, "templates");

		triggerScan();
	}

	@Override
	protected void onRemove(ProjectReference<Void> reference, String resourceId) {
		// nothing to do
	}

	@Override
	protected ProjectReference<Void> loadReference(File resourceFolder, String resourceFileName,
			String resourceId) throws IOException {
		ProjectReference<Void> ref = new ProjectReference<>(resourceFolder, resourceFileName,
				resourceId, null);
		ref.update(null);
		return ref;
	}

	@Override
	protected void updateResource(ProjectReference<Void> reference, String resourceId) {
		reference.update(null);
	}

}
