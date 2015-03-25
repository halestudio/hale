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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.wfs.ui.getfeature;

import java.awt.BorderLayout;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.server.openstreetmap.OpenStreetMapServer;
import eu.esdihumboldt.hale.ui.util.swing.SwingComposite;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage;

/**
 * TODO Type description
 * 
 * @author sitemple
 */
public class BBOXPage<T> extends ConfigurationWizardPage<T> {

	private BasicMapKit mapKit;

	/**
	 * @param wizard
	 * @param pageName
	 */
	public BBOXPage(ConfigurationWizard<? extends T> wizard) {
		super(wizard, "wfsBBOX");
		setTitle("Query bounding box");
		setDescription("Please set bounding box corners with left click, remove with right click.");

		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage#updateConfiguration(java.lang.Object)
	 */
	@Override
	public boolean updateConfiguration(T configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);

		SwingComposite wrapper = new SwingComposite(page);
		GridDataFactory.fillDefaults().grab(true, true).hint(500, 400).applyTo(wrapper);
		wrapper.getContentPane().setLayout(new BorderLayout());

		// create map kit
		mapKit = new BasicMapKit();
		mapKit.setServer(new OpenStreetMapServer(), true);
//		mapKit.addCustomPainter(mypainter);

		// add map kit
		wrapper.getContentPane().add(mapKit, BorderLayout.CENTER);

		// new MapToolAction(tool, mapKit, true);

		setControl(page);
	}

}
