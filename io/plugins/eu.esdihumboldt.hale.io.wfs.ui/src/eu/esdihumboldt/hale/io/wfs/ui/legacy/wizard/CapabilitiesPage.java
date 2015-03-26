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

package eu.esdihumboldt.hale.io.wfs.ui.legacy.wizard;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.io.wfs.ui.internal.Messages;

/**
 * Page for specifying the capabilities URL
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CapabilitiesPage extends AbstractWfsPage<WfsConfiguration> {

	/**
	 * The location editor
	 */
	private WfsLocationFieldEditor location;

	/**
	 * Constructor
	 * 
	 * @param configuration the WFS configuration
	 */
	public CapabilitiesPage(WfsConfiguration configuration) {
		super(configuration, Messages.CapabilitiesPage_0); //$NON-NLS-1$

		setTitle(Messages.CapabilitiesPage_1); //$NON-NLS-1$
		setMessage(Messages.CapabilitiesPage_2); //$NON-NLS-1$
	}

	/**
	 * @see AbstractWfsPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(2, false));

		location = new WfsLocationFieldEditor("location", Messages.CapabilitiesPage_4, page); //$NON-NLS-1$ //$NON-NLS-2$
		location.setPage(this);
		location.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					update();
				}
			}
		});

		String currentValue = getConfiguration().getCapabilitiesURL();
		if (currentValue != null) {
			location.setValue(currentValue);
		}

		setControl(page);

		update();
	}

	/**
	 * @see AbstractWfsPage#updateConfiguration(WfsConfiguration)
	 */
	@Override
	public boolean updateConfiguration(WfsConfiguration configuration) {
		if (location.isValid()) {
			configuration.setCapabilitiesURL(location.getValue());
			return true;
		}

		return false;
	}

	/**
	 * Update the list of recently used WFSes
	 */
	public void updateRecent() {
		if (location != null) {
			location.updateRecent();
		}
	}

	private void update() {
		setPageComplete(location.isValid());
	}

	/**
	 * Get the capabilities URL currently selected
	 * 
	 * @return the capabilities URL
	 */
	public String getCapabilitiesURL() {
		return location.getValue();
	}

}
