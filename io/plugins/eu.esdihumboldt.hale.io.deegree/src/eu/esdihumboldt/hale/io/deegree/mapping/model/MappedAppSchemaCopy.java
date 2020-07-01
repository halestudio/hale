/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.deegree.mapping.model;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.deegree.feature.persistence.sql.FeatureTypeMapping;
import org.deegree.feature.persistence.sql.MappedAppSchema;
import org.deegree.feature.types.FeatureType;

/**
 * Copy class for {@link MappedAppSchema}.
 * 
 * @author Simon Templer
 */
public class MappedAppSchemaCopy extends MappedAppSchema {

	private boolean includeFeatureCollections = false;
	private boolean includeAbstractTypes = false;

	/**
	 * Creates a copy of a {@link MappedAppSchema}.
	 * 
	 * @param ms the original mapped schema
	 */
	public MappedAppSchemaCopy(MappedAppSchema ms) {
		super(ms.getFeatureTypes(), ms.getFtToSuperFt(), ms.getNamespaceBindings(),
				ms.getGMLSchema(),
				ms.getFtMappings().values()
						.toArray(new FeatureTypeMapping[ms.getFtMappings().values().size()]),
				ms.getBBoxMapping(), ms.getBlobMapping(), ms.getGeometryParams(), true,
				ms.getRelationalModel(), ms.getGmlObjectTypes(), ms.getGeometryToSuperType());
	}

	/**
	 * Creates a copy of a {@link MappedAppSchema} where all feature type
	 * mappings can be adapted.
	 * 
	 * @param ms the original mapped schema
	 * @param adaptMappings the function to adapt the mappings
	 */
	public MappedAppSchemaCopy(MappedAppSchema ms,
			Function<Stream<? extends FeatureTypeMapping>, Stream<? extends FeatureTypeMapping>> adaptMappings) {
		super(ms.getFeatureTypes(), ms.getFtToSuperFt(), ms.getNamespaceBindings(),
				ms.getGMLSchema(),
				adaptMappings.apply(ms.getFtMappings().values().stream())
						.toArray(i -> new FeatureTypeMapping[i]),
				ms.getBBoxMapping(), ms.getBlobMapping(), ms.getGeometryParams(), true,
				ms.getRelationalModel(), ms.getGmlObjectTypes(), ms.getGeometryToSuperType());
	}

	/**
	 * Creates a copy of a {@link MappedAppSchema} where all feature type
	 * mappings can be adapted.
	 * 
	 * @param ms the original mapped schema
	 * @param adaptMappings the function to adapt the mappings
	 * @param includeFeatureCollections whether to include feature collections
	 *            in the mapping
	 * @param includeAbstracts whether to include abstract types in the mapping
	 */
	public MappedAppSchemaCopy(MappedAppSchema ms,
			Function<Stream<? extends FeatureTypeMapping>, Stream<? extends FeatureTypeMapping>> adaptMappings,
			boolean includeFeatureCollections, boolean includeAbstracts) {
		this(ms, adaptMappings);

		this.includeFeatureCollections = includeFeatureCollections;
		this.includeAbstractTypes = includeAbstracts;
	}

	@Override
	public List<FeatureType> getFeatureTypes(String namespace, boolean includeCollections,
			boolean includeAbstracts) {
		return super.getFeatureTypes(namespace, this.includeFeatureCollections,
				this.includeAbstractTypes);
	}

}
