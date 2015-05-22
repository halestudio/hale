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
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;
import eu.esdihumboldt.hale.common.align.service.FunctionService;

/**
 * Function service implementation using only statically defined functions.
 * 
 * @author Simon Templer
 */
public class StaticFunctionService implements FunctionService {

	@Override
	public FunctionDefinition<?> getFunction(String id) {
		AbstractFunction<?> result = null;

		result = TypeFunctionExtension.getInstance().get(id);

		if (result == null) {
			result = PropertyFunctionExtension.getInstance().get(id);
		}

		return result;
	}

	@Override
	public PropertyFunctionDefinition getPropertyFunction(String id) {
		return PropertyFunctionExtension.getInstance().get(id);
	}

	@Override
	public TypeFunctionDefinition getTypeFunction(String id) {
		return TypeFunctionExtension.getInstance().get(id);
	}

	@Override
	public Collection<? extends TypeFunctionDefinition> getTypeFunctions() {
		return TypeFunctionExtension.getInstance().getElements();
	}

	@Override
	public Collection<? extends PropertyFunctionDefinition> getPropertyFunctions() {
		return PropertyFunctionExtension.getInstance().getElements();
	}

	@Override
	public Collection<? extends TypeFunctionDefinition> getTypeFunctions(String categoryId) {
		return TypeFunctionExtension.getInstance().getFunctions(categoryId);
	}

	@Override
	public Collection<? extends PropertyFunctionDefinition> getPropertyFunctions(String categoryId) {
		return PropertyFunctionExtension.getInstance().getFunctions(categoryId);
	}

}
