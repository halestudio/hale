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

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
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

	private BooleanFieldEditor create;
	private StringFieldEditor title;
	private StringFieldEditor subTitle;
	private StringFieldEditor rights;
	private StringFieldEditor authorName;
	private StringFieldEditor authorMail;
	private StringFieldEditor selfLink;
	private StringFieldEditor gmlLink;

	/**
	 * Default constructor.
	 */
	public InspireDatasetFeedConfigurationPage() {
		super("inspire.feed");

		setTitle("Dataset feed creation");
		setDescription("Create an INSPIRE dataset feed for use in an INSPIRE Download Service.");
		setPageComplete(true);
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
	protected void createContent(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).spacing(6, 12)
				.applyTo(page);

		// TODO "intro" explanation
		// TODO defaults? explanations?

		create = new BooleanFieldEditor("create", "Create feed", BooleanFieldEditor.SEPARATE_LABEL,
				page);
		title = new StringFieldEditor("title", "Feed title", page);
		subTitle = new StringFieldEditor("title", "Feed sub title", page); // optional
		rights = new StringFieldEditor("title", "Rights", page); // e
		authorName = new StringFieldEditor("title", "Author name", page);
		authorMail = new StringFieldEditor("title", "Author mail", page);
		selfLink = new StringFieldEditor("title", "Feed URI", page); // d+e
		gmlLink = new StringFieldEditor("title", "GML URI", page);// d+e

	}

	@Override
	public boolean updateConfiguration(InspireInstanceWriter provider) {
		boolean valid = true; // TODO
		if (valid) {
			provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_CREATE_FEED,
					Value.of(create.getBooleanValue()));
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_TITLE,
					Value.of(title.getStringValue()));
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_SUBTITLE,
					Value.of(subTitle.getStringValue()));
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_RIGHTS,
					Value.of(rights.getStringValue()));
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_NAME,
					Value.of(authorName.getStringValue()));
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_MAIL,
					Value.of(authorMail.getStringValue()));
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_SELFLINK,
					Value.of(selfLink.getStringValue()));
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_GMLLINK,
					Value.of(gmlLink.getStringValue()));
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

		// TODO fill fields?
	}
}
