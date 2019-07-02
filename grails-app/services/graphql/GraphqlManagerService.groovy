package graphql

import java.lang.reflect.Method

import grails.gorm.transactions.Transactional

import org.grails.datastore.gorm.neo4j.Neo4jMappingContext
import org.grails.datastore.mapping.config.Property
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.gorm.graphql.GraphQLEntityHelper
import org.grails.gorm.graphql.GraphQLServiceManager
import org.grails.gorm.graphql.entity.dsl.GraphQLMapping
import org.grails.gorm.graphql.entity.dsl.GraphQLPropertyMapping
import org.grails.gorm.graphql.entity.operations.ListOperation
import org.grails.gorm.graphql.entity.property.GraphQLDomainProperty
import org.grails.gorm.graphql.entity.property.manager.GraphQLDomainPropertyManager
import org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType
import org.grails.gorm.graphql.fetcher.PaginatingGormDataFetcher
import org.grails.gorm.graphql.fetcher.impl.EntityDataFetcher
import org.grails.gorm.graphql.fetcher.impl.PaginatedEntityDataFetcher
import org.grails.gorm.graphql.fetcher.interceptor.InterceptingDataFetcher
import org.grails.gorm.graphql.fetcher.interceptor.InterceptorInvoker
import org.grails.gorm.graphql.fetcher.interceptor.QueryInterceptorInvoker
import org.grails.gorm.graphql.fetcher.manager.GraphQLDataFetcherManager
import org.grails.gorm.graphql.response.errors.GraphQLErrorsResponseHandler
import org.grails.gorm.graphql.response.pagination.GraphQLPaginationResponseHandler
import org.grails.gorm.graphql.types.GraphQLPropertyType
import org.grails.gorm.graphql.types.GraphQLTypeManager
import org.grails.gorm.graphql.types.output.ShowObjectTypeBuilder

import graphql.fetcher.DataSetAttributeDataFetcher
import graphql.fetcher.DataSetEntityFetcher
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


    GraphQL graphQL
    Neo4jMappingContext neo4jMappingContext
    GraphQLDataFetcherManager graphQLDataFetcherManager
    GraphQLPaginationResponseHandler graphQLPaginationResponseHandler
    GraphQLServiceManager graphQLServiceManager

    GraphQLDomainPropertyManager graphQLDomainPropertyManager
    GraphQLErrorsResponseHandler graphQLErrorsResponseHandler
    GraphQLTypeManager graphQLTypeManager

    private ShowObjectTypeBuilder showObjectTypeBuilder


    Map<String, GraphQLInputType> listArguments

    private static Method derivedMethod
    static {
        try {
            derivedMethod = Property.getMethod('isDerived', (Class<?>[]) null)
        }
        catch (NoSuchMethodException | SecurityException e) {
        }
    }


    GraphQL getGraphQL() {
//        if (!currentGraphQL) {
//            currentGraphQL = Holders.applicationContext.getBean('graphQL', GraphQL)
//            showObjectTypeBuilder = new ShowObjectTypeBuilder(graphQLDomainPropertyManager, graphQLTypeManager, graphQLErrorsResponseHandler)
//        }

        return graphQL
    }

    void updateSchema(DataModel dataModel) {
        if (!listArguments) {
            listArguments = buildListArguments()
        }

       // showObjectTypeBuilder = new ShowObjectTypeBuilder(graphQLDomainPropertyManager, graphQLTypeManager, graphQLErrorsResponseHandler)

        Params params = new Params()


        InterceptorInvoker queryInterceptorInvoker = new QueryInterceptorInvoker()

        PersistentEntity entity = neo4jMappingContext.getPersistentEntity('core.DataSet')
        GraphQLMapping mapping = GraphQLEntityHelper.getMapping(entity)

        GraphQLObjectType objectType = buildGraphQLObject(dataModel, entity)

        GraphQLObjectType.Builder queryType = newObject().name('Query')
        List<GraphQLFieldDefinition.Builder> queryFields = []


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

        this.graphQL = GraphQL
            .newGraphQL(graphQLSchema)
            .build()

        //graphQLSchema.query(graphQL.graphQLSchema.getQueryType())

//        graphQL.graphQLSchema.transform({ builder -> builder.query(queryType) })

//        graphQL = GraphQL
//            .newGraphQL(graphQL.graphQLSchema.transform({ builder -> builder.query(queryType) }))
//            .build()



        graphQL.graphQLSchema.queryType.fieldDefinitionsByName
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


    private buildGraphQLObject(DataModel dataModel, PersistentEntity entity) {
        List<GraphQLFieldDefinition> definitionList = []

        // attr estáticos del dataset
        List<GraphQLDomainProperty> properties = graphQLDomainPropertyManager.builder().alwaysNullable().getProperties(entity)

        for (GraphQLDomainProperty prop : properties) {
            if (prop.output) {
                GraphQLFieldDefinition.Builder field = newFieldDefinition()
                    .name(prop.name)
                    .deprecate(prop.deprecationReason)
                    .description(prop.description)

                if (prop.dataFetcher != null) {
                    field.dataFetcher(prop.dataFetcher)
                }

                field.type((GraphQLOutputType) prop.getGraphQLType(graphQLTypeManager, GraphQLPropertyType.OUTPUT))

                definitionList.add(field.build())
            }
        }

        // attr dinámicos
        for (Attribute attribute : dataModel.attributes) {
            GraphQLFieldDefinition graphQLFieldDefinition = newFieldDefinition()
                .name(namingConvention(attribute.name))
                .type((GraphQLOutputType) graphQLTypeManager.getType(String))
                .dataFetcher(new DataSetAttributeDataFetcher(attribute.name))
                .build()

            definitionList.add(graphQLFieldDefinition)
        }

        definitionList.add(graphQLErrorsResponseHandler.getFieldDefinition(graphQLTypeManager))

//        if (!entity.isRoot()) {
//            obj.withInterface(typeManager.createReference(entity.rootEntity, GraphQLPropertyType.OUTPUT))
//        }

//
//        objectTypeCache.put(entity, objectType)
//        objectType


        return newObject()
            .name(namingConvention(dataModel.name))
            .fields(definitionList)
            .build()
    }


    private GraphQLPropertyMapping getPropertyMapping(PersistentProperty property, GraphQLMapping mapping, boolean id = false) {
        GraphQLPropertyMapping propertyMapping
        if (mapping.propertyMappings.containsKey(property.name)) {
            propertyMapping = mapping.propertyMappings.get(property.name)
        }
        else {
            propertyMapping = new GraphQLPropertyMapping()
        }

        // ver
        boolean overrideNullable = false
        if (overrideNullable) {
            propertyMapping.nullable(true)
        }
        else if (id && propertyMapping.nullable == null) {
            propertyMapping.nullable(false)
        }

        // ver
        if (derivedMethod != null) {
            Property prop = property.mapping.mappedForm
            if (derivedMethod.invoke(prop, (Object[]) null)) {
                propertyMapping.input(false)
            }
        }
        propertyMapping
    }

    // ver el tema d los underscore, debería ser algo tipo vul_def --> vulDef
    private String namingConvention(String name) {
        return name
    }


    private class Params {

        PersistentEntity entity
        GraphQLMapping mapping
        InterceptorInvoker queryInterceptorInvoker = new QueryInterceptorInvoker()

        Params() {
            this.entity = neo4jMappingContext.getPersistentEntity('core.DataSet')
            this.mapping = GraphQLEntityHelper.getMapping(entity)
        }
    }
}
