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

package eu.esdihumboldt.hale.io.xls.test

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.Schema
import groovy.transform.CompileStatic

class XLSInstanceWriterTestExamples {
	
	static InstanceCollection createInstanceCollection(){
		
		Schema schema = createSchema()
		
		// create the instance collection
		// concrete types are only strings, since the test is not able to choose the correct type in wizard
		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			ItemType{
					id('12')
					name('item12')
					price('1.2')
					description('Item number 12')
				}

			ItemType {
					id('42')
					name('item42')
					price('4.2')
					description('Item number 42')
				}
			
			ItemType {
				id('42')
				price('4.2')
				description('Item number 42')
				name(null)
			}
			
			ItemType {
				id('42')
				name('item42')
				price(null)
				description(null)
			}
			
			OtherType {
				children('2')
				name('other')
				number('1')
				description('other type')
			}
		}
	}
	
	static InstanceCollection createFalseTestInstanceCollection(){
		Schema schema = createSchema()
		
		// create the instance collection
		InstanceCollection instances = new InstanceBuilder(types: schema).createCollection {
			ItemType{
					id('55')
					name('item55')
					price('5.5')
					description('Item number 55')
				}
		}
	}
	
	static Schema createSchema(){
		Schema schema = new SchemaBuilder().schema {
			ItemType {
				id(String)
				name(String)
				price(String)
				description(String)
			}
			
			OtherType {
				children(String)
				name(String)
				number(String)
				description(String)
			}
		}
		return schema;
	}

}
