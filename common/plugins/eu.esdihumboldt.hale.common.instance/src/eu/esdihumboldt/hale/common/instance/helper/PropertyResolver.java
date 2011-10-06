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
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;


/**
 * TODO Type description
 * 
 * @author Sebastian Reinhardt
 */
public class PropertyResolver {

	private static Map<QueryDefinitionIndex, LinkedList<String>> definitioncache = new HashMap<QueryDefinitionIndex, LinkedList<String>>();

	/**
	 * @param instance
	 * @param propertyPath
	 * @return
	 */
	public static Collection<Object> getValues(Instance instance,
			String propertyPath) {

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
						
						result.add(((Instance) finalProp).getValue());
						
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
	
	
	

	private static ArrayList<String> splitPath(String propertyPath) {

		ArrayList<String> pathParts = new ArrayList<String>();

		boolean dotsplit = true;
		int lastSplitPosition = 0;
		for (int i = 0; i < propertyPath.length(); i++) {

			char c = propertyPath.charAt(i);

			if (c == '{')
				dotsplit = false;
			else if (c == '}')
				dotsplit = true;

			if (dotsplit == true && c == '.') {

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
	 * @param instance
	 * @param query
	 * @return
	 */
	public static boolean hasProperty(Instance instance, String query) {

		QueryDefinitionIndex qdi = new QueryDefinitionIndex(
				instance.getDefinition(), query);
		
		if(definitioncache.containsKey(qdi)){
			
			if(definitioncache.get(qdi).isEmpty()){
				return false;
			}
			else return true;
			
		}
		else return analyzeDefinition(instance, qdi);
	}

	/**
	 * @param children
	 * @param path
	 * @param qci
	 * @return
	 */
	private static void analyzeSimpleQueryChildDefinition(
			Collection<? extends ChildDefinition<?>> children,
			ArrayList<QName> path, QueryDefinitionIndex qdi) {

		QName current = path.get(0);

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = children
				.iterator();

		while (childIterator.hasNext()) {

			ChildDefinition<?> child = childIterator.next();
			//System.out.println("Adding to queue: " + child.getName().toString());
			QueueDefinitionItem queueItem = new QueueDefinitionItem(child, child.getName());
			//queueItem.addQname(child.getParentType().getName());
			propertyqueue.add(queueItem);
			
		}

		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();
			
			//System.out.println("Taking from Queue: " + currentItem.getDefinition().getName().toString());
			
			
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
					//System.out.println("Existing child: " + tempdef.getName().toString());
					
					//TODO loop-prevention - still not working
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
			//TODO hier ist ein fehler		
					
					qudi.addQnames(currentItem.getQnames());
					
					
					for(ArrayList<QName> loop : currentItem.getLoopQNames()){
						qudi.addLoopQNames(loop);
						}

					propertyqueue.add(qudi);

				}

			}

		}
		
	}

	private static QueueDefinitionItem analyzeSubChild(QueueDefinitionItem qudi, QName current){
		

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();


			Iterator<? extends ChildDefinition<?>> childIterator = qudi.getDefinition()
					.asProperty().getPropertyType().getChildren().iterator();

			while (childIterator.hasNext()) {

				ChildDefinition<?> child = childIterator.next();
				//System.out.println("Adding to queue: " + child.getName().toString());
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
				
				//if(isGroup(currentItem.getDefinition())){
					
					tempit = currentItem
							.getDefinition().asGroup().getDeclaredChildren().iterator();
					
				//}
				
	/*			else{
					tempit = currentItem.getDefinition().asProperty().getPropertyType().getChildren()
							.iterator();
				}*/

				while (tempit.hasNext()) {

					ChildDefinition<?> tempdef = tempit.next();
					//System.out.println("Existing child: " + tempdef.getName().toString());
					
					//TODO loop-prevention - still not working
					
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
			//TODO hier ist ein fehler		

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
		

	
	private static void analyzeSpecialQueryChildDefinition(
			Collection<? extends ChildDefinition<?>> children,
			ArrayList<QName> path, QueryDefinitionIndex qdi) {

		QName current = path.get(0);

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = children
				.iterator();

		while (childIterator.hasNext()) {

			ChildDefinition<?> child = childIterator.next();
			//System.out.println("Adding to queue: " + child.getName().toString());
			QueueDefinitionItem queueItem = new QueueDefinitionItem(child, child.getName());
			//queueItem.addQname(child.getParentType().getName());
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
				//isInstance(currentItem.getDefinition()) ||
				
				
				Iterator<? extends ChildDefinition<?>> tempit; 
				
				//if(isGroup(currentItem.getDefinition())){
					
					tempit = currentItem
							.getDefinition().asGroup().getDeclaredChildren().iterator();
					
				//}
				
				/*else{
					tempit = currentItem.getDefinition().asProperty().getPropertyType().getChildren()
							.iterator();
				}*/

				while (tempit.hasNext()) {

					ChildDefinition<?> tempdef = tempit.next();
					//System.out.println("Existing child: " + tempdef.getName().toString());
					
					//TODO loop-prevention - still not working correctly
					
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
			//TODO hier ist ein fehler		
					

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
	
	private static boolean compareQName(QName qname1, QName qname2){
		
		if(qname1.getNamespaceURI().isEmpty()){
			
			if(qname1.getLocalPart().equals(qname2.getLocalPart())){
				return true;
			}
			else return false;
		}
		else if (qname1.equals(qname2)){
			return true;
		}
		
		else return false;
	}
	
	
	public static LinkedList<String> getKnownQueryPath(Instance instance, String query){
		
		QueryDefinitionIndex qdi = new QueryDefinitionIndex(instance.getDefinition(), query);
		
		
		return definitioncache.get(qdi);
		
	}

}




