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

package eu.esdihumboldt.hale.common.instance.orient.storage;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.orient.OInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import net.jcip.annotations.Immutable;

/**
 * Instance reference for an instance stored in a {@link LocalOrientDB}.
 * 
 * @author Simon Templer
 */
@Immutable
public class OrientInstanceReference implements InstanceReference {

	private static final ALogger log = ALoggerFactory.getLogger(OrientInstanceReference.class);

	private final ORID id;
	private final DataSet dataSet;
	private final TypeDefinition typeDefinition;

	/**
	 * Create a reference to an instance
	 * 
	 * @param id the record ID
	 * @param dataSet the data set
	 * @param typeDefinition the associated type definition
	 */
	public OrientInstanceReference(ORID id, DataSet dataSet, TypeDefinition typeDefinition) {
		this.id = id;
		this.dataSet = dataSet;
		this.typeDefinition = typeDefinition;
	}

	/**
	 * @return the id
	 */
	public ORID getId() {
		return id;
	}

	@Override
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @return the typeDefinition
	 */
	public TypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSet == null) ? 0 : dataSet.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrientInstanceReference other = (OrientInstanceReference) obj;
		if (dataSet != other.dataSet)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Create a reference for an instance
	 * 
	 * @param instance the instance, which must be an {@link OInstance}
	 * @return the instance reference
	 * @throws IllegalArgumentException if the instance is no {@link OInstance}
	 */
	public static InstanceReference createReference(Instance instance) {
		if (instance.getDataSet() == null) {
			throw new IllegalArgumentException(
					"Instance data set may not be null for retrieving reference");
		}

		OInstance inst = (OInstance) instance;
		ORID id = inst.getDocument().getIdentity();

		return new OrientInstanceReference(id, instance.getDataSet(), inst.getDefinition());
	}

	/**
	 * Load the instance specified by the reference from the given database.
	 * 
	 * @param lodb the database
	 * @param owner the instance collection owning the reference
	 * @return the instance or <code>null</code> if no instance matching the
	 *         reference is present
	 */
	public Instance load(LocalOrientDB lodb, Object owner) {
		SharedDatabaseConnection connection = SharedDatabaseConnection.openRead(lodb, owner);
		DatabaseReference<ODatabaseDocumentTx> db = connection.getDb();
		DatabaseHandle handle = connection.getHandle();
		try {
			ODocument document = db.getDatabase().load(getId());
			if (document != null) {
				OInstance instance = new OInstance(document, getTypeDefinition(), db.getDatabase(),
						getDataSet());
				return handle.addInstance(instance);
			}
			else
				return null;
		} catch (IllegalArgumentException e) {
			// ignore - instance does not exist
			return null;
		} catch (ODatabaseException e) {
			// for newer versions the exception seems to be wrapped in an
			// ODatabaseException
			if (!(e.getCause() instanceof IllegalArgumentException)) {
				log.error("Failed to retrieve instance with ID " + id, e);
			}
			return null;
		} finally {
			// connection is closed in DatabaseHandle
			db.dispose(false);
			// try closing the database handle (e.g. if no objects were
			// added)
			handle.tryClose();
		}
	}

}
