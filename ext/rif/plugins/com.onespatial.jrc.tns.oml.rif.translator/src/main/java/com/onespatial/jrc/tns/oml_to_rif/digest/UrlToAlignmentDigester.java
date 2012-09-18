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
package com.onespatial.jrc.tns.oml_to_rif.digest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.onespatial.jrc.tns.oml_to_rif.HaleAlignment;
import com.onespatial.jrc.tns.oml_to_rif.api.AbstractFollowableTranslator;
import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;

/**
 * Translates a document at a particular URL to an {@link Alignment}.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Templer / Fraunhofer IGD
 */
public class UrlToAlignmentDigester extends AbstractFollowableTranslator<URL, HaleAlignment>
{
    /**
     * Translates an {@link URL} into a HALE {@link Alignment}.
     * 
     * @param source
     *            {@link URL} the source URL
     * @return {@link Alignment}
     * @throws TranslationException
     *             if anything goes wrong during the translation
     */
    @Override
    public HaleAlignment translate(URL source) throws TranslationException
    {
        if (source == null)
        {
            throw new TranslationException("url is null"); //$NON-NLS-1$
        }
        Alignment al = new OmlRdfReader().read(source);
        
        ApacheSchemaProvider sp = new ApacheSchemaProvider();
        Schema s, t;
        try {
        	URI suri = new URI(al.getSchema1().getLocation());
//        	if (!suri.isAbsolute()) {
//        		suri = source.toURI();
//				suri = new URI(suri.getScheme(), suri.getUserInfo(), 
//						suri.getHost(), suri.getPort(), 
//						al.getSchema1().getLocation(), null, null);
//        	}
	        s = sp.loadSchema(suri, null);
	        t = sp.loadSchema(new URI(al.getSchema2().getLocation()), null);
        } catch (Exception e) {
			throw new TranslationException("Error loading schemas", e);  //$NON-NLS-1$
		}
        
        return new HaleAlignment(al, s.getElements().values(), 
        		t.getElements().values());
    }

}
