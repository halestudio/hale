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

package eu.esdihumboldt.hale.ui.service.instance.internal.orient;

import java.io.Closeable;
import java.io.IOException;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;

/**
 * Instance sink based on a {@link LocalOrientDB}
 * @author Simon Templer
 */
public class OrientInstanceSink implements InstanceSink, Closeable {

	private final LocalOrientDB database;
	private DatabaseReference<ODatabaseDocumentTx> ref;
	
	/**
	 * Create an instance sink based on a {@link LocalOrientDB}
	 * @param database the sink database
	 */
	public OrientInstanceSink(LocalOrientDB database) {
		super();
		this.database = database;
	}

	/**
	 * @see InstanceSink#addInstance(Instance)
	 */
	@Override
	public void addInstance(Instance instance) {
		if (ref == null) {
			ref = database.openWrite();
		}
		
		ODatabaseDocumentTx db = ref.getDatabase();
		
		// get/create OInstance
		OInstance conv = ((instance instanceof OInstance)?
				((OInstance) instance):(new OInstance(instance)));
		
		ODatabaseRecordThreadLocal.INSTANCE.set(db);
		// configure the document
		ODocument doc = conv.configureDocument(db);
		// and save it
		doc.save();
	}

	/**
	 * @see Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if (ref != null) {
			ref.dispose();
		}
	}

}
