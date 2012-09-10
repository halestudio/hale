/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;

/**
 * Bean representing a {@link ChildContext}. Instead of the child definition it
 * contains its name.
 * 
 * @author Simon Templer
 */
public class ChildContextBean {

	private Integer contextName;

	private Integer contextIndex;

	private String conditionFilter;

	private QName childName;

	/**
	 * Default constructor
	 */
	public ChildContextBean() {
		super();
	}

	/**
	 * Create a child context bean from the given child context
	 * 
	 * @param context the child context
	 */
	public ChildContextBean(ChildContext context) {
		this(context.getContextName(), context.getIndex(),
				(context.getCondition() == null) ? (null) : (FilterDefinitionManager.getInstance()
						.asString(context.getCondition().getFilter())), context.getChild()
						.getName());
	}

	/**
	 * Create a child context bean with the given content
	 * 
	 * @param contextName the instance context name
	 * @param contextIndex the context index
	 * @param conditionFilter the condition filter
	 * @param childName the child definition name
	 */
	public ChildContextBean(Integer contextName, Integer contextIndex, String conditionFilter,
			QName childName) {
		super();
		this.contextName = contextName;
		this.childName = childName;
		this.contextIndex = contextIndex;
		this.conditionFilter = conditionFilter;
	}

	/**
	 * @return the instance context name
	 */
	public Integer getContextName() {
		return contextName;
	}

	/**
	 * @param contextName the instance context name to set
	 */
	public void setContextName(Integer contextName) {
		this.contextName = contextName;
	}

	/**
	 * @return the child definition name
	 */
	public QName getChildName() {
		return childName;
	}

	/**
	 * @param childName the child definition name to set
	 */
	public void setChildName(QName childName) {
		this.childName = childName;
	}

	/**
	 * @return the context index
	 */
	public Integer getContextIndex() {
		return contextIndex;
	}

	/**
	 * @param contextIndex the context index to set
	 */
	public void setContextIndex(Integer contextIndex) {
		this.contextIndex = contextIndex;
	}

	/**
	 * @return the condition filter
	 */
	public String getConditionFilter() {
		return conditionFilter;
	}

	/**
	 * @param conditionFilter the condition filter to set
	 */
	public void setConditionFilter(String conditionFilter) {
		this.conditionFilter = conditionFilter;
	}

}
