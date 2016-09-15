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

package eu.esdihumboldt.hale.io.json.ui;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyCondition;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyOrChildrenTypeCondition;
import eu.esdihumboldt.hale.common.align.model.condition.impl.GeometryCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.json.GeoJSONConfig;
import eu.esdihumboldt.hale.io.json.GeoJSONInstanceWriter;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.common.PropertyEntitySelector;
import eu.esdihumboldt.hale.ui.geometry.service.GeometrySchemaService;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.util.components.DynamicScrolledComposite;

/**
 * Configuration page for configuring a SpatialDataSet.
 * 
 * @author Kai Schwierczek
 */
public class GeoJSONConfigurationPage
		extends AbstractConfigurationPage<GeoJSONInstanceWriter, IOWizard<GeoJSONInstanceWriter>> {

	private final GeoJSONConfig config = new GeoJSONConfig();

	/**
	 * Default constructor
	 */
	public GeoJSONConfigurationPage() {
		super("GeoJSON.geometry-config");

		setTitle("GeoJSON geometry configuration");
		setDescription("Please select the geometries to use for the GeoJSON export");
		setPageComplete(true);
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(GeoJSONInstanceWriter provider) {
		provider.setParameter(GeoJSONInstanceWriter.PARAM_GEOMETRY_CONFIG,
				new ComplexValue(config));
		return true;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(final Composite page) {
		page.setLayout(new GridLayout(1, false));
		Label explanation = new Label(page, SWT.NONE);
		explanation.setText(
				"If a geometry is set to \"none\", instances will still be included as GeoJSON features,\nbut without default geometries.");
		final DynamicScrolledComposite sc = new DynamicScrolledComposite(page, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite parent = new Composite(sc, SWT.NONE);
		sc.setExpandHorizontal(true);

		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).spacing(6, 12)
				.applyTo(parent);

		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		GeometrySchemaService gss = PlatformUI.getWorkbench()
				.getService(GeometrySchemaService.class);

		Set<TypeDefinition> types = is.getInstanceTypes(DataSet.TRANSFORMED);

		for (final TypeDefinition type : types) {
			Label label = new Label(parent, SWT.NONE);
			label.setText(type.getDisplayName() + ":");

			PropertyCondition condition = new PropertyOrChildrenTypeCondition(
					new GeometryCondition());
			PropertyParameterDefinition param = new PropertyParameter("", 0, 1, "Geometry", null,
					Collections.singletonList(condition), false);
			PropertyEntitySelector selector = new PropertyEntitySelector(SchemaSpaceID.TARGET,
					param, parent, new TypeEntityDefinition(type, SchemaSpaceID.TARGET, null));
			selector.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					if (!event.getSelection().isEmpty()
							&& event.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						PropertyEntityDefinition property = (PropertyEntityDefinition) selection
								.getFirstElement();
						config.addDefaultGeometry(type, property);
					}
					else {
						config.addDefaultGeometry(type, null);
					}
				}
			});
			selector.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			// initial selection
			List<QName> path = gss.getDefaultGeometry(type);
			if (path != null) {
				EntityDefinition entityDef = new TypeEntityDefinition(type, SchemaSpaceID.TARGET,
						null);
				for (QName child : path)
					entityDef = AlignmentUtil.getChild(entityDef, child);
				selector.setSelection(new StructuredSelection(entityDef));
			}
		}

		sc.setContent(parent);
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// do nothing
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#loadPreSelection(eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration)
	 */
	@Override
	public void loadPreSelection(IOConfiguration conf) {
		// XXX geojsonconfig does not support serialization -> cannot be
		// restored here.
	}
}
