/*
 * Copyright (c) 1Spatial Group Ltd.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

import java.util.ArrayList;
import java.util.List;

import org.w3._2007.rif.Document;

/**
 * A model of a RIF {@link Document}.
 * 
 * @author simonp
 */
public class ModelRifDocument
{
    private final List<ModelSentence> sentences;

    /**
     * Default constructor.
     */
    public ModelRifDocument()
    {
        super();
        sentences = new ArrayList<ModelSentence>();
    }

    /**
     * @return List&lt;{@link ModelSentence}&gt;
     */
    public List<ModelSentence> getSentences()
    {
        return sentences;
    }
}
