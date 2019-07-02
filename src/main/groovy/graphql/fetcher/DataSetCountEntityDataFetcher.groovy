package graphql.fetcher

import grails.gorm.DetachedCriteria

import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.fetcher.impl.CountEntityDataFetcher

import core.model.DataModel

class DataSetCountEntityDataFetcher extends CountEntityDataFetcher {

    String dataModelName

    DataSetCountEntityDataFetcher(PersistentEntity entity, String dataModelName) {
        super(entity)
        this.dataModelName = dataModelName
    }

    @Override
    protected Integer queryCount() {
        DataModel dataModel = DataModel.findByName(this.dataModelName)

        new DetachedCriteria(entity.javaClass).build {
            eq 'dataModel', dataModel
        }.count()
    }
}
