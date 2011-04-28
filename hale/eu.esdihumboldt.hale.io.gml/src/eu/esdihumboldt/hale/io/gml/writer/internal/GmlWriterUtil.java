/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.util.Set;

import org.opengis.feature.type.Name;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Utility methods used for the GML writer
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class GmlWriterUtil {
	
	private static final ALogger log = ALoggerFactory.getLogger(GmlWriterUtil.class);
	
	/**
	 * Get the element name from a type definition
	 * 
	 * @param type the type definition
	 * @return the element name
	 */
	public static Name getElementName(TypeDefinition type) {
		Set<SchemaElement> elements = type.getDeclaringElements();
		if (elements == null || elements.isEmpty()) {
			log.debug("No schema element for type " + type.getDisplayName() +  //$NON-NLS-1$
					" found, using type name instead"); //$NON-NLS-1$
			return type.getName();
		}
		else {
			Name elementName = elements.iterator().next().getElementName();
			if (elements.size() > 1) {
				log.warn("Multiple element definitions for type " +  //$NON-NLS-1$
						type.getDisplayName() + " found, using element " +  //$NON-NLS-1$
						elementName.getLocalPart());
			}
			return elementName;
		}
	}

}
