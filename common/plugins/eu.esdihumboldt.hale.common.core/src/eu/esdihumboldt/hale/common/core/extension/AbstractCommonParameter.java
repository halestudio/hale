/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.extension;

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Class used as a common parameter representation
 * 
 * @author Yasmina Kammeyer
 */
@Immutable
public class AbstractCommonParameter {

	private final String name;
	private final String label;
	private final String description;

	/**
	 * Create a parameter definition
	 * 
	 * @param conf the configuration element
	 */
	public AbstractCommonParameter(IConfigurationElement conf) {
		super();

		this.name = conf.getAttribute("name");
		this.label = conf.getAttribute("label");
		this.description = conf.getAttribute("description");

	}

	/**
	 * Create a parameter definition.
	 * 
	 * @param name the parameter name
	 * @param label human readable label
	 * @param description human readable description
	 */
	public AbstractCommonParameter(String name, String label, String description) {
		super();
		this.name = name;
		this.label = label;
		this.description = description;
	}

	/**
	 * @return the parameter name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Get the display name for the parameter. If present the parameter label
	 * will be used, otherwise the parameter name is returned. In case the
	 * parameter name is <code>null</code> an empty string is returned.
	 * 
	 * @return the parameter display name
	 */
	public String getDisplayName() {
		String displayName = label;
		if (displayName == null) {
			displayName = name;
		}
		if (displayName == null) {
			displayName = "";
		}
		return displayName;
	}

	/**
	 * Get the parameter description
	 * 
	 * @return the description, may be <code>null</code>
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AbstractCommonParameter other = (AbstractCommonParameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}
}
