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

package eu.esdihumboldt.hale.models.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.models.ConfigSchemaService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.UpdateService;
import eu.esdihumboldt.hale.models.project.generated.ConfigSchema;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ConfigSchemaServiceImpl implements ConfigSchemaService {
	
	/**
	 * Contains the button configuration.
	 */
	private Map<String, String> config = new HashMap<String, String>();
	
	/**
	 * Contains all classes which will be notified.
	 */
	private ArrayList<HaleServiceListener> listeners = new ArrayList<HaleServiceListener>();
	
	/**
	 * @see UpdateService#addListener(HaleServiceListener)
	 */
	@Override
	public boolean addListener(HaleServiceListener sl) {
		return this.listeners.add(sl);
	}

	/**
	 * @see UpdateService#removeListener(HaleServiceListener)
	 */
	@Override
	public void removeListener(HaleServiceListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update(new UpdateMessage(ConfigSchemaServiceImpl.class, null));
		}
	}

	@Override
	public void add(String key, String value) {
		this.config.put(key, value);
	}

	@Override
	public void remove(String key) {
		this.config.remove(key);
	}

	@Override
	public String get(String key) {
		return this.config.get(key);
	}

	@Override
	public List<ConfigSchema> getAll() {
		List<ConfigSchema> list = new ArrayList<ConfigSchema>();
		ConfigSchema schema = null;
		
		for(Map.Entry<String, String> entry : config.entrySet()) {
			schema = new ConfigSchema();
			schema.setKey(entry.getKey());
			schema.setValue(entry.getValue());
			
			list.add(schema);
		}
		
		return list;
	}
	
	@Override
	public void setAll(List<ConfigSchema> list) {
		for(ConfigSchema s : list) {
			this.add(s.getKey(), s.getValue());
		}
		
		this.updateListeners();
	}
}
