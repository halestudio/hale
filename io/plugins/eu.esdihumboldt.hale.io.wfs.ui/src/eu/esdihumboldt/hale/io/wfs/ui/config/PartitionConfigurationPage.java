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

package eu.esdihumboldt.hale.io.wfs.ui.config;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.wfs.PartitioningWFSWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for WFS-T request partitioning.
 * 
 * @author Simon Templer
 */
public class PartitionConfigurationPage extends
		AbstractConfigurationPage<PartitioningWFSWriter, IOWizard<PartitioningWFSWriter>> {

	private Spinner instances;

	/**
	 * Default constructor.
	 */
	public PartitionConfigurationPage() {
		super("wfsPartition");

		setTitle("WFS transactions");
		setDescription("WFS-T request settings for multiple transactions");
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
	public boolean updateConfiguration(PartitioningWFSWriter provider) {
		provider.setParameter(PartitioningWFSWriter.PARAM_INSTANCES_THRESHOLD,
				Value.of(instances.getSelection()));
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);

		Group part = new Group(page, SWT.NONE);
		part.setLayout(new GridLayout(2, false));
		part.setText("Partitioning");
		groupData.applyTo(part);

		instances = new Spinner(part, SWT.BORDER);
		instances.setMinimum(1);
		instances.setMaximum(100000);
		instances.setSelection(PartitioningWFSWriter.DEFAULT_INSTANCES_THRESHOLD);
		instances.setIncrement(100);
		instances.setPageIncrement(1000);

		Label ext = new Label(part, SWT.NONE);
		ext.setText("instances per transaction");

		Label desc = new Label(part, SWT.WRAP);
		GridDataFactory.swtDefaults().hint(400, SWT.DEFAULT).align(SWT.FILL, SWT.BEGINNING)
				.span(2, 1).grab(true, false).applyTo(desc);
		desc.setText("Number of instances that will be tried to fit into a single transaction. This number will only be exceeded if there are more objects that are all interconnected (e.g. a network).");

		setPageComplete(true);
	}

}
