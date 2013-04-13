/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.ui.util.bbr.impl

import org.eclipse.core.runtime.Platform

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder

import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.ui.util.bbr.Documentation
import eu.esdihumboldt.hale.ui.util.bbr.DocumentationService
import groovy.sql.Sql


/**
 * BBR documentation service based on a CSV file placed in the instance location.
 * 
 * @author Simon Templer
 */
class DocumentationServiceImpl implements DocumentationService {

	/**
	 * Namespace URI expected for BBR types and attributes.
	 */
	public static final String NS_URI_AGEOBW = 'http://www.bund.de/AGeoBw'

	public static final String BBR_FILE_NAME = 'bbr.csv'

	static final String tableName = 'bbr'

	/*
	 * Table definition for a file with every column between 'Complete' and
	 * 'Dataset' removed (including).
	 */
	static final String tableDefinition = """CREATE TEXT TABLE $tableName (
    	InUse VARCHAR(255),
		Notes VARCHAR(255),
		Register VARCHAR(255),
		AlphaCode VARCHAR(255),
		Type VARCHAR(255),
		F531 VARCHAR(255),
		A531 VARCHAR(255),
		V531 VARCHAR(255),
		FAlpha VARCHAR(255),
		AAlpha VARCHAR(255),
		VAlpha VARCHAR(255),
		Name VARCHAR(255),
		Definition LONGVARCHAR,
		Description LONGVARCHAR,
		Alias VARCHAR(255),
		Multiplicity VARCHAR(255),
		Collection VARCHAR(255),
		DatatypeLink VARCHAR(255),
		PhysicalQuantity VARCHAR(255),
		RecommendedUnitOfMeasure VARCHAR(255),
		NonComparableUnitOfMeasure VARCHAR(255),
		ListedValue VARCHAR(255),
		DatatypeLinkShortName VARCHAR(255),
		DatatypeName VARCHAR(255),
		NameDEU VARCHAR(255),
		DefinitionDEU VARCHAR(255),
		DescriptionDEU VARCHAR(255),
		ID INTEGER PRIMARY KEY
	);"""

	private Sql db

	private final Cache<Definition<?>, Documentation> cache

	DocumentationServiceImpl() {
		cache = CacheBuilder.newBuilder().weakKeys().build()

		init()
	}

	void init() {
		URL instanceLoc = Platform.instanceLocation.getURL()
		File instanceDir = new File(instanceLoc.toURI())
		// File instanceDir = new File('/home/simon-local/workspaces/runtime-HALE.product')

		//create a new file database and a table corresponding to the csv file
		db = Sql.newInstance("jdbc:hsqldb:file:${instanceDir.absolutePath}/bbrdb",
				'sa', '', 'org.hsqldb.jdbcDriver')

		try {
			// create table
			db.execute(tableDefinition)
		} catch (any) {
			// ignore, DB already created
		}

		//set the source to the csv file
		db.execute("SET TABLE $tableName SOURCE '${BBR_FILE_NAME};ignore_first=true;all_quoted=true'".toString())
	}

	@Override
	public Documentation getDocumentation(Definition<?> definition) {
		if (definition.name.namespaceURI != NS_URI_AGEOBW)
			return null

		def sql = null;

		String shortCodeField;
		switch (definition) {
			case TypeDefinition:
				sql = "SELECT * FROM bbr WHERE Type = 'Feat' AND FAlpha = ${definition.displayName}"
				shortCodeField = 'F531'
				break;
			case PropertyDefinition:
				sql = "SELECT * FROM bbr WHERE Type = 'Att' AND AAlpha = ${definition.name.localPart}"
				shortCodeField = 'A531'
				break;
		}

		if (sql != null) {
			synchronized (cache) {
				Documentation cached = cache.getIfPresent(definition);
				if (cached) return cached

				def result = db.firstRow(sql)
				if (result) {
					Documentation doc = createDoc(result, shortCodeField)

					if (definition instanceof PropertyDefinition) {
						// there may also be value documentation
						db.eachRow("SELECT * FROM bbr WHERE Type = 'Value' AND AAlpha = ${definition.name.localPart}") {
							Documentation valueDoc = createDoc(it, 'V531')

							// test value doc against property type enum
							checkForUseConflict(valueDoc, definition.propertyType)

							doc.values << valueDoc
						}
					}

					cache.put(definition, doc)
					return doc
				}
				//TODO also cache nulls?
			}
		}

		return null;
	}

	private Documentation createDoc(def row, String shortCodeField = null) {
		Documentation result = new Documentation(name: row.Name,
				code: row.AlphaCode,
				definition: row.Definition,
				description: row.Description,
				inUse: row.InUse == 'YES' ? true : false)

		if (shortCodeField) {
			String shortCode = row."$shortCodeField"
			if (shortCode) {
				result.shortCode = shortCode
			}
		}

		result
	}

	private void checkForUseConflict(Documentation doc, TypeDefinition type) {
		def enumeration = type.getConstraint(eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration);

		if (enumeration.values) {
			// only do a check for actual enumerations
			//XXX try validation for other types?

			if (doc.inUse && !enumeration.isAllowOthers()) {
				// test if a value that should be there actually is there
				doc.useDiffers = !enumeration.values.contains(doc.code)
			}
			else if (!doc.inUse) {
				// test if a value that should not be there is there
				doc.useDiffers = enumeration.values.contains(doc.code)
			}
		}
	}

}
