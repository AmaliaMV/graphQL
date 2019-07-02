package graphql.fetcher

import grails.gorm.DetachedCriteria

import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.fetcher.impl.SingleEntityDataFetcher

import core.model.DataModel
import graphql.schema.DataFetchingEnvironment

class DataSetSingleEntityDataFetcher<T> extends SingleEntityDataFetcher<T> {

    private String dataModelName

    DataSetSingleEntityDataFetcher(PersistentEntity entity, String dataModelName) {
        super(entity)
        this.dataModelName = dataModelName
    }

    protected DetachedCriteria buildCriteria(DataFetchingEnvironment environment) {
        DataModel dataModel = DataModel.findByName(this.dataModelName)
        Map<String, Object> idProperties = getIdentifierValues(environment)
        new DetachedCriteria(entity.javaClass).build {
            eq 'dataModel', dataModel
            for (Map.Entry<String, Object> prop : idProperties) {
                eq(prop.key, prop.value)
            }
        }
    }
}
