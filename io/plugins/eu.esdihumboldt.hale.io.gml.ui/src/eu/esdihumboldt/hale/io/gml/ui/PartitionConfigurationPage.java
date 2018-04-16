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

import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
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
	private ComboViewer partitionMode;
	private Button activatePartitioningByFeatureType;

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
		if (activatePartitioning.getSelection()) {
			provider.setParameter(StreamGmlWriter.PARAM_PARTITION_BY_FEATURE_TYPE, Value.of(false));

			int threshold = instances.getSelection();
			provider.setParameter(StreamGmlWriter.PARAM_INSTANCES_THRESHOLD, Value.of(threshold));
			applyPartitionMode(partitionMode, provider);
		}
		else if (activatePartitioningByFeatureType.getSelection()) {
			provider.setParameter(StreamGmlWriter.PARAM_INSTANCES_THRESHOLD,
					Value.of(StreamGmlWriter.NO_PARTITIONING));
			provider.setParameter(StreamGmlWriter.PARAM_PARTITION_BY_FEATURE_TYPE, Value.of(true));
		}

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

		// activation and threshold
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
		instances.setSelection(1000);
		instances.setIncrement(100);
		instances.setPageIncrement(1000);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(instances);

		Label ext = new Label(part, SWT.NONE);
		ext.setText("instances per file");

		// threshold description
		Label desc = new Label(part, SWT.WRAP);
		GridDataFactory.swtDefaults().hint(400, SWT.DEFAULT).align(SWT.FILL, SWT.BEGINNING)
				.span(3, 1).grab(true, false).applyTo(desc);
		desc.setText(
				"Number of instances that will be tried to fit into a single file. This number may only be exceeded if the partitioning mode does not allow separating a larger group of instances.");

		// partitioning mode
		Label mode = new Label(part, SWT.NONE);
		mode.setText("Partitioning mode:");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(mode);

		partitionMode = createPartitionModeSelector(part);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.applyTo(partitionMode.getControl());

		Group gml = new Group(page, SWT.NONE);
		gml.setLayout(new GridLayout(3, false));
		gml.setText("Split by feature type");
		groupData.applyTo(gml);

		activatePartitioningByFeatureType = new Button(gml, SWT.CHECK);
		activatePartitioningByFeatureType
				.setText("Create separate output file for every feature type");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).span(3, 1).grab(true, false)
				.applyTo(activatePartitioningByFeatureType);
		activatePartitioningByFeatureType.setSelection(false);
		activatePartitioningByFeatureType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});

		update();
		setPageComplete(true);
	}

	/**
	 * Create an element for selecting the partitioning mode.
	 * 
	 * @param parent the parent composite
	 * 
	 * @return the combo viewer
	 */
	public static ComboViewer createPartitionModeSelector(Composite parent) {
		ComboViewer viewer = new ComboViewer(parent, SWT.READ_ONLY);

		// for now fixed configuration - rather based on IOProviderDescriptor
		// (which is only available later)

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					switch (((String) element)) {
					case StreamGmlWriter.PARTITION_MODE_CUT:
						return "Cut strictly at threshold (may break local references)";
					case StreamGmlWriter.PARTITION_MODE_RELATED:
						return "Keep instances that reference each other together";
					case StreamGmlWriter.PARTITION_MODE_NONE:
						return "No partitioning (create one part)";
					}
				}

				return super.getText(element);
			}

		});
		viewer.setInput(Arrays.asList(StreamGmlWriter.PARTITION_MODE_CUT,
				StreamGmlWriter.PARTITION_MODE_RELATED, StreamGmlWriter.PARTITION_MODE_NONE));
		// set default
		viewer.setSelection(new StructuredSelection(StreamGmlWriter.PARTITION_MODE_RELATED));

		return viewer;
	}

	/**
	 * Apply partition mode configuration to I/O provider.
	 * 
	 * @param viewer the partition mode selector (see
	 *            {@link PartitionConfigurationPage#createPartitionModeSelector(Composite)}
	 *            )
	 * @param provider the I/O provider to configure
	 */
	public static void applyPartitionMode(ComboViewer viewer, IOProvider provider) {
		if (viewer == null || provider == null) {
			return;
		}

		ISelection sel = viewer.getSelection();
		if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
			provider.setParameter(StreamGmlWriter.PARAM_PARTITION_MODE,
					Value.of(((IStructuredSelection) sel).getFirstElement().toString()));
		}
		else {
			// use default
			provider.setParameter(StreamGmlWriter.PARAM_PARTITION_MODE,
					Value.of(StreamGmlWriter.PARTITION_MODE_RELATED));
		}
	}

	private void update() {
		instances.setEnabled(activatePartitioning.getSelection());
		partitionMode.getControl().setEnabled(activatePartitioning.getSelection());
		activatePartitioningByFeatureType.setEnabled(!activatePartitioning.getSelection());
		activatePartitioning.setEnabled(!activatePartitioningByFeatureType.getSelection());
	}

}
