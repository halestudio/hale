/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.goml.omwg;

import java.util.List;

/**
 * This class represents the <xs:group name="propCond">.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PropertyCondition {
	
	/**
	 * <xs:element ref="omwg:domainRestriction" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<FeatureClass> domainRestriction;
	
	/**
	 * <xs:element ref="omwg:valueCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<Restriction> valueCondition;
	
	/**
	 * TODO: use actual geometry classes from GeoAPI instead of String.
	 * 
	 * <xs:element ref="omwg:typeCondition" minOccurs="0" maxOccurs="unbounded" />
	 */
	private List<String> typeCondition;
}
