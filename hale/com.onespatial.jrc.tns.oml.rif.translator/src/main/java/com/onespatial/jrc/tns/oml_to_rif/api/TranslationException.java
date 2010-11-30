/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.api;

/**
 * Translation Exception Thrown when an exception is generated during a
 * translation.
 * 
 * @author richards
 */
public class TranslationException extends Exception
{

    /**
     * @param message
     *            String
     * @param cause
     *            {@link Throwable}
     */
    public TranslationException(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     *            String
     */
    public TranslationException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     *            {@link Throwable}
     */
    public TranslationException(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * Default serial Id.
     */
    private static final long serialVersionUID = 1L;
}
