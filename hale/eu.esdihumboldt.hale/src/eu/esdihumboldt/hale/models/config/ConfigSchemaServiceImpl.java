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
import eu.esdihumboldt.hale.models.project.generated.ConfigData;
import eu.esdihumboldt.hale.models.project.generated.ConfigSchema;
import eu.esdihumboldt.hale.models.project.generated.ConfigSection;

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
	
//	private ArrayList<ConfigSection> sections = new ArrayList<ConfigSection>();
	//			section			key,	value
	private Map<String, HashMap<String, String>> sections = new HashMap<String, HashMap<String, String>>();
	
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
			hsl.update(new UpdateMessage(ConfigSchemaService.class, null));
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

	@Override
	public void addSection(String name) {
		this.addSection(name, new HashMap<String, String>());
	}

	@Override
	public void addSection(String name, HashMap<String, String> data) {
		this.sections.put(name, data);
	}

	@Override
	public void removeSection(String name) {
		this.sections.remove(name);
	}

	@Override
	public HashMap<String, String> getSectionData(String name) {
		return this.sections.get(name);
	}
	
	@Override
	public void addItem(String section, String key, String value) {
		if (this.sections.get(section) == null) {
			this.addSection(section);
		}
		this.sections.get(section).put(key, value);
	}

	@Override
	public void parseConfig(List<ConfigSection> list) {
		for(ConfigSection section : list) {
			String name = section.getName();
			this.addSection(name);
			
			for(ConfigData data : section.getData()) {
				this.addItem(name, data.getKey(), data.getValue());
			}
		}
		
		this.updateListeners();
	}

	@Override
	public List<ConfigSection> generateConfig() {
		List<ConfigSection> list = new ArrayList<ConfigSection>();
		
		for(Map.Entry<String, HashMap<String, String>> entry : this.sections.entrySet()) {
			String name = entry.getKey();
			List<ConfigData> lData = new ArrayList<ConfigData>();
			
			for(Map.Entry<String, String> data : entry.getValue().entrySet()) {
				ConfigData configData = new ConfigData();
				configData.setKey(data.getKey());
				configData.setValue(data.getValue());
				lData.add(configData);
			}
			
			ConfigSection configSection = new ConfigSection();
			configSection.setName(name);
			configSection.getData().addAll(lData);
			
			list.add(configSection);
		}
		
		return list;
	}
}
