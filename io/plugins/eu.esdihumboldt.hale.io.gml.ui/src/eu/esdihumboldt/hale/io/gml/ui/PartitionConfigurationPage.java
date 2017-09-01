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

package eu.esdihumboldt.hale.io.gml.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for GML file partitioning.
 * 
 * @author Simon Templer
 * @author Florian Esser
 */
@SuppressWarnings("restriction")
public class PartitionConfigurationPage
		extends AbstractConfigurationPage<StreamGmlWriter, IOWizard<StreamGmlWriter>> {

	private Button activatePartitioning;
	private Spinner instances;

	/**
	 * Default constructor.
	 */
	public PartitionConfigurationPage() {
		super("gmlPartition");

		setTitle("XML/GML settings");
		setDescription("Settings for splitting to multiple output files");
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
	public boolean updateConfiguration(StreamGmlWriter provider) {
		int threshold = activatePartitioning.getSelection() ? instances.getSelection()
				: StreamGmlWriter.NO_PARTITIONING;
		provider.setParameter(StreamGmlWriter.PARAM_INSTANCES_THRESHOLD, Value.of(threshold));
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);

		Group part = new Group(page, SWT.NONE);
		part.setLayout(new GridLayout(3, false));
		part.setText("Partitioning");
		groupData.applyTo(part);

		activatePartitioning = new Button(part, SWT.CHECK);
		activatePartitioning.setSelection(false);
		activatePartitioning.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});

		instances = new Spinner(part, SWT.BORDER);
		instances.setMinimum(1);
		instances.setMaximum(100000);
		instances.setSelection(1);
		instances.setIncrement(100);
		instances.setPageIncrement(1000);

		Label ext = new Label(part, SWT.NONE);
		ext.setText("instances per file");

		Label desc = new Label(part, SWT.WRAP);
		GridDataFactory.swtDefaults().hint(400, SWT.DEFAULT).align(SWT.FILL, SWT.BEGINNING)
				.span(3, 1).grab(true, false).applyTo(desc);
		desc.setText(
				"Number of instances that will be tried to fit into a single file. This number will only be exceeded if there are more objects that are all interconnected (e.g. a network).");

		update();
		setPageComplete(true);
	}

	private void update() {
		instances.setEnabled(activatePartitioning.getSelection());
	}

}
