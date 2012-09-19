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
