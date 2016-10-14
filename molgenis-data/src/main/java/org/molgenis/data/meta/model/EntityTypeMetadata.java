package org.molgenis.data.meta.model;

import org.molgenis.data.meta.SystemEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.util.Objects.requireNonNull;
import static org.molgenis.AttributeType.*;
import static org.molgenis.data.meta.model.EntityType.AttributeRole.*;
import static org.molgenis.data.meta.model.MetaPackage.PACKAGE_META;
import static org.molgenis.data.meta.model.Package.PACKAGE_SEPARATOR;

@Component
public class EntityTypeMetadata extends SystemEntityType
{
	private static final String SIMPLE_NAME_ = "EntityType";
	public static final String ENTITY_TYPE_META_DATA = PACKAGE_META + PACKAGE_SEPARATOR + SIMPLE_NAME_;

	public static final String FULL_NAME = "fullName";
	public static final String SIMPLE_NAME = "simpleName";
	public static final String PACKAGE = "package";
	public static final String LABEL = "label";
	public static final String DESCRIPTION = "description";
	public static final String ATTRIBUTES = "attributes";
	public static final String ID_ATTRIBUTE = "idAttribute";
	public static final String LABEL_ATTRIBUTE = "labelAttribute";
	public static final String LOOKUP_ATTRIBUTES = "lookupAttributes";
	public static final String IS_ABSTRACT = "isAbstract";
	public static final String EXTENDS = "extends";
	public static final String TAGS = "tags";
	public static final String BACKEND = "backend";

	private AttributeMetadata attributeMetadata;
	private PackageMetadata packageMetadata;
	private TagMetaData tagMetaData;

	private List<String> backendEnumOptions;
	private String defaultBackend;

	EntityTypeMetadata()
	{
		super(SIMPLE_NAME_, PACKAGE_META);
	}

	public void init()
	{
		requireNonNull(backendEnumOptions, "backend enum options not set!");

		setLabel("Entity");
		setDescription("Meta data for entity classes");

		addAttribute(FULL_NAME, ROLE_ID).setVisible(false).setLabel("Qualified name");
		addAttribute(SIMPLE_NAME, ROLE_LABEL).setNillable(false).setReadOnly(true).setLabel("Name");
		// TODO discuss whether package should be nillable
		addAttribute(PACKAGE).setDataType(XREF).setRefEntity(packageMetadata).setLabel("Package").setReadOnly(true);
		addAttribute(LABEL, ROLE_LOOKUP).setNillable(false).setLabel("Label");
		addAttribute(DESCRIPTION).setDataType(TEXT).setLabel("Description");
		addAttribute(ATTRIBUTES).setDataType(MREF).setRefEntity(attributeMetadata).setNillable(false)
				.setLabel("Attributes");
		addAttribute(ID_ATTRIBUTE).setDataType(XREF).setRefEntity(attributeMetadata).setReadOnly(true)
				.setLabel("ID attribute");
		addAttribute(LABEL_ATTRIBUTE).setDataType(XREF).setRefEntity(attributeMetadata).setLabel("Label attribute");
		addAttribute(LOOKUP_ATTRIBUTES).setDataType(MREF).setRefEntity(attributeMetadata).setLabel("Lookup attributes");
		addAttribute(IS_ABSTRACT).setDataType(BOOL).setNillable(false).setReadOnly(true).setLabel("Abstract")
				.setReadOnly(true).setDefaultValue(FALSE.toString());
		// TODO replace with autowired self-reference after update to Spring 4.3
		addAttribute(EXTENDS).setDataType(XREF).setRefEntity(this).setReadOnly(true).setLabel("Extends");
		addAttribute(TAGS).setDataType(MREF).setRefEntity(tagMetaData).setLabel("Tags");
		addAttribute(BACKEND).setDataType(ENUM).setEnumOptions(backendEnumOptions).setNillable(false).setReadOnly(true)
				.setDefaultValue(defaultBackend).setLabel("Backend").setDescription("Backend data store");
	}

	/**
	 * Used during bootstrapping to set the enum options for the backend field. Circumvents unresolvable circular
	 * dependencies when autowiring RepositoryCollectionRegistry into this bean.
	 *
	 * @param repositoryCollectionNames list of RepositoryCollection names
	 */
	public void setBackendEnumOptions(List<String> repositoryCollectionNames)
	{
		this.backendEnumOptions = requireNonNull(repositoryCollectionNames);
	}

	/**
	 * Used during bootstrapping to set the default value of the backend field.
	 *
	 * @param repositoryCollectionName list of RepositoryCollection names
	 */
	public void setDefaultBackend(String repositoryCollectionName)
	{
		this.defaultBackend = requireNonNull(repositoryCollectionName);
	}

	// setter injection instead of constructor injection to avoid unresolvable circular dependencies
	@Autowired
	public void setAttributeMetadata(AttributeMetadata attributeMetadata)
	{
		this.attributeMetadata = requireNonNull(attributeMetadata);
	}

	@Autowired
	public void setPackageMetadata(PackageMetadata packageMetadata)
	{
		this.packageMetadata = requireNonNull(packageMetadata);
	}

	@Autowired
	public void setTagMetaData(TagMetaData tagMetaData)
	{
		this.tagMetaData = requireNonNull(tagMetaData);
	}
}