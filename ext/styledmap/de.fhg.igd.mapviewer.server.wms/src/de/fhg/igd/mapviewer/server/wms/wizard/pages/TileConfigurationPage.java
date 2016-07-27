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

import de.fhg.igd.mapviewer.server.wms.Messages;
import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;
import de.fhg.igd.mapviewer.server.wms.WMSTileConfiguration;
import de.fhg.igd.mapviewer.server.wms.wizard.WMSWizardPage;

/**
 * Tile configuration page.
 * 
 * @author Simon Templer
 */
public class TileConfigurationPage extends WMSWizardPage<WMSTileConfiguration>
		implements IPropertyChangeListener {

	private IntegerFieldEditor zoomLevels;

	private IntegerFieldEditor minTileSize;

	private IntegerFieldEditor minMapSize;

	/**
	 * Constructor
	 * 
	 * @param configuration the WMS configuration
	 */
	public TileConfigurationPage(WMSTileConfiguration configuration) {
		super(configuration, Messages.TileConfigurationPage_0);

		setTitle(Messages.TileConfigurationPage_1);
		setMessage(Messages.TileConfigurationPage_2);
	}

	/**
	 * @see WMSWizardPage#updateConfiguration(WMSConfiguration)
	 */
	@Override
	public boolean updateConfiguration(WMSTileConfiguration configuration) {
		if (isPageComplete()) {
			configuration.setZoomLevels(zoomLevels.getIntValue());
			configuration.setMinTileSize(minTileSize.getIntValue());
			configuration.setMinMapSize(minMapSize.getIntValue());
			return true;
		}

		return false;
	}

	/**
	 * @see WMSWizardPage#createContent(Composite)
	 */
	@Override
	public void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		page.setLayout(new GridLayout(3, false));

		// zoom levels
		zoomLevels = new IntegerFieldEditor("ZoomLevels", Messages.TileConfigurationPage_4, page); //$NON-NLS-1$
		zoomLevels.setValidRange(1, 100);
		zoomLevels.setStringValue(String.valueOf(getConfiguration().getZoomLevels()));
		zoomLevels.setPage(this);
		zoomLevels.setPropertyChangeListener(this);
		final String zoomLevelsTip = Messages.TileConfigurationPage_3;
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

		// tile size
		minTileSize = new IntegerFieldEditor("TileSize", Messages.TileConfigurationPage_6, page); //$NON-NLS-1$
		minTileSize.setValidRange(64, 1024);
		minTileSize.setStringValue(String.valueOf(getConfiguration().getMinTileSize()));
		minTileSize.setPage(this);
		minTileSize.setPropertyChangeListener(this);
		final String minTileSizeTip = Messages.TileConfigurationPage_5;
		minTileSize.getLabelControl(page).setToolTipText(minTileSizeTip);
		minTileSize.getTextControl(page).setToolTipText(minTileSizeTip);
		minTileSize.getTextControl(page).addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setMessage(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setMessage(minTileSizeTip, INFORMATION);
			}
		});

		// map size
		minMapSize = new IntegerFieldEditor("MapSize", Messages.TileConfigurationPage_8, page); //$NON-NLS-1$
		minMapSize.setValidRange(64, 1024 * 64);
		minMapSize.setStringValue(String.valueOf(getConfiguration().getMinMapSize()));
		minMapSize.setPage(this);
		minMapSize.setPropertyChangeListener(this);
		final String minMapSizeTip = Messages.TileConfigurationPage_7;
		minMapSize.getLabelControl(page).setToolTipText(minMapSizeTip);
		minMapSize.getTextControl(page).setToolTipText(minMapSizeTip);
		minMapSize.getTextControl(page).addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setMessage(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setMessage(minMapSizeTip, INFORMATION);
			}
		});

		setControl(page);

		update();
	}

	private void update() {
		setPageComplete(zoomLevels.isValid() && minTileSize.isValid() && minMapSize.isValid());
	}

	/**
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.IS_VALID)) {
			update();
		}
	}

}
