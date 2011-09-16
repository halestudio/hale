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
package eu.esdihumboldt.cst.iobridge;


import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;

/**
 * TODO: Enter Type comment.
 * 
 * @author Thorsten Reitz
 */
public interface CstServiceBridge {

	/**
	 * This method will perform schema transformation based on already loaded 
	 * instance data.
	 * @param schemaFilename the filename or URL from which the target schema 
	 * can be loaded
	 * @param omlFilename the filename or URL of the OML mapping file to use
	 * @param gmlFilename the local filename to the GML that is to be used as 
	 * source data
	 * @return the filename where the result of the CST service execution is 
	 * stored.
	 */
	public String transform(String schemaFilename, String omlFilename, 
			String gmlFilename) throws TransformationException;
	
	/**
	 * This method will perform schema transformation based on already loaded 
	 * instance data.
	 * 
	 * @param schemaFilename the filename or URL from which the target schema 
	 * can be loaded
	 * @param omlFilename the filename or URL of the OML mapping file to use
	 * @param gmlFilename the local filename to the GML that is to be used as 
	 * source data
	 * 
	 * @param sourceSchema the filename or URL of the source schema, if
	 *   <code>null</code> the source schema will be ignored for parsing the
	 *   source data
	 * @param sourceVersion the GML version of the source data, if <code>null</code>
	 *   it is tried to be determined from the data file  
	 * 
	 * @return the filename where the result of the CST service execution is 
	 * stored.
	 * @throws TransformationException 
	 */
	public String transform(String schemaFilename, String omlFilename, 
			String gmlFilename, String sourceSchema, 
			ConfigurationType sourceVersion) throws TransformationException;
	
}
