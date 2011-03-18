package eu.esdihumboldt.hale.models.config;

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.models.ConfigSchemaService;
import eu.esdihumboldt.hale.models.HaleServiceListener;

public class ConfigSchemaServiceImpl implements ConfigSchemaService {
	
	private Map<String, String> config = new HashMap<String, String>();

	@Override
	public boolean addListener(HaleServiceListener sl) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(HaleServiceListener listener) {
		// TODO Auto-generated method stub

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

}
