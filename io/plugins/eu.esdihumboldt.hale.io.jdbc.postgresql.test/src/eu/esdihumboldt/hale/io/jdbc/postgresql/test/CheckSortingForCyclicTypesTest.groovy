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

package eu.esdihumboldt.hale.io.jdbc.postgresql.test;

import static org.junit.Assert.*

import javax.xml.namespace.QName

import org.junit.Test

import ru.yandex.qatools.allure.annotations.Features
import ru.yandex.qatools.allure.annotations.Stories
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceWriter

/**
 * Test to check the sorting of the cyclic reference between the types
 * 
 * <p><b>Example</b></p>
 * Consider this cyclic reference: <pre>t1 -> t2-> t3 -> t4 -> t1 </pre> In this case there is cycle of referencing and it will be infinite recursion for sorting the types based on referencing. </br>
 * 
 * @author Sameer Sheikh
 */
@Features("Databases")
@Stories("PostgreSQL")
public class CheckSortingForCyclicTypesTest {

	private static final NAMESPACE = "jdbc:postgresql:gis:public";
	/**
	 * create the cyclic referencing and test the sorting of these
	 */
	@Test
	public void checkCyclicTypes() {
		TypeDefinition varchar = new DefaultTypeDefinition(new QName("jdbc:postgresql:gis",
				"varchar"));
		TypeDefinition typeOne = new DefaultTypeDefinition(new QName(NAMESPACE,
				"table1"));
		TypeDefinition typeTwo = new DefaultTypeDefinition(new QName(NAMESPACE,
				"table2"));
		TypeDefinition typeThree = new DefaultTypeDefinition(new QName(NAMESPACE,
				"table3"));

		/* Creating properties for type one*/
		DefaultPropertyDefinition propertyOne = new DefaultPropertyDefinition(new QName("first_id"), typeOne,
				varchar);
		DefaultPropertyDefinition propertyTwo = new DefaultPropertyDefinition(new QName("name"), typeOne,
				varchar);

		DefaultPropertyDefinition propertyThree = new DefaultPropertyDefinition(new QName("test_id"), typeOne,
				varchar);
		// setting reference TO type two
		propertyThree.setConstraint(new Reference(typeTwo));

		/* Creating properties for type two*/
		DefaultPropertyDefinition propertyFour = new DefaultPropertyDefinition(new QName("second_id"), typeOne,
				varchar);
		DefaultPropertyDefinition propertyFive = new DefaultPropertyDefinition(new QName("name"), typeOne,
				varchar);
		DefaultPropertyDefinition propertySix = new DefaultPropertyDefinition(new QName("test_id"), typeOne,
				varchar);

		// setting reference to type three
		propertyFive.setConstraint(new Reference(typeThree));

		/* Creating properties for type three*/
		DefaultPropertyDefinition propertySeven = new DefaultPropertyDefinition(new QName("third_id"), typeOne,
				varchar);
		DefaultPropertyDefinition propertyEight = new DefaultPropertyDefinition(new QName("name"), typeOne,
				varchar);
		DefaultPropertyDefinition propertyNine = new DefaultPropertyDefinition(new QName("test_id"), typeOne,
				varchar);
		// setting reference to type one
		propertyNine.setConstraint(new Reference(typeOne))

		//adding childs to type one
		typeOne.addChild(propertyOne);
		typeOne.addChild(propertyTwo);
		typeOne.addChild(propertyThree);

		//adding childs to type two
		typeTwo.addChild(propertyFour);
		typeTwo.addChild(propertyFive);
		typeTwo.addChild(propertySix);

		//adding childs to type three
		typeThree.addChild(propertySeven);
		typeThree.addChild(propertyEight);
		typeThree.addChild(propertyNine);


		List<TypeDefinition> types = new ArrayList<TypeDefinition>();
		types.add(typeOne);
		types.add(typeTwo);
		types.add(typeThree);


		JDBCInstanceWriter writer = new JDBCInstanceWriter();
		Set<TypeDefinition> sortedTypes = writer.getSortedSchemas(types);

		assertNotNull(sortedTypes);
	}
}
