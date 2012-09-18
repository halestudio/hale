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
 * A generic class containing features common to translators.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
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
