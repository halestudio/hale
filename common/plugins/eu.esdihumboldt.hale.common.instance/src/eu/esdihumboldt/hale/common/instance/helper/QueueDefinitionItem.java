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

package eu.esdihumboldt.hale.common.instance.helper;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Objects of these Class are used by the PropertyResolver in a Queue for traversing the instance-definition-tree	
 * @author Sebastian Reinhardt
 */


public class QueueDefinitionItem {

	ChildDefinition<?> def;
	ArrayList<QName> qnames;
	ArrayList<ArrayList<QName>> loops;
	
	public QueueDefinitionItem(ChildDefinition<?> def, QName qname){
		this.def = def;
		this.qnames = new ArrayList<QName>();
		this.loops = new ArrayList<ArrayList<QName>>();
		qnames.add(qname);
	}

	/**
	 * @return the propDef returns the instance definition in this item
	 */
	public ChildDefinition<?> getDefinition() {
		return def;
	}

	/**
	 * @param propDef sets the instance definition in this item
	 */
	public void setDef(ChildDefinition<?> propDef) {
		this.def = propDef;
	}

	/**
	 * @return the qnames from the path of the definition inside the instance-definition-tree
	 */
	public ArrayList<QName> getQnames() {
		return qnames;
	}

	/**
	 * Adds a single QName to the path
	 * @param qname the QName to be add
	 */
	public void addQname(QName qname) {
		this.qnames.add(0, qname);
	}
	
	/**
	 * Adds multiple QNames to the path
	 * @param qnames the QName sto be add
	 */
	public void addQnames(ArrayList<QName> qnames){
		
		int i = 0;
		for (QName name : qnames){
			this.qnames.add(i, name);
			i++;
		}
		
	}
	
	
	/**
	 * adds known loop paths wich appear in the 
	 * path of the instance-definition-tree on the way to the definition of this item
	 * @param loopQNames the loop paths to add
	 */
	public void addLoopQNames(ArrayList<QName> loopQNames) {
		loops.add(loopQNames);
	}
	
	/**
	 * returns the known loop paths wich appear in the 
	 * path of the instance-definition-tree on the way to the definition of this item
	 * @return the known loop-paths
	 */
	public ArrayList<ArrayList<QName>> getLoopQNames(){
		return loops;
	}
	
	/**
	 * returns the path of the definition of this item in the instance-definition-tree as a String
	 * @return the stringrespresentation of the path
	 */
	public String qNamesToString(){
		String result = "";
		for(int i = 0; i < qnames.size(); i++){
			if(result.equals("")){
				result = qnames.get(i).toString();
			}
			else{ 
				result = result.concat("." + qnames.get(i));
			}
		}
		
		return result;
	}
}


