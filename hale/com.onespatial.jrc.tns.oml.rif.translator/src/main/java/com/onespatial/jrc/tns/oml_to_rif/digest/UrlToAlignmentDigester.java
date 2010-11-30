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
package com.onespatial.jrc.tns.oml_to_rif.digest;

import java.net.URL;

import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;

/**
 * Translates a document at a particular URL to an {@link Alignment}.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 */
public class UrlToAlignmentDigester extends AbstractFollowableTranslator<URL, Alignment>
{
    /**
     * Translates an {@link URL} into a HALE {@link Alignment}.
     * 
     * @param source
     *            {@link URL} the source URL
     * @return {@link Alignment}
     * @throws TranslationException
     *             if anything goes wrong during the translation
     */
    @Override
    public Alignment translate(URL source) throws TranslationException
    {
        if (source == null)
        {
            throw new TranslationException("url is null");
        }
        return new OmlRdfReader().read(source);
    }

}
