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
 * A follow-on translator. Creates a translator that combines two translators.
 * All type checking is done using generics (and therefore at compile time) to
 * avoid runtime costs.
 * 
 * @author richards
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
