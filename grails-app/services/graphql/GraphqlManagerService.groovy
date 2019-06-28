package graphql

import grails.gorm.transactions.Transactional
import grails.util.Holders

import org.grails.datastore.gorm.neo4j.Neo4jMappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.GraphQLEntityHelper
import org.grails.gorm.graphql.GraphQLServiceManager
import org.grails.gorm.graphql.entity.dsl.GraphQLMapping
import org.grails.gorm.graphql.entity.operations.ListOperation
import org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType
import org.grails.gorm.graphql.fetcher.PaginatingGormDataFetcher
import org.grails.gorm.graphql.fetcher.impl.ClosureDataFetcher
import org.grails.gorm.graphql.fetcher.impl.EntityDataFetcher
import org.grails.gorm.graphql.fetcher.impl.PaginatedEntityDataFetcher
import org.grails.gorm.graphql.fetcher.interceptor.InterceptingDataFetcher
import org.grails.gorm.graphql.fetcher.interceptor.InterceptorInvoker
import org.grails.gorm.graphql.fetcher.interceptor.QueryInterceptorInvoker
import org.grails.gorm.graphql.fetcher.manager.GraphQLDataFetcherManager
import org.grails.gorm.graphql.response.pagination.GraphQLPaginationResponseHandler
import org.grails.gorm.graphql.types.GraphQLPropertyType
import org.grails.gorm.graphql.types.GraphQLTypeManager

import core.DataSet
import core.DataSetAttributeDataFetcher
import core.DataSetEntityFetcher
import core.model.Attribute
import core.model.DataModel
import graphql.schema.DataFetcher

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType

import static graphql.schema.GraphQLArgument.newArgument
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLList.list
import static graphql.schema.GraphQLObjectType.newObject
import static org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType.LIST

@Transactional
class GraphqlManagerService {


    private GraphQL currentGraphQL
    Neo4jMappingContext neo4jMappingContext
    GraphQLTypeManager graphQLTypeManager
    GraphQLDataFetcherManager graphQLDataFetcherManager
    GraphQLPaginationResponseHandler graphQLPaginationResponseHandler
    GraphQLServiceManager graphQLServiceManager


    Map<String, GraphQLInputType> listArguments

    GraphQL getGraphQL() {
        if (!currentGraphQL) {
            currentGraphQL = Holders.applicationContext.getBean('graphQL', GraphQL)
        }

        return currentGraphQL
    }

    void updateSchema(DataModel dataModel) {
        if (!listArguments) {
            listArguments = buildListArguments()
        }

        PersistentEntity entity = neo4jMappingContext.getPersistentEntity('core.DataSet')
        GraphQLMapping mapping = GraphQLEntityHelper.getMapping(entity)
        InterceptorInvoker queryInterceptorInvoker = new QueryInterceptorInvoker()

        GraphQLObjectType.Builder queryType = newObject().name('Query')
        List<GraphQLFieldDefinition.Builder> queryFields = []


        GraphQLObjectType objectType = buildGraphQLObject(dataModel)


        ListOperation listOperation = mapping.operations.list

        DataFetcher listFetcher = graphQLDataFetcherManager.getReadingFetcher(entity, GraphQLDataFetcherType.LIST).orElse(null)

        GraphQLFieldDefinition.Builder queryAll = newFieldDefinition()
            .name('employeeList')

        if (listOperation.paginate) {
            if (listFetcher == null) {
                listFetcher = new PaginatedEntityDataFetcher(entity)
            }
            queryAll.type(graphQLTypeManager.getQueryType(entity, GraphQLPropertyType.OUTPUT_PAGED))
        }
        else {
            if (listFetcher == null) {
                listFetcher = new DataSetEntityFetcher(entity, dataModel.name)
            }
            queryAll.type(list(objectType))
        }

        if (listFetcher instanceof PaginatingGormDataFetcher) {
            ((PaginatingGormDataFetcher) listFetcher).responseHandler = graphQLPaginationResponseHandler
        }

        queryAll.dataFetcher(new InterceptingDataFetcher(entity, graphQLServiceManager, queryInterceptorInvoker, LIST, listFetcher))

        queryFields.add(queryAll)

        for (Map.Entry<String, GraphQLInputType> argument : listArguments) {
            queryAll.argument(newArgument()
                                  .name(argument.key)
                                  .type(argument.value))
        }




        queryType.fields(queryFields*.build())



        GraphQLSchema graphQLSchema = GraphQLSchema.newSchema()
            .query(queryType)
            .build()

        currentGraphQL = GraphQL
            .newGraphQL(graphQLSchema)
            .build()
    }

    // refactor
    Map<String, GraphQLInputType> buildListArguments() {
        Map<String, Class> arguments = EntityDataFetcher.ARGUMENTS

        Map<String, GraphQLInputType> listArguments = [:]
        for (Map.Entry<String, Class> entry : arguments) {
            GraphQLType type = graphQLTypeManager.getType(entry.value)
            if (!(type instanceof GraphQLInputType)) {
                throw new IllegalArgumentException(
                    "Error while setting list arguments. Invalid returnType found for ${entry.value.name}. GraphQLType found ${type.name} of returnType ${type.class.name} is not an instance of ${GraphQLInputType.name}")
            }
            listArguments.put(entry.key, (GraphQLInputType) type)
        }
        listArguments
    }


    private buildGraphQLObject(DataModel dataModel) {
        List<GraphQLFieldDefinition> definitionList = []

        // attr estáticos del dataset

        // attr dinámicos
        for (Attribute attribute : dataModel.attributes) {
            GraphQLFieldDefinition graphQLFieldDefinition = newFieldDefinition()
                .name(namingConvention(attribute.name))
                .type((GraphQLOutputType) graphQLTypeManager.getType(String))
                .dataFetcher(new DataSetAttributeDataFetcher(attribute.name))
                .build()

            definitionList.add(graphQLFieldDefinition)
        }

        return newObject()
            .name(namingConvention(dataModel.name))
            .fields(definitionList)
            .build()
    }

    // ver el tema d los underscore, debería ser algo tipo vul_def --> vulDef
    private String namingConvention(String name) {
        return name
    }
}
