/*
 * Copyright (c) 2021 wetransform GmbH
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

package eu.esdihumboldt.hale.io.geopackage.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.io.geopackage.GeopackageInstanceWriter;
import eu.esdihumboldt.hale.io.geopackage.GeopackageSpatialIndexType;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;

/**
 * Configuration page for {@link GeopackageInstanceWriter} settings.
 * 
 * @author Florian Esser
 */
public class GeopackageWriterSettingsPage extends
		AbstractConfigurationPage<GeopackageInstanceWriter, IOWizard<GeopackageInstanceWriter>> {

	private ComboViewer spatialIndexType;
	private Button createEmptyTables;

	/**
	 * Default constructor
	 */
	public GeopackageWriterSettingsPage() {
		super("gpkg.settings");

		setTitle("GeoPackage Writer settings");
		setDescription("Settings for the GeoPackage writer");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		if (firstShow) {
			spatialIndexType
					.setSelection(new StructuredSelection(GeopackageSpatialIndexType.RTREE));
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// do nothing

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(GeopackageInstanceWriter provider) {
		setMessage("Settings for the GeoPackage writer");

		GeopackageSpatialIndexType indexType = GeopackageSpatialIndexType.RTREE;
		ISelection order = spatialIndexType.getSelection();
		if (!order.isEmpty() && order instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) spatialIndexType.getSelection())
					.getFirstElement();
			if (selected instanceof GeopackageSpatialIndexType) {
				indexType = (GeopackageSpatialIndexType) selected;
			}
		}
		provider.setSpatialIndexType(indexType.getParameterValue());

		provider.setCreateEmptyTables(createEmptyTables.getSelection());

		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);

		Group tableGroup = new Group(page, SWT.NONE);
		tableGroup.setText("Table generation settings");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(tableGroup);
		groupData.applyTo(tableGroup);
		createEmptyTables = new Button(tableGroup, SWT.CHECK);

		Label createEmptyTablesLabel = new Label(tableGroup, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(createEmptyTablesLabel);
		createEmptyTablesLabel.setText("Also create tables for types that have no instances");

		Group spatialIndexGroup = new Group(page, SWT.NONE);
		spatialIndexGroup.setText("Spatial index");
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(spatialIndexGroup);
		groupData.applyTo(spatialIndexGroup);

		Label spatialIndexTypeLabel = new Label(spatialIndexGroup, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(spatialIndexTypeLabel);
		spatialIndexTypeLabel.setText("Type of spatial index to create:");

		spatialIndexType = new ComboViewer(spatialIndexGroup);
		// TODO Replace by extension-based approach where index types are
		// provided via an extension point, similar to the way
		// InterpolationAlgorithm is handled
		spatialIndexType.setContentProvider(EnumContentProvider.getInstance());
		spatialIndexType.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof GeopackageSpatialIndexType) {
					return ((GeopackageSpatialIndexType) element).getDescription();
				}
				return super.getText(element);
			}
		});
		spatialIndexType.setInput(GeopackageSpatialIndexType.class);
	}

}
