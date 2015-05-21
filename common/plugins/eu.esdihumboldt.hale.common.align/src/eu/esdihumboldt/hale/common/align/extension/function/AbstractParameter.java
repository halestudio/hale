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

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.IConfigurationElement;

import eu.esdihumboldt.hale.common.core.parameter.AbstractCommonParameter;

/**
 * Abstract definition of a parameter based on an {@link IConfigurationElement}
 * 
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractParameter extends AbstractCommonParameter implements
		ParameterDefinition {

	private final int minOccurrence;
	private final int maxOccurrence;

	/**
	 * Create a parameter definition
	 * 
	 * @param conf the configuration element
	 */
	public AbstractParameter(IConfigurationElement conf) {
		super(conf);

		String minOccurrence = conf.getAttribute("minOccurrence");
		String maxOccurrence = conf.getAttribute("maxOccurrence");

		int min;
		try {
			min = Integer.parseInt(minOccurrence);
		} catch (Throwable e) {
			min = 0; // default
		}

		int max;
		if (maxOccurrence.equalsIgnoreCase("n") || maxOccurrence.equals("*")) {
			// allow 'n' and '*' specifying unbounded max occurrence
			max = UNBOUNDED;
		}
		else {
			try {
				max = Integer.parseInt(maxOccurrence);
			} catch (Throwable e) {
				max = 1; // default
			}
		}

		this.minOccurrence = min;
		this.maxOccurrence = max;
	}

	/**
	 * Create a parameter definition.
	 * 
	 * @param name the parameter name
	 * @param minOccurrence min occurrences
	 * @param maxOccurrence max occurrences
	 * @param label human readable label
	 * @param description human readable description
	 */
	public AbstractParameter(String name, int minOccurrence, int maxOccurrence, String label,
			String description) {
		super(name, label, description);
		this.minOccurrence = minOccurrence;
		this.maxOccurrence = maxOccurrence;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition#getMinOccurrence()
	 */
	@Override
	public final int getMinOccurrence() {
		return minOccurrence;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition#getMaxOccurrence()
	 */
	@Override
	public final int getMaxOccurrence() {
		return maxOccurrence;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractParameter other = (AbstractParameter) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		}
		else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

}
