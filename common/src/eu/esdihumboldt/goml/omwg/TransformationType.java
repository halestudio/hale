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

import java.net.URI;

/**
 * This class represents <xs:complexType name="TransformationType" >.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TransformationType {
	
	/**
	 * FIXME; I did not understand this type.
	 * <xs:group name="pov">
	 *    <xs:choice>
	 *       <xs:group ref="omwg:pathExpr"/>
	 *       <xs:element name="value" type="omwg:valueExprType" />
	 *    </xs:choice>
	 * </xs:group>
	 */
	private String pov;

	
	/**
	 * The identifier of the transformation.
	 * <xs:attribute ref="rdf:resource" use="optional"/>
	 */
	private URI resource;
	
}
