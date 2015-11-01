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

package eu.esdihumboldt.hale.io.appschema.writer.internal;

import java.util.Collection;
import java.util.HashSet;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.appschema.model.FeatureChaining;

/**
 * Holds information about the mapping context, i.e. a reference to the mapping
 * wrapper object, a reference to the alignment, a list of the target types
 * relevant to the mapping and the feature chaining configuration.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaMappingContext {

	private Alignment alignment;
	private Collection<? extends TypeDefinition> relevantTargetTypes;
	private FeatureChaining chainingConf;
	private final AppSchemaMappingWrapper mappingWrapper;

	/**
	 * Single argument constructor.
	 * 
	 * @param mappingWrapper the mapping wrapper
	 */
	public AppSchemaMappingContext(AppSchemaMappingWrapper mappingWrapper) {
		this.mappingWrapper = mappingWrapper;
		this.relevantTargetTypes = new HashSet<TypeDefinition>();
	}

	/**
	 * Two arguments constructor.
	 * 
	 * @param mappingWrapper the mapping wrapper
	 * @param alignment the alignment
	 */
	public AppSchemaMappingContext(AppSchemaMappingWrapper mappingWrapper, Alignment alignment) {
		this(mappingWrapper);
		this.alignment = alignment;
	}

	/**
	 * Three arguments constructor.
	 * 
	 * @param mappingWrapper the mapping wrapper
	 * @param alignment the aligment
	 * @param relevantTargetTypes the set of mapping relevant target types
	 */
	public AppSchemaMappingContext(AppSchemaMappingWrapper mappingWrapper, Alignment alignment,
			Collection<? extends TypeDefinition> relevantTargetTypes) {
		this(mappingWrapper, alignment);
		if (this.relevantTargetTypes != null) {
			this.relevantTargetTypes = relevantTargetTypes;
		}
	}

	/**
	 * Four arguments constructor.
	 * 
	 * @param mappingWrapper the mapping wrapper
	 * @param alignment the aligment
	 * @param relevantTargetTypes the set of mapping relevant target types
	 * @param chainingConf the feature chaining configuration
	 */
	public AppSchemaMappingContext(AppSchemaMappingWrapper mappingWrapper, Alignment alignment,
			Collection<? extends TypeDefinition> relevantTargetTypes, FeatureChaining chainingConf) {
		this(mappingWrapper, alignment);
		if (this.relevantTargetTypes != null) {
			this.relevantTargetTypes = relevantTargetTypes;
		}
		this.chainingConf = chainingConf;
	}

	/**
	 * @return the mappingWrapper
	 */
	public AppSchemaMappingWrapper getMappingWrapper() {
		return mappingWrapper;
	}

	/**
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * Return a copy of the collection containing the mapping relevant target
	 * types.
	 * 
	 * @return the set of relevant target types
	 */
	public Collection<? extends TypeDefinition> getRelevantTargetTypes() {
		return new HashSet<TypeDefinition>(relevantTargetTypes);
	}

	/**
	 * @return the feature chaining configuration
	 */
	public FeatureChaining getFeatureChaining() {
		return chainingConf;
	}

}
