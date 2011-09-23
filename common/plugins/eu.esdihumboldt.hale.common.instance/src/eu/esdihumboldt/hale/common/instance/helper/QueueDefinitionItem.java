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
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * TODO Type description
 * @author Basti
 */
public class QueueDefinitionItem {

	ChildDefinition<?> def;
	ArrayList<QName> qnames;
	ArrayList<QName> loops;
	
	public QueueDefinitionItem(ChildDefinition<?> def, QName qname){
		this.def = def;
		this.qnames = new ArrayList<QName>();
		this.loops = new ArrayList<QName>();
		qnames.add(qname);
	}

	/**
	 * @return the propDef
	 */
	public ChildDefinition<?> getDefinition() {
		return def;
	}

	/**
	 * @param propDef the propDef to set
	 */
	public void setDef(ChildDefinition<?> propDef) {
		this.def = def;
	}

	/**
	 * @return the qnames
	 */
	public ArrayList<QName> getQnames() {
		return qnames;
	}

	/**
	 * @param qname the qname to add
	 */
	public void addQnames(QName qname) {
		this.qnames.add(0, qname);
	}
	
	public void setLoopQNames(ArrayList<QName> loopQNames) {
		for(QName n : loopQNames){
			loops.add(n);
		}
	}
	
	public ArrayList<QName> getLoopQNames(){
		return loops;
	}
	
	
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


