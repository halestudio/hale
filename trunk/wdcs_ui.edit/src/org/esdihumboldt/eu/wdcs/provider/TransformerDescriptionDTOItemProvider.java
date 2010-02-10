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

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.esdihumboldt.eu.wdcs.TransformerDescriptionDTO;
import org.esdihumboldt.eu.wdcs.WdcsFactory;
import org.esdihumboldt.eu.wdcs.WdcsPackage;

/**
 * This is the item provider adapter for a {@link org.esdihumboldt.eu.wdcs.TransformerDescriptionDTO} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class TransformerDescriptionDTOItemProvider
	extends TransformerItemProvider
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
	public TransformerDescriptionDTOItemProvider(AdapterFactory adapterFactory) {
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

			addKeywordsPropertyDescriptor(object);
			addTitlePropertyDescriptor(object);
			addAbstractPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Keywords feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addKeywordsPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_TransformerDescriptionDTO_keywords_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_TransformerDescriptionDTO_keywords_feature", "_UI_TransformerDescriptionDTO_type"),
				 WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__KEYWORDS,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Title feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTitlePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_TransformerDescriptionDTO_title_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_TransformerDescriptionDTO_title_feature", "_UI_TransformerDescriptionDTO_type"),
				 WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__TITLE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Abstract feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAbstractPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_TransformerDescriptionDTO_abstract_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_TransformerDescriptionDTO_abstract_feature", "_UI_TransformerDescriptionDTO_type"),
				 WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__ABSTRACT,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__PRECONDITIONS);
			childrenFeatures.add(WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__POSTCONDITION);
			childrenFeatures.add(WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__GROUNDING_INFORMATION);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((TransformerDescriptionDTO)object).getGetName();
		return label == null || label.length() == 0 ?
			getString("_UI_TransformerDescriptionDTO_type") :
			getString("_UI_TransformerDescriptionDTO_type") + " " + label;
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

		switch (notification.getFeatureID(TransformerDescriptionDTO.class)) {
			case WdcsPackage.TRANSFORMER_DESCRIPTION_DTO__KEYWORDS:
			case WdcsPackage.TRANSFORMER_DESCRIPTION_DTO__TITLE:
			case WdcsPackage.TRANSFORMER_DESCRIPTION_DTO__ABSTRACT:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case WdcsPackage.TRANSFORMER_DESCRIPTION_DTO__PRECONDITIONS:
			case WdcsPackage.TRANSFORMER_DESCRIPTION_DTO__POSTCONDITION:
			case WdcsPackage.TRANSFORMER_DESCRIPTION_DTO__GROUNDING_INFORMATION:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
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

		newChildDescriptors.add
			(createChildParameter
				(WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__PRECONDITIONS,
				 WdcsFactory.eINSTANCE.createComplexPrecondition()));

		newChildDescriptors.add
			(createChildParameter
				(WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__PRECONDITIONS,
				 WdcsFactory.eINSTANCE.createLiteralPrecondition()));

		newChildDescriptors.add
			(createChildParameter
				(WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__POSTCONDITION,
				 WdcsFactory.eINSTANCE.createComplexPostcondition()));

		newChildDescriptors.add
			(createChildParameter
				(WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__POSTCONDITION,
				 WdcsFactory.eINSTANCE.createLiteralPostcondition()));

		newChildDescriptors.add
			(createChildParameter
				(WdcsPackage.Literals.TRANSFORMER_DESCRIPTION_DTO__GROUNDING_INFORMATION,
				 WdcsFactory.eINSTANCE.createGroundingInformation()));
	}

}
