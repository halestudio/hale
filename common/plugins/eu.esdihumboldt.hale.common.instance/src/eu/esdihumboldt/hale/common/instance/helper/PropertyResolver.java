/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * This class provides plubic static methods for resolving propertys from
 * instances. A cache provides that former accessed propertys are found faster
 * and the programm does not need to search over the whole definitiontree of the
 * instances again. Note: stringquery or querypath in comments references to a
 * path of indicies reassambling a path of definitions inside the
 * instance-definition-tree
 * 
 * @author Sebastian Reinhardt
 */
public class PropertyResolver {

	// the cache for storing found paths in instance definitions for certain
	// querys
	private static final Map<QueryDefinitionIndex, LinkedList<String>> definitioncache = new ConcurrentHashMap<QueryDefinitionIndex, LinkedList<String>>();

	/**
	 * This variable holds state about the last
	 * {@link #hasProperty(Instance, String)} call.
	 */
	private static final ThreadLocal<QueryDefinitionIndex> lastQDI = new ThreadLocal<QueryDefinitionIndex>();

	/**
	 * Method for retrieving values from instances using a certain path query
	 * for searching through the instance definitions. Calls methods for
	 * traversing the definition tree.<br>
	 * <br>
	 * If at the end of the path there is an instance, its value will be
	 * returned.
	 * 
	 * @param instance the instance
	 * @param propertyPath the property path
	 * @return the values contained in the instance matching the path
	 */
	public static Collection<Object> getValues(Instance instance, String propertyPath) {
		return getValues(instance, propertyPath, true);
	}

	/**
	 * Method for retrieving values from instances using a certain path query
	 * for searching through the instance definitions. Calls methods for
	 * traversing the definition tree.
	 * 
	 * @param instance the instance
	 * @param propertyPath the property path
	 * @param forceValue if this is <code>true</code>, when the object at the
	 *            end of a path is an instance, its value will be returned
	 * @return the values or instances contained in the instance matching the
	 *         given path, may be <code>null</code>
	 */
	public static Collection<Object> getValues(Instance instance, String propertyPath,
			boolean forceValue) {
		if (instance.getDefinition() == null) {
			// instance w/o a definition -> search only in instance structure
			// XXX group hiding and incomplete names not supported!
			Collection<Object> result = new ArrayList<Object>();

			Collection<Object> parents = new ArrayList<Object>();
			parents.add(instance);

			Queue<QName> names = new LinkedList<QName>(getQNamesFromPath(propertyPath));
			while (!names.isEmpty() && !parents.isEmpty()) {
				QName property = names.poll();

				Collection<Object> values = new ArrayList<Object>();

				for (Object parent : parents) {
					if (parent instanceof Group) {
						Object[] propertyValues = ((Group) parent).getProperty(property);
						if (propertyValues != null) {
							for (Object propertyValue : propertyValues) {
								if (propertyValue instanceof Instance
										&& ((Instance) propertyValue).getDefinition() != null) {
									Instance pi = (Instance) propertyValue;
									// call getValues again an perform the
									// search based on definitions
									// with a reduced path
									String path = pathString(names);
									Collection<Object> deepValues = getValues(pi, path, forceValue);
									if (deepValues != null) {
										result.addAll(deepValues);
									}
								}
								else {
									values.add(propertyValue);
								}
							}
						}
					}
				}

				parents = values;
			}
			if (names.isEmpty()) {
				// return all values found
				for (Object value : parents) {
					if (!forceValue) {
						result.add(value);
					}
					else {
						if (value instanceof Instance) {
							result.add(((Instance) value).getValue());
						}
						else if (!(value instanceof Group)) {
							result.add(value);
						}
					}
				}
			}
			return result;
		}
		// definition based check and retrieval
		if (hasProperty(instance, propertyPath)) {
			LinkedList<String> paths = getQueryPath(instance, propertyPath);
			Collection<Object> result = new ArrayList<Object>();

			for (String path : paths) {
				List<QName> qnames = getQNamesFromPath(path);

				Object[] props = instance.getProperty(qnames.get(0));

				if (props == null) {
					continue;
				}

				Queue<Object> currentQueue = new LinkedList<Object>();
				Queue<Object> nextQueue = new LinkedList<Object>();

				for (Object prop : props) {
					currentQueue.add(prop);
				}

				for (int i = 1; i < qnames.size(); i++) {
					while (!currentQueue.isEmpty()) {
						Object prop = currentQueue.poll();

						if (prop instanceof Group) {
							Object[] nextPropertys = ((Group) prop).getProperty(qnames.get(i));

							if (nextPropertys == null) {
								continue;
							}

							for (Object np : nextPropertys) {
								nextQueue.add(np);
							}
						}
						else {
							// TODO ERROR wrong path given from the cache
						}
					}

					while (!nextQueue.isEmpty()) {
						currentQueue.add(nextQueue.poll());
					}
				}

				while (!currentQueue.isEmpty()) {
					Object finalProp = currentQueue.poll();

					if (finalProp instanceof Instance) {
						if (forceValue) {
							result.add(((Instance) finalProp).getValue());
						}
						else {
							result.add(finalProp);
						}
					}
					else if (finalProp instanceof Group && forceValue) {
						// TODO error
					}
					else
						result.add(finalProp);
				}
			}
			if (!result.isEmpty()) {
				return result;
			}
			else {
				return null;
			}
		}
		else
			return null;
	}

	private static String pathString(Collection<QName> names) {
		return Joiner.on('.').join(Collections2.transform(names, new Function<QName, String>() {

			@Override
			public String apply(QName input) {
				return input.toString();
			}
		}));
	}

	/**
	 * Method for spliting up the path in the given query. The Method splits the
	 * String when a dot occurs. Are there an URL-parts inside the path, all
	 * dots inside thos parts are ignored ( checks of "{}" )
	 * 
	 * @param propertyPath The definitionpath part of the query
	 * @return An arraylist of split up parts of the path
	 */
	private static ArrayList<String> splitPath(String propertyPath) {

		ArrayList<String> pathParts = new ArrayList<String>();

		boolean dotsplit = true;
		int lastSplitPosition = 0;
		for (int i = 0; i < propertyPath.length(); i++) {

			char c = propertyPath.charAt(i);

			// check if there is an URL-part
			if (c == '{')
				// dont split if a dot occurs now
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

	/**
	 * Split a property path into a list of {@link QName}s.
	 * 
	 * @param propertyPath the property path
	 * @return the list of represented qualified names
	 */
	public static List<QName> getQNamesFromPath(String propertyPath) {
		ArrayList<String> pathParts = splitPath(propertyPath);

		ArrayList<QName> qnames = new ArrayList<QName>();

		for (int i = 0; i < pathParts.size(); i++) {
			String current = pathParts.get(i);

			if (current.startsWith("{")) {
				String uri = current.substring(current.indexOf("{") + 1, current.indexOf("}"));
				String name = current.substring(current.indexOf("}") + 1);
				qnames.add(new QName(uri, name));
			}
			else {
				qnames.add(new QName(current));
			}
		}

		return qnames;
	}

	/**
	 * this method starts the analysis of the instance-definition-tree
	 * 
	 * @param instance the given instance we are analysing
	 * @param qdi the cache index object with the querypath
	 * @return true, if the cache is not empty for the given cache index object
	 *         after the analysis
	 */
	private static boolean analyzeDefinition(Instance instance, QueryDefinitionIndex qdi) {
		List<QName> qnames = getQNamesFromPath(qdi.getQuery());

		definitioncache.put(qdi, new LinkedList<String>());

		// this can be used to search a single index over the whole
		// Instance-Definition-Tree

		/*
		 * if (qnames.size() == 1) {
		 * 
		 * analyzeSimpleQueryChildDefinition(instance
		 * .getDefinition().getChildren(), qnames, qdi);
		 * 
		 * return !definitioncache.get(qdi).isEmpty(); }
		 * 
		 * else {
		 */

		analyzeSpecialQueryChildDefinition(instance.getDefinition().getChildren(), qnames, qdi);
		return !definitioncache.get(qdi).isEmpty();
		// }

	}

	/**
	 * Determines of the given Instance contains certain definitions questioned
	 * by a given stringquery. If the cache allready contains this special path
	 * of the instance-definition-tree, true will be returned, else the method
	 * calls the analysismethods for searching of the definition-tree
	 * 
	 * @param instance the given instance we are searching in
	 * @param query the given pathquery we are searching inside the
	 *            definition-tree
	 * @return true if the path was found, else false
	 */
	public static boolean hasProperty(Instance instance, String query) {
		QueryDefinitionIndex qdi = new QueryDefinitionIndex(instance.getDefinition(),
				instance.getDataSet(), query);

		lastQDI.set(qdi);

		if (definitioncache.containsKey(qdi))
			return !definitioncache.get(qdi).isEmpty();
		else
			return analyzeDefinition(instance, qdi);
	}

	/**
	 * Returns all possible fully qualified (with namespaces) paths matching the
	 * given query.
	 * 
	 * @param typeDef the type definition
	 * @param dataSet the data set
	 * @param query the query
	 * @return a list of all possible paths matching the query (which may be
	 *         empty)
	 */
	public static List<List<QName>> getQueryPaths(TypeDefinition typeDef, DataSet dataSet,
			String query) {
		Instance instance = new DefaultInstance(typeDef, dataSet);
		List<String> paths = getQueryPath(instance, query);
		ArrayList<List<QName>> result = new ArrayList<List<QName>>(paths.size());
		for (String path : paths)
			result.add(getQNamesFromPath(path));
		return Collections.unmodifiableList(result);
	}

	/**
	 * this method can be used to search a single index over the whole
	 * instance-definition-tree (for example "*" queries) the method writes the
	 * found paths into the cache
	 * 
	 * @param children a list of ChildDefinitions from the root definition of
	 *            the instance-definition-tree
	 * @param path the list of QNames split up from the original querypath
	 * @param qdi the cacheindex produced from the instance root definition and
	 *            the querypath
	 */
	@SuppressWarnings("unused")
	private static void analyzeSimpleQueryChildDefinition(
			Collection<? extends ChildDefinition<?>> children, ArrayList<QName> path,
			QueryDefinitionIndex qdi) {

		QName current = path.get(0);

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = children.iterator();

		while (childIterator.hasNext()) {

			ChildDefinition<?> child = childIterator.next();
			QueueDefinitionItem queueItem = new QueueDefinitionItem(child, child.getName());
			propertyqueue.add(queueItem);

		}

		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();

			if (compareQName(current, currentItem.getDefinition().getName())
					&& isProperty(currentItem.getDefinition())) {

				definitioncache.get(qdi).add(currentItem.qNamesToString());

			}

			if (isInstance(currentItem.getDefinition()) || isGroup(currentItem.getDefinition())) {

				Iterator<? extends ChildDefinition<?>> tempit;

				if (isGroup(currentItem.getDefinition())) {

					tempit = currentItem.getDefinition().asGroup().getDeclaredChildren().iterator();

				}

				else {
					tempit = currentItem.getDefinition().asProperty().getPropertyType()
							.getChildren().iterator();
				}

				while (tempit.hasNext()) {

					ChildDefinition<?> tempdef = tempit.next();

					for (List<QName> loop : currentItem.getLoopQNames()) {
						if (loop.contains(tempdef.getName())) {
							continue;
						}
					}

					if (currentItem.getQnames().contains(tempdef.getName())) {
						List<QName> loops = new ArrayList<QName>();

						for (int i = currentItem.getQnames().indexOf(
								tempdef.getName()); i < currentItem.getQnames().size(); i++) {
							loops.add(currentItem.getQnames().get(i));
						}
						currentItem.addLoopQNames(loops);
						continue;

					}

					QueueDefinitionItem qudi = new QueueDefinitionItem(tempdef, tempdef.getName());

					qudi.addQnames(currentItem.getQnames());

					for (List<QName> loop : currentItem.getLoopQNames()) {
						qudi.addLoopQNames(loop);
					}

					propertyqueue.add(qudi);

				}

			}

		}

	}

	/**
	 * this method searches for the indices given from the querypath inside the
	 * instance-definition-tree but only for one iteration. this is used to
	 * avoid recursion and is used by the analyzeSpecialQueryChild method. the
	 * indices must be children in order to their appearance in the path. only
	 * groups may be between them.
	 * 
	 * @param current the current searched index as a QName
	 * @param qudi a queue item of the current found index and its definition
	 * @return returns a queue item of the searched index, if it has been found
	 */
	private static QueueDefinitionItem analyzeSubChild(QueueDefinitionItem qudi, QName current) {

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = qudi.getDefinition().asProperty()
				.getPropertyType().getChildren().iterator();

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

				tempit = currentItem.getDefinition().asGroup().getDeclaredChildren().iterator();

				while (tempit.hasNext()) {

					ChildDefinition<?> tempdef = tempit.next();

					// FIXME this condition is never true because LoopQNames is
					// a list of lists

					// XXX commenting out because this has no effect
//					if (currentItem.getLoopQNames().contains(tempdef.getName())) {
//						continue;
//					}

					if (currentItem.getQnames().contains(tempdef.getName())) {

						if (!compareQName(current, tempdef.getName())) {

							ArrayList<QName> loops = new ArrayList<QName>();

							for (int i = currentItem.getQnames().indexOf(
									tempdef.getName()); i < currentItem.getQnames().size(); i++) {
								loops.add(currentItem.getQnames().get(i));
							}
							currentItem.addLoopQNames(loops);
							continue;
						}
					}

					QueueDefinitionItem quditemp = new QueueDefinitionItem(tempdef,
							tempdef.getName());

					quditemp.addQnames(currentItem.getQnames());

					for (List<QName> loop : currentItem.getLoopQNames()) {
						qudi.addLoopQNames(loop);
					}

					propertyqueue.add(quditemp);

				}

			}

		}
		return null;

	}

	/**
	 * this method searches for the indices given from the querypath inside the
	 * instance-definition-tree the indices must be children in order to their
	 * appearance in the path. only groups may be between them. the method
	 * writes the found paths into the cache
	 * 
	 * @param children a list of Childdefinitions from the rootdefinition of the
	 *            instance-definition-tree
	 * @param path the list of QNames split up from the original querypath
	 * @param qdi the cacheindex produced from the instance root definition and
	 *            the querypath
	 */
	private static void analyzeSpecialQueryChildDefinition(
			Collection<? extends ChildDefinition<?>> children, List<QName> path,
			QueryDefinitionIndex qdi) {

		QName current = path.get(0);

		Queue<QueueDefinitionItem> propertyqueue = new LinkedList<QueueDefinitionItem>();

		Iterator<? extends ChildDefinition<?>> childIterator = children.iterator();

		while (childIterator.hasNext()) {

			ChildDefinition<?> child = childIterator.next();
			QueueDefinitionItem queueItem = new QueueDefinitionItem(child, child.getName());
			propertyqueue.add(queueItem);
		}

		while (!propertyqueue.isEmpty()) {

			QueueDefinitionItem currentItem = propertyqueue.poll();

			if (compareQName(current, currentItem.getDefinition().getName())
					&& isProperty(currentItem.getDefinition())) {

				for (int i = 1; i < path.size(); i++) {
					currentItem = analyzeSubChild(currentItem, path.get(i));
					if (currentItem == null) {
						break;
					}
				}

				if (currentItem != null) {

					definitioncache.get(qdi).add(currentItem.qNamesToString());
				}

			}

			else if (isGroup(currentItem.getDefinition())) {

				Iterator<? extends ChildDefinition<?>> tempit;

				tempit = currentItem.getDefinition().asGroup().getDeclaredChildren().iterator();

				while (tempit.hasNext()) {

					ChildDefinition<?> tempdef = tempit.next();

					// FIXME this condition is never true because LoopQNames is
					// a list of lists

					// XXX commenting out because this has no effect
//					if (currentItem.getLoopQNames().contains(tempdef.getName())) {
//						continue;
//					}

					if (currentItem.getQnames().contains(tempdef.getName())) {
						ArrayList<QName> loops = new ArrayList<QName>();

						for (int i = currentItem.getQnames().indexOf(
								tempdef.getName()); i < currentItem.getQnames().size(); i++) {
							loops.add(currentItem.getQnames().get(i));
						}
						currentItem.addLoopQNames(loops);
						continue;

					}

					QueueDefinitionItem qudi = new QueueDefinitionItem(tempdef, tempdef.getName());

					qudi.addQnames(currentItem.getQnames());

					for (List<QName> loop : currentItem.getLoopQNames()) {
						qudi.addLoopQNames(loop);
					}

					propertyqueue.add(qudi);

				}

			}

		}

	}

	private static boolean isGroup(ChildDefinition<?> def) {
		return def.asGroup() != null && def.asProperty() == null;
	}

	private static boolean isProperty(ChildDefinition<?> def) {
		return def.asGroup() == null && def.asProperty() != null;
	}

	private static boolean isInstance(ChildDefinition<?> def) {

		if (def.asProperty() == null) {
			return false;
		}
		else if (!def.asProperty().getPropertyType().getChildren().isEmpty()) {
			return true;
		}
		else
			return false;
	}

	/**
	 * Method for easy comparing of two QName objects. The first QName can miss
	 * the URI part. Then only the local parts getting compared.
	 * 
	 * @param qname1 the QName (usually from the filterquery), which can miss an
	 *            URI part
	 * @param qname2 the second QName
	 * @return true, if both are equal or if the first QName doesn't have an URI
	 *         part and both localparts are equal. Else false...
	 */
	private static boolean compareQName(QName qname1, QName qname2) {

		// contains the first QName an URI part?
		if (qname1.getNamespaceURI().isEmpty()) {

			// only compare the local parts
			if (qname1.getLocalPart().equals(qname2.getLocalPart())) {
				return true;
			}
			else
				return false;
		}
		// first Qname does contain the URI part -> compare them completely
		else if (qname1.equals(qname2)) {
			return true;
		}

		else
			return false;
	}

	/**
	 * Returns possible fully qualified (with namespace) paths for the given
	 * String which could not contain all namespaces.<br>
	 * The path can be splitted using {@link #getQNamesFromPath(String)}
	 * 
	 * @param instance the given instance which should contain the definitions
	 *            mentioned in the paths
	 * @param query the pathstring from the filterquery
	 * @return a list of Strings with possible paths inside the definitions of
	 *         the instance
	 */
	public static LinkedList<String> getQueryPath(Instance instance, String query) {
		QueryDefinitionIndex qdi = new QueryDefinitionIndex(instance.getDefinition(),
				instance.getDataSet(), query);

		lastQDI.set(qdi);

		if (!definitioncache.containsKey(qdi))
			analyzeDefinition(instance, qdi);

		return definitioncache.get(qdi);

	}

	/**
	 * Determines if the last query path was unique. This will only yield a
	 * reliable result if the last call to
	 * {@link #hasProperty(Instance, String)} was done from the current thread.
	 * The information on the last {@link #hasProperty(Instance, String)} call
	 * will be reset on calling this method.
	 * 
	 * @return <code>true</code> if the last query path was unique or if there
	 *         is no information on the last query path, <code>false</code>
	 *         otherwise
	 */
	public static boolean isLastQueryPathUnique() {
		QueryDefinitionIndex qdi = lastQDI.get();
		lastQDI.remove();

		if (qdi != null) {
			LinkedList<String> paths = definitioncache.get(qdi);
			if (paths != null && paths.size() > 1) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Clear the definition cache, e.g. when the type definitions may no longer
	 * be valid.
	 * 
	 * FIXME cache in service instead?
	 */
	public static void clearCache() {
		definitioncache.clear();
	}

}
