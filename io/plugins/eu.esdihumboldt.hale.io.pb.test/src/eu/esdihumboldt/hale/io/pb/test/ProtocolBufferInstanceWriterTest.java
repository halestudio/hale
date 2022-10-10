/*
 * Copyright (c) 2022 wetransform GmbH
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

package eu.esdihumboldt.hale.io.pb.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;
import org.junit.Test;

import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;

import eu.esdihumboldt.hale.io.pb.ProtocolBufferInstanceWriter;

/**
 * Test class for {@link ProtocolBufferInstanceWriter}
 * 
 * @author Flaminia Catalli
 */
public class ProtocolBufferInstanceWriterTest {

	private String getJsonString() {
		System.out.println("Creating simple json...");
		Map<String, Object> obj = new HashMap<>();
		obj.put("name", "Jon Doe");
		// obj.put("age", 27);
		obj.put("salary", 600000.0);
		String jsonText = JSONValue.toJSONString(obj);

		System.out.println(jsonText);

		return jsonText;
	}

	/**
	 * Test - convert a simple json string into a Protocol Buffer file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testConvertJson2PB() throws Exception {
		String example = getJsonString();
		Reader jsonReader = new StringReader(example);

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		ProtocolBufferInstanceWriter.writeJsonToProtocolBuffer(jsonReader, () -> result);

		Struct data = Struct.parseFrom(result.toByteArray());
		String jsonFromPB = JsonFormat.printer().print(data);

		System.out.println("Json recreated from PB:");
		System.out.println(jsonFromPB);

		Object parsedJsonSource = JSONValue.parse(example);
		Object parsedJsonFromPB = JSONValue.parse(jsonFromPB);
		assertEquals(parsedJsonSource, parsedJsonFromPB);
	}

}
