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

package eu.esdihumboldt.hale.io.appschema.model;

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Holds the feature chaining configuration for a single pair of container /
 * nested target types.
 * 
 * <p>
 * A chain is identified by a chain index, which is unique per Join cell and
 * depends on the types forming the chain. The chain configuration only stores
 * information on the nested target type; the container target type can be
 * inferred from the previous chain, as it is equivalent to the previous chain's
 * nested target type.
 * </p>
 * <p>
 * E.g. in a join involving three types, chain 0 would refer to types 1 (nested)
 * and 0 (container), while chain 1 would refer to types 2 (nested) and either 0
 * or 1 (container), depending on the join configuration.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class ChainConfiguration {

	int chainIndex;
	int prevChainIndex = -1;
	PropertyType jaxbNestedTypeTarget;
	PropertyEntityDefinition nestedTypeTarget;
	String mappingName;

	/**
	 * @return the chain index
	 */
	public int getChainIndex() {
		return chainIndex;
	}

	/**
	 * @param chainIndex the chain index to set
	 */
	public void setChainIndex(int chainIndex) {
		this.chainIndex = chainIndex;
	}

	/**
	 * @return the previous chain index
	 */
	public int getPrevChainIndex() {
		return prevChainIndex;
	}

	/**
	 * @param prevChainIndex the previous chain index
	 */
	public void setPrevChainIndex(int prevChainIndex) {
		this.prevChainIndex = prevChainIndex;
	}

	/**
	 * The {@link PropertyEntityDefinition} of the nested type target, converted
	 * to its JAXB counterpart.
	 * 
	 * <p>
	 * Usually, if this method returns a value, it means that the corresponding
	 * {@link PropertyEntityDefinition} has not been resolved yet.
	 * </p>
	 * 
	 * @return a JAXB {@link PropertyType} instance describing the nested type
	 *         target
	 */
	public PropertyType getJaxbNestedTypeTarget() {
		return jaxbNestedTypeTarget;
	}

	/**
	 * Should be used to set the nested type target's entity definition, when
	 * entity definitions cannot be resolved (e.g. during project loading).
	 * 
	 * <p>
	 * As soon as entity definitions can be resolved,
	 * <code>setJaxbNestedTypeTarget(null)</code> should be invoked to keep
	 * track that entity resolution has been performed and should not be
	 * attempted again.
	 * </p>
	 * 
	 * @param jaxbNestedTypeTarget a JAXB {@link PropertyType} instance
	 *            describing the nested type target
	 */
	public void setJaxbNestedTypeTarget(PropertyType jaxbNestedTypeTarget) {
		this.jaxbNestedTypeTarget = jaxbNestedTypeTarget;
	}

	/**
	 * The nested type target's entity definition.
	 * 
	 * <p>
	 * May return <code>null</code> if entity resolution has not been performed
	 * yet (e.g. during project loading), in which case
	 * {@link #getJaxbNestedTypeTarget()} should be invoked to retrieve the JAXB
	 * equivalent of the entity definition.
	 * </p>
	 * 
	 * @return the nested type target's {@link PropertyEntityDefinition}
	 */
	public PropertyEntityDefinition getNestedTypeTarget() {
		return nestedTypeTarget;
	}

	/**
	 * Should be used to set the nested type target's entity definition, after
	 * entity definitions have been resolved.
	 * 
	 * @param nestedTypeTarget the {@link PropertyEntityDefinition} to set as
	 *            the nested type target's entity definition
	 */
	public void setNestedTypeTarget(PropertyEntityDefinition nestedTypeTarget) {
		this.nestedTypeTarget = nestedTypeTarget;
	}

	/**
	 * Convenience method to return the property type of the nested type target.
	 * 
	 * @return the property type of the nested type target
	 */
	public TypeDefinition getNestedTypeTargetType() {
		if (nestedTypeTarget != null) {
			return nestedTypeTarget.getDefinition().getPropertyType();
		}

		return null;
	}

	/**
	 * The value of the <code>&lt;mappingName&gt;</code> element in the nested
	 * type target's mapping (may be <code>null</code>, in which case no
	 * <code>&lt;mappingName&gt;</code> element will be added to the mapping).
	 * 
	 * @return the mapping name
	 */
	public String getMappingName() {
		return mappingName;
	}

	/**
	 * Sets the value of the <code>&lt;mappingName&gt;</code> element in the
	 * nested type target's mapping (may be <code>null</code>, in which case no
	 * <code>&lt;mappingName&gt;</code> element will be added to the mapping).
	 * 
	 * <p>
	 * Please note that the mapping name should be unique across all configured
	 * mappings.
	 * </p>
	 * 
	 * @param mappingName the mapping name to set
	 */
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

}
