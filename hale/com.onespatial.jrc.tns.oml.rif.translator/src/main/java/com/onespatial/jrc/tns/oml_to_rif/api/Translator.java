/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.api;

/**
 * Translate the contents of the input model into the output model.
 * 
 * @author richards
 * @param <IM>
 *            the input model to read.
 * @param <OM>
 *            the output model to write.
 */
public interface Translator<IM, OM>
{
    /**
     * Translate the input model into the output model.
     * 
     * @param source
     *            the model to translate.
     * @return the populated output model.
     * @throws TranslationException
     *             if failed to translate.
     */
    OM translate(IM source) throws TranslationException;

    /**
     * Create a translator to a different format by connecting a followon
     * translator that handles the next translation. See
     * {@link FollowOnTranslator} for class that support the implementation of
     * this method.
     * 
     * @param <AOM>
     *            alternative output model.
     * @param followOnTranslator
     *            the translator that converts the output model to the
     *            alternative output model.
     * @return translator capable of performing direct translation from input
     *         model to alternative output model.
     */
    <AOM> Translator<IM, AOM> connect(Translator<OM, AOM> followOnTranslator);
}
