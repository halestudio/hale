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

package eu.esdihumboldt.hale.align.extension.function;

import org.eclipse.core.runtime.IConfigurationElement;

import net.jcip.annotations.Immutable;

/**
 * Abstract definition of a parameter based on an {@link IConfigurationElement}
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
	
	/**
	 * Create a parameter definition
	 * @param conf the configuration element
	 */
	public AbstractParameter(IConfigurationElement conf) {
		super();
		
		this.name = conf.getAttribute("name");
		String minOccurrence = conf.getAttribute("minOccurrence");
		String maxOccurrence = conf.getAttribute("maxOccurrence");
		
		int min;
		try {
			min = Integer.parseInt(minOccurrence);
		} catch (Throwable e) {
			min = 0; // default
		}
		
		int max;
		try {
			max = Integer.parseInt(maxOccurrence);
		} catch (Throwable e) {
			max = 1; // default
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
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
