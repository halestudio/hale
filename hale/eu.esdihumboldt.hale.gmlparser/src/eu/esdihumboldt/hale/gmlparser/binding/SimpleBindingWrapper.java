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

package eu.esdihumboldt.hale.gmlparser.binding;

import javax.xml.namespace.QName;

import org.geotools.xml.AttributeInstance;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.InstanceComponent;
import org.geotools.xml.SimpleBinding;

/**
 * Simple binding wrapper
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimpleBindingWrapper implements SimpleBinding {

	private final SimpleBinding binding;
	
	private final QName name;

	/**
	 * Constructor
	 * 
	 * @param name the target name 
	 * @param binding the internal simple binding
	 */
	public SimpleBindingWrapper(QName name, SimpleBinding binding) {
		super();
		
		this.name = name;
		this.binding = binding;
	}
	
	/**
	 * @see org.geotools.xml.SimpleBinding#encode(java.lang.Object, java.lang.String)
	 */
	@Override
	public String encode(Object object, String value) throws Exception {
		return binding.encode(object, value);
	}

	/**
	 * @see org.geotools.xml.Binding#getExecutionMode()
	 */
	@Override
	public int getExecutionMode() {
		return binding.getExecutionMode();
	}

	/**
	 * @see org.geotools.xml.Binding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return name;
//		return binding.getTarget();
	}

	/**
	 * @see org.geotools.xml.Binding#getType()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Class getType() {
		return binding.getType();
	}

	/**
	 * @see org.geotools.xml.SimpleBinding#parse(org.geotools.xml.InstanceComponent, java.lang.Object)
	 */
	@Override
	public Object parse(InstanceComponent instance, Object value)
			throws Exception {
		Object result = binding.parse(instance, value);
		
		if (instance instanceof ElementInstance) {
			AttributeInstance[] attributes = ((ElementInstance) instance).getAttributes();
			if (attributes != null && attributes.length > 0) {
				AttributesWrapper wrapper = new AttributesWrapper(result);
				
				for (AttributeInstance attribute : attributes) {
					wrapper.addAttribute(attribute.getName(), attribute.getText());
				}
				
				result = wrapper;
			}
		}
		
		return result;
	}

}
