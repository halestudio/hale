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

package eu.esdihumboldt.util;

/**
 * Stores identifiers for certain objects
 * 
 * @author Simon Templer
 * @param <T> the type of objects that shall be identified
 */
public class Identifiers<T> extends IdentifiersBase<T> {

	private int num = 0;
	private String prefix;

	/**
	 * Creates Identifiers with type name prefix
	 * 
	 * @param clazz the object type
	 * @param useEquals if the objects shall be compared using equals instead of
	 *            the == operator
	 */
	public Identifiers(Class<T> clazz, boolean useEquals) {
		this(clazz.getSimpleName() + "_", useEquals);
	}

	/**
	 * Creates Identifiers with the given prefix
	 * 
	 * @param prefix the identifier prefix
	 * @param useEquals if the objects shall be compared using equals instead of
	 *            the == operator
	 */
	public Identifiers(String prefix, boolean useEquals) {
		this(prefix, useEquals, 0);
	}

	/**
	 * Creates Identifiers with the given prefix
	 * 
	 * @param prefix the identifier prefix
	 * @param useEquals if the objects shall be compared using equals instead of
	 *            the == operator
	 * @param startCounter number given to first identifier
	 */
	public Identifiers(String prefix, boolean useEquals, int startCounter) {
		super(useEquals);
		this.prefix = prefix;
		this.num = startCounter;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Get the id of the given object.
	 * 
	 * @param object the object
	 * @return the id of the object
	 */
	@Override
	public final String getId(T object) {
		String id = ids.get(object);

		if (id == null) {
			id = prefix + num++; // "you're beautiful" - james blunt
			putObjectIdentifier(object, id);
			onInsertion(num - 1, id, object);
		}

		return id;
	}

	/**
	 * @param num the number used to generate the id
	 * @param id the id corresponding num
	 * @param object the object just
	 */
	protected void onInsertion(int num, String id, T object) {
		/* do nothing */
	}
}
