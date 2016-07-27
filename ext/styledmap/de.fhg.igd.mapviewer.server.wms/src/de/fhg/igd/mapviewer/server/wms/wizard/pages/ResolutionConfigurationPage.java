/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.server.wms.wizard.pages;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.fhg.igd.mapviewer.server.wms.WMSResolutionConfiguration;
import de.fhg.igd.mapviewer.server.wms.wizard.WMSWizardPage;

/**
 * Wizard page for configuring the resolution for orthophotos.
 * 
 * @author Benedikt Hiemenz
 */
public class ResolutionConfigurationPage extends WMSWizardPage<WMSResolutionConfiguration>
		implements IPropertyChangeListener {

	private IntegerFieldEditor xTileSize;
	private IntegerFieldEditor yTileSize;

	/**
	 * Constructor
	 * 
	 * @param configuration the WMS client configuration
	 */
	public ResolutionConfigurationPage(WMSResolutionConfiguration configuration) {
		super(configuration, Messages.ResolutionConfigurationPage_0); // $NON-NLS-1$

		setTitle(Messages.ResolutionConfigurationPage_0);
		setMessage(Messages.ResolutionConfigurationPage_1);
	}

	@Override
	public boolean updateConfiguration(WMSResolutionConfiguration configuration) {

		if (isPageComplete()) {
			configuration.setxTileSize(xTileSize.getIntValue());
			configuration.setyTileSize(yTileSize.getIntValue());
			return true;
		}

		return false;
	}

	@Override
	public void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		page.setLayout(new GridLayout(3, false));

		// x size
		xTileSize = new IntegerFieldEditor("xSize", Messages.ResolutionConfigurationPage_2, page); //$NON-NLS-1$
		xTileSize.setValidRange(64, 32768);
		xTileSize.setStringValue(String.valueOf(getConfiguration().getxTileSize()));
		xTileSize.setPage(this);
		xTileSize.setPropertyChangeListener(this);
		final String xTileTip = Messages.ResolutionConfigurationPage_3;
		xTileSize.getLabelControl(page).setToolTipText(xTileTip);
		xTileSize.getTextControl(page).setToolTipText(xTileTip);
		xTileSize.getTextControl(page).addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setMessage(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setMessage(xTileTip, INFORMATION);
			}
		});

		// y size
		yTileSize = new IntegerFieldEditor("ySize", Messages.ResolutionConfigurationPage_4, page); //$NON-NLS-1$
		yTileSize.setValidRange(64, 32768);
		yTileSize.setStringValue(String.valueOf(getConfiguration().getyTileSize()));
		yTileSize.setPage(this);
		yTileSize.setPropertyChangeListener(this);
		final String yTileTip = Messages.ResolutionConfigurationPage_5;
		yTileSize.getLabelControl(page).setToolTipText(yTileTip);
		yTileSize.getTextControl(page).setToolTipText(yTileTip);
		yTileSize.getTextControl(page).addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setMessage(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setMessage(yTileTip, INFORMATION);
			}
		});

		setControl(page);

		update();
	}

	private void update() {
		setPageComplete(xTileSize.isValid() && yTileSize.isValid());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.IS_VALID)) {
			update();
		}
	}
}
