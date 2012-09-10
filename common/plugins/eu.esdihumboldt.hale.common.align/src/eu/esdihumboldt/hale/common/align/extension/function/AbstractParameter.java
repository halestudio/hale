/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.extension.function;

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Abstract definition of a parameter based on an {@link IConfigurationElement}
 * 
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractParameter {

	/**
	 * Value for {@link #maxOccurrence} that represents an unbounded maximum
	 * occurrence
	 */
	public static final int UNBOUNDED = -1;

	private final String name;
	private final int minOccurrence;
	private final int maxOccurrence;

	private final String label;
	private final String description;

	/**
	 * Create a parameter definition
	 * 
	 * @param conf the configuration element
	 */
	public AbstractParameter(IConfigurationElement conf) {
		super();

		this.name = conf.getAttribute("name");
		this.label = conf.getAttribute("label");
		this.description = conf.getAttribute("description");

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
	 * @return the parameter name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the minimum occurrence of the parameter
	 */
	public final int getMinOccurrence() {
		return minOccurrence;
	}

	/**
	 * @return the maximum occurrence of the parameter
	 */
	public final int getMaxOccurrence() {
		return maxOccurrence;
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
		AbstractParameter other = (AbstractParameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
