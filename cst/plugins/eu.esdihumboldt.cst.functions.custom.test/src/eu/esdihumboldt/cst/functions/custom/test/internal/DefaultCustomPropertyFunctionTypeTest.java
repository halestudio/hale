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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomFunctionExplanation;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionParameter;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import groovy.xml.XmlUtil;

/**
 * Custom function serialization tests.
 * 
 * @author Simon Templer
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

		List<DefaultCustomPropertyFunctionParameter> parameters = new ArrayList<>();

		DefaultCustomPropertyFunctionParameter param1 = new DefaultCustomPropertyFunctionParameter();
		param1.setName("gender");
		Set<String> enumeration = new TreeSet<>();
		enumeration.add("male");
		enumeration.add("female");
		param1.setMinOccurrence(1);
		param1.setMaxOccurrence(1);
		param1.setEnumeration(enumeration);
		parameters.add(param1);

		DefaultCustomPropertyFunctionParameter param2 = new DefaultCustomPropertyFunctionParameter();
		param2.setName("name");
		param2.setBindingClass(String.class);
		param2.setMinOccurrence(0);
		param2.setMaxOccurrence(1);
		parameters.add(param2);

		DefaultCustomPropertyFunctionParameter param3 = new DefaultCustomPropertyFunctionParameter();
		param3.setName("flag");
		param3.setBindingClass(Boolean.class);
		param3.setMinOccurrence(1);
		param3.setMaxOccurrence(1);
		param3.setDefaultValue(Value.of(false));
		parameters.add(param3);

		f.setParameters(parameters);

		// explanation
		Map<Locale, Value> templates = new HashMap<>();
		templates.put(Locale.ROOT, Value.of(new Text("Hello")));
		templates.put(Locale.GERMAN, Value.of(new Text("Hallo")));
		templates.put(Locale.FRANCE, Value.of(new Text("Salut")));
		DefaultCustomFunctionExplanation explanation = new DefaultCustomFunctionExplanation(
				templates, null);
		f.setExplanation(explanation);

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(f);

		// DEBUG
		System.out.println(XmlUtil.serialize(fragment));

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
		assertEquals(null, conv.getTarget().getName());

		// parameters
		assertEquals(3, conv.getParameters().size());

		DefaultCustomPropertyFunctionParameter cp1 = conv.getParameters().get(0);
		assertEquals("gender", cp1.getName());
		assertEquals(2, cp1.getEnumeration().size());

		DefaultCustomPropertyFunctionParameter cp2 = conv.getParameters().get(1);
		assertEquals("name", cp2.getName());
		assertEquals(0, cp2.getMinOccurrence());
		assertEquals(1, cp2.getMaxOccurrence());
		assertEquals(String.class, cp2.getBindingClass());

		DefaultCustomPropertyFunctionParameter cp3 = conv.getParameters().get(2);
		assertEquals("flag", cp3.getName());
		assertEquals(1, cp3.getMinOccurrence());
		assertEquals(1, cp3.getMaxOccurrence());
		assertEquals(Boolean.class, cp3.getBindingClass());
		assertEquals(false, cp3.getDefaultValue().as(Boolean.class));

		// explanation
		assertNotNull(conv.getExplanation());
		Map<Locale, Value> tempConv = conv.getExplanation().getTemplates();
		assertEquals(3, tempConv.size());

		Value tempRoot = tempConv.get(Locale.ROOT);
		assertNotNull(tempRoot);
		assertEquals("Hello", tempRoot.as(Text.class).getText());

		Value tempGerman = tempConv.get(Locale.GERMAN);
		assertNotNull(tempGerman);
		assertEquals("Hallo", tempGerman.as(Text.class).getText());

		Value tempFrance = tempConv.get(Locale.FRANCE);
		assertNotNull(tempFrance);
		assertEquals("Salut", tempFrance.as(Text.class).getText());
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
