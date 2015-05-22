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
 * Property function
 * 
 * @author Simon Templer
 */
@Immutable
public final class PropertyFunction extends AbstractFunction<PropertyParameterDefinition> implements
		PropertyFunctionDefinition {

	private final Set<PropertyParameter> source;
	private final Set<PropertyParameter> target;

	/**
	 * @see AbstractFunction#AbstractFunction(IConfigurationElement)
	 */
	public PropertyFunction(IConfigurationElement conf) {
		super(conf);

		// populate source and target properties
		source = new LinkedHashSet<PropertyParameter>();
		addProperties(source, conf.getChildren("sourceProperties"));

		target = new LinkedHashSet<PropertyParameter>();
		addProperties(target, conf.getChildren("targetProperties"));
	}

	private static void addProperties(Set<PropertyParameter> collector,
			IConfigurationElement[] propertiesElements) {
		if (propertiesElements != null) {
			for (IConfigurationElement propertiesElement : propertiesElements) {
				IConfigurationElement[] properties = propertiesElement.getChildren("property");
				if (properties != null) {
					for (IConfigurationElement property : properties) {
						collector.add(new PropertyParameter(property));
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
	public Set<PropertyParameter> getSource() {
		return Collections.unmodifiableSet(source);
	}

	/**
	 * Get the target properties
	 * 
	 * @return the target properties
	 */
	@Override
	public Set<PropertyParameter> getTarget() {
		return Collections.unmodifiableSet(target);
	}

	/**
	 * @see AbstractFunction#getIconURL()
	 */
	@Override
	public URL getIconURL() {
		URL icon = super.getIconURL();
		if (icon == null) {
			if (isAugmentation()) {
				icon = getClass().getResource("/icons/augmentation.gif");
			}
			else {
				icon = getClass().getResource("/icons/propertyFunction.gif");
			}
		}
		return icon;
	}

}
