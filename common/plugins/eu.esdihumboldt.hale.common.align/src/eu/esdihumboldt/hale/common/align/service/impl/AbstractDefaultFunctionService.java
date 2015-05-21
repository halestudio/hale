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

package eu.esdihumboldt.hale.common.align.service.impl;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;
import eu.esdihumboldt.hale.common.align.model.Alignment;

/**
 * TODO Type description
 * 
 * @author simon
 */
public abstract class AbstractDefaultFunctionService extends StaticFunctionService {

	protected abstract Alignment getCurrentAlignment();

	/**
	 * @see eu.esdihumboldt.hale.common.align.service.impl.StaticFunctionService#getFunction(java.lang.String)
	 */
	@Override
	public AbstractFunction<?> getFunction(String id) {
		// TODO Auto-generated method stub
		return super.getFunction(id);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.service.impl.StaticFunctionService#getPropertyFunction(java.lang.String)
	 */
	@Override
	public PropertyFunction getPropertyFunction(String id) {
		// TODO Auto-generated method stub
		return super.getPropertyFunction(id);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.service.impl.StaticFunctionService#getTypeFunction(java.lang.String)
	 */
	@Override
	public TypeFunction getTypeFunction(String id) {
		// TODO Auto-generated method stub
		return super.getTypeFunction(id);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.service.impl.StaticFunctionService#getTypeFunctions()
	 */
	@Override
	public Collection<? extends TypeFunction> getTypeFunctions() {
		// TODO Auto-generated method stub
		return super.getTypeFunctions();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.service.impl.StaticFunctionService#getPropertyFunctions()
	 */
	@Override
	public Collection<? extends PropertyFunction> getPropertyFunctions() {
		// TODO Auto-generated method stub
		return super.getPropertyFunctions();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.service.impl.StaticFunctionService#getTypeFunctions(java.lang.String)
	 */
	@Override
	public Collection<? extends TypeFunction> getTypeFunctions(String categoryId) {
		// TODO Auto-generated method stub
		return super.getTypeFunctions(categoryId);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.service.impl.StaticFunctionService#getPropertyFunctions(java.lang.String)
	 */
	@Override
	public Collection<? extends PropertyFunction> getPropertyFunctions(String categoryId) {
		// TODO Auto-generated method stub
		return super.getPropertyFunctions(categoryId);
	}

}
