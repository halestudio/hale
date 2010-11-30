/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.api;

/**
 * A generic class containing features common to translators.
 * 
 * @author simonp
 * @param <IM>
 * @param <OM>
 */
public abstract class AbstractFollowableTranslator<IM, OM> implements Translator<IM, OM>
{

    /**
     * {@inheritDoc}
     */
    @Override
    public <AOM> Translator<IM, AOM> connect(Translator<OM, AOM> followOnTranslator)
    {
        return new FollowOnTranslator<IM, OM, AOM>(this, followOnTranslator);
    }
}
