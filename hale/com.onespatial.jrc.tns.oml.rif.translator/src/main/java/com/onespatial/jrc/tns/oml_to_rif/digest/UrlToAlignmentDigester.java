/*
 * Copyright (c) 1Spatial Group Ltd.
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
 * @author simonp
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
