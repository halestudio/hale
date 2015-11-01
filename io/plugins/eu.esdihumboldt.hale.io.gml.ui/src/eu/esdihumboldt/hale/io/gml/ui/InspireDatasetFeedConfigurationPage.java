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

import java.net.URI;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.io.gml.writer.InspireDatasetFeedWriter;
import eu.esdihumboldt.hale.io.gml.writer.InspireInstanceWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for the INSPIRE dataset feed writer.
 * 
 * @author Kai Schwierczek
 */
@SuppressWarnings("restriction")
public class InspireDatasetFeedConfigurationPage extends
		AbstractConfigurationPage<StreamGmlWriter, IOWizard<StreamGmlWriter>> {

	private Composite parent;
	private BooleanFieldEditor create;
	private StringFieldEditor title;
	private StringFieldEditor subTitle;
	private StringFieldEditor rights;
	private StringFieldEditor authorName;
	private StringFieldEditor authorMail;
	private StringFieldEditor selfLink;
	private StringFieldEditor gmlLink;
	private URI lastGMLTarget;

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
		// update defaults for self+gml uri
		if (getWizard().getProvider().getTarget() != null) {
			// may be null when creating custom export configuration

			URI gmlTarget = getWizard().getProvider().getTarget().getLocation();
			if (!gmlTarget.equals(lastGMLTarget)) {
				lastGMLTarget = gmlTarget;
				selfLink.setStringValue(getFeedURIDefault());
				gmlLink.setStringValue(getGMLURIDefault());
			}
		}
	}

	@Override
	public void enable() {
		// do nothing
	}

	@Override
	public void disable() {
		// do nothing
	}

	private String getFileName(URI uri) {
		String s = uri.toString();
		return s.substring(s.lastIndexOf('/') + 1);
	}

	private String getFeedURIDefault() {
		URI gmlTarget = getWizard().getProvider().getTarget().getLocation();
		URI feedTarget = InspireInstanceWriter.getDatasetFeedTarget(gmlTarget);
		return getFileName(feedTarget);
	}

	private String getGMLURIDefault() {
		URI gmlTarget = getWizard().getProvider().getTarget().getLocation();
		return getFileName(gmlTarget);
	}

	@Override
	protected void createContent(Composite page) {
		parent = page;
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).spacing(6, 12)
				.applyTo(page);

		// TODO "intro" explanation

		FieldDecoration fieldDec = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_INFORMATION);
		Image infoImage = fieldDec.getImage();

		create = new BooleanFieldEditor("create", "Create feed", BooleanFieldEditor.SEPARATE_LABEL,
				page);
		create.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				updateStatus();
			}
		});
		title = new StringFieldEditor("title", "Feed title", page);
		subTitle = new StringFieldEditor("subtitle", "Feed sub title", page);
		rights = new StringFieldEditor("rights", "Rights", page); // e
		ControlDecoration rightsExplanation = new ControlDecoration(rights.getTextControl(page),
				SWT.LEFT | SWT.TOP);
		rightsExplanation.setImage(infoImage);
		rightsExplanation
				.setDescriptionText("Any information about rights or restrictions to the dataset.");
		authorName = new StringFieldEditor("name", "Author name", page);
		authorMail = new StringFieldEditor("mail", "Author mail", page);
		selfLink = new StringFieldEditor("self", "Feed URI", page); // e
		ControlDecoration selfLinkExplanation = new ControlDecoration(
				selfLink.getTextControl(page), SWT.LEFT | SWT.TOP);
		selfLinkExplanation.setImage(infoImage);
		selfLinkExplanation
				.setDescriptionText("The address under which the feed will be accessible.\n"
						+ "Changes do not affect the location of the created file.");
		gmlLink = new StringFieldEditor("gml", "GML URI", page);// e
		ControlDecoration gmlLinkExplanation = new ControlDecoration(gmlLink.getTextControl(page),
				SWT.LEFT | SWT.TOP);
		gmlLinkExplanation.setImage(infoImage);
		gmlLinkExplanation
				.setDescriptionText("The address under which the gml data will be accessible.\n"
						+ "Changes do not affect the location of the created file.");
		updateStatus();
	}

	private void updateStatus() {
		// enable/disable input fields
		boolean createFeed = create.getBooleanValue();

		title.setEnabled(createFeed, parent);
		subTitle.setEnabled(createFeed, parent);
		rights.setEnabled(createFeed, parent);
		authorName.setEnabled(createFeed, parent);
		authorMail.setEnabled(createFeed, parent);
		selfLink.setEnabled(createFeed, parent);
		gmlLink.setEnabled(createFeed, parent);
	}

	@Override
	public boolean updateConfiguration(StreamGmlWriter provider) {
		// ATOM allows basically anything (well, mail must match '.+@.+')
		// so no validation here...
		boolean createFeed = create.getBooleanValue();
		provider.setParameter(InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_CREATE_FEED,
				Value.of(createFeed));
		if (createFeed) {
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
			// this is not really necessary, but for the sake of tidiness
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_TITLE, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_SUBTITLE, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_RIGHTS, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_NAME, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_MAIL, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_SELFLINK, null);
			provider.setParameter(InspireDatasetFeedWriter.PARAM_FEED_GMLLINK, null);
		}

		return true;
	}

	@Override
	public void loadPreSelection(IOConfiguration conf) {
		Value createValue = conf.getProviderConfiguration().get(
				InspireInstanceWriter.PARAM_SPATIAL_DATA_SET_CREATE_FEED);
		Value titleValue = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_TITLE);
		Value subTitleValue = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_SUBTITLE);
		Value rightsValue = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_RIGHTS);
		Value authorNameValue = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_NAME);
		Value authorMailValue = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_AUTHOR_MAIL);
		Value selfLinkValue = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_SELFLINK);
		Value gmlLinkValue = conf.getProviderConfiguration().get(
				InspireDatasetFeedWriter.PARAM_FEED_GMLLINK);

		if (create != null && createValue != null) {
			// there is no other way to set the selection for the button except
			// using the preference store
			IPreferenceStore store = new PreferenceStore();
			store.setValue(create.getPreferenceName(), createValue.as(Boolean.class, false));
			create.setPreferenceStore(store);
			create.load();
			updateStatus();
		}
		if (title != null && titleValue != null) {
			title.setStringValue(titleValue.as(String.class));
		}
		if (subTitle != null && subTitleValue != null) {
			subTitle.setStringValue(subTitleValue.as(String.class));
		}
		if (rights != null && rightsValue != null) {
			rights.setStringValue(rightsValue.as(String.class));
		}
		if (authorName != null && authorNameValue != null) {
			authorName.setStringValue(authorNameValue.as(String.class));
		}
		if (authorMail != null && authorMailValue != null) {
			authorMail.setStringValue(authorMailValue.as(String.class));
		}
		if (selfLink != null && selfLinkValue != null) {
			selfLink.setStringValue(selfLinkValue.as(String.class));
		}
		if (gmlLink != null && gmlLinkValue != null) {
			gmlLink.setStringValue(gmlLinkValue.as(String.class));
		}
	}
}
