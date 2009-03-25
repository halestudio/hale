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
 * This class represents <xs:group name="transformation">.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Transformation {
	
	private List<TransformationType> transf;
	
	private List<TransformationType> service;
	
	/**
	 * <xs:element name="transfPipe" type="omwg:TransfPipeType" minOccurs="0" />
	 */
	private List<Transformation> pipes;

}
