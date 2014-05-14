package org.molgenis.omx.biobankconnect.ontologytree;

import java.io.IOException;
import java.util.Iterator;

import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.Query;
import org.molgenis.data.Queryable;
import org.molgenis.data.Repository;
import org.molgenis.data.support.DefaultAttributeMetaData;
import org.molgenis.data.support.DefaultEntityMetaData;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.omx.biobankconnect.utils.OntologyRepository;
import org.molgenis.omx.biobankconnect.utils.OntologyTermRepository;
import org.molgenis.search.Hit;
import org.molgenis.search.SearchRequest;
import org.molgenis.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractOntologyIndexRepository implements Repository, Queryable
{
	protected DefaultEntityMetaData entityMetaData = null;
	protected final SearchService searchService;
	protected final String entityName;

	@Autowired
	public AbstractOntologyIndexRepository(String entityName, SearchService searchService)
	{
		this.entityName = entityName;
		this.searchService = searchService;
	}

	@Override
	public Iterator<Entity> iterator()
	{
		return findAll(new QueryImpl()).iterator();
	}

	public Hit findOneInternal(Query q)
	{
		for (Hit hit : searchService.search(new SearchRequest(null, q, null)).getSearchHits())
		{
			return hit;
		}
		return null;
	}

	@Override
	public EntityMetaData getEntityMetaData()
	{
		if (entityMetaData == null)
		{
			entityMetaData = new DefaultEntityMetaData(entityName);
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData("ID"));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.ONTOLOGY_TERM_IRI,
					FieldTypeEnum.HYPERLINK));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.ONTOLOGY_TERM));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.SYNONYMS));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.ENTITY_TYPE));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.NODE_PATH));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.PARENT_NODE_PATH));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData("fieldType", FieldTypeEnum.ENUM));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.LAST,
					FieldTypeEnum.BOOL));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.ROOT,
					FieldTypeEnum.BOOL));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(
					OntologyTermRepository.ONTOLOGY_TERM_DEFINITION));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyTermRepository.ONTOLOGY_IRI,
					FieldTypeEnum.HYPERLINK));
			entityMetaData.addAttributeMetaData(new DefaultAttributeMetaData(OntologyRepository.ONTOLOGY_LABEL));
			DefaultAttributeMetaData childrenAttributeMetatData = new DefaultAttributeMetaData("attributes",
					FieldTypeEnum.MREF);
			childrenAttributeMetatData.setRefEntity(entityMetaData);
			entityMetaData.addAttributeMetaData(childrenAttributeMetatData);
			entityMetaData.setIdAttribute("ID");
		}
		return entityMetaData;
	}

	@Override
	public Iterable<Entity> findAll(Iterable<Object> ids)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public long count()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> Iterable<E> findAll(Query q, Class<E> clazz)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> Iterable<E> findAll(Iterable<Object> ids, Class<E> clazz)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> E findOne(Object id, Class<E> clazz)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> E findOne(Query q, Class<E> clazz)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> Iterable<E> iterator(Class<E> clazz)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName()
	{
		return getEntityMetaData().getName();
	}

	@Override
	public void close() throws IOException
	{
	}
}
