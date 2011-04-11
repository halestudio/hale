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
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

import com.onespatial.jrc.tns.oml_to_rif.translate.context.RifVariable;

/**
 * Holds the data required to construct an INSPIRE identifier mapping
 * within a RIF {@link Sentence}.
 * 
 * @author Susanne Reinwarth / TU Dresden
 */
public class IdentifierMapping extends PropertyMapping
{
	private String namespace;
	private String versionId;
	private String versionNilReason;
	
	/**
	 * constructor
	 * 
	 * @param sourceAttribute
	 * 				{@link RifVariable}
	 * @param targetAttribute
	 * 				{@link RifVariable}
	 * @param namespace
	 * 				{@link String}
	 * @param versionId
	 * 				{@link String}
	 * @param versionNilReason
	 * 				{@link String}
	 */
	public IdentifierMapping (RifVariable sourceAttribute, RifVariable targetAttribute,
			String namespace, String versionId, String versionNilReason)
	{
		super(sourceAttribute, targetAttribute);
		this.namespace = namespace;
		this.versionId = versionId;
		this.versionNilReason = versionNilReason;
	}
	
	/**
	 * @return {@link String}
	 */
	public String getNamespace()
	{
		return namespace;
	}
	
	/**
	 * @param namespace {@link String}
	 */
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}
	
	/**
	 * @return {@link String}
	 */
	public String getVersionId()
	{
		return versionId;
	}
	
	/**
	 * @param versionId {@link String}
	 */
	public void setVersionId(String versionId)
	{
		this.versionId = versionId;
	}
	
	/**
	 * @return {@link String}
	 */
	public String getVersionNilReason()
	{
		return versionNilReason;
	}
	
	/**
	 * @param versionNilReason {@link String}
	 */
	public void setVersionNilReason(String versionNilReason)
	{
		this.versionNilReason = versionNilReason;
	}
}
