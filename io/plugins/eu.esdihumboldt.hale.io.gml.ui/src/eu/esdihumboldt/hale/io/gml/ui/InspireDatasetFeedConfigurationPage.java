/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.gml.ui;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.io.gml.writer.InspireDatasetFeedWriter;
import eu.esdihumboldt.hale.io.gml.writer.InspireInstanceWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for the INSPIRE dataset feed writer.
 * 
 * @author Kai Schwierczek
 */
public class InspireDatasetFeedConfigurationPage extends
		AbstractConfigurationPage<InspireInstanceWriter, IOWizard<InspireInstanceWriter>> {

	/**
	 * Default constructor.
	 */
	public InspireDatasetFeedConfigurationPage() {
		super("inspire.feed");
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		// TODO update gml-/self-link
	}

	@Override
	public void enable() {
		// do nothing
	}

	@Override
	public void disable() {
		// do nothing
	}

	@Override
	public boolean updateConfiguration(InspireInstanceWriter provider) {
		boolean valid = false;
		if (valid) {
			// TODO read fields
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_CREATE_FEED, value);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_TITLE, value);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_SUBTITLE, value);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_RIGHTS, value);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_NAME, value);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_MAIL, value);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_SELFLINK, value);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_GMLLINK, value);
		}
		else {
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_CREATE_FEED, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_TITLE, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_SUBTITLE, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_RIGHTS, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_NAME, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_MAIL, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_SELFLINK, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_GMLLINK, null);
		}
		return valid;
	}

	@Override
	protected void createContent(Composite page) {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadPreSelection(IOConfiguration conf) {
		Value enabled = conf.getProviderConfiguration().get(
				InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_CREATE_FEED);
		Value title = conf.getProviderConfiguration()
				.get(InspireDatasetFeedWriter.PARAM_FEED_TITLE);
		Value subtitle = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_SUBTITLE);
		Value rights = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_RIGHTS);
		Value authorName = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_NAME);
		Value authorMail = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_MAIL);
		Value selflink = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_SELFLINK);
		Value gmllink = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_GMLLINK);

		// TODO fill fields
//		if (localId != null)
//			localIdEditor.setAsText(localId.getStringRepresentation());
	}
}
