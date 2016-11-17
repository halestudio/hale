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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Objects;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;

/**
 * Function service that includes dynamic content in addition to the statically
 * defined functions.
 * 
 * @author Simon Templer
 */
public abstract class AbstractDefaultFunctionService extends StaticFunctionService
		implements CustomFunctionIdentifiers {

	private static class AlignmentFunctionDescriptor implements PropertyFunctionDefinition {

		private final PropertyFunctionDefinition descriptor;

		public AlignmentFunctionDescriptor(PropertyFunctionDefinition descriptor) {
			this.descriptor = descriptor;
		}

		@Override
		public String getDisplayName() {
			return descriptor.getDisplayName();
		}

		@Override
		public String getDescription() {
			return descriptor.getDescription();
		}

		@Override
		public String getCategoryId() {
			return descriptor.getCategoryId();
		}

		@Override
		public boolean isAugmentation() {
			return descriptor.isAugmentation();
		}

		@Override
		public String getId() {
			return PREFIX_ALIGNMENT_FUNCTION + descriptor.getId();
		}

		@Override
		public Collection<FunctionParameterDefinition> getDefinedParameters() {
			return descriptor.getDefinedParameters();
		}

		@Override
		public FunctionParameterDefinition getParameter(String paramName) {
			return descriptor.getParameter(paramName);
		}

		@Override
		public URL getIconURL() {
			return descriptor.getIconURL();
		}

		@Override
		public String getDefiningBundle() {
			return descriptor.getDefiningBundle();
		}

		@Override
		public URL getHelpURL() {
			return descriptor.getHelpURL();
		}

		@Override
		public CellExplanation getExplanation() {
			return descriptor.getExplanation();
		}

		@Override
		public Set<? extends PropertyParameterDefinition> getSource() {
			return descriptor.getSource();
		}

		@Override
		public Set<? extends PropertyParameterDefinition> getTarget() {
			return descriptor.getTarget();
		}

		@Override
		public Optional<CellMigrator> getCustomMigrator() {
			return descriptor.getCustomMigrator();
		}

	}

	/**
	 * @return the current alignment
	 */
	protected abstract Alignment getCurrentAlignment();

	@Override
	public FunctionDefinition<?> getFunction(String id) {
		FunctionDefinition<?> function = super.getFunction(id);

		if (function == null) {
			return getCustomPropertyFunction(id);
		}
		return function;
	}

	private PropertyFunctionDefinition getCustomPropertyFunction(String id) {
		Alignment al = getCurrentAlignment();
		if (al != null) {
			String localId = id;
			if (localId.startsWith(PREFIX_ALIGNMENT_FUNCTION)) {
				localId = localId.substring(PREFIX_ALIGNMENT_FUNCTION.length());
			}
			CustomPropertyFunction cf = al.getAllCustomPropertyFunctions().get(localId);
			if (cf != null) {
				return new AlignmentFunctionDescriptor(cf.getDescriptor());
			}
		}

		return null;
	}

	@Override
	public PropertyFunctionDefinition getPropertyFunction(String id) {
		PropertyFunctionDefinition function = super.getPropertyFunction(id);

		if (function == null) {
			return getCustomPropertyFunction(id);
		}
		return function;
	}

	@Override
	public TypeFunctionDefinition getTypeFunction(String id) {
		return super.getTypeFunction(id);
	}

	@Override
	public Collection<? extends TypeFunctionDefinition> getTypeFunctions() {
		return super.getTypeFunctions();
	}

	@Override
	public Collection<? extends PropertyFunctionDefinition> getPropertyFunctions() {
		Collection<? extends PropertyFunctionDefinition> functions = super.getPropertyFunctions();

		Alignment al = getCurrentAlignment();
		if (al != null) {
			List<PropertyFunctionDefinition> cfs = new ArrayList<>();
			for (CustomPropertyFunction cf : al.getAllCustomPropertyFunctions().values()) {
				cfs.add(new AlignmentFunctionDescriptor(cf.getDescriptor()));
			}
			cfs.addAll(functions);

			functions = cfs;
		}

		return functions;
	}

	@Override
	public Collection<? extends TypeFunctionDefinition> getTypeFunctions(String categoryId) {
		return super.getTypeFunctions(categoryId);
	}

	@Override
	public Collection<? extends PropertyFunctionDefinition> getPropertyFunctions(
			String categoryId) {
		Collection<? extends PropertyFunctionDefinition> functions = super.getPropertyFunctions(
				categoryId);

		Alignment al = getCurrentAlignment();
		if (al != null) {
			List<PropertyFunctionDefinition> cfs = new ArrayList<>();
			for (CustomPropertyFunction cf : al.getAllCustomPropertyFunctions().values()) {
				PropertyFunctionDefinition descriptor = cf.getDescriptor();
				if (Objects.equal(categoryId, descriptor.getCategoryId())) {
					cfs.add(new AlignmentFunctionDescriptor(descriptor));
				}
			}
			cfs.addAll(functions);

			functions = cfs;
		}

		return functions;
	}

}
