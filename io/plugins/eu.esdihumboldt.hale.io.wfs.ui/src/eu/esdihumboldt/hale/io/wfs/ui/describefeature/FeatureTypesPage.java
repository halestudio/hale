/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.wfs.ui.describefeature;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.io.wfs.ui.capabilities.HasCapabilities;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage;

/**
 * Page for specifying the capabilities URL (and loading the capabilities).
 * 
 * @author Simon Templer
 * @param <T> the configuration object type
 */
public abstract class FeatureTypesPage<T extends HasCapabilities> extends
		ConfigurationWizardPage<T> {

	/**
	 * Constructor
	 * 
	 * @param wizard the parent wizard
	 */
	public FeatureTypesPage(ConfigurationWizard<? extends T> wizard) {
		super(wizard, "wfsFeatureTypes");
		setTitle("WFS Feature types");
		setMessage("Optionally select specific feature types");
	}

	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(2, false));

		// FIXME

		setControl(page);

		update();
	}

	@Override
	public boolean updateConfiguration(T configuration) {
		// FIXME

		return false;
	}

	private void update() {
		// FIXME
//		setPageComplete(xxx);
	}

}
