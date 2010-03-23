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

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.NameImpl;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

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
	
	public ShapeSchemaProvider() {
		super.addSupportedFormat("shp");
	}

	/**
	 * @see eu.esdihumboldt.hale.schemaprovider.SchemaProvider#loadSchema(java.net.URI, eu.esdihumboldt.hale.schemaprovider.ProgressIndicator)
	 */
	public Schema loadSchema(URI location, ProgressIndicator progress)
			throws IOException {
		progress.setCurrentTask("Analysing shapefile.");
		DataStore store = FileDataStoreFinder.getDataStore(location.toURL());
		
		
		progress.setCurrentTask("Extracting Type Definitions.");
		Map<String, SchemaElement> elements = new HashMap<String, SchemaElement>();
		
		for (Name name : store.getNames()) {
			SimpleFeatureType sft = store.getSchema(name);
			progress.setCurrentTask("Extracting Type Definition for " 
					+ sft.getTypeName());
			
			
			TypeDefinition type = new TypeDefinition(name, sft, null);
			for (PropertyDescriptor pd : sft.getDescriptors()) {
				type.addDeclaredAttribute(new ShapeAttributeDefintion(pd));
			}
			
			elements.put(
					sft.getName().getNamespaceURI() + "/" + sft.getTypeName(), 
					new SchemaElement(name, type.getType().getName(), type ));
		}

		String namespace = "http://www.esdi-humboldt.eu/schema/temp";
		return new Schema(elements, namespace, location.toURL());
	}
	
	public class ShapeAttributeDefintion extends AttributeDefinition {
		
		private PropertyDescriptor pd = null;

		/**
		 * 
		 * @param pd
		 */
		public ShapeAttributeDefintion(PropertyDescriptor pd) {
			super(pd.getName().getLocalPart(), ShapeSchemaProvider.getName(pd), 
					new TypeDefinition(ShapeSchemaProvider.getName(pd), 
							(AttributeType) pd.getType(), 
							null), true);
			this.pd = pd;
		}
		
		

		@Override
		public AttributeDefinition copyAttribute(TypeDefinition parentType) {
			// TODO Auto-generated method stub
			return this;
		}

		@Override
		public AttributeDescriptor createAttributeDescriptor() {
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
	
	private static Name getName(PropertyDescriptor pd) {
		if (pd.getType().getBinding().equals(Integer.class)) {
			return new NameImpl("http://www.w3.org/2001/XMLSchema", 
					"int");
		}
		else if (pd.getType().getBinding().equals(Long.class)) {
			return new NameImpl("http://www.w3.org/2001/XMLSchema", 
					"long");
		}
		else if (pd.getType().getBinding().equals(Double.class)) {
			return new NameImpl("http://www.w3.org/2001/XMLSchema", 
					"double");
		}
		else if (pd.getType().getBinding().equals(String.class)) {
			return new NameImpl("http://www.w3.org/2001/XMLSchema", 
					"string");
		}
		else if (Geometry.class.isAssignableFrom(pd.getType().getBinding())) {
			return new NameImpl("http://www.opengis.net/gml", 
					pd.getType().getBinding().getSimpleName());
		}
		else {
			return pd.getType().getName();
		}
	}

}
