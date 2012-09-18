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
