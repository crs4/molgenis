package org.molgenis.data.importer;

import com.google.common.collect.*;
import org.molgenis.data.Entity;
import org.molgenis.data.i18n.model.I18nStringMetaData;
import org.molgenis.data.i18n.model.LanguageMetaData;
import org.molgenis.data.importer.EmxMetaDataParser.EmxAttribute;
import org.molgenis.data.meta.model.AttributeMetaData;
import org.molgenis.data.meta.model.EntityMetaData;
import org.molgenis.data.meta.model.EntityMetaDataFactory;
import org.molgenis.data.meta.model.Package;
import org.molgenis.data.semantic.LabeledResource;
import org.molgenis.data.semantic.SemanticTag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mutable bean to store intermediate parse results. Uses lookup tables to map simple names to the parsed objects. Is
 * used by the {@link EmxMetaDataParser}
 */
public final class IntermediateParseResults
{
	/**
	 * Maps full name to EntityMetaData
	 */
	private final Map<String, EntityMetaData> entities;
	/**
	 * Maps full name to PackageImpl (with tags)
	 */
	private final Map<String, Package> packages;
	/**
	 * Contains all Attribute tags
	 */
	private final SetMultimap<String, SemanticTag<AttributeMetaData, LabeledResource, LabeledResource>> attributeTags;
	/**
	 * Contains all Entity tags
	 */
	private final List<SemanticTag<EntityMetaData, LabeledResource, LabeledResource>> entityTags;
	/**
	 * Contains all tag entities from the tag sheet
	 */
	private final Map<String, Entity> tags;
	/**
	 * Contains all language enities from the languages sheet
	 */
	private final Map<String, Entity> languages;
	/**
	 * Contains all i18nString entities from the i18nstrings sheet
	 */
	private final Map<String, Entity> i18nStrings;
	private final EntityMetaDataFactory entityMetaDataFactory;

	public IntermediateParseResults(EntityMetaDataFactory entityMetaDataFactory)
	{
		this.tags = new LinkedHashMap<>();
		this.entities = new LinkedHashMap<>();
		this.packages = new LinkedHashMap<>();
		this.attributeTags = LinkedHashMultimap.create();
		this.entityTags = new ArrayList<>();
		this.languages = new LinkedHashMap<>();
		this.i18nStrings = new LinkedHashMap<>();
		this.entityMetaDataFactory = entityMetaDataFactory;
	}

	public void addTagEntity(String identifier, Entity tagEntity)
	{
		tags.put(identifier, tagEntity);
	}

	public void addAttributes(String entityName, List<EmxAttribute> emxAttrs)
	{
		EntityMetaData entityMeta = getEntityMetaData(entityName);
		if (entityMeta == null) entityMeta = addEntityMetaData(entityName);

		for (EmxAttribute emxAttr : emxAttrs)
		{
			AttributeMetaData attr = emxAttr.getAttr();
			entityMeta.addAttribute(attr);

			// set attribute roles
			if (emxAttr.isIdAttr())
			{
				entityMeta.setIdAttribute(attr);
			}
			if (emxAttr.isLabelAttr())
			{
				entityMeta.setLabelAttribute(attr);
			}
			if (emxAttr.isLookupAttr())
			{
				entityMeta.addLookupAttribute(attr);
			}
		}
	}

	public EntityMetaData addEntityMetaData(String name)
	{
		String simpleName = name;
		for (String packageName : packages.keySet())
		{
			if (name.toLowerCase().startsWith(packageName.toLowerCase()))
			{
				simpleName = name.substring(packageName.length() + 1);// package_entity
			}
		}

		EntityMetaData emd = entityMetaDataFactory.create().setName(simpleName);
		entities.put(name, emd);
		return emd;
	}

	public EntityMetaData getEntityMetaData(String name)
	{
		return entities.get(name);
	}

	public void addLanguage(Entity language)
	{
		languages.put(language.getString(LanguageMetaData.CODE), language);
	}

	public void addI18nString(Entity i18nString)
	{
		i18nStrings.put(i18nString.getString(I18nStringMetaData.MSGID), i18nString);
	}

	/**
	 * Checks if it knows entity with given simple name.
	 * 
	 * @param name
	 *            simple name of the entity
	 * @return true if entity with simple name name is known, false otherwise
	 */
	public boolean knowsEntity(String name)
	{
		return entities.containsKey(name);
	}

	public void addPackage(String name, Package p)
	{
		packages.put(name, p);
	}

	public ImmutableMap<String, EntityMetaData> getEntityMap()
	{
		return ImmutableMap.copyOf(entities);
	}

	public ImmutableList<EntityMetaData> getEntities()
	{
		return ImmutableList.copyOf(entities.values());
	}

	public ImmutableMap<String, Package> getPackages()
	{
		return ImmutableMap.copyOf(packages);
	}

	public ImmutableSetMultimap<String, SemanticTag<AttributeMetaData, LabeledResource, LabeledResource>> getAttributeTags()
	{
		return ImmutableSetMultimap.copyOf(attributeTags);
	}

	public ImmutableList<SemanticTag<EntityMetaData, LabeledResource, LabeledResource>> getEntityTags()
	{
		return ImmutableList.copyOf(entityTags);
	}

	public ImmutableMap<String, Entity> getLanguages()
	{
		return ImmutableMap.copyOf(languages);
	}

	public ImmutableMap<String, Entity> getI18nStrings()
	{
		return ImmutableMap.copyOf(i18nStrings);
	}

	@Override
	public String toString()
	{
		return "IntermediateParseResults [entities=" + entities + ", packages=" + packages + ", attributeTags="
				+ attributeTags + ", entityTags=" + entityTags + ", tags=" + tags + ", languages=" + languages
				+ ", i18nStrings=" + i18nStrings + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeTags == null) ? 0 : attributeTags.hashCode());
		result = prime * result + ((entities == null) ? 0 : entities.hashCode());
		result = prime * result + ((entityTags == null) ? 0 : entityTags.hashCode());
		result = prime * result + ((i18nStrings == null) ? 0 : i18nStrings.hashCode());
		result = prime * result + ((languages == null) ? 0 : languages.hashCode());
		result = prime * result + ((packages == null) ? 0 : packages.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		IntermediateParseResults other = (IntermediateParseResults) obj;
		if (attributeTags == null)
		{
			if (other.attributeTags != null) return false;
		}
		else if (!attributeTags.equals(other.attributeTags)) return false;
		if (entities == null)
		{
			if (other.entities != null) return false;
		}
		else if (!entities.equals(other.entities)) return false;
		if (entityTags == null)
		{
			if (other.entityTags != null) return false;
		}
		else if (!entityTags.equals(other.entityTags)) return false;
		if (i18nStrings == null)
		{
			if (other.i18nStrings != null) return false;
		}
		else if (!i18nStrings.equals(other.i18nStrings)) return false;
		if (languages == null)
		{
			if (other.languages != null) return false;
		}
		else if (!languages.equals(other.languages)) return false;
		if (packages == null)
		{
			if (other.packages != null) return false;
		}
		else if (!packages.equals(other.packages)) return false;
		if (tags == null)
		{
			if (other.tags != null) return false;
		}
		else if (!tags.equals(other.tags)) return false;
		return true;
	}

	/**
	 * Gets a specific package
	 * 
	 * @param name
	 *            the name of the package
	 * @return
	 */
	public Package getPackage(String name)
	{
		return getPackages().get(name);
	}

	public Entity getTagEntity(String tagIdentifier)
	{
		return tags.get(tagIdentifier);
	}

	public void addEntityTag(String entityName, SemanticTag tag)
	{
		entityTags.add(tag);
	}

	public void addAttributeTag(String entityName, SemanticTag tag)
	{
		attributeTags.put(entityName, tag);
	}

}
