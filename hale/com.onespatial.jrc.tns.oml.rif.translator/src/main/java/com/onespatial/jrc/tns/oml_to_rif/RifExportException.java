/*
 * Copyright (c) 1Spatial Group Ltd.
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
