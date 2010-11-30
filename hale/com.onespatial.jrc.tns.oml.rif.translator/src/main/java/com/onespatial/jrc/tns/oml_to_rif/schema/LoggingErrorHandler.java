package com.onespatial.jrc.tns.oml_to_rif.schema;

import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
