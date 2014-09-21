/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.lookup.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.w3c.dom.Element;

import com.google.common.collect.Sets;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.lookup.LookupTable;
import eu.esdihumboldt.hale.common.lookup.impl.LookupTableImpl;
import groovy.json.JsonBuilder;

/**
 * Tests for {@link LookupTable} complex value conversion.
 * 
 * @author Simon Templer
 */
public class LookupTableTypeTest {

	/**
	 * Test if a simple lookup table containing only string values is the same
	 * when converted to DOM and back again.
	 */
	@Test
	public void testStringLookup() {
		Map<Value, Value> values = createStringLookup();

		LookupTable org = new LookupTableImpl(values);

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(org);

		// convert back
		LookupTable conv = HaleIO.getComplexValue(fragment, LookupTable.class, null);

		checkTable(conv, values);
	}

	/**
	 * Test if a simple lookup table containing only string values is the same
	 * when converted to JSON and back again.
	 */
	@Test
	public void testStringLookupJson() {
		Map<Value, Value> values = createStringLookup();

		LookupTable org = new LookupTableImpl(values);

		// converter
		LookupTableType ltt = new LookupTableType();

		// convert to Json
		StringWriter writer = new StringWriter();
		ltt.toJson(org, writer);

		System.out.println(writer.toString());

		// convert back
		LookupTable conv = ltt.fromJson(new StringReader(writer.toString()), null);

		checkTable(conv, values);
	}

	private Map<Value, Value> createStringLookup() {
		Map<Value, Value> values = new HashMap<Value, Value>();

		values.put(Value.of("a"), Value.of("1"));
		values.put(Value.of("b"), Value.of("1"));
		values.put(Value.of("c"), Value.of("1"));
		values.put(Value.of("d"), Value.of("2"));
		values.put(Value.of("e"), Value.of("2"));

		return values;
	}

	/**
	 * Test if a lookup table containing only complex values is the same when
	 * converted to DOM and back again.
	 */
	@Test
	public void testComplexLookup() {
		Map<Value, Value> values2 = createComplexLookup();

		LookupTable org2 = new LookupTableImpl(values2);

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(org2);

		// convert back
		LookupTable conv = HaleIO.getComplexValue(fragment, LookupTable.class, null);

		checkTable(conv, values2);
	}

	/**
	 * Test if a lookup table containing only complex values is the same when
	 * converted to JSON and back again.
	 */
	@Test
	public void testComplexLookupJson() {
		Map<Value, Value> values2 = createComplexLookup();

		LookupTable org2 = new LookupTableImpl(values2);

		// converter
		LookupTableType ltt = new LookupTableType();

		// convert to Json
		StringWriter writer = new StringWriter();
		ltt.toJson(org2, writer);

		System.out.println(writer.toString());

		// convert back
		LookupTable conv = ltt.fromJson(new StringReader(writer.toString()), null);
		checkTable(conv, values2);
	}

	/**
	 * Test if a lookup table containing only complex values is the same when
	 * converted to JSON and back again.
	 */
	@Test
	public void testComplexLookupJsonGroovy() {
		Map<Value, Value> values2 = createComplexLookup();

		LookupTable org2 = new LookupTableImpl(values2);

		// converter
		LookupTableType ltt = new LookupTableType();

		// convert to Json
		Object json = ltt.toJson(org2);

		System.out.println(new JsonBuilder(json).toString());

		// convert back
		LookupTable conv = ltt.fromJson(json, null);
		checkTable(conv, values2);
	}

	private Map<Value, Value> createComplexLookup() {
		// simple internal table
		Map<Value, Value> values = new HashMap<Value, Value>();

		values.put(Value.of("a"), Value.of("1"));
		values.put(Value.of("b"), Value.of("1"));

		LookupTable org = new LookupTableImpl(values);

		// external table
		Map<Value, Value> values2 = new HashMap<Value, Value>();

		values2.put(Value.of("sub"), Value.complex(org));
		values2.put(Value.of("c"), Value.of("2"));

		return values2;
	}

	private void checkTable(LookupTable conv, Map<Value, Value> values) {
		assertTrue(Sets.difference(conv.getKeys(), values.keySet()).isEmpty());

		for (Entry<Value, Value> entry : values.entrySet()) {
			if (entry.getValue().getValue() instanceof LookupTable) {
				// compare internal table
				assertTrue(conv.lookup(entry.getKey()).getValue() instanceof LookupTable);
				checkTable((LookupTable) conv.lookup(entry.getKey()).getValue(),
						((LookupTable) (entry.getValue().getValue())).asMap());
			}
			else {
				assertEquals(entry.getValue(), conv.lookup(entry.getKey()));
			}
		}
	}

}
