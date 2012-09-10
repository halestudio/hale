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
package eu.esdihumboldt.specification.annotations.spec;

/**
 * Name: eu.esdihumboldt.annotations.spec / RequiredIn<br/>
 * Purpose: This Annotation can be used to specifiy - for any given operation or
 * type - in which Use Case or Requirement this method or type is required. This
 * allows an easier tracing of requirements into code.<br/>
 * 
 * For Use Cases, use the following syntax: <i>Use Case Identifier.Step</i>
 * Example: <i>UC0001.2</i> If you are referring to an alternative process,
 * follow this syntax: <i>Use Case Identifier.Process Identifier.Step</i>
 * Example: <i>UC0001.AP01.1</i>
 * 
 * If there are multiple sources requiring this operation, you can use this
 * syntax: Example <i>("UC0001.AP01.1, UC0002.2, ...")</i>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public @interface RequiredIn {
	String value();
}
