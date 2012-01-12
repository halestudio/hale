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
package eu.esdihumboldt.hale.ui.style.service;

import java.net.URL;

import org.eclipse.swt.graphics.RGB;
import org.geotools.styling.Style;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * The {@link StyleService} provides access to the Styles currently loaded.
 * 
 * @author Thorsten Reitz, Simon Templer 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface StyleService {
	
	/**
	 * Get the style for a given type.
	 * @param type the type definition
	 * @param dataSet the type data set
	 * @return a {@link Style} for the given type.
	 */
	public Style getStyle(TypeDefinition type, DataSet dataSet);
	
	/**
	 * Get a style combining all registered styles.
	 * @return the style
	 */
	public Style getStyle();
	
	/**
	 * Get a style combining all styles for the given data set.
	 * @param dataset the data set
	 * @return the style
	 */
	public Style getStyle(DataSet dataset);
	
	/**
	 * 
	 * @param name the String that identifies the {@link Style} that should be 
	 * returned.
	 * @return a {@link Style} for the given name. Will return a new 
	 * {@link Style} object if there is no Style of the given name.
	 */
	public Style getNamedStyle(String name);
	
	/**
	 * Add styles from a given URL.
	 * @param url the URL from which to load an SLD document.
	 * @return true if loading the URL was successful.
	 */
	public boolean addStyles(URL url);
	
	/**
	 * Add styles to the style service. Will override any styles that
	 * exist for the same types.
	 * @param styles the styles to add
	 */
	public void addStyles(Style...styles);
	
	/**
	 * Clear all styles
	 */
	public void clearStyles();

	/**
	 * Get a style combining all selection styles for the given data set.
	 * @param type the data set
	 * @return the style
	 */
	public Style getSelectionStyle(DataSet type);
	
	/**
	 * Get the defined style for the given type. If none is defined,
	 * <code>null</code> will be returned.
	 * @param type the type definition
	 * @return the type style or <code>null</code>
	 */
	public Style getDefinedStyle(TypeDefinition type);
	
	/**
	 * Get the map background.
	 * @return the map background color
	 */
	public RGB getBackground();
	
	/**
	 * Set the map background.
	 * @param color the map background color
	 */
	public void setBackground(RGB color);
	
	/**
	 * Adds a style service listener.
	 * @param listener the listener to add
	 */
	public void addListener(StyleServiceListener listener);
	
	/**
	 * Removes a style service listener.
	 * @param listener the listener to remove
	 */
	public void removeListener(StyleServiceListener listener);

}
