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
		Complete VARCHAR(255),
		Source VARCHAR(255),
		Extraction VARCHAR(255),
		FabricationProcessing VARCHAR(255),
		Agriculture VARCHAR(255),
		PowerSupplies VARCHAR(255),
		Communication VARCHAR(255),
		AssociatedSupportStructures VARCHAR(255),
		StorageProvision VARCHAR(255),
		WasteManagement VARCHAR(255),
		Habitats VARCHAR(255),
		SettlementsAssociated VARCHAR(255),
		EconomicCommercial VARCHAR(255),
		Leisure VARCHAR(255),
		PoliticsAdministration VARCHAR(255),
		SciencesEducation VARCHAR(255),
		CulturalContext VARCHAR(255),
		Railways VARCHAR(255),
		RoadsTracks VARCHAR(255),
		GuidedTransportation VARCHAR(255),
		WaterBorneTransportation VARCHAR(255),
		AirTransportation VARCHAR(255),
		Restrictions VARCHAR(255),
		CrossingsLinks VARCHAR(255),
		TransportationAssociated VARCHAR(255),
		SpaceTransportation VARCHAR(255),
		DistributionNetworks VARCHAR(255),
		CoastalLittoralZones VARCHAR(255),
		PortsHarbours VARCHAR(255),
		Depths VARCHAR(255),
		NatureOfSeabed VARCHAR(255),
		OffshoreConstructionsInstallations VARCHAR(255),
		TidesCurrents VARCHAR(255),
		RoutesNavigation VARCHAR(255),
		HazardsObstructions VARCHAR(255),
		SeaIce VARCHAR(255),
		RegulatedRestrictedZones VARCHAR(255),
		InlandWaters VARCHAR(255),
		PhysicsOfWater VARCHAR(255),
		Hypsography VARCHAR(255),
		Geomorphology VARCHAR(255),
		Rocks VARCHAR(255),
		Soils VARCHAR(255),
		NaturalResources VARCHAR(255),
		SeismologyVolcanology VARCHAR(255),
		Glaciers VARCHAR(255),
		Anomalies VARCHAR(255),
		GlobalEarthCover VARCHAR(255),
		CultivatedLand VARCHAR(255),
		Rangeland VARCHAR(255),
		Woodland VARCHAR(255),
		Wetland VARCHAR(255),
		AridAreas VARCHAR(255),
		RegionsRestrictedAreas VARCHAR(255),
		Fauna VARCHAR(255),
		Flora VARCHAR(255),
		BoundariesLimits VARCHAR(255),
		LandSurveyRealEstate VARCHAR(255),
		AerodromesMovementSurfacesLighting VARCHAR(255),
		AirspaceRoutes VARCHAR(255),
		NAVAIDSLandingAidsPointsObstacles VARCHAR(255),
		ServicesOrganisationsTimetables VARCHAR(255),
		TerminalProcedures VARCHAR(255),
		DefensiveOperationalStructures VARCHAR(255),
		RestrictedAreasBoundaries VARCHAR(255),
		OperationsEvents VARCHAR(255),
		WeatherPhenomena VARCHAR(255),
		ClimateConditions VARCHAR(255),
		ClimateZonesRegions VARCHAR(255),
		Geointelligence VARCHAR(255),
		Position VARCHAR(255),
		MeasurableValues VARCHAR(255),
		DatesDurations VARCHAR(255),
		Appearance VARCHAR(255),
		FunctionStatus VARCHAR(255),
		Names VARCHAR(255),
		Designations VARCHAR(255),
		Annotation VARCHAR(255),
		Portrayal VARCHAR(255),
		DateCurrency VARCHAR(255),
		Quality VARCHAR(255),
		ReferencesSources VARCHAR(255),
		SystemsClassification VARCHAR(255),
		InformationEntity VARCHAR(255),
		Dataset VARCHAR(255),
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

		switch (definition) {
			case TypeDefinition:
				sql = "SELECT * FROM bbr WHERE Type = 'Feat' AND FAlpha = ${definition.displayName}"
				break;
			case PropertyDefinition:
				sql = "SELECT * FROM bbr WHERE Type = 'Att' AND AAlpha = ${definition.name.localPart}"
				break;
		}

		if (sql != null) {
			synchronized (cache) {
				Documentation cached = cache.getIfPresent(definition);
				if (cached) return cached

				def result = db.firstRow(sql)
				if (result) {
					Documentation doc = createDoc(result)

					if (definition instanceof PropertyDefinition) {
						// there may also be value documentation
						db.eachRow("SELECT * FROM bbr WHERE Type = 'Value' AND AAlpha = ${definition.name.localPart}") {
							doc.values << createDoc(it)
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

	private Documentation createDoc(def row) {
		new Documentation(name: row.AlphaCode,
				definition: row.Definition,
				description: row.Description)
	}

}
