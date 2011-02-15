/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.schemaprovider.provider;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.schemaprovider.AbstractSchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.ProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Reads a schema from a shapefile.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class ShapeSchemaProvider 
	extends AbstractSchemaProvider {
	
	/**
	 * Default constructor for {@link ShapeSchemaProvider}.
	 */
	public ShapeSchemaProvider() {
		super.addSupportedFormat("shp");
	}

	/**
	 * @see eu.esdihumboldt.hale.schemaprovider.SchemaProvider#loadSchema(java.net.URI, eu.esdihumboldt.hale.schemaprovider.ProgressIndicator)
	 */
	public Schema loadSchema(URI location, ProgressIndicator progress)
			throws IOException {
		progress.setCurrentTask("Analysing shapefile.");
//		DataStore store = new ShapefileDataStoreFactory().createDataStore(location.toURL());
		DataStore store = FileDataStoreFinder.getDataStore(location.toURL());
		
		progress.setCurrentTask("Extracting Type Definitions.");
		Map<String, SchemaElement> elements = new HashMap<String, SchemaElement>();
		
		// build AbstractfeatureType as root for all types extracted from Shapefile
		Name aftName = new NameImpl("http://www.opengis.net/gml", "AbstractFeatureType");

		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(aftName.getLocalPart());
			ftbuilder.setNamespaceURI(aftName.getNamespaceURI());
			ftbuilder.setAbstract(true);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		TypeDefinition abstractFeatureType = new TypeDefinition(
				aftName, ft, null);
		abstractFeatureType.setAbstract(true);
		elements.put(aftName.getNamespaceURI() + "/" + aftName.getLocalPart(), 
				new SchemaElement(aftName, abstractFeatureType.getName(), 
						abstractFeatureType, null));
		
		// build actual FeatureTypes based on Schema extracted by geotools
		for (Name name : store.getNames()) {
			SimpleFeatureType sft = store.getSchema(name);
			try {
				SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
				ftbuilder.setName(sft.getName().getLocalPart());
				ftbuilder.setNamespaceURI("http://www.opengis.net/gml");
				for (AttributeDescriptor ad : sft.getAttributeDescriptors()) {
					ftbuilder.add(ad);
				}
				ftbuilder.setCRS(sft.getCoordinateReferenceSystem());
				ftbuilder.setSuperType(ft);
				sft = ftbuilder.buildFeatureType();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			progress.setCurrentTask("Extracting Type Definition for " 
					+ sft.getTypeName());
			
			TypeDefinition type = new TypeDefinition(sft.getName(), sft, abstractFeatureType);
			for (PropertyDescriptor pd : sft.getDescriptors()) {
				type.addDeclaredAttribute(new ShapeAttributeDefintion(pd));
			}
			
			SchemaElement se = new SchemaElement(sft.getName(), 
					type.getType(null).getName(), type, null);
			elements.put(sft.getName().getNamespaceURI() + "/" + sft.getTypeName(), 
					se);
		}

		String namespace = "http://www.opengis.net/gml";
		return new Schema(elements, namespace, location.toURL(), null);
	}
	
	/**
	 * A specific {@link AttributeDefinition} that applies custom naming etc. 
	 * rules.
	 */
	public class ShapeAttributeDefintion extends AttributeDefinition {
		
		private PropertyDescriptor pd = null;

		/**
		 * 
		 * @param pd
		 */
		public ShapeAttributeDefintion(PropertyDescriptor pd) {
			super(pd.getName().getLocalPart(), 
					ShapeSchemaProvider.getName((AttributeType) pd.getType()), 
					new TypeDefinition(
							ShapeSchemaProvider.getName((AttributeType) pd.getType()), 
							ShapeSchemaProvider.getCompletedAttributeType(pd.getType()), 
							null), 
					true, null);
			this.pd = pd;
		}
		
		

		@Override
		public AttributeDefinition copyAttribute(TypeDefinition parentType) {
			// TODO Auto-generated method stub
			return this;
		}

		@Override
		public AttributeDescriptor createAttributeDescriptor(Set<TypeDefinition> resolving) {
			return (AttributeDescriptor) this.pd;
		}

		@Override
		public long getMaxOccurs() {
			return this.pd.getMaxOccurs();
		}

		@Override
		public long getMinOccurs() {
			return this.pd.getMinOccurs();
		}

		@Override
		public boolean isNillable() {
			return this.pd.isNillable();
		}
		
	}
	
	/**
	 * adds namespace and correct typename for given bindings in a passed 
	 * {@link AttributeType}.
	 * @param at
	 * @return
	 */
	private static Name getName(PropertyType at) {
		
		if (at.getBinding().equals(Integer.class)) {
			return new NameImpl("http://www.w3.org/2001/XMLSchema", 
					"int");
		}
		else if (at.getBinding().equals(Long.class)) {
			return new NameImpl("http://www.w3.org/2001/XMLSchema", 
					"long");
		}
		else if (at.getBinding().equals(Double.class)) {
			return new NameImpl("http://www.w3.org/2001/XMLSchema", 
					"double");
		}
		else if (at.getBinding().equals(String.class)) {
			return new NameImpl("http://www.w3.org/2001/XMLSchema", 
					"string");
		}
		else if (Geometry.class.isAssignableFrom(at.getBinding())) {
			return new NameImpl("http://www.opengis.net/gml", 
					at.getBinding().getSimpleName());
		}
		else {
			return at.getName();
		}
	}
	
	private static AttributeType getCompletedAttributeType(PropertyType pt) {
		if (pt instanceof AttributeTypeImpl) {
			AttributeType at = (AttributeType) pt;
			return new AttributeTypeImpl(
					getName(at), 
					at.getBinding(), 
					at.isIdentified(), 
					at.isAbstract(), 
					at.getRestrictions(), 
					at.getSuper(), 
					at.getDescription());
		}
		else if (pt instanceof GeometryTypeImpl) {
			GeometryTypeImpl ga = (GeometryTypeImpl) pt;
			return new GeometryTypeImpl(
					getName(ga), 
					ga.getBinding(), 
					ga.getCoordinateReferenceSystem(), 
					ga.isIdentified(), 
					ga.isAbstract(), 
					ga.getRestrictions(), 
					ga.getSuper(), 
					ga.getDescription());
		}
		else {
			return (AttributeType) pt;
		}
		
	}

}
