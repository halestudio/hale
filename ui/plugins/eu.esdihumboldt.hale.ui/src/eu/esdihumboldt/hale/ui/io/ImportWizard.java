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

package eu.esdihumboldt.hale.ui.io;

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;

/**
 * Abstract import wizard
 * 
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class ImportWizard<P extends ImportProvider> extends IOWizard<P> {

	private ImportSelectSourcePage<P, ? extends ImportWizard<P>> selectSourcePage;

	/**
	 * @see IOWizard#IOWizard(Class)
	 */
	public ImportWizard(Class<P> providerType) {
		super(providerType);

		setWindowTitle("Import wizard");

		setDefaultPageImageDescriptor(HALEUIPlugin.imageDescriptorFromPlugin(
				HALEUIPlugin.PLUGIN_ID, "/icons/banner/import_wiz.png"));
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		addPage(selectSourcePage = new ImportSelectSourcePage<P, ImportWizard<P>>());
	}

	/**
	 * @return the selectSourcePage
	 */
	public ImportSelectSourcePage<P, ? extends ImportWizard<P>> getSelectSourcePage() {
		return selectSourcePage;
	}

}
