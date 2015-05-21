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

package eu.esdihumboldt.hale.common.align.extension.function.custom.impl;

import java.util.List;

import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Custom property function.
 * 
 * @author Simon Templer
 */
public class DefaultCustomPropertyFunction implements CustomPropertyFunction {

	private DefaultCustomPropertyFunctionEntity target;
	private List<DefaultCustomPropertyFunctionEntity> sources;

	private String functionType;
	private Value functionDefinition;

	/**
	 * @return the functionType
	 */
	public String getFunctionType() {
		return functionType;
	}

	/**
	 * @param functionType the functionType to set
	 */
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	/**
	 * @return the functionDefinition
	 */
	public Value getFunctionDefinition() {
		return functionDefinition;
	}

	/**
	 * @param functionDefinition the functionDefinition to set
	 */
	public void setFunctionDefinition(Value functionDefinition) {
		this.functionDefinition = functionDefinition;
	}

	/**
	 * @return the target
	 */
	public DefaultCustomPropertyFunctionEntity getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(DefaultCustomPropertyFunctionEntity target) {
		this.target = target;
	}

	/**
	 * @return the sources
	 */
	public List<DefaultCustomPropertyFunctionEntity> getSources() {
		return sources;
	}

	/**
	 * @param sources the sources to set
	 */
	public void setSources(List<DefaultCustomPropertyFunctionEntity> sources) {
		this.sources = sources;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.custom.CustomFunction#getDescriptor()
	 */
	@Override
	public Function getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.custom.CustomFunction#getTransformation()
	 */
	@Override
	public PropertyTransformation<?> getTransformation() {
		// TODO Auto-generated method stub
		return null;
	}

}
