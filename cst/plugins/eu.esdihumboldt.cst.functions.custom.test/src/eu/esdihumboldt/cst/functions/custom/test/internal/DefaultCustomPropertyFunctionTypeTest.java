/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.custom.test.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * TODO Type description
 * 
 * @author simon
 */
public class DefaultCustomPropertyFunctionTypeTest {

	/**
	 * Test if a simple lookup table containing only string values is the same
	 * when converted to DOM and back again.
	 */
	@Test
	public void testWriteRead() {
		DefaultCustomPropertyFunction f = new DefaultCustomPropertyFunction();
		f.setIdentifier("ident");
		f.setFunctionType("groovy");
		f.setName("My function");

		f.setFunctionDefinition(Value.of(new Text("a + b")));

		List<DefaultCustomPropertyFunctionEntity> sources = new ArrayList<>();
		sources.add(createEntity("a", 1, 1, false));
		sources.add(createEntity("b", 0, 1, false));
		f.setSources(sources);

		f.setTarget(createEntity(null, 1, 1, false));

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(f);

		// convert back
		DefaultCustomPropertyFunction conv = HaleIO.getComplexValue(fragment,
				DefaultCustomPropertyFunction.class, null);

		// checks
		assertNotNull(conv);

		assertEquals(f.getIdentifier(), conv.getIdentifier());
		assertEquals(f.getName(), conv.getName());
		assertEquals(f.getFunctionType(), conv.getFunctionType());

		// function definition
		Text text = conv.getFunctionDefinition().as(Text.class);
		assertNotNull(text);
		assertEquals("a + b", text.getText());

		// sources
		assertEquals(2, conv.getSources().size());

		DefaultCustomPropertyFunctionEntity source1 = conv.getSources().get(0);
		assertEquals("a", source1.getName());
		assertEquals(1, source1.getMinOccurrence());
		assertEquals(1, source1.getMaxOccurrence());

		DefaultCustomPropertyFunctionEntity source2 = conv.getSources().get(1);
		assertEquals("b", source2.getName());
		assertEquals(0, source2.getMinOccurrence());

		// target
		assertNotNull(conv.getTarget());
	}

	private DefaultCustomPropertyFunctionEntity createEntity(String name, int min, int max,
			boolean eager) {
		DefaultCustomPropertyFunctionEntity result = new DefaultCustomPropertyFunctionEntity();

		result.setName(name);
		result.setMinOccurrence(min);
		result.setMaxOccurrence(max);
		result.setEager(eager);

		// XXX
		result.setBindingClass(String.class);

		return result;
	}

}
