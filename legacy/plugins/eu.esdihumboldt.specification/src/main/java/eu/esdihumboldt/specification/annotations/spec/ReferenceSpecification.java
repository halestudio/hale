/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.annotations.spec;

/**
 * This annotation is used to allow for tracking of elements with respect to an
 * external or internal specification, such as a ISO 191xx norm, an OGC document
 * or something else.
 * 
 * Use the following formatting:
 * <ul>
 * <li>ISO Norms:
 * ISO&lt;NumberofNorm&gt;:&lt;Year&gt;-&lt;SectionIdentifier&gt;. Example:
 * ISO19110:2005-B1.1 would refer to the FC_FeatureCatalogue Element of ISO
 * 19110 in the 2005 edition.</li>
 * <li>OGC documents:
 * OGC&lt;OGCDocumentNumber&gt;:&lt;Version&gt;-&lt;SectionIdentifier&gt;.
 * Example: OGC07-036.9.5 would refer to the Geometry properties of a Feature in
 * GML 3.2.1.</li>
 * </ul>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public @interface ReferenceSpecification {
	/**
	 * @return a String containing the identifier of the Specification that was
	 *         used as reference for element annotated.
	 */
	String value();
}
