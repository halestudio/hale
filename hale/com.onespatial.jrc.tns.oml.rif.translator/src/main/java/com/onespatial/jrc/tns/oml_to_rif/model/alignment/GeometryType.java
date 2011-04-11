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
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

/**
 * Different kinds of geometry types.
 * 
 * @author Susanne Reinwarth / TU Dresden
 */
public enum GeometryType {
	
	/**
	 * A surface.
	 */
	SURFACE,
	/**
	 * A multi-surface (consisting of one or more surfaces).
	 */ 	
	MULTI_SURFACE;
	
	/**
     * RIF IRI for the centroid function for a surface.
     */
	public static final String SURFACE_CENTROID_IRI = "http://"
		+ "www.opengeospatial.org/standards/sfa/ISurface#Centroid";
	
	/**
     * RIF IRI for the centroid function for a multi-surface.
     */
	public static final String MULTI_SURFACE_CENTROID_IRI = "http://"
		+ "www.opengeospatial.org/standards/sfa/IMultiSurface#Centroid";
	
	/**
	 * @return String
	 */
	public String getRifCentroidFunctionIri()
	{
		if (this.equals(SURFACE))
		{
			return SURFACE_CENTROID_IRI;
		}
		else if (this.equals(MULTI_SURFACE))
		{
			return MULTI_SURFACE_CENTROID_IRI;
		}
		return null;
	}
}
