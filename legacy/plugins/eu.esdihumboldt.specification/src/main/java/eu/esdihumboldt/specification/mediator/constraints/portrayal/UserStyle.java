/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator.constraints.portrayal;

import java.util.Set;

/**
 * A UserStyle allows user-defined styling and is semantically equivalent to a
 * WMS named style.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: UserStyle.java,v 1.2 2007-11-06 09:32:37 pitaeva Exp $
 * 
 */
public interface UserStyle {

	/**
	 * 
	 * @return a unique StyleName.
	 * 
	 */
	public String getName();

	/**
	 * 
	 * @return a Title-Element for this style.
	 */
	public String getTitle();

	/**
	 * 
	 * @return an Abstract-Element for this Style.
	 * 
	 */
	public String getAbstract();

	/**
	 * 
	 * @return true, if this Style set als default Style, false, else.
	 */
	public boolean getDefault();

	/**
	 * 
	 * @return a List of FeatureTypeStyle, defined for this UserStyle.
	 */

	public Set<FeatureTypeStyle> getFeatureTypeStyle();
}
