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
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

import java.util.ArrayList;
import java.util.List;

import org.w3._2007.rif.Document;

/**
 * A model of a RIF {@link Document}.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
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
