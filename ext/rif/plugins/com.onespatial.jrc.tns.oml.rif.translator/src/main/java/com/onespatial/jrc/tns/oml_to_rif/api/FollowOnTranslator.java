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
 * A follow-on translator. Creates a translator that combines two translators.
 * All type checking is done using generics (and therefore at compile time) to
 * avoid runtime costs.
 * 
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @param <IM>
 *            input model of first translator
 * @param <MM>
 *            output model of first translator and input model of second
 *            translator.
 * @param <OM>
 *            output model of second translator.
 */
public class FollowOnTranslator<IM, MM, OM> implements Translator<IM, OM>
{
    /**
     * First translator that converts input model to output model.
     */
    private final Translator<IM, MM> firstTranslator;

    /**
     * Second translator that converts output model into second output model.
     */
    private final Translator<MM, OM> secondTranslator;

    /**
     * @param firstTranslator
     *            {@link Translator}&lt;IM,MM&gt;
     * @param secondTranslator
     *            {@link Translator}&lt;MM,OM&gt;
     */
    public FollowOnTranslator(Translator<IM, MM> firstTranslator,
            Translator<MM, OM> secondTranslator)
    {
        this.firstTranslator = firstTranslator;
        this.secondTranslator = secondTranslator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OM translate(IM source) throws TranslationException
    {
        MM intermediateResult = firstTranslator.translate(source);
        return secondTranslator.translate(intermediateResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <AOM> Translator<IM, AOM> connect(Translator<OM, AOM> followOnTranslator)
    {
        return new FollowOnTranslator<IM, OM, AOM>(this, followOnTranslator);
    }
}
