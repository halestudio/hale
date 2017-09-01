/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import eu.esdihumboldt.hale.common.schema.Classification;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;

/**
 * Constraint that determines if a type is mapping relevant based on if it is a
 * GML feature type.
 * 
 * @author Simon Templer
 */
public class MappingRelevantIfFeatureType extends MappingRelevantFlag {

	private final XmlTypeDefinition type;

	/**
	 * Create a mapping constraint that checks if a type is a GML feature type.
	 * 
	 * @param type the type defintion
	 */
	public MappingRelevantIfFeatureType(XmlTypeDefinition type) {
		super();

		this.type = type;
	}

	/**
	 * @see AbstractFlagConstraint#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		Classification clazz = Classification.getClassification(type);

		if (clazz != null) {
			switch (clazz) {
			case CONCRETE_FT:
				// only accept feature types
				return true;
			default:
				return false;
			}
		}

		return false;
	}

}
