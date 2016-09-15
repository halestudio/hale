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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.fhg.igd.mapviewer.server.tiles.CustomTileMapServerConfiguration;
import de.fhg.igd.mapviewer.server.tiles.wizard.Messages;

/**
 * Basic custom tile map server configuration page
 * 
 * @author Arun
 */
public class CustomTileServerConfigPage extends CustomTileWizardPage
		implements IPropertyChangeListener {

	private Composite page;

	/**
	 * Server Name field
	 */
	private ConfigurationNameFieldEditor name;
	/**
	 * URL pattern field
	 */
	private ConfigurationURLFieldEditor url;

	private boolean checkName = true;

	/**
	 * Constructor
	 * 
	 * @param configuration Custom Tile Map Server configuration
	 */
	public CustomTileServerConfigPage(CustomTileMapServerConfiguration configuration) {
		super(configuration, Messages.BasicConfigurationPage_0);
		setTitle(Messages.BasicConfigurationPage_1);
		setMessage(Messages.BasicConfigurationPage_2);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
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
	 * Create components for basic options, the server name and URL pattern.
	 */
	protected void createComponent() {

		// name
		String serverName = getConfiguration().getName();
		boolean enterName = serverName == null || serverName.isEmpty();

		name = new ConfigurationNameFieldEditor(getConfiguration());
		name.fillIntoGrid(page, 3);
		name.setStringValue(serverName);
		if (!enterName) {
			checkName = false;
			name.setEnabled(false, page);
		}
		name.setLabelText(Messages.BasicConfigurationPage_3);
		name.setPage(this);
		name.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					update();
				}
			}
		});

		final String nameTip = Messages.BasicConfigurationPage_4;
		name.getTextControl(page).addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setMessage(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setMessage(nameTip, INFORMATION);
			}
		});

		// url
		url = new ConfigurationURLFieldEditor();
		url.fillIntoGrid(page, 3);
		url.setEmptyStringAllowed(false);
		url.setStringValue(getConfiguration().getUrlPattern());
		url.setLabelText(Messages.BasicConfigurationPage_5);
		url.getTextControl(page).setToolTipText(Messages.BasicConfigurationPage_5);
		url.setPage(this);
		url.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					update();
				}
			}
		});

		final String urlTip = Messages.BasicConfigurationPage_5;
		url.getTextControl(page).addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setMessage(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setMessage(urlTip, INFORMATION);
			}
		});

		// url information
		Label label = new Label(page, SWT.WRAP);
		label.setText(Messages.BasicConfigurationPage_11);
		label.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 3, 1));
	}

	/**
	 * To update configuration
	 * 
	 * @param configuration {@link CustomTileMapServerConfiguration}
	 * @return true or false based on validity of fields
	 */
	@Override
	public boolean updateConfiguration(CustomTileMapServerConfiguration configuration) {
		if (url.isValid() && (!checkName || name.isValid())) {
			configuration.setUrlPattern(url.getStringValue());
			configuration.setName(name.getStringValue());
			return true;
		}

		return false;
	}

	private void update() {
		setPageComplete(url.isValid() && (!checkName || name.isValid()));
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.IS_VALID)) {
			update();
		}
	}
}
