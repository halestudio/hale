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

package eu.esdihumboldt.hale.ui.service.config.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.ui.service.config.ConfigSchemaService;
import eu.esdihumboldt.hale.ui.service.config.ConfigSchemaServiceListener;
import eu.esdihumboldt.hale.ui.service.config.ConfigSchemaServiceListener.Message;
import eu.esdihumboldt.hale.ui.service.project.internal.generated.ConfigData;
import eu.esdihumboldt.hale.ui.service.project.internal.generated.ConfigSection;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ConfigSchemaServiceImpl implements ConfigSchemaService {
	
	/**
	 * Contains the internal representation of configSchema
	 */
	private Map<String, HashMap<String, String>> sections = new HashMap<String, HashMap<String, String>>();
	
	private Map<String, List<ConfigSchemaServiceListener>> listeners = new HashMap<String, List<ConfigSchemaServiceListener>>();
	
	@Override
	public void addListener(ConfigSchemaServiceListener sl, String section) {
		List<ConfigSchemaServiceListener> sls = this.listeners.get(section);
		if (sls == null) {
			sls = new ArrayList<ConfigSchemaServiceListener>();
			sls.add(sl);
			this.listeners.put(section, sls);
		} else {
			if (!sls.contains(sl)) {
				sls.add(sl);
			}
		}
	}
	
	@Override
	public void removeListener(String section, ConfigSchemaServiceListener sl) {
		this.listeners.get(section).remove(sl);
	}
	
	@Override
	public void removeListener(ConfigSchemaServiceListener sl) {
		for(Map.Entry<String, List<ConfigSchemaServiceListener>> entry : this.listeners.entrySet()) {
			entry.getValue().remove(sl);
		}
	}
	
	private void updateListeners(String section, Message message) {
		Set<ConfigSchemaServiceListener> listeners = new HashSet<ConfigSchemaServiceListener>();
		
		// get listeners for section
		List<ConfigSchemaServiceListener> list = this.listeners.get(section);
		if (list != null) {
			listeners.addAll(list);
		}
		
		// get listeners for any section
		list = this.listeners.get(null);
		if (list != null) {
			listeners.addAll(list);
		}
		
		for(ConfigSchemaServiceListener cssl : listeners) {
			cssl.update(section, message);
		}
	}

	@Override
	public void addSection(String name) {
		this.addSection(name, new HashMap<String, String>());
	}

	@Override
	public void addSection(String name, HashMap<String, String> data) {
		this.sections.put(name, data);
		this.updateListeners(name, Message.SECTION_ADDED);
	}

	@Override
	public void removeSection(String name) {
		this.sections.remove(name);
		this.updateListeners(name, Message.SECTION_REMOVED);
	}

	@Override
	public HashMap<String, String> getSectionData(String name) {
		return this.sections.get(name);
	}
	
	@Override
	public String getItem(String section, String key) {
		if (this.sections.get(section) == null) {
			return null;
		}
		return this.sections.get(section).get(key);
	}
	
	@Override
	public void addItem(String section, String key, String value) {
		Message message = Message.ITEM_CHANGED;
		
		if (this.sections.get(section) == null) {
			message = Message.ITEM_ADDED;
			this.addSection(section);
			this.sections.get(section).put(key, value);
			this.updateListeners(section, message);
			return;
		}
		
		if (this.sections.get(section).get(key) != null) {
			if (!this.sections.get(section).get(key).equals(value)) {
				this.sections.get(section).put(key, value);
				this.updateListeners(section, message);
			}
		} else {
			this.sections.get(section).put(key, value);
		}
		
		
	}

	@Override
	public void parseConfig(List<ConfigSection> list) {
		for(ConfigSection section : list) {
			String name = section.getName();
			this.addSection(name);
			
			for(ConfigData data : section.getData()) {
				this.addItem(name, data.getKey(), data.getValue());
			}
			
			this.updateListeners(name, Message.CONFIG_PARSED);
		}
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
		
		this.updateListeners("", Message.CONFIG_GENERATED);
		
		return list;
	}
}
