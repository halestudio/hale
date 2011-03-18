package eu.esdihumboldt.hale.models;

public interface ConfigSchemaService extends UpdateService {

	public void add(String key, String value);
	
	public void remove(String key);
	
	public String get(String key);
}
