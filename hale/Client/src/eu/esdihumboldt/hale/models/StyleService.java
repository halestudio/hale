/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models;

import java.net.URL;

import org.geotools.styling.Style;
import org.opengis.feature.type.FeatureType;

/**
 * The {@link StyleService} provides access to the Styles currently loaded.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface StyleService 
	extends UpdateService {
	
	/**
	 * @param ft the {@link FeatureType} for which to return a {@link Style}.
	 * @return a {@link Style} for the given {@link FeatureType}.
	 */
	public Style getStyle(FeatureType ft);
	
	/**
	 * 
	 * @param name the String that identifies the {@link Style} that should be 
	 * returned.
	 * @return a {@link Style} for the given name. Will return a new 
	 * {@link Style} object if there is no Style of the given name.
	 */
	public Style getNamedStyle(String name);
	
	/**
	 * @param url the URL from which to load an SLD document.
	 * @return true if loading the URL was successful.
	 */
	public boolean addStyles(URL url);

}
