package core

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class DataSetAttributeDataFetcher implements DataFetcher {

    String attributeName

    DataSetAttributeDataFetcher(String attributeName) {
        this.attributeName = attributeName
    }

    @Override
    Object get(DataFetchingEnvironment environment) throws Exception {
        DataSet source = (DataSet) environment.source
        return source[this.attributeName]
    }
}
