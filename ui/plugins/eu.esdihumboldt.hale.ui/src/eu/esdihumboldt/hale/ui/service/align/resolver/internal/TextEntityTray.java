/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.align.resolver.internal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractEntityType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ObjectFactory;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import groovy.xml.XmlUtil;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;

/**
 * Dialog tray that displays details on a JAXB entity that could not be resolved
 * as schema entity.
 * 
 * @author Simon Templer
 */
public class TextEntityTray extends DialogTray {

	private final AbstractEntityType entity;

	/**
	 * @param entity the JAXB entity
	 */
	public TextEntityTray(AbstractEntityType entity) {
		this.entity = entity;
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogTray#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		GridLayoutFactory.fillDefaults().applyTo(page);

		Label label = new Label(page, SWT.NONE);

		ObjectFactory of = new ObjectFactory();
		JAXBElement<?> element = null;
		if (entity instanceof PropertyType) {
			element = of.createProperty((PropertyType) entity);
		}
		else if (entity instanceof ClassType) {
			element = of.createClass((ClassType) entity);
		}

		String text;
		if (element != null) {
			try {
				JAXBContext jc = JAXBContext.newInstance(JaxbAlignmentIO.ALIGNMENT_CONTEXT,
						ObjectFactory.class.getClassLoader());
				Marshaller m = jc.createMarshaller();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.newDocument();

				m.marshal(element, doc);

				text = XmlUtil.serialize((Element) doc.getFirstChild());
			} catch (Exception e) {
				text = e.getLocalizedMessage();
			}
		}
		else {
			text = "###";
		}

		label.setText(text);

		return page;
	}
}
