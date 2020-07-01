/*
 * Copyright (c) 2020 wetransform GmbH
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

package eu.esdihumboldt.hale.io.gml.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.gml.writer.XPlanGmlInstanceWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for XPlanGML file partitioning.
 * 
 * @author Florian Esser
 */
@SuppressWarnings("restriction")
public class XPlanGmlWriterSettingsPage
		extends AbstractConfigurationPage<StreamGmlWriter, IOWizard<StreamGmlWriter>> {

	private Button activatePartitioningByPlan;

	/**
	 * Default constructor.
	 */
	public XPlanGmlWriterSettingsPage() {
		super("xPlanGmlPartition");

		setTitle("XPlanGML settings");
		setDescription("Settings for writing XPlanGML files");
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
		if (activatePartitioningByPlan.getSelection()) {
			provider.setParameter(XPlanGmlInstanceWriter.PARAM_PARTITION_BY_PLAN, Value.of(true));
		}

		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);

		Group split = new Group(page, SWT.NONE);
		split.setLayout(new GridLayout(3, false));
		split.setText("Split by plan");
		groupData.applyTo(split);

		activatePartitioningByPlan = new Button(split, SWT.CHECK);
		activatePartitioningByPlan.setText("Create separate output file for every plan element");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).span(3, 1).grab(true, false)
				.applyTo(activatePartitioningByPlan);
		activatePartitioningByPlan.setSelection(false);

		setPageComplete(true);
	}
}
