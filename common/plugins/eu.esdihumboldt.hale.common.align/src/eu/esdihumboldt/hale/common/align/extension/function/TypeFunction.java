/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.extension.function;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Type function
 * 
 * @author Simon Templer
 */
@Immutable
public final class TypeFunction extends AbstractFunction<TypeParameterDefinition> implements
		TypeFunctionDefinition {

	private final Set<TypeParameter> source;
	private final Set<TypeParameter> target;

	/**
	 * @see AbstractFunction#AbstractFunction(IConfigurationElement)
	 */
	public TypeFunction(IConfigurationElement conf) {
		super(conf);

		// populate source and target properties
		source = new LinkedHashSet<TypeParameter>();
		addTypes(source, conf.getChildren("sourceTypes"));

		target = new LinkedHashSet<TypeParameter>();
		addTypes(target, conf.getChildren("targetTypes"));
	}

	private static void addTypes(Set<TypeParameter> collector, IConfigurationElement[] typesElements) {
		if (typesElements != null) {
			for (IConfigurationElement typesElement : typesElements) {
				IConfigurationElement[] types = typesElement.getChildren("type");
				if (types != null) {
					for (IConfigurationElement type : types) {
						collector.add(new TypeParameter(type));
					}
				}
			}
		}
	}

	/**
	 * Get the source properties
	 * 
	 * @return the source properties
	 */
	@Override
	public Set<TypeParameter> getSource() {
		return Collections.unmodifiableSet(source);
	}

	/**
	 * Get the target properties
	 * 
	 * @return the target properties
	 */
	@Override
	public Set<TypeParameter> getTarget() {
		return Collections.unmodifiableSet(target);
	}

	/**
	 * @see AbstractFunction#getIconURL()
	 */
	@Override
	public URL getIconURL() {
		URL icon = super.getIconURL();
		if (icon == null) {
			icon = getClass().getResource("/icons/typeFunction.png");
		}
		return icon;
	}

}
