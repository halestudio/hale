/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
        bld.append(exception.getLocalizedMessage()).append(" conext-"); //$NON-NLS-1$
        bld.append("public-id:").append(exception.getPublicId()); //$NON-NLS-1$
        bld.append(" system-id:").append(exception.getSystemId()); //$NON-NLS-1$
        bld.append(" line:").append(exception.getLineNumber()); //$NON-NLS-1$
        bld.append(" column:").append(exception.getColumnNumber()); //$NON-NLS-1$
        bld.append("]"); //$NON-NLS-1$
        return bld.toString();
    }
}
