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

		// the old way
		// Object[] values = instance.getProperty(new QName(propertyPath));

		return null;
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

			if (propertyPath.startsWith("{")) {
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

			if (qnames.size() == 1) {

				return analyzeSimpleQueryChildDefinition(instance
						.getDefinition().getChildren(), qnames, qdi);
			}

			else {
				return analyzeSpecialQueryChildDefinition(instance
						.getDefinition().getChildren(), qnames, qdi);
			}


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
			
			if(!definitioncache.get(qdi).isEmpty()){
				return true;
			}
			else return false;
			
		}
		else return analyzeDefinition(instance, qdi);
	}

	/**
	 * @param children
	 * @param path
	 * @param qci
	 * @return
	 */
	private static boolean analyzeSimpleQueryChildDefinition(
			Collection<? extends ChildDefinition<?>> children,
			ArrayList<QName> path, QueryDefinitionIndex qdi) {

		QName current = path.get(0);

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = children
				.iterator();

		while (childIterator.hasNext()) {

			ChildDefinition<?> child = childIterator.next();
			//System.out.println("Adding to queue: " + child.getName().toString());
			propertyqueue.add(new QueueDefinitionItem(child, child.getName()));

		}

		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();
			
			//System.out.println("Taking from Queue: " + currentItem.getDefinition().getName().toString());
			
			
			if (current.getNamespaceURI().equals("")
					&& isProperty(currentItem.getDefinition())) {

				if (currentItem.getDefinition().getName().getLocalPart()
						.equals(current.getLocalPart())) {
					definitioncache.get(qdi).add(
							currentItem.qNamesToString());

				}
			}

			else if (currentItem.getDefinition().getName().equals(current)
					&& isProperty(currentItem.getDefinition())) {
				definitioncache.get(qdi).add(currentItem.qNamesToString());
			}

			if (!isFlatProperty(currentItem.getDefinition()) || isGroup(currentItem.getDefinition())) {

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
					
					if(currentItem.getLoopQNames().contains(tempdef.getName())){
						continue;
					}
					
					if (currentItem.getQnames().contains(tempdef.getName())){
						ArrayList<QName> loops = new ArrayList<QName>();
						
						for (int i = currentItem.getQnames().indexOf(tempdef.getName());
								i < currentItem.getQnames().size(); i++){
							loops.add(currentItem.getQnames().get(i));							
						}
					currentItem.setLoopQNames(loops);
					continue;
						
					}
					
					
					QueueDefinitionItem qudi = new QueueDefinitionItem(
							tempdef, tempdef.getName());
			//TODO hier ist ein fehler		
					for (int i = currentItem.getQnames().size() - 1; i >= 0; i--) {

						qudi.addQnames(currentItem.getQnames().get(i));
					
					}
					
					qudi.setLoopQNames(currentItem.getLoopQNames());

					propertyqueue.add(qudi);

				}

			}

		}
		if (!definitioncache.get(qdi).isEmpty()) {
			return true;
		} else
			return false;

	}

	private static QueueDefinitionItem analyzeSubChild(QueueDefinitionItem qudi, QName current){
		

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();


			Iterator<? extends ChildDefinition<?>> childIterator = qudi.getDefinition()
					.asProperty().getPropertyType().getChildren().iterator();

			while (childIterator.hasNext()) {

				ChildDefinition<?> child = childIterator.next();
				//System.out.println("Adding to queue: " + child.getName().toString());
				propertyqueue.add(new QueueDefinitionItem(child, child.getName()));

			}


		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();

			if (current.getNamespaceURI().equals("")
					&& isProperty(currentItem.getDefinition())) {

				if (currentItem.getDefinition().getName().getLocalPart()
						.equals(current.getLocalPart())) {
					return currentItem;

				}
			}

			else if (currentItem.getDefinition().getName().equals(current)
					&& isProperty(currentItem.getDefinition())) {
				return currentItem;
			}

			if (isGroup(currentItem.getDefinition())) {

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
					
					if(currentItem.getLoopQNames().contains(tempdef.getName())){
						continue;
					}
					
					if (currentItem.getQnames().contains(tempdef.getName())){
						ArrayList<QName> loops = new ArrayList<QName>();
						
						for (int i = currentItem.getQnames().indexOf(tempdef.getName());
								i < currentItem.getQnames().size(); i++){
							loops.add(currentItem.getQnames().get(i));							
						}
					currentItem.setLoopQNames(loops);
					continue;
						
					}
					
					
					QueueDefinitionItem quditemp = new QueueDefinitionItem(
							tempdef, tempdef.getName());
			//TODO hier ist ein fehler		
					for (int i = currentItem.getQnames().size() - 1; i >= 0; i--) {

						quditemp.addQnames(currentItem.getQnames().get(i));
					
					}
					
					quditemp.setLoopQNames(currentItem.getLoopQNames());

					propertyqueue.add(quditemp);

				}


			}

		}
		return null;
	
	}
		

	
	private static boolean analyzeSpecialQueryChildDefinition(
			Collection<? extends ChildDefinition<?>> children,
			ArrayList<QName> path, QueryDefinitionIndex qdi) {

		QName current = path.get(0);

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = children
				.iterator();

		while (childIterator.hasNext()) {

			ChildDefinition<?> child = childIterator.next();
			//System.out.println("Adding to queue: " + child.getName().toString());
			propertyqueue.add(new QueueDefinitionItem(child, child.getName()));

		}

		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();

			if (current.getNamespaceURI().equals("")
					&& isProperty(currentItem.getDefinition())){

				if (currentItem.getDefinition().getName().getLocalPart()
						.equals(current.getLocalPart())) {
					
						for (int i = 1; i < path.size(); i++)
						currentItem = analyzeSubChild(currentItem, path.get(i));
								if(currentItem == null){
									break;
						}
						if(currentItem != null){
							
							definitioncache.get(qdi).add(
									currentItem.qNamesToString());							
					}
					
				}
			}

			else if (currentItem.getDefinition().getName().equals(current)
					&& isProperty(currentItem.getDefinition())) {
				

				for (int i = 1; i < path.size(); i++)
					currentItem = analyzeSubChild(currentItem, path.get(i));
							if(currentItem == null){
								break;
					}
				if(currentItem != null){
					
					definitioncache.get(qdi).add(
							currentItem.qNamesToString());							
					}
			
				}

			else if (!isFlatProperty(currentItem.getDefinition()) || isGroup(currentItem.getDefinition())) {

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
					
					if(currentItem.getLoopQNames().contains(tempdef.getName())){
						continue;
					}
					
					if (currentItem.getQnames().contains(tempdef.getName())){
						ArrayList<QName> loops = new ArrayList<QName>();
						
						for (int i = currentItem.getQnames().indexOf(tempdef.getName());
								i < currentItem.getQnames().size(); i++){
							loops.add(currentItem.getQnames().get(i));							
						}
					currentItem.setLoopQNames(loops);
					continue;
						
					}
					
					
					QueueDefinitionItem qudi = new QueueDefinitionItem(
							tempdef, tempdef.getName());
			//TODO hier ist ein fehler		
					for (int i = currentItem.getQnames().size() - 1; i >= 0; i--) {

						qudi.addQnames(currentItem.getQnames().get(i));
					
					}
					
					qudi.setLoopQNames(currentItem.getLoopQNames());

					propertyqueue.add(qudi);

				}


			}

		}
		if (!definitioncache.get(qdi).isEmpty()) {
			return true;
		} else
			return false;

	}
	
	private static boolean isGroup(ChildDefinition<?> def){
		return def.asGroup() != null && def.asProperty() == null;
	}
	
	private static boolean isProperty(ChildDefinition<?> def){
		return def.asGroup() == null && def.asProperty() != null;
	}
	private static boolean isFlatProperty(ChildDefinition<?> def){
		
		if(def.asProperty() == null){
			return false;
		}
		else if(!def.asProperty().getPropertyType().getChildren().isEmpty()){
			return false;
		}
		else return true;
	}

}
