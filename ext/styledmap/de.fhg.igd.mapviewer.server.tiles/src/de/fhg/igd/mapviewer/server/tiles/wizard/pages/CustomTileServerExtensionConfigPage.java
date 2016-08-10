/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package de.fhg.igd.mapviewer.server.tiles.wizard.pages;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.fhg.igd.mapviewer.server.tiles.CustomTileMapServerConfiguration;
import de.fhg.igd.mapviewer.server.tiles.wizard.Messages;

/**
 * Extension of basic custom tile map server configuration page
 * 
 * @author Arun
 */
public class CustomTileServerExtensionConfigPage extends CustomTileWizardPage
		implements IPropertyChangeListener {

	private Composite page;

	private IntegerFieldEditor zoomLevels;

	private StringFieldEditor attributionText;

	/**
	 * Constructor
	 * 
	 * @param configuration {@link CustomTileMapServerConfiguration}
	 */
	public CustomTileServerExtensionConfigPage(CustomTileMapServerConfiguration configuration) {
		super(configuration, Messages.BasicConfigurationPage_0);
		setTitle(Messages.BasicConfigurationPage_1);
		setMessage(Messages.BasicConfigurationPage_10);
	}

	/**
	 * @see de.fhg.igd.mapviewer.server.tiles.wizard.pages.CustomTileWizardPage#updateConfiguration(de.fhg.igd.mapviewer.server.tiles.CustomTileMapServerConfiguration)
	 */
	@Override
	public boolean updateConfiguration(CustomTileMapServerConfiguration configuration) {
		if (zoomLevels.isValid()) {
			configuration.setZoomLevel(zoomLevels.getIntValue());
			configuration.setAttributionText(attributionText.getStringValue());
			return true;
		}
		return false;
	}

	/**
	 * @see de.fhg.igd.mapviewer.server.tiles.wizard.pages.CustomTileWizardPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(3, false));

		createComponent();

		setControl(page);

		update();
	}

	/**
	 * Create components for basic options, zoom level and attribution text.
	 */
	protected void createComponent() {

		// zoom levels
		zoomLevels = new IntegerFieldEditor("ZoomLevels", Messages.BasicConfigurationPage_7, page); //$NON-NLS-1$
		zoomLevels.fillIntoGrid(page, 3);
		zoomLevels.setValidRange(1, 100);
		zoomLevels.setStringValue(String.valueOf(getConfiguration().getZoomLevel()));
		zoomLevels.setPage(this);
		zoomLevels.setPropertyChangeListener(this);

		final String zoomLevelsTip = Messages.BasicConfigurationPage_7;

		zoomLevels.getLabelControl(page).setToolTipText(zoomLevelsTip);
		zoomLevels.getTextControl(page).setToolTipText(zoomLevelsTip);
		zoomLevels.getTextControl(page).addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setMessage(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setMessage(zoomLevelsTip, INFORMATION);
			}
		});

		// attribution text
		attributionText = new StringFieldEditor("AttributionText",
				Messages.BasicConfigurationPage_8, page);

		attributionText.fillIntoGrid(page, 3);
		attributionText.setPage(this);
		if (getConfiguration().getAttributionText() != null)
			attributionText.setStringValue(String.valueOf(getConfiguration().getAttributionText()));
		else
			attributionText.setStringValue("");

		final String attributionTip = Messages.BasicConfigurationPage_8;
		attributionText.getLabelControl(page).setToolTipText(attributionTip);
		attributionText.getTextControl(page).setToolTipText(attributionTip);

		attributionText.getTextControl(page).addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setMessage(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setMessage(attributionTip, INFORMATION);
			}
		});

	}

	private void update() {
		setPageComplete(zoomLevels.isValid());
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.IS_VALID)) {
			update();
		}
	}

}
