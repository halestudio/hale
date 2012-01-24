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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;


/**
 * This class provides plubic static methods for resolving propertys from instances.
 * A cache provides that former accessed propertys are found faster and the programm 
 * does not need to search over the whole definitiontree of the instances again.
 * Note: stringquery or querypath in comments references to a path of indicies reassambling a path of definitions inside the instance-definition-tree
 * 
 * @author Sebastian Reinhardt
 */
public class PropertyResolver {

	
	//the cache for storing found paths in instance definitions for certain querys
	private static Map<QueryDefinitionIndex, LinkedList<String>> definitioncache = new HashMap<QueryDefinitionIndex, LinkedList<String>>();
	private static QueryDefinitionIndex lastQDI;

	/**
	 * Method for retrieving values from instances using a certain path query 
	 * for searching through the instance definitions. Calls methods for 
	 * traversing the definition tree.<br>
	 * <br>
	 * If at the end of the path there is an instance, its value will be 
	 * returned.
	 * @param instance the instance
	 * @param propertyPath the property path
	 * @return the values contained in the instance matching the path
	 */
	public static Collection<Object> getValues(Instance instance,
			String propertyPath) {
		return getValues(instance, propertyPath, true);
	}

		
	/**
	 * Method for retrieving values from instances using a certain path query 
	 * for searching through the instance definitions. Calls methods for 
	 * traversing the definition tree.
	 * @param instance the instance
	 * @param propertyPath the property path
	 * @param forceValue if this is <code>true</code>, when the object at the
	 *   end of a path is an instance, its value will be returned
	 * @return the values or instances contained in the instance matching the
	 *   given path
	 */
	public static Collection<Object> getValues(Instance instance,
			String propertyPath, boolean forceValue) {
		if(hasProperty(instance, propertyPath)){
			
			
			LinkedList<String> paths = getKnownQueryPath(instance, propertyPath);
			Collection<Object> result = new ArrayList<Object>();
			
			for(String path : paths){
				
				
				ArrayList<QName> qnames = getQNamesFromPath(path);
				
				
					
				Object[] props = instance.getProperty(qnames.get(0));
				
				if(props == null){
					continue;
				}
				
				Queue<Object> currentQueue = new LinkedList<Object>();
				Queue<Object> nextQueue = new LinkedList<Object>();
				
				for (Object prop : props){
				
					currentQueue.add(prop);
					
				}
				
				for(int i = 1; i < qnames.size(); i++){
				
					while (!currentQueue.isEmpty()){
						
						Object prop = currentQueue.poll();
						
						if(prop instanceof Group){
							
							Object[] nextPropertys = ((Group) prop).getProperty(qnames.get(i));
							
							if(nextPropertys == null){
								continue;
							}
							
							for (Object np : nextPropertys){
								
								nextQueue.add(np);
								
							}
		
						}
						
						else{
							//TODO ERROR wrong path given from the cache
						}

					}
					
					while(!nextQueue.isEmpty()){
					currentQueue.add(nextQueue.poll());
					}
				}
				
				
				int i = 0;
				while(!currentQueue.isEmpty()){
					
					
					Object finalProp = currentQueue.poll();
					
					if (finalProp instanceof Instance){
						if (forceValue) {
							result.add(((Instance) finalProp).getValue());
						}
						else {
							result.add(finalProp);
						}
					}
					
					else if (finalProp instanceof Group){
						//TODO error
					}
					
					else result.add(finalProp);
					
					
				}
				
				
				
				
			}
			if(!result.isEmpty()) return result;
			else return null;
		}
		
		else return null;

		
	}
	
	
	

	/**
	 * Method for spliting up the path in the given query. The Method splits the String when a dot occurs.
	 * Are there an URL-parts inside the path, all dots inside thos parts are ignored ( checks of "{}" )
	 * @param propertyPath The definitionpath part of the query
	 * @return An arraylist of split up parts of the path
	 */
	private static ArrayList<String> splitPath(String propertyPath) {

		ArrayList<String> pathParts = new ArrayList<String>();

		boolean dotsplit = true;
		int lastSplitPosition = 0;
		for (int i = 0; i < propertyPath.length(); i++) {

			char c = propertyPath.charAt(i);
			
			//check if there is an URL-part
			if (c == '{')
				//dont split if a dot occurs now
				dotsplit = false;
			else if (c == '}')
				dotsplit = true;
				
			if (dotsplit == true && (c == '/' || c == '.')) {

				pathParts.add(propertyPath.substring(lastSplitPosition, i));
				lastSplitPosition = i + 1;

			}

		}
		pathParts.add(propertyPath.substring(lastSplitPosition));

		return pathParts;

	}

	private static ArrayList<QName> getQNamesFromPath(String propertyPath) {

		ArrayList<String> pathParts = splitPath(propertyPath);

		ArrayList<QName> qnames = new ArrayList<QName>();

		for (int i = 0; i < pathParts.size(); i++) {

			String current = pathParts.get(i);

			if (current.startsWith("{")) {
				String uri = current.substring(current.indexOf("{") + 1,
						current.indexOf("}"));
				String name = current.substring(current.indexOf("}") + 1);
				qnames.add(new QName(uri, name));
			} else {
				qnames.add(new QName(current));
			}
		}

		return qnames;

	}

	
	/**
	 * this method starts the analysis of the instance-definition-tree
	 * @param instance the given instance we are analysing
	 * @param qdi the cache index object with the querypath 
	 * @return true, if the cache is not empty for the given cache index object after the analysis
	 */
	private static boolean analyzeDefinition(Instance instance, QueryDefinitionIndex qdi) {

		ArrayList<QName> qnames = getQNamesFromPath(qdi.getQuery());

		

			definitioncache.put(qdi, new LinkedList<String>());
			
			//this can be used to search a single index over the whole Instance-Definition-Tree
			
		/*	if (qnames.size() == 1) {

				analyzeSimpleQueryChildDefinition(instance
						.getDefinition().getChildren(), qnames, qdi);
			 
				return !definitioncache.get(qdi).isEmpty();
			}

			else {*/
			
				analyzeSpecialQueryChildDefinition(instance
						.getDefinition().getChildren(), qnames, qdi);
				return !definitioncache.get(qdi).isEmpty();
		//	}


	}

	/**
	 * Determines of the given Instance contains certain definitions questioned by a given stringquery.
	 * If the cache allready contains this special path of the instance-definition-tree, true will be returned,
	 * else the method calls the analysismethods for searching of the definition-tree
	 * @param instance the given instance we are searching in
	 * @param query the given pathquery we are searching inside the definition-tree
	 * @return true if the path was found, else false
	 */
	public static boolean hasProperty(Instance instance, String query) {

		QueryDefinitionIndex qdi = new QueryDefinitionIndex(
				instance.getDefinition(), query);
		
		lastQDI = qdi;
		
		if(definitioncache.containsKey(qdi)){
			
			if(definitioncache.get(qdi).isEmpty()){
				return false;
			}
			else return true;
			
		}
		else return analyzeDefinition(instance, qdi);
	}

	/**
	 * this method can be used to search a single index over the whole instance-definition-tree (for example "*"querys)
	 * the method writes the found paths into the cache
	 * @param children a list of Childdefinitions from the rootdefinition of the instance-definition-tree
	 * @param path the list of QNames split up from the original querypath
	 * @param qdi 
	 * @param qci the cacheindex produced from the instance root definition and the querypath
	 */
	@SuppressWarnings("unused")
	private static void analyzeSimpleQueryChildDefinition(
			Collection<? extends ChildDefinition<?>> children,
			ArrayList<QName> path, QueryDefinitionIndex qdi) {

		QName current = path.get(0);

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = children
				.iterator();

		while (childIterator.hasNext()) {

			ChildDefinition<?> child = childIterator.next();
			QueueDefinitionItem queueItem = new QueueDefinitionItem(child, child.getName());
			propertyqueue.add(queueItem);
			
		}

		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();
			
			if (compareQName(current, currentItem.getDefinition().getName())
					&& isProperty(currentItem.getDefinition())) {

				
					definitioncache.get(qdi).add(
							currentItem.qNamesToString());

			}


			if (isInstance(currentItem.getDefinition()) || isGroup(currentItem.getDefinition())) {

				Iterator<? extends ChildDefinition<?>> tempit; 
				
				if(isGroup(currentItem.getDefinition())){
					
					tempit = currentItem
							.getDefinition().asGroup().getDeclaredChildren().iterator();
					
				}
				
				else{
					tempit = currentItem.getDefinition().asProperty().getPropertyType().getChildren()
							.iterator();
				}
						

				while (tempit.hasNext()) {

					ChildDefinition<?> tempdef = tempit.next();

					for(ArrayList<QName> loop : currentItem.getLoopQNames()){
						if(loop.contains(tempdef.getName())){
							continue;
						}
					}
					
					if (currentItem.getQnames().contains(tempdef.getName())){
						ArrayList<QName> loops = new ArrayList<QName>();
						
						for (int i = currentItem.getQnames().indexOf(tempdef.getName());
								i < currentItem.getQnames().size(); i++){
							loops.add(currentItem.getQnames().get(i));							
						}
					currentItem.addLoopQNames(loops);
					continue;
						
					}
					
					
					QueueDefinitionItem qudi = new QueueDefinitionItem(
							tempdef, tempdef.getName());

					qudi.addQnames(currentItem.getQnames());
					
					
					for(ArrayList<QName> loop : currentItem.getLoopQNames()){
						qudi.addLoopQNames(loop);
						}

					propertyqueue.add(qudi);

				}

			}

		}
		
	}

	/**
	 * this method searches for the indices given from the querypath inside the instance-definition-tree
	 * but only for one iteration. this is used to avoid recursion and is used by the analyzeSpecialQueryChild method.
	 * the indices must be children in order to their appearance in the path. only groups may be between them.
	 * 
	 * @param current the current searched index as a QName
	 * @param qudi a queue item of the current found index and its definition
	 * @return returns a queue item of the searched index, if it has been found
	 */
	private static QueueDefinitionItem analyzeSubChild(QueueDefinitionItem qudi, QName current){
		

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();


			Iterator<? extends ChildDefinition<?>> childIterator = qudi.getDefinition()
					.asProperty().getPropertyType().getChildren().iterator();

			while (childIterator.hasNext()) {

				ChildDefinition<?> child = childIterator.next();
				QueueDefinitionItem queueItem = new QueueDefinitionItem(child, child.getName());
				
				queueItem.addQnames(qudi.getQnames());
				propertyqueue.add(queueItem);

			}


		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();

			if (compareQName(current, currentItem.getDefinition().getName())
					&& isProperty(currentItem.getDefinition())) {

					return currentItem;

				}
			

			if (isGroup(currentItem.getDefinition())) {

				Iterator<? extends ChildDefinition<?>> tempit; 
					
					tempit = currentItem
							.getDefinition().asGroup().getDeclaredChildren().iterator();
	

				while (tempit.hasNext()) {

					ChildDefinition<?> tempdef = tempit.next();
					
					if(currentItem.getLoopQNames().contains(tempdef.getName())){
						continue;
					}
					
					if (currentItem.getQnames().contains(tempdef.getName())){
						
						if(!compareQName(current, tempdef.getName())){
							
						ArrayList<QName> loops = new ArrayList<QName>();
						
						for (int i = currentItem.getQnames().indexOf(tempdef.getName());
								i < currentItem.getQnames().size(); i++){
							loops.add(currentItem.getQnames().get(i));							
						}
					currentItem.addLoopQNames(loops);
					continue;
						}
					}
					
					
					QueueDefinitionItem quditemp = new QueueDefinitionItem(
							tempdef, tempdef.getName());

					quditemp.addQnames(currentItem.getQnames());
					
					
					
					for(ArrayList<QName> loop : currentItem.getLoopQNames()){
						qudi.addLoopQNames(loop);
						}

					propertyqueue.add(quditemp);

				}


			}

		}
		return null;
	
	}
		

	/**
	 * this method searches for the indices given from the querypath inside the instance-definition-tree
	 * the indices must be children in order to their appearance in the path. only groups may be between them.
	 * the method writes the found paths into the cache
	 * @param children a list of Childdefinitions from the rootdefinition of the instance-definition-tree
	 * @param path the list of QNames split up from the original querypath
	 * @param qdi 
	 * @param qci the cacheindex produced from the instance root definition and the querypath
	 */
	private static void analyzeSpecialQueryChildDefinition(
			Collection<? extends ChildDefinition<?>> children,
			ArrayList<QName> path, QueryDefinitionIndex qdi) {

		QName current = path.get(0);

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = children
				.iterator();

		while (childIterator.hasNext()) {

			ChildDefinition<?> child = childIterator.next();
			QueueDefinitionItem queueItem = new QueueDefinitionItem(child, child.getName());
			propertyqueue.add(queueItem);
		}

		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();
			
			if (compareQName(current, currentItem.getDefinition().getName())
					&& isProperty(currentItem.getDefinition())) {
					
					for (int i = 1; i < path.size(); i++){
						currentItem = analyzeSubChild(currentItem, path.get(i));
						if(currentItem == null){
							break;
						}
					}
						
					if(currentItem != null){
							
						definitioncache.get(qdi).add(
								currentItem.qNamesToString());							
					}
					
				}
			

			else if (isGroup(currentItem.getDefinition())) {
				
				
				Iterator<? extends ChildDefinition<?>> tempit; 
				

					tempit = currentItem
							.getDefinition().asGroup().getDeclaredChildren().iterator();
					

				while (tempit.hasNext()) {

					ChildDefinition<?> tempdef = tempit.next();

					if(currentItem.getLoopQNames().contains(tempdef.getName())){
						continue;
					}
					
					if (currentItem.getQnames().contains(tempdef.getName())){
						ArrayList<QName> loops = new ArrayList<QName>();
						
						for (int i = currentItem.getQnames().indexOf(tempdef.getName());
								i < currentItem.getQnames().size(); i++){
							loops.add(currentItem.getQnames().get(i));							
						}
					currentItem.addLoopQNames(loops);
					continue;
						
					}
					
					
					QueueDefinitionItem qudi = new QueueDefinitionItem(
							tempdef, tempdef.getName());
					

					qudi.addQnames(currentItem.getQnames());
					
					
					
					for(ArrayList<QName> loop : currentItem.getLoopQNames()){
					qudi.addLoopQNames(loop);
					}
					

					propertyqueue.add(qudi);

				}


			}

		}
		
	}
	
	private static boolean isGroup(ChildDefinition<?> def){
		return def.asGroup() != null && def.asProperty() == null;
	}
	
	private static boolean isProperty(ChildDefinition<?> def){
		return def.asGroup() == null && def.asProperty() != null;
	}
	private static boolean isInstance(ChildDefinition<?> def){
		
		if(def.asProperty() == null){
			return false;
		}
		else if(!def.asProperty().getPropertyType().getChildren().isEmpty()){
			return true;
		}
		else return false;
	}
	
	
	
	/**
	 * Method for easy comparing of two QName objects. 
	 * The first QName can miss the URI part. Then only the local parts getting compared.
	 * @param qname1 the QName (usually from the filterquery), wich can miss an URI part
	 * @param qname2 the second QName
	 * @return true, if both are equal or if the first QName doesn't have an URI part 
	 *         and poth localparts are equal. Else false...
	 */
	private static boolean compareQName(QName qname1, QName qname2){
		
		//contains the first QName an URI part?
		if(qname1.getNamespaceURI().isEmpty()){
			
			//only compare the local parts
			if(qname1.getLocalPart().equals(qname2.getLocalPart())){
				return true;
			}
			else return false;
		}
		//first Qname does contain the URI part -> compare them completely
		else if (qname1.equals(qname2)){
			return true;
		}
		
		else return false;
	}
	
	
	
	/**
	 * Gets a certain definitionpath from the Cache by generating the cacheindex object 
	 * from a given Instance (its Definitions) and the pathquery
	 * @param instance the given instance wich should contain the definitions mentioned in the paths
	 * @param query the pathstring from the filterquery
	 * @return a list of Strings with possible paths inside the definitions of the instance
	 */
	public static LinkedList<String> getKnownQueryPath(Instance instance, String query){
		
		QueryDefinitionIndex qdi = new QueryDefinitionIndex(instance.getDefinition(), query);
		
		
		return definitioncache.get(qdi);
		
	}
	
	public static boolean isLastQueryPathUnique(){
		
		if(definitioncache.get(lastQDI).size() > 1) {
			return false;
		}
		
		else return true;
		
	}

}




