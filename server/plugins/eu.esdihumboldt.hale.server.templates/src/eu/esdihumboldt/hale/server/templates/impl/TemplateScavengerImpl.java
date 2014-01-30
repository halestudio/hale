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
import java.util.Date;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.server.db.orient.DatabaseHelper;
import eu.esdihumboldt.hale.server.model.Template;
import eu.esdihumboldt.hale.server.templates.TemplateProject;
import eu.esdihumboldt.hale.server.templates.TemplateScavenger;
import eu.esdihumboldt.util.blueprints.entities.NonUniqueResultException;
import eu.esdihumboldt.util.scavenger.AbstractResourceScavenger;

/**
 * Scavenger for (template) projects.
 * 
 * @author Simon Templer
 */
public class TemplateScavengerImpl extends AbstractResourceScavenger<TemplateProject> implements
		TemplateScavenger {

	private static final ALogger log = ALoggerFactory.getLogger(TemplateScavengerImpl.class);

	private final ThreadLocal<OrientGraph> graph = new ThreadLocal<>();

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
	protected void onRemove(TemplateProject reference, String resourceId) {
		// invalidate database reference (if it exists)
		OrientGraph g = graph.get();
		boolean cleanUp = g == null;
		if (g == null) {
			g = DatabaseHelper.getGraph();
		}
		try {
			Template template = Template.getByTemplateId(g, resourceId);
			if (template != null) {
				template.setValid(false);
				log.info("Template {} was removed - updating status to invalid", resourceId);
			}
		} catch (NonUniqueResultException e) {
			log.error("Duplicate template representation in database");
		} finally {
			if (cleanUp) {
				g.shutdown();
			}
		}
	}

	@Override
	protected TemplateProject loadReference(File resourceFolder, String resourceFileName,
			String resourceId) throws IOException {
		TemplateProject ref = new TemplateProject(resourceFolder, resourceFileName, resourceId,
				null);
		return ref;
	}

	@Override
	public synchronized void triggerScan() {
		// provide a graph for use in updateResource and onRemove
		graph.set(DatabaseHelper.getGraph());
		try {
			super.triggerScan();

			/*
			 * Check if there are templates in the database that no longer have
			 * an associated template in the file system.
			 */
			for (Template template : Template.findAll(graph.get())) {
				if (template.isValid() && getReference(template.getTemplateId()) == null) {
					// invalidate template w/ missing resource
					template.setValid(false);
					log.warn("Invalidated template {}, the resource folder is missing",
							template.getTemplateId());
				}
			}
		} finally {
			graph.get().shutdown();
			graph.set(null);
		}
	}

	@Override
	protected void onAdd(TemplateProject reference, String resourceId) {
		reference.update(null);

		Template template;
		try {
			// get existing representation in database
			template = Template.getByTemplateId(graph.get(), resourceId);
		} catch (NonUniqueResultException e) {
			log.error("Duplicate template representation in database");
			return;
		}

		if (template != null) {
			// update valid status
			boolean valid = reference.isValid();
			template.setValid(valid);
			log.info("Updating template {} - {}", resourceId, (valid) ? ("valid") : ("invalid"));
		}
		else {
			/*
			 * Only create a new database reference if the project actually is
			 * valid
			 */
			if (reference.isValid()) {
				ProjectInfo info = reference.getProjectInfo();

				// create new template representation in DB
				template = Template.create(graph.get());

				// populated with resource ID and values from project
				template.setTemplateId(resourceId);
				template.setName(info.getName());
				template.setAuthor(info.getAuthor());
				template.setDescription(info.getDescription());
				template.setValid(true);
				Date now = new Date();
				template.setCreated(now);
				template.setLastUpdate(now);

				log.info("Creating database representation for template {}", resourceId);
			}
		}
	}

	@Override
	public synchronized void forceUpdate(String templateId) {
		TemplateProject ref = getReference(templateId);
		ref.forceUpdate(null);
		graph.set(DatabaseHelper.getGraph());
		try {
			onAdd(ref, templateId);
		} finally {
			graph.get().shutdown();
			graph.set(null);
		}
	}

	@Override
	protected void updateResource(TemplateProject reference, String resourceId) {
		/*
		 * nothing to do
		 * 
		 * Updates on existing templates must be triggered explicitly through
		 * forceUpdate(...)
		 */
	}

}
