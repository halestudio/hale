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

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities;
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSOperation;
import eu.esdihumboldt.hale.io.wfs.ui.capabilities.AbstractWFSCapabilitiesPage;
import eu.esdihumboldt.hale.io.wfs.ui.types.AbstractFeatureTypesPage;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;

/**
 * Wizard for determining a GetFeature URL from WFS capabilities.
 * 
 * @author Simon Templer
 */
public class WFSGetFeatureWizard extends ConfigurationWizard<WFSGetFeatureConfig> {

	private final SchemaSpaceID schemaSpaceID;

	/**
	 * Create a new wizard for
	 * 
	 * @param configuration the configuration object
	 * @param schemaSpaceID the schema space of the associated schema
	 */
	public WFSGetFeatureWizard(WFSGetFeatureConfig configuration, SchemaSpaceID schemaSpaceID) {
		super(configuration);
		setWindowTitle("Determine from Capabilities");
		this.schemaSpaceID = schemaSpaceID;
	}

	@Override
	protected boolean validate(WFSGetFeatureConfig configuration) {
		return configuration.getGetFeatureUri() != null && configuration.getVersion() != null;
	}

	@Override
	public void addPages() {
		super.addPages();

		/**
		 * Page for specifying the WFS capabilities URL.
		 */
		AbstractWFSCapabilitiesPage<WFSGetFeatureConfig> capPage = new AbstractWFSCapabilitiesPage<WFSGetFeatureConfig>(
				this) {

			@Override
			protected boolean updateConfiguration(WFSGetFeatureConfig configuration,
					URL capabilitiesUrl, WFSCapabilities capabilities) {
				if (capabilities != null && capabilities.getGetFeatureOp() != null) {
					WFSOperation op = capabilities.getGetFeatureOp();

					configuration.setGetFeatureUri(URI.create(op.getHttpGetUrl()));
					configuration.setVersion(capabilities.getVersion());
					return true;
				}
				setErrorMessage("Invalid capabilities or WFS does not support GetFeature KVP");
				return false;
			}
		};
		addPage(capPage);

		addPage(new AbstractFeatureTypesPage<WFSGetFeatureConfig>(this, capPage,
				"Please specify the feature types to request") {

			private boolean selectAll = false;

			@Override
			protected void updateState(Set<QName> selected) {
				// at least one type must be specified
				setPageComplete(!selected.isEmpty());
			}

			@Override
			protected Collection<? extends QName> initialSelection(Set<QName> types) {
				// select all by default
				if (selectAll) {
					return types;
				}
				return super.initialSelection(types);
			}

			@Override
			protected Set<QName> filterTypes(Set<QName> types) {
				// by default display only types that are represent as mapping
				// relevant types
				if (schemaSpaceID != null) {
					SchemaService ss = PlatformUI.getWorkbench().getService(SchemaService.class);
					if (ss != null) {
						Set<QName> relevantElements = new HashSet<>();
						SchemaSpace schemas = ss.getSchemas(schemaSpaceID);
						for (TypeDefinition type : schemas.getMappingRelevantTypes()) {
							XmlElements elms = type.getConstraint(XmlElements.class);
							for (XmlElement elm : elms.getElements()) {
								relevantElements.add(elm.getName());
							}
						}

						Set<QName> selection = new HashSet<>(types);
						selection.retainAll(relevantElements);
						// don't filter if we have no match at all
						if (!selection.isEmpty()) {
							selectAll = true;
							return selection;
						}
					}
				}
				selectAll = false;
				return super.filterTypes(types);
			}

			@Override
			protected boolean updateConfiguration(WFSGetFeatureConfig configuration,
					Set<QName> selected) {
				configuration.getTypeNames().clear();
				configuration.getTypeNames().addAll(selected);
				return true;
			}

		});

		// bounding box
		addPage(new BBOXPage(this, capPage));

		// additional params
		addPage(new GetFeatureParamsPage(this));
	}

}
