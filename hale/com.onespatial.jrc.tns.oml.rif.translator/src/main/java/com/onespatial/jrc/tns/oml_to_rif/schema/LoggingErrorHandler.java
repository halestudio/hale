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
package com.onespatial.jrc.tns.oml_to_rif.schema;

import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
class LoggingErrorHandler implements ErrorHandler
{

    private static final Logger ERRORLOGGER = Logger.getLogger(LoggingErrorHandler.class
            .getCanonicalName());

    /**
     * @param schemaErrors
     *            list of exceptions to be appended.
     */
    public LoggingErrorHandler(List<Exception> schemaErrors)
    {
        // does nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warning(SAXParseException exception) throws SAXException
    {
        ERRORLOGGER.warning(processException(exception));
        throw exception;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fatalError(SAXParseException exception) throws SAXException
    {
        ERRORLOGGER.severe(processException(exception));
        throw exception;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(SAXParseException exception) throws SAXException
    {
        ERRORLOGGER.severe(processException(exception));
        throw exception;
    }

    private String processException(SAXParseException exception)
    {
        StringBuilder bld = new StringBuilder();
        bld.append(exception.getLocalizedMessage()).append(" conext-");
        bld.append("public-id:").append(exception.getPublicId());
        bld.append(" system-id:").append(exception.getSystemId());
        bld.append(" line:").append(exception.getLineNumber());
        bld.append(" column:").append(exception.getColumnNumber());
        bld.append("]");
        return bld.toString();
    }
}
