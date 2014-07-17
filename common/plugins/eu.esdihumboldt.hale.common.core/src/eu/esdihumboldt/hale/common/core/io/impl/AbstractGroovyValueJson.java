/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import eu.esdihumboldt.hale.common.core.io.ComplexValueJson;
import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

/**
 * Base class for {@link ComplexValueJson} implementations based on Groovy Json
 * objects.
 * 
 * @author Simon Templer
 * @param <T> the type of the complex value
 * @param <C> the type of the context that should be supplied
 */
public abstract class AbstractGroovyValueJson<T, C> implements ComplexValueJson<T, C> {

	@Override
	public T fromJson(Reader json, C context) {
		return fromJson(new JsonSlurper().parse(json), context);
	}

	@Override
	public void toJson(T value, Writer writer) throws IOException {
		writer.write(new JsonBuilder(toJson(value)).toString());
	}

}
