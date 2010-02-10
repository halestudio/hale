/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.esdihumboldt.eu.wdcs.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.esdihumboldt.eu.wdcs.HarmonisationTransformationDescription;
import org.esdihumboldt.eu.wdcs.WdcsPackage;

/**
 * This is the item provider adapter for a {@link org.esdihumboldt.eu.wdcs.HarmonisationTransformationDescription} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class HarmonisationTransformationDescriptionItemProvider
	extends TransformerDescriptionDTOItemProvider
	implements
		IEditingDomainItemProvider,
		IStructuredItemContentProvider,
		ITreeItemContentProvider,
		IItemLabelProvider,
		IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HarmonisationTransformationDescriptionItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addStatusPropertyDescriptor(object);
			addHarmonisationCategoryPropertyDescriptor(object);
			addUniqueWorkflowIDPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Status feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addStatusPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_HarmonisationTransformationDescription_status_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_HarmonisationTransformationDescription_status_feature", "_UI_HarmonisationTransformationDescription_type"),
				 WdcsPackage.Literals.HARMONISATION_TRANSFORMATION_DESCRIPTION__STATUS,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Harmonisation Category feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addHarmonisationCategoryPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_HarmonisationTransformationDescription_harmonisationCategory_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_HarmonisationTransformationDescription_harmonisationCategory_feature", "_UI_HarmonisationTransformationDescription_type"),
				 WdcsPackage.Literals.HARMONISATION_TRANSFORMATION_DESCRIPTION__HARMONISATION_CATEGORY,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Unique Workflow ID feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addUniqueWorkflowIDPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_HarmonisationTransformationDescription_uniqueWorkflowID_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_HarmonisationTransformationDescription_uniqueWorkflowID_feature", "_UI_HarmonisationTransformationDescription_type"),
				 WdcsPackage.Literals.HARMONISATION_TRANSFORMATION_DESCRIPTION__UNIQUE_WORKFLOW_ID,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This returns HarmonisationTransformationDescription.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/HarmonisationTransformationDescription"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((HarmonisationTransformationDescription)object).getGetName();
		return label == null || label.length() == 0 ?
			getString("_UI_HarmonisationTransformationDescription_type") :
			getString("_UI_HarmonisationTransformationDescription_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(HarmonisationTransformationDescription.class)) {
			case WdcsPackage.HARMONISATION_TRANSFORMATION_DESCRIPTION__STATUS:
			case WdcsPackage.HARMONISATION_TRANSFORMATION_DESCRIPTION__HARMONISATION_CATEGORY:
			case WdcsPackage.HARMONISATION_TRANSFORMATION_DESCRIPTION__UNIQUE_WORKFLOW_ID:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

}
