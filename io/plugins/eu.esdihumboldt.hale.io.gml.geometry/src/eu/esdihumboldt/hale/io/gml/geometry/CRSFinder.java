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

package eu.esdihumboldt.hale.io.gml.geometry;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.helper.InstanceTraversalCallback;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * Finds a CRS definition in a GML instance. The first valid definition found
 * will be stored, traversal in this case is aborted.
 * @author Simon Templer
 */
public class CRSFinder implements InstanceTraversalCallback {
	
	/**
	 * A CRS definition if found
	 */
	private CRSDefinition definition;

	/**
	 * @see InstanceTraversalCallback#visit(Instance, QName)
	 */
	@Override
	public boolean visit(Instance instance, QName name) {
		return true;
	}

	/**
	 * @see InstanceTraversalCallback#visit(Group, QName)
	 */
	@Override
	public boolean visit(Group group, QName name) {
		return true;
	}

	/**
	 * @see InstanceTraversalCallback#visit(Object, QName)
	 */
	@Override
	public boolean visit(Object value, QName name) {
		if (value != null && name != null && name.getLocalPart().equals("srsName")) {
			String candidate = value.toString();
			
			// EPSG:(:)xxx style codes
			if (checkCode(candidate, "EPSG:")) {
				// if definition is set, abort the traversal
				return false;
			}
			
			// urn:ogc:def:crs:EPSG:(:)xxx style code
			if (checkCode(candidate, "urn:ogc:def:crs:EPSG:")) {
				// if definition is set, abort the traversal
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Check a candidate for a CRS code. Set {@link #definition} to the
	 * corresponding {@link CRSDefinition} if it represents a CRS.
	 * @param candidate the CRS code candidate
	 * @param prefix the expected code prefix
	 * @return if {@link #definition} was set
	 */
	private boolean checkCode(String candidate, String prefix) {
		if (candidate.length() > prefix.length()) {
			String authPart = candidate.substring(0, prefix.length());
			String codePart = candidate.substring(prefix.length());
			
			try {
				// remove leading :'s
				while (codePart.startsWith(":")) {
					codePart = codePart.substring(1);
				}
				
				// check if codePart represents an integer
				Integer.parseInt(codePart);
				
				if (authPart.equalsIgnoreCase(prefix)) {
					definition = new CodeDefinition("EPSG:" + codePart, null);
					//XXX check if valid through getCRS()?
					return true;
				}
			} catch (NumberFormatException e) {
				// invalid
			}
		}
		
		return false;
	}

	/**
	 * Get the CRS definition found during traversal.
	 * @return the definition the CRS definition or <code>null</code>
	 */
	public CRSDefinition getDefinition() {
		return definition;
	}

	/**
	 * Reset for reuse
	 */
	public void reset() {
		definition = null;
	}

}
