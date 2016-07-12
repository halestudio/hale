/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.convert.core;

import java.net.URI;

import org.codehaus.groovy.runtime.GStringImpl;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert a {@link GStringImpl} to an {@link URI}.
 * 
 * @author Simon Templer
 */
public class GStringImplToURIConverter implements Converter<GStringImpl, URI> {

	@Override
	public URI convert(GStringImpl source) {
		if (source == null) {
			return null;
		}
		return URI.create(source.toString());
	}

}
