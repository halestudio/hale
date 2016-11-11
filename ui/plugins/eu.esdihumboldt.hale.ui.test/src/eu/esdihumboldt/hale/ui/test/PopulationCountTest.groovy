package eu.esdihumboldt.hale.ui.test

import java.util.Set;

import javax.xml.namespace.QName

import org.junit.Before;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl
import eu.esdihumboldt.hale.common.instance.InstanceServiceFactory;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Filter
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.InstanceReference
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.ui.common.service.population.Population;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;
import eu.esdihumboldt.hale.ui.service.population.internal.PopulationServiceImpl

@SuppressWarnings("restriction")
class PopulationCountTest extends GroovyTestCase {
	
	private PopulationServiceImpl ps

	private InstanceCollection instances

	private Schema schema

	@Override
	protected void setUp() throws Exception {
		super.setUp()
		ps = new PopulationServiceImpl(getInstanceServiceObject())
		assertNotNull(ps)
	}

	/**
	 * Test creating nested default properties.
	 */
	void testNestedDefaultPropertiesWithInstances() {
		
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
	 * Test creating nested default properties.
	 */
	void test2() {
		
		String mainNS = 'http://www.my.namespace'
		
		String propertyNS = 'http://www.my.namespace/property'

		// build schema
		Schema schema = new SchemaBuilder(defaultPropertyTypeNamespace: propertyNS).schema(mainNS) {
			Person {
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

		// build instance
		Instance instance = new InstanceBuilder(types: schema).Person {
			name 'xyz'
			age 31
			address {
				street 'ale'
				number 12
				city 'Musterstadt'
			}
			address {
				street 'Taubengasse'
				number 13
			}
			relative('father') {
				name 'Markus Mustermann'
				age 56
			}
		}
		
		
		

	}

	private void addToPopulation(EntityDefinition entityDef){
		ResourceIterator<Instance> resIt = instances.iterator()
		while(resIt.hasNext()){
			if(!entityDef){
				ps.addToPopulation(resIt.next(), DataSet.SOURCE)
			}else{
				ps.countPopulation(entityDef,resIt.next())
			}
		}
	}

	private void verifyPopulation(EntityDefinition entityDef, int overallCount, int parentCount){
		Population pop = ps.getPopulation((EntityDefinition) entityDef)
		assertEquals overallCount, pop.getOverallCount();
		assertEquals parentCount, pop.getParentsCount();

	}

	private InstanceService getInstanceServiceObject(){
		return new InstanceService() {

					@Override
					public InstanceReference getReference(Instance instance) {
						// TODO Auto-generated method stub
						return null
					}

					@Override
					public Instance getInstance(InstanceReference reference) {
						// TODO Auto-generated method stub
						return null
					}

					@Override
					public void setTransformationEnabled(boolean enabled) {
						// TODO Auto-generated method stub

					}

					@Override
					public void removeListener(InstanceServiceListener listener) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean isTransformationEnabled() {
						// TODO Auto-generated method stub
						return false
					}

					@Override
					public InstanceCollection getInstances(DataSet dataset) {
						// TODO Auto-generated method stub
						return null
					}

					@Override
					public Set<TypeDefinition> getInstanceTypes(DataSet dataset) {
						// TODO Auto-generated method stub
						return null
					}

					@Override
					public void dropInstances() {
						// TODO Auto-generated method stub

					}

					@Override
					public void clearInstances() {
						// TODO Auto-generated method stub

					}

					@Override
					public void addSourceInstances(InstanceCollection sourceInstances) {
						// TODO Auto-generated method stub

					}

					@Override
					public void addListener(InstanceServiceListener listener) {
						// TODO Auto-generated method stub

					}
				};

	}



	//	Person {
	//		name()
	//		age(Integer)
	//		address(cardinality: '0..n') {
	//			street()
	//			number()
	//			city()
	//		}
	//		relative(cardinality: '0..n', String) {
	//			name()
	//			age(Integer)
	//		}
	//	}

}

