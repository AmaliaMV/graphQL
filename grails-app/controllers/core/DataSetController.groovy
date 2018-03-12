package core

class DataSetController {

    def index() {
        respond DataSet.list()
    }
}
