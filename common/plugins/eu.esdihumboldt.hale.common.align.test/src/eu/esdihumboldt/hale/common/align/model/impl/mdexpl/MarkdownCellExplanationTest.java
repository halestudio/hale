/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.model.impl.mdexpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.mdexpl.test.TestExplanation1;
import eu.esdihumboldt.hale.common.align.model.impl.mdexpl.test.TestExplanation2;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import groovy.text.Template;

/**
 * Tests for {@link MarkdownCellExplanation}.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public class MarkdownCellExplanationTest {

	/**
	 * Test if different templates for different languages are retrieved
	 * properly.
	 */
	@Test
	public void testTemplate() {
		TestExplanation1 exp = new TestExplanation1();

		Optional<Template> english = exp.getTemplate(TestExplanation1.class, Locale.ENGLISH);
		assertTrue(english.isPresent());
		assertEquals("I'm an English man", english.get().make().toString());

		Optional<Template> german = exp.getTemplate(TestExplanation1.class, Locale.GERMAN);
		assertTrue(german.isPresent());
		assertEquals("Ich bin Deutscher", german.get().make().toString());

		Optional<Template> germany = exp.getTemplate(TestExplanation1.class, Locale.GERMANY);
		assertTrue(germany.isPresent());
		assertEquals("Ich bin Deutscher", germany.get().make().toString());
	}

	@Test
	public void testExplanation() {
		TestExplanation2 exp = new TestExplanation2();

		Cell cell = createTestCell();

		String expected = "Source 'source1Type'\n" + //
				"Source 'source1Type'\n" + //
				"Target 'target1Type'\n" + //
				"test 1\n" + //
				"test 2\n" + //
				"pattern 3";

		String expl = exp.getExplanation(cell, null, Locale.getDefault());
		org.junit.Assert.assertNotNull(expl);
		assertEquals(expected, expl);
	}

	@Test
	@Ignore("PathMatchingResourcePatternResolver doesn't pick up files with wild card for unknown reason and needs to be investigated")
	public void testLocales1() {
		TestExplanation1 exp = new TestExplanation1();

		Set<Locale> locales = new HashSet<>();
		Iterables.addAll(locales, exp.getSupportedLocales());

		assertTrue(locales.contains(Locale.ENGLISH));
		assertTrue(locales.contains(Locale.GERMAN));
		assertEquals(2, locales.size());
	}

	@Test
	@Ignore("PathMatchingResourcePatternResolver doesn't pick up files with wild card for unknown reason and needs to be investigated")
	public void testLocales2() {
		TestExplanation2 exp = new TestExplanation2();

		Set<Locale> locales = new HashSet<>();
		Iterables.addAll(locales, exp.getSupportedLocales());

		assertTrue(locales.contains(Locale.ENGLISH));
		assertEquals(1, locales.size());
	}

	@SuppressWarnings("unused")
	private Cell createTestCell() {
		// cell 1
		MutableCell cell1 = new DefaultCell();
		String id1;
		// must be an existing function
		cell1.setTransformationIdentifier(id1 = "eu.esdihumboldt.hale.align.formattedstring");

		ListMultimap<String, ParameterValue> parameters1 = LinkedListMultimap.create();
		// parameter that does not exist (for parameter list testing)
		parameters1.put("test", new ParameterValue("1"));
		parameters1.put("test", new ParameterValue("2"));
		// existing parameter
		parameters1.put("pattern", new ParameterValue("3"));
		cell1.setTransformationParameters(parameters1);

		ListMultimap<String, Type> source1 = ArrayListMultimap.create();
		QName source1TypeName;
		String source1EntityName;
		TypeDefinition sourceType1 = new DefaultTypeDefinition(
				source1TypeName = new QName("source1Type"));
		Filter filter = null;
		source1.put(source1EntityName = "var", new DefaultType(
				new TypeEntityDefinition(sourceType1, SchemaSpaceID.SOURCE, filter)));
		cell1.setSource(source1);

		ListMultimap<String, Type> target1 = ArrayListMultimap.create();
		QName target1TypeName;
		String target1EntityName;
		TypeDefinition targetType1 = new DefaultTypeDefinition(
				target1TypeName = new QName("http://some.name.space/t1", "target1Type"));
		target1.put(target1EntityName = null,
				new DefaultType(new TypeEntityDefinition(targetType1, SchemaSpaceID.TARGET, null)));
		cell1.setTarget(target1);

		return cell1;
	}

}
