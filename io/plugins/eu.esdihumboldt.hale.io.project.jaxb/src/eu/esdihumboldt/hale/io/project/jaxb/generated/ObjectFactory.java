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

package eu.esdihumboldt.hale.io.project.jaxb.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the
 * eu.esdihumboldt.hale.models.project.generated package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@SuppressWarnings("all")
@XmlRegistry
public class ObjectFactory {

	private final static QName _ContextElement_QNAME = new QName("", "ContextElement");
	private final static QName _InstanceData_QNAME = new QName("", "InstanceData");
	private final static QName _Task_QNAME = new QName("", "Task");
	private final static QName _MappedSchema_QNAME = new QName("", "MappedSchema");
	private final static QName _TaskStatus_QNAME = new QName("", "TaskStatus");
	private final static QName _HaleProject_QNAME = new QName("", "HaleProject");
	private final static QName _Styles_QNAME = new QName("", "Styles");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package:
	 * eu.esdihumboldt.hale.models.project.generated
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Task }
	 * 
	 */
	public Task createTask() {
		return new Task();
	}

	/**
	 * Create an instance of {@link MappedSchema }
	 * 
	 */
	public MappedSchema createMappedSchema() {
		return new MappedSchema();
	}

	/**
	 * Create an instance of {@link Styles }
	 * 
	 */
	public Styles createStyles() {
		return new Styles();
	}

	/**
	 * Create an instance of {@link ConfigSection }
	 * 
	 */
	public ConfigSection createConfigSection() {
		return new ConfigSection();
	}

	/**
	 * Create an instance of {@link ConfigData }
	 * 
	 */
	public ConfigData createConfigData() {
		return new ConfigData();
	}

	/**
	 * Create an instance of {@link TaskStatus }
	 * 
	 */
	public TaskStatus createTaskStatus() {
		return new TaskStatus();
	}

	/**
	 * Create an instance of {@link HaleProject }
	 * 
	 */
	public HaleProject createHaleProject() {
		return new HaleProject();
	}

	/**
	 * Create an instance of {@link InstanceData }
	 * 
	 */
	public InstanceData createInstanceData() {
		return new InstanceData();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "ContextElement")
	public JAXBElement<String> createContextElement(String value) {
		return new JAXBElement<String>(_ContextElement_QNAME, String.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link InstanceData }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "InstanceData")
	public JAXBElement<InstanceData> createInstanceData(InstanceData value) {
		return new JAXBElement<InstanceData>(_InstanceData_QNAME, InstanceData.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Task }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "Task")
	public JAXBElement<Task> createTask(Task value) {
		return new JAXBElement<Task>(_Task_QNAME, Task.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link MappedSchema }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "MappedSchema")
	public JAXBElement<MappedSchema> createMappedSchema(MappedSchema value) {
		return new JAXBElement<MappedSchema>(_MappedSchema_QNAME, MappedSchema.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link TaskStatus }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "TaskStatus")
	public JAXBElement<TaskStatus> createTaskStatus(TaskStatus value) {
		return new JAXBElement<TaskStatus>(_TaskStatus_QNAME, TaskStatus.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link HaleProject }
	 * {@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "HaleProject")
	public JAXBElement<HaleProject> createHaleProject(HaleProject value) {
		return new JAXBElement<HaleProject>(_HaleProject_QNAME, HaleProject.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Styles }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "", name = "Styles")
	public JAXBElement<Styles> createStyles(Styles value) {
		return new JAXBElement<Styles>(_Styles_QNAME, Styles.class, null, value);
	}

}
