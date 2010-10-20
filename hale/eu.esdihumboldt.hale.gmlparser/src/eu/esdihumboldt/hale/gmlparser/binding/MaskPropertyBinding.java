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

import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.geotools.xml.impl.InstanceBinding;

/**
 * Binding that forwards a value from one child (the property that is masked by
 * the type associated to this binding)
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MaskPropertyBinding extends AbstractComplexBinding 
	implements InstanceBinding {
	
	private QName name;
	
	private Class<?> binding;

	/**
	 * @param name the type name
	 * @param binding the type binding (which matches the masked property's type
	 *   binding)
	 */
	public MaskPropertyBinding(QName name, Class<?> binding) {
		super();
		this.name = name;
		this.binding = binding;
	}

	/**
	 * @see org.geotools.xml.Binding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return name;
	}

	/**
	 * @see org.geotools.xml.Binding#getType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class getType() {
		return binding;
	}

	/**
	 * @see AbstractComplexBinding#parse(ElementInstance, Node, Object)
	 */
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		
		return node.getChildValue(binding);
	}

}
