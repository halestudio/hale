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
package com.onespatial.jrc.tns.oml_to_rif;

/**
 * Specific exception related to the RIF export context.
 * 
 * @author simonp
 */
public class RifExportException extends RuntimeException
{

    /**
     * @param msg
     *            String the exception message
     */
    public RifExportException(String msg)
    {
        super(msg);
    }

    /**
     * @param cause
     *            Exception the nested exception
     */
    public RifExportException(Exception cause)
    {
        super(cause);
    }

    /**
     * for serialization.
     */
    private static final long serialVersionUID = 5811625378688158029L;

}
