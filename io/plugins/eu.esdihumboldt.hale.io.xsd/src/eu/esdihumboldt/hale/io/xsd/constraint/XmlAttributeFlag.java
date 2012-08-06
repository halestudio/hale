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

package eu.esdihumboldt.hale.io.xsd.constraint;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;

/**
 * Flags if a property is represented by a XML attribute, disabled by default
 * @author Simon Templer
 */
@Immutable
@Constraint(mutable = false)
public final class XmlAttributeFlag extends AbstractFlagConstraint implements PropertyConstraint {

	/**
	 * Enabled XML attribute flag
	 */
	public static final XmlAttributeFlag ENABLED = new XmlAttributeFlag(true);
	
	/**
	 * Disabled XML attribute flag
	 */
	public static final XmlAttributeFlag DISABLED = new XmlAttributeFlag(false);
	
	/**
	 * Get the XML attribute flag
	 * 
	 * @param isAttribute if the flag shall be enabled
	 * @return the flag
	 */
	public static XmlAttributeFlag get(boolean isAttribute) {
		return (isAttribute)?(ENABLED):(DISABLED);
	}
	
	/**
	 * Creates a default XML attribute flag, which is disabled. If possible, 
	 * instead of creating an instance, use {@link #get(boolean)}, 
	 * {@link #ENABLED} or {@link #DISABLED}.
	 * 
	 * @see Constraint
	 */
	public XmlAttributeFlag() {
		this(false);
	}
	
	/**
	 * @see AbstractFlagConstraint#AbstractFlagConstraint(boolean)
	 */
	private XmlAttributeFlag(boolean enabled) {
		super(enabled);
	}
	
}
