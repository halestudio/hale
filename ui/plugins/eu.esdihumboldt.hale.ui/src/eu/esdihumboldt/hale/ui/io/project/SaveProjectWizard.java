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

package eu.esdihumboldt.hale.ui.io.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.ui.io.ExportSelectTargetPage;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Wizard for saving a project
 * 
 * @author Simon Templer
 */
public class SaveProjectWizard extends ExportWizard<ProjectWriter> {

	/**
	 * Advisor identifier for saving a project
	 */
	public static final String ADVISOR_PROJECT_SAVE = "project.save";

	private final IContentType restrictToContentType;

	/**
	 * Create a wizard that saves a project
	 * 
	 * @param restrictToContentType the content type the save should be
	 *            restricted to, or <code>null</code>
	 */
	public SaveProjectWizard(IContentType restrictToContentType) {
		super(ProjectWriter.class);
		this.restrictToContentType = restrictToContentType;
	}

	/**
	 * @see ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		addPage(new SaveProjectDetailsPage());
	}

	@Override
	public List<IOProviderDescriptor> getFactories() {
		if (restrictToContentType == null) {
			return super.getFactories();
		}

		/*
		 * Remove all providers that do not support the content type the export
		 * is restricted to.
		 */
		List<IOProviderDescriptor> factories = new ArrayList<>(super.getFactories());
		Iterator<IOProviderDescriptor> it = factories.iterator();
		while (it.hasNext()) {
			IOProviderDescriptor pd = it.next();
			if (pd.getSupportedTypes() == null
					|| !pd.getSupportedTypes().contains(restrictToContentType)) {
				it.remove();
			}
		}

		return factories;
	}

	@Override
	protected ExportSelectTargetPage<ProjectWriter, ? extends ExportWizard<ProjectWriter>> createSelectTargetPage() {
		return new ExportSelectTargetPage<ProjectWriter, SaveProjectWizard>() {

			@Override
			public void setAllowedContentTypes(Collection<IContentType> contentTypes) {
				if (restrictToContentType == null) {
					super.setAllowedContentTypes(contentTypes);
				}
				else {
					// restrict to given specific content type
					super.setAllowedContentTypes(Collections.singleton(restrictToContentType));
				}
			}

		};
	}

	/**
	 * @see IOWizard#updateConfiguration(IOProvider)
	 */
	@Override
	protected void updateConfiguration(ProjectWriter provider) {
		super.updateConfiguration(provider);

		// project has been set and can be adapted

		// populate and set the save configuration
		IOConfiguration saveConfiguration = new IOConfiguration();
		saveConfiguration.setActionId(ADVISOR_PROJECT_SAVE);
		saveConfiguration.setProviderId(getProviderFactory().getIdentifier());
		provider.storeConfiguration(saveConfiguration.getProviderConfiguration());
		provider.getProject().setSaveConfiguration(saveConfiguration);
	}

}
