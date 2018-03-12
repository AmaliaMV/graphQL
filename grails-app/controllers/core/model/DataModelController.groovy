package core.model

class DataModelController {

    def index() {
        respond DataModel.list()
    }
}
