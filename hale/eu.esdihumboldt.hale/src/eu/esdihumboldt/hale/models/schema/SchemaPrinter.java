package eu.esdihumboldt.hale.models.schema;
import java.util.Collection;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : HALE
 * 	 
 * Classname    : SchemaPrinter.java 
 * 
 * Author       : Bernd Schneiders, Logica
 * 
 * Created on   : Jun 3, 2009 -- 4:50:10 PM
 *
 */

/**
 * Helper class which provides a method to print a FeatureType collection
 * as a tree to the console.
 */
public class SchemaPrinter {
	
	/**
	 * Method to print a FeatureType collection to the console.
	 * 	
	 * @param featureTypes Collection of feature types which should be printed
	 */
	static public void printFeatureTypeCollection(Collection<FeatureType> featureTypes) {
		printFeatureTypeCollection(featureTypes, null, -1);
	}
	
	static private void printFeatureTypeCollection(Collection<FeatureType> featureTypes, FeatureType superType, int level) {
		level++;
		if (level == 0) System.out.println("Size of feature type collection: " + featureTypes.size());
		if (level == 10) return;
		for(FeatureType type : featureTypes) {
			if (level == 0 && type.getSuper() == null) {
				System.out.println(type.getName().getLocalPart());
				
				printFeatureTypeCollection(featureTypes, type, level);

				for (PropertyDescriptor pd : type.getDescriptors()) {
					for (int i = 0; i < level + 1; i++)	{
						System.out.print("\t");
					}
					System.out.println("-" + pd.getName().getLocalPart() + ":<" + pd.getType().getBinding().getSimpleName() + ">");
				}
				
			} else if (type.getSuper() != null && superType != null && superType.getName().getLocalPart().equals(type.getSuper().getName().getLocalPart())) {
					
				for (int i = 0; i < level; i++)	System.out.print("\t");
				System.out.println("+" +  type.getName().getLocalPart() );

				printFeatureTypeCollection(featureTypes, type, level);

				for (PropertyDescriptor pd : type.getDescriptors()) {
					for (int i = 0; i < level + 1; i++)	{
						System.out.print("\t");
					}
					System.out.println("oo " + pd.getName().getLocalPart() + ":<" + pd.getType() + ">");
				}
			}
		}
	}
}
