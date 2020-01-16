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

package eu.esdihumboldt.hale.io.geopackage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ext.impl.PerTypeInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.io.geopackage.internal.GeopackageFeatureCollection;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.attributes.AttributesDao;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.manager.GeoPackageManager;

/**
 * Instance reader for {@link GeoPackage} files.
 * 
 * @author Simon Templer
 */
public class GeopackageInstanceReader extends AbstractInstanceReader {

	private InstanceCollection collection;

	@Override
	public InstanceCollection getInstances() {
		return collection;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Inspecting GeoPackage", ProgressIndicator.UNKNOWN);
		try {
			URI loc = getSource().getLocation();
			File file;
			try {
				file = new File(loc);
			} catch (Exception e) {
				throw new IllegalArgumentException("Only files are supported as data source", e);
			}

			GeoPackage gpkg = GeoPackageManager.open(file, true);

			Map<TypeDefinition, InstanceCollection> collections = new HashMap<>();

			// try to load each feature table
			for (String table : gpkg.getFeatureTables()) {
				TypeDefinition type = findType(table);

				if (type == null) {
					reporter.warn(
							"For feature table {0} no matching schema type could be identified",
							table);
				}
				else {
					FeatureDao features = gpkg.getFeatureDao(table);
					collections.put(type, new GeopackageFeatureCollection(features, type));
				}
			}

			// try to load each attribute table
			for (String table : gpkg.getAttributesTables()) {
				TypeDefinition type = findType(table);

				if (type == null) {
					reporter.warn(
							"For attribute table {0} no matching schema type could be identified",
							table);
				}
				else {
					AttributesDao attributes = gpkg.getAttributesDao(table);
					collections.put(type, new GeopackageFeatureCollection(attributes, type));
				}
			}

			collection = new PerTypeInstanceCollection(collections);
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Error configuring database connection", e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;
	}

	private TypeDefinition findType(String table) {
		QName defTypeName = new QName(GeopackageSchemaBuilder.DEFAULT_NAMESPACE, table);
		TypeDefinition type = getSourceSchema().getType(defTypeName);
		if (type != null && !type.getConstraint(MappingRelevantFlag.class).isEnabled()) {
			type = null; // only mapping relevant types allowed
		}
		if (type == null) {
			// try to find an alternate type (e.g. a type defined in a
			// schema not loaded from GeoPackage)
			for (TypeDefinition candidate : getSourceSchema().getMappingRelevantTypes()) {
				if (table.equals(candidate.getName().getLocalPart())) {
					// local name match
					type = candidate;
					break;
				}
			}
		}
		return type;
	}

	@Override
	protected String getDefaultTypeName() {
		return "GeoPackage";
	}

}
