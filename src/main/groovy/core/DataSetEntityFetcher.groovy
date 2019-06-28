package core

import grails.gorm.DetachedCriteria

import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.fetcher.impl.EntityDataFetcher

import core.model.DataModel
import graphql.schema.DataFetchingEnvironment

class DataSetEntityFetcher<T> extends EntityDataFetcher<T> {

    private String dataModelName

    DataSetEntityFetcher(PersistentEntity entity, String dataModelName) {
        super(entity)
        this.dataModelName = dataModelName
    }

    protected T executeQuery(DataFetchingEnvironment environment, Map queryArgs) {
        DataModel dataModel = DataModel.findByName(this.dataModelName)
        new DetachedCriteria(entity.javaClass).build {
            eq 'dataModel', dataModel
        }.list(queryArgs)
    }
}
