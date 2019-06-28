package core.model

import graphql.GraphqlManagerService

class DataModelController {

    GraphqlManagerService graphqlManagerService

    def index() {
        respond DataModel.list()
    }

    def reloadSchema() {
        DataModel dataModel = DataModel.findByName('employee')
        graphqlManagerService.updateSchema(dataModel)
        render 'recargado'
    }
}
