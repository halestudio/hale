package eu.esdihumboldt.hale.common.service.helper.test

import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.service.helper.test.dummy.Population
import eu.esdihumboldt.hale.common.service.helper.test.dummy.PopulationContainer
import groovy.test.GroovyTestCase

/**
 * Test for Entity Population Count
 * @author Arun
 */
class PopulationCountTest extends GroovyTestCase {

	private InstanceCollection instances

	private Schema schema

	private static final PopulationContainer container = new PopulationContainer()

	@Override
	protected void setUp() throws Exception {
		super.setUp()
	}

	/**
	 * Test creating filter on nested properties and on type 
	 */
	void test1() {

		String mainNS = 'http://www.example.com'

		String propertyNS = 'http://www.example.com/property'

		TypeDefinition orderType;

		// create the schema
		schema = new SchemaBuilder(defaultPropertyTypeNamespace: propertyNS).schema(mainNS) {
			orderType = OrderType {
				entry{
					item(Long) {
						name(String)
						price(Double)
						description(String)
					}
					quantity(Integer)
				}
			}
		}

		// create the instance collection
		instances = new InstanceBuilder(types: schema).createCollection {
			OrderType {
				entry {
					item(1) {
						name('item1')
						price(1.1)
						description('Item number 1')
					}
					quantity(3)
				}
			}
			OrderType {
				entry {
					item(2) {
						name('item2')
						price(11.5)
						description('Item number 2')
					}
					quantity(4)
				}
			}
			OrderType {
				entry {
					item(3) {
						name('item2')
						price(14.5)
						description('Item number 2')
					}
					quantity(1)
				}
			}
			OrderType {
				entry {
					item(4) {
						name('item3')
						price(5.7)
						description('Item number 3')
					}
					quantity(2)
				}
			}
		}

		assertNotNull instances
		assertFalse instances.empty
		assertEquals 4, instances.size()

		TypeEntityDefinition typeEntity1 = new TypeEntityDefinition(orderType, SchemaSpaceID.SOURCE, null)
		PropertyEntityDefinition baseProp = typeEntity1.accessor().entry as PropertyEntityDefinition

		PropertyEntityDefinition propItem = baseProp.accessor().item as PropertyEntityDefinition
		PropertyEntityDefinition propName = propItem.accessor().name as PropertyEntityDefinition
		PropertyEntityDefinition propPrice = propItem.accessor().price as PropertyEntityDefinition
		PropertyEntityDefinition propQuant = baseProp.accessor().quantity as PropertyEntityDefinition

		//Add instances to Population
		addToPopulation()

		verifyPopulation(typeEntity1,4,4);

		//filtered propertydefiniton
		PropertyEntityDefinition propItem2 = baseProp.accessor().item(filter: "value.name = 'item2'") as PropertyEntityDefinition
		addToPopulation(propItem2)
		verifyPopulation(propItem2,2,2);

		//filtered propertydefiniton
		PropertyEntityDefinition propName1 = propItem.accessor().name(filter: "value = 'item3'") as PropertyEntityDefinition
		addToPopulation(propName1)
		verifyPopulation(propName1,1,1);

		//filtered propertydefiniton
		PropertyEntityDefinition propPrice1 = propItem.accessor().price(filter: "value > '10.0'") as PropertyEntityDefinition
		addToPopulation(propName1)
		verifyPopulation(propName1,2,2);


		//filtered propertydefiniton
		PropertyEntityDefinition baseProp2 =  typeEntity1.accessor().entry(filter: "value.quantity = '2'") as PropertyEntityDefinition
		addToPopulation(baseProp2)
		verifyPopulation(baseProp2,1,1);
		PropertyEntityDefinition baseProp2Item = baseProp2.accessor().item as PropertyEntityDefinition
		verifyPopulation(baseProp2Item,1,1);
	}


	/**
	 * Test creating filter on nested properties and on type 
	 */
	void test2() {

		String mainNS = 'http://www.my.namespace'

		String propertyNS = 'http://www.my.namespace/property'

		TypeDefinition personType;

		// build schema
		schema = new SchemaBuilder(defaultPropertyTypeNamespace: propertyNS).schema(mainNS) {
			personType = Person {
				name()
				age(Integer)
				address(cardinality: '0..n') {
					street()
					number()
					city()
				}
				relative(cardinality: '0..n', String) {
					name()
					age(Integer)
				}
			}
		}

		// build instance collection
		instances = new InstanceBuilder(types: schema).createCollection {
			Person {
				name 'john'
				age 31
				address {
					street 'alexander'
					number 05
					city 'Darmstadt'
				}
				address {
					street 'Frid-Wilh'
					number 02
				}
				relative('father') {
					name 'Markus Mustermann'
					age 56
				}
			}
			Person {
				name 'dev'
				age 24
				address {
					street 'Taunusanlage'
					number 12
					city 'Frankfurt'
				}
				address {
					street 'Harthweg'
					number 05
				}
				relative('father') {
					name 'David Donald'
					age 45
				}
			}
			Person {
				name 'robin'
				age 18
				address {
					street 'ale'
					number 12
					city 'Musterstadt'
				}
				address {
					street 'Taubengasse'
					number 13
				}
				relative('mother') {
					name 'Maria'
					age 52
				}
				relative('father') {
					name 'Neil'
					age 54
				}
			}
			Person {
				name 'alex'
				age 35
				address {
					street 'HugoJunkerStrasse'
					number 23
					city 'Frankfurt'
				}
				address {
					street 'Taubengasse'
					number 13
					city 'Frankfurt'
				}
				relative('father') {
					name 'Markus Mustermann'
					age 56
				}
			}
		}

		assertNotNull instances
		assertFalse instances.empty
		assertEquals 4, instances.size()

		TypeEntityDefinition typeEntity1 = new TypeEntityDefinition(personType, SchemaSpaceID.SOURCE, null)

		PropertyEntityDefinition propName = typeEntity1.accessor().name as PropertyEntityDefinition
		PropertyEntityDefinition propAge = typeEntity1.accessor().age as PropertyEntityDefinition
		PropertyEntityDefinition propAddress = typeEntity1.accessor().address as PropertyEntityDefinition

		PropertyEntityDefinition propInnerStreet = propAddress.accessor().street as PropertyEntityDefinition
		PropertyEntityDefinition propInnerNumber = propAddress.accessor().number as PropertyEntityDefinition
		PropertyEntityDefinition propInnerCity = propAddress.accessor().city as PropertyEntityDefinition

		PropertyEntityDefinition propRelative = typeEntity1.accessor().relative as PropertyEntityDefinition
		PropertyEntityDefinition propInnerName = propRelative.accessor().name as PropertyEntityDefinition
		PropertyEntityDefinition propInnerAge = propRelative.accessor().age as PropertyEntityDefinition

		//Add instances to Population
		addToPopulation()

		verifyPopulation(typeEntity1,4,4)
		verifyPopulation(propAddress,8,4)
		verifyPopulation(propRelative,5,4)
		verifyPopulation(propInnerStreet,8,8)
		verifyPopulation(propInnerNumber,8,8)
		verifyPopulation(propInnerCity,5,5)

		//filtered propertydefiniton
		PropertyEntityDefinition propAge2 = typeEntity1.accessor().age(filter: "value > '30'") as PropertyEntityDefinition
		addToPopulation(propAge2)
		verifyPopulation(propAge2,2,2)

		TypeEntityDefinition typeFilterPerson = new TypeEntityDefinition(personType, SchemaSpaceID.SOURCE, new FilterGeoCqlImpl("age = 24"))
		addToPopulation(typeFilterPerson)
		verifyPopulation(typeFilterPerson,1,1)
		PropertyEntityDefinition propFilterRelative = typeFilterPerson.accessor().relative as PropertyEntityDefinition
		verifyPopulation(typeFilterPerson.accessor().name as PropertyEntityDefinition,1,1)
		verifyPopulation(typeFilterPerson.accessor().age as PropertyEntityDefinition,1,1)
		verifyPopulation(typeFilterPerson.accessor().address as PropertyEntityDefinition,2,1)
		verifyPopulation(propFilterRelative,1,1)
		verifyPopulation(propFilterRelative.accessor().name as PropertyEntityDefinition,1,1)
		verifyPopulation(propFilterRelative.accessor().age as PropertyEntityDefinition,1,1)

		//filtered propertydefiniton
		PropertyEntityDefinition propFilterAddress2 = typeEntity1.accessor().address(filter: "value.city ='Frankfurt'") as PropertyEntityDefinition
		addToPopulation(propFilterAddress2)
		verifyPopulation(propFilterAddress2,3,2)
		verifyPopulation(propFilterAddress2.accessor().street as PropertyEntityDefinition,3,3)
		verifyPopulation(propFilterAddress2.accessor().number as PropertyEntityDefinition,3,3)
		verifyPopulation(propFilterAddress2.accessor().city as PropertyEntityDefinition,3,3)
	}

	private void addToPopulation(EntityDefinition entityDef){
		ResourceIterator<Instance> resIt = instances.iterator()
		while(resIt.hasNext()){
			container.addToPopulation(resIt.next(),entityDef)
		}
	}

	private void verifyPopulation(EntityDefinition entityDef, int overallCount, int parentCount){
		Population pop = container.getPopulation((EntityDefinition) entityDef)
		assertEquals overallCount, pop.getOverallCount();
		assertEquals parentCount, pop.getParentsCount();
	}
}

