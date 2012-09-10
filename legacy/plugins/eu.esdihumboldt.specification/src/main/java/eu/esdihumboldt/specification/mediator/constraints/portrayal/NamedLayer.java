/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator.constraints.portrayal;

import java.util.Set;

/**
 * A NamedLayer describes a layer of data that has a name advertised by a WMS.
 * 
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: NamedLayer.java,v 1.2 2007-11-06 09:32:37 pitaeva Exp $
 * 
 */
public interface NamedLayer {

	/**
	 * 
	 * @return Layer Name.
	 */
	public String getName();

	/**
	 * 
	 * @return a List of User Defined Styles.
	 */
	public Set<UserStyle> getUserStyle();

	/**
	 * 
	 * @ return a List of NamedStyles.
	 */
	public Set<NamedStyle> getNamedStyle();

	/**
	 * 
	 * @return a LayerFeatureConstraint.
	 */
	public LayerFeatureConstraint getLayerFeatureConstraint();

}
