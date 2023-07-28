/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.FilteredInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.PseudoInstanceReference;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.json.reader.JsonToInstance;
import eu.esdihumboldt.util.io.InputSupplier;

/**
 * Instance collection backed by a Json document.
 * 
 * @author Simon Templer
 */
public class JsonInstanceCollection implements InstanceCollection2 {

	private static final ALogger log = ALoggerFactory.getLogger(JsonInstanceCollection.class);

	private class JsonIterator implements InstanceIterator {

		private boolean closed = false;

		private Reader reader = null;
		private JsonParser parser = null;

		private void proceedToNext() {
			if (closed) {
				return;
			}

			if (parser == null) {
				try {
					reader = new BufferedReader(new InputStreamReader(input.getInput(), charset));
					ObjectMapper mapper = new ObjectMapper();
					JsonFactory jsonFactory = mapper.getJsonFactory();
					parser = jsonFactory.createJsonParser(reader);
				} catch (Exception e) {
					close("Error accessing JSON source", e);
				}

				try {
					translator.init(parser);
				} catch (IOException e) {
					close("Error initializing reading JSON source", e);
				}
			}
		}

		private void close(String message, Exception e) {
			log.error(message, e);
			close();
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			else {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void close() {
			closed = true;
			if (parser != null) {
				try {
					parser.close();
				} catch (IOException e) {
					log.error("Error closing JSON parser", e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("Error closing JSON reader", e);
				}
			}
		}

		@Override
		public boolean hasNext() {
			if (closed) {
				return false;
			}

			proceedToNext();

			return parser.getCurrentToken() == JsonToken.START_OBJECT;
		}

		@Override
		public Instance next() {
			if (closed) {
				return null;
			}

			proceedToNext();

			if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
				try {
					return translator.readInstance(parser);
				} catch (IOException e) {
					close("Error reading instance from JSON source", e);
					return null;
				}
			}
			else
				throw new NoSuchElementException("No object start: " + parser.getCurrentToken());
		}

		@Override
		public TypeDefinition typePeek() {
			return null;
		}

		@Override
		public boolean supportsTypePeek() {
			return false;
		}

		@Override
		public void skip() {
			if (closed) {
				return;
			}

			proceedToNext();

			try {
				translator.skipValue(parser);
			} catch (IOException e) {
				close("Error skipping instance in JSON source", e);
			}
		}

	}

	private final JsonToInstance translator;

	private Boolean empty;

	private final InputSupplier<? extends InputStream> input;

	private final Charset charset;

	/**
	 * Create a new instance collection.
	 * 
	 * @param translator translator for creating instances from Json
	 * @param input the input to load
	 * @param charset the character set to use for reading the input
	 */
	public JsonInstanceCollection(JsonToInstance translator,
			InputSupplier<? extends InputStream> input, Charset charset) {
		super();
		this.translator = translator;
		this.input = input;
		this.charset = charset;
	}

	@Override
	public InstanceIterator iterator() {
		return new JsonIterator();
	}

	@Override
	public boolean hasSize() {
		return false;
	}

	@Override
	public int size() {
		return UNKNOWN_SIZE;
	}

	@Override
	public boolean isEmpty() {
		if (empty != null) {
			return empty;
		}

		try (ResourceIterator<Instance> it = iterator()) {
			empty = !it.hasNext();
		}
		return empty;
	}

	@Override
	public InstanceCollection select(Filter filter) {
		return FilteredInstanceCollection.applyFilter(this, filter);
	}

	@Override
	public InstanceReference getReference(Instance instance) {
		return new PseudoInstanceReference(instance);
	}

	@Override
	public Instance getInstance(InstanceReference reference) {
		if (reference instanceof PseudoInstanceReference) {
			return ((PseudoInstanceReference) reference).getInstance();
		}

		return null;
	}

	@Override
	public boolean supportsFanout() {
		return false;
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		return null;
	}

}
