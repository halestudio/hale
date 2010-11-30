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
package com.onespatial.jrc.tns.oml_to_rif.api;

/**
 * Translate the contents of the input model into the output model.
 * 
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
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
