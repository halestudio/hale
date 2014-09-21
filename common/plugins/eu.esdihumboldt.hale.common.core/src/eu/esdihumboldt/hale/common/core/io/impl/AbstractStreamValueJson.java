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
import java.io.StringReader;
import java.io.StringWriter;

import eu.esdihumboldt.hale.common.core.io.ComplexValueJson;
import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

/**
 * Base class for {@link ComplexValueJson} implementations based on readers and
 * writers.
 * 
 * @author Simon Templer
 * @param <T> the type of the complex value
 * @param <C> the type of the context that should be supplied
 */
public abstract class AbstractStreamValueJson<T, C> implements ComplexValueJson<T, C> {

	@Override
	public T fromJson(Object json, C context) {
		try (StringReader reader = new StringReader(new JsonBuilder(context).toString())) {
			return fromJson(reader, context);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to extract value from Json representation", e);
		}
	}

	@Override
	public Object toJson(T value) {
		StringWriter writer = new StringWriter();
		try {
			toJson(value, writer);
			writer.close();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create Json representation from value", e);
		}
		return new JsonSlurper().parseText(writer.toString());
	}

}
