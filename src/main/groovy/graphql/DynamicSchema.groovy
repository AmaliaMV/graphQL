package graphql

import org.grails.datastore.gorm.neo4j.Neo4jMappingContext
import org.grails.gorm.graphql.GraphQLEntityHelper
import org.grails.gorm.graphql.Schema
import org.grails.gorm.graphql.entity.property.GraphQLDomainProperty
import org.grails.gorm.graphql.fetcher.ReadingGormDataFetcher

import core.DataSet
import core.model.Attribute
import core.model.DataModel
import graphql.fetcher.DataSetAttributeDataFetcher
import graphql.fetcher.DataSetCountEntityDataFetcher
import graphql.fetcher.DataSetEntityFetcher
import graphql.fetcher.DataSetSingleEntityDataFetcher
import graphql.schema.*

import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.entity.dsl.GraphQLMapping
import org.grails.gorm.graphql.entity.operations.CustomOperation
import org.grails.gorm.graphql.entity.operations.ListOperation
import org.grails.gorm.graphql.entity.operations.ProvidedOperation
import org.grails.gorm.graphql.fetcher.PaginatingGormDataFetcher
import org.grails.gorm.graphql.fetcher.impl.CountEntityDataFetcher
import org.grails.gorm.graphql.fetcher.impl.EntityDataFetcher
import org.grails.gorm.graphql.fetcher.impl.PaginatedEntityDataFetcher
import org.grails.gorm.graphql.fetcher.impl.SingleEntityDataFetcher
import org.grails.gorm.graphql.fetcher.interceptor.InterceptingDataFetcher
import org.grails.gorm.graphql.fetcher.interceptor.InterceptorInvoker
import org.grails.gorm.graphql.fetcher.interceptor.QueryInterceptorInvoker
import org.grails.gorm.graphql.interceptor.GraphQLSchemaInterceptor
import org.grails.gorm.graphql.types.GraphQLPropertyType

import static graphql.schema.GraphQLArgument.newArgument
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLList.list
import static graphql.schema.GraphQLObjectType.newObject
import static org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType.*

//@CompileStatic
class DynamicSchema extends Schema {

    private boolean initialized = false

    private MappingContext mappingContext
    private GraphQLSchema graphQLSchema


    DynamicSchema(Neo4jMappingContext neo4jMappingContext) {
        super([])
        this.mappingContext = neo4jMappingContext
    }

    GraphQLSchema generate() {

        if (!initialized) {
            initialize()
        }

        GraphQLObjectType.Builder queryType = newObject().name('Query')
        GraphQLObjectType.Builder mutationType = newObject().name('Mutation')
        Set<GraphQLType> additionalTypes = []

        addStaticEntities(queryType, mutationType, additionalTypes)

        for (GraphQLSchemaInterceptor schemaInterceptor : interceptorManager.interceptors) {
            schemaInterceptor.interceptSchema(queryType, mutationType, additionalTypes)
        }

        addDynamicEntities(queryType)

        GraphQLSchema.newSchema()
            .query(queryType)
            .mutation(mutationType)
            .additionalTypes(additionalTypes)
            .build()
    }

    void addStaticEntities(GraphQLObjectType.Builder queryType, GraphQLObjectType.Builder mutationType, Set<GraphQLType> additionalTypes) {
        Set<PersistentEntity> childrenNotMapped = []

        for (PersistentEntity entity : mappingContext.persistentEntities) {

            GraphQLMapping mapping = GraphQLEntityHelper.getMapping(entity)
            if (mapping == null) {
                if (!entity.root) {
                    childrenNotMapped.add(entity)
                }
                continue
            }

            Params params = new PersistentEntityParams(entity, mapping)

            List<Closure> postIdentityExecutables = []

            addGetQuery(params)
            addListQuery(params)
            addCountQuery(params)

//            InterceptorInvoker mutationInterceptorInvoker = new MutationInterceptorInvoker()
//
//            GraphQLDataBinder dataBinder = dataBinderManager.getDataBinder(entity.javaClass)
//
//            ProvidedOperation createOperation = mapping.operations.create
//            if (createOperation.enabled && !Modifier.isAbstract(entity.javaClass.modifiers)) {
//                if (dataBinder == null) {
//                    throw new DataBinderNotFoundException(entity)
//                }
//                GraphQLInputType createObjectType = typeManager.getMutationType(entity, GraphQLPropertyType.CREATE, true)
//
//                BindingGormDataFetcher createFetcher = dataFetcherManager.getBindingFetcher(entity, CREATE).orElse(new CreateEntityDataFetcher(entity))
//
//                createFetcher.dataBinder = dataBinder
//
//                GraphQLFieldDefinition.Builder create = newFieldDefinition()
//                    .name(namingConvention.getCreate(entity))
//                    .type(objectType)
//                    .description(createOperation.description)
//                    .deprecate(createOperation.deprecationReason)
//                    .argument(newArgument()
//                                  .name(entity.decapitalizedName)
//                                  .type(createObjectType))
//                    .dataFetcher(new InterceptingDataFetcher(entity, serviceManager, mutationInterceptorInvoker, CREATE, createFetcher))
//
//                mutationFields.add(create)
//            }
//
//            ProvidedOperation updateOperation = mapping.operations.update
//            if (updateOperation.enabled) {
//                if (dataBinder == null) {
//                    throw new DataBinderNotFoundException(entity)
//                }
//                GraphQLInputType updateObjectType = typeManager.getMutationType(entity, GraphQLPropertyType.UPDATE, true)
//
//                BindingGormDataFetcher updateFetcher = dataFetcherManager.getBindingFetcher(entity, UPDATE).orElse(new UpdateEntityDataFetcher(entity))
//
//                updateFetcher.dataBinder = dataBinder
//
//                GraphQLFieldDefinition.Builder update = newFieldDefinition()
//                    .name(namingConvention.getUpdate(entity))
//                    .type(objectType)
//                    .description(updateOperation.description)
//                    .deprecate(updateOperation.deprecationReason)
//                    .dataFetcher(new InterceptingDataFetcher(entity, serviceManager, mutationInterceptorInvoker, UPDATE, updateFetcher))
//
//                postIdentityExecutables.add {
//                    update.argument(newArgument()
//                                        .name(entity.decapitalizedName)
//                                        .type(updateObjectType))
//                }
//
//                requiresIdentityArguments.add(update)
//                mutationFields.add(update)
//            }
//
//            ProvidedOperation deleteOperation = mapping.operations.delete
//            if (deleteOperation.enabled) {
//
//                DeletingGormDataFetcher deleteFetcher = dataFetcherManager.getDeletingFetcher(entity).orElse(new DeleteEntityDataFetcher(entity))
//
//                deleteFetcher.responseHandler = deleteResponseHandler
//
//                GraphQLFieldDefinition.Builder delete = newFieldDefinition()
//                    .name(namingConvention.getDelete(entity))
//                    .type(deleteResponseHandler.getObjectType(typeManager))
//                    .description(deleteOperation.description)
//                    .deprecate(deleteOperation.deprecationReason)
//                    .dataFetcher(new InterceptingDataFetcher(entity, serviceManager, mutationInterceptorInvoker, DELETE, deleteFetcher))
//
//                requiresIdentityArguments.add(delete)
//                mutationFields.add(delete)
//            }
//
            populateIdentityArguments(entity, params.requiresIdentityArguments.toArray(new GraphQLFieldDefinition.Builder[0]))

            for (Closure c : postIdentityExecutables) {
                c.call()
            }

            for (CustomOperation operation : mapping.customQueryOperations) {
                params.queryFields.add(operation.createField(entity, serviceManager, mappingContext, listArguments))
            }

//            for (CustomOperation operation : mapping.customMutationOperations) {
//                mutationFields.add(operation.createField(entity, serviceManager, mappingContext, Collections.emptyMap()))
//            }

            for (GraphQLSchemaInterceptor schemaInterceptor : interceptorManager.interceptors) {
                schemaInterceptor.interceptEntity(entity, params.queryFields, params.mutationFields)
            }

            queryType.fields(params.queryFields*.build())

            mutationType.fields(params.mutationFields*.build())
        }

        for (PersistentEntity entity : childrenNotMapped) {
            GraphQLMapping mapping = GraphQLEntityHelper.getMapping(entity.rootEntity)
            if (mapping == null) {
                continue
            }

            additionalTypes.add(typeManager.getQueryType(entity, GraphQLPropertyType.OUTPUT))
        }
    }

    void addDynamicEntities(GraphQLObjectType.Builder queryType) {
        DataModel.withTransaction {
            for (DataModel dataModel : DataModel.findAll()) {
                updateSchema(queryType, dataModel)
            }
        }
    }


    private void addGetQuery(Params params) {
        ProvidedOperation getOperation = params.mapping.operations.get
        if (getOperation.enabled) {

            DataFetcher getFetcher = dataFetcherManager.getReadingFetcher(params.entity, GET).orElse(params.getSingleEntityDataFetcher())

            GraphQLFieldDefinition.Builder queryOne = newFieldDefinition()
                .name(params.getQueryName())
                .type(params.objectType)
                .description(getOperation.description)
                .deprecate(getOperation.deprecationReason)
                .dataFetcher(new InterceptingDataFetcher(params.entity, serviceManager, params.queryInterceptorInvoker, GET, getFetcher))

            params.requiresIdentityArguments.add(queryOne)
            params.queryFields.add(queryOne)
        }
    }

    private void addListQuery(Params params) {
        ListOperation listOperation = params.mapping.operations.list
        if (listOperation.enabled) {

            DataFetcher listFetcher = dataFetcherManager.getReadingFetcher(params.entity, LIST).orElse(null)

            GraphQLFieldDefinition.Builder queryAll = newFieldDefinition()
                .name(params.listQueryName())
                .description(listOperation.description)
                .deprecate(listOperation.deprecationReason)

            if (listOperation.paginate) {
                if (listFetcher == null) {
                    listFetcher = new PaginatedEntityDataFetcher(params.entity)
                }
                queryAll.type(typeManager.getQueryType(params.entity, GraphQLPropertyType.OUTPUT_PAGED))
            }
            else {
                if (listFetcher == null) {
                    listFetcher = params.getEntityDataFetcher()
                }
                queryAll.type(list(params.objectType))
            }

            if (listFetcher instanceof PaginatingGormDataFetcher) {
                ((PaginatingGormDataFetcher) listFetcher).responseHandler = paginationResponseHandler
            }

            queryAll.dataFetcher(new InterceptingDataFetcher(params.entity, serviceManager, params.queryInterceptorInvoker, LIST, listFetcher))

            for (Map.Entry<String, GraphQLInputType> argument : listArguments) {
                queryAll.argument(newArgument()
                                      .name(argument.key)
                                      .type(argument.value))
            }

            params.queryFields.add(queryAll)
        }
    }

    private void addCountQuery(Params params) {
        ProvidedOperation countOperation = params.mapping.operations.count
        if (countOperation.enabled) {

            DataFetcher countFetcher = dataFetcherManager.getReadingFetcher(params.entity, COUNT).orElse(params.getCountEntityDataFetcher())

            GraphQLFieldDefinition.Builder queryCount = newFieldDefinition()
                .name(params.countQueryName())
                .type((GraphQLOutputType) typeManager.getType(Integer))
                .description(countOperation.description)
                .deprecate(countOperation.deprecationReason)

            queryCount.dataFetcher(new InterceptingDataFetcher(params.entity, serviceManager, params.queryInterceptorInvoker, COUNT, countFetcher))

            params.queryFields.add(queryCount)
        }
    }

    void updateSchema(GraphQLObjectType.Builder queryType, DataModel dataModel) {
        Params params = new DynamicEntityParams(dataModel)

        addGetQuery(params)
        addListQuery(params)
        addCountQuery(params)

        populateIdentityArguments(params.entity, params.requiresIdentityArguments.toArray(new GraphQLFieldDefinition.Builder[0]))

        queryType.fields(params.queryFields*.build())
    }

    private abstract class Params {

        GraphQLMapping mapping
        PersistentEntity entity
        GraphQLOutputType objectType
        InterceptorInvoker queryInterceptorInvoker = new QueryInterceptorInvoker()
        List<GraphQLFieldDefinition.Builder> queryFields = []
        List<GraphQLFieldDefinition.Builder> mutationFields = []
        List<GraphQLFieldDefinition.Builder> requiresIdentityArguments = []

        abstract String listQueryName()

        abstract DataFetcher getEntityDataFetcher()

        abstract String getQueryName()

        abstract ReadingGormDataFetcher getSingleEntityDataFetcher()

        abstract String countQueryName()

        abstract ReadingGormDataFetcher getCountEntityDataFetcher()
    }

    private class PersistentEntityParams extends Params {

        PersistentEntityParams(PersistentEntity entity, GraphQLMapping mapping) {
            this.entity = entity
            this.mapping = mapping
            this.objectType = typeManager.getQueryType(entity, GraphQLPropertyType.OUTPUT)
        }

        @Override
        String listQueryName() {
            return namingConvention.getList(entity)
        }

        DataFetcher getEntityDataFetcher() {
            return new EntityDataFetcher(entity)
        }

        String getQueryName() {
            return namingConvention.getGet(entity)
        }

        ReadingGormDataFetcher getSingleEntityDataFetcher() {
            return new SingleEntityDataFetcher(entity)
        }

        @Override
        String countQueryName() {
            return namingConvention.getCount(entity)
        }

        @Override
        ReadingGormDataFetcher getCountEntityDataFetcher() {
            return new CountEntityDataFetcher(entity)
        }
    }

    private class DynamicEntityParams extends Params {

        DataModel dataModel

        DynamicEntityParams(DataModel dataModel) {
            // acá hay q usar el domainClazz
            this.entity = mappingContext.getPersistentEntity(DataSet.name)
            this.mapping = GraphQLEntityHelper.getMapping(entity)
            this.dataModel = dataModel
            this.objectType = buildGraphQLObject(dataModel, entity)
        }

        private GraphQLObjectType buildGraphQLObject(DataModel dataModel, PersistentEntity entity) {
            List<GraphQLFieldDefinition> definitionList = []

            addStaticProperties(definitionList)
            addDynamicProperties(definitionList)

            // definitionList.add(errorsResponseHandler.getFieldDefinition(typeManager))

//        if (!entity.isRoot()) {
//            obj.withInterface(typeManager.createReference(entity.rootEntity, GraphQLPropertyType.OUTPUT))
//        }

//
//        objectTypeCache.put(entity, objectType)

            return newObject()
                .name(transformName(dataModel.name))
                .fields(definitionList)
                .build()
        }

        private void addStaticProperties(List<GraphQLFieldDefinition> definitionList) {
            List<GraphQLDomainProperty> properties = domainPropertyManager.builder().alwaysNullable().getProperties(entity)

            for (GraphQLDomainProperty prop : properties) {
                if (prop.output) {
                    GraphQLFieldDefinition.Builder field = newFieldDefinition()
                        .name(prop.name)
                        .deprecate(prop.deprecationReason)
                        .description(prop.description)

                    if (prop.dataFetcher != null) {
                        field.dataFetcher(prop.dataFetcher)
                    }

                    field.type((GraphQLOutputType) prop.getGraphQLType(typeManager, GraphQLPropertyType.OUTPUT))

                    definitionList.add(field.build())
                }
            }
        }

        private void addDynamicProperties(List<GraphQLFieldDefinition> definitionList) {
            for (Attribute attribute : dataModel.attributes) {
                GraphQLFieldDefinition graphQLFieldDefinition = newFieldDefinition()
                    .name(transformName(attribute.name))
                    .type((GraphQLOutputType) typeManager.getType(String))
                    .dataFetcher(new DataSetAttributeDataFetcher(attribute.name))
                    .build()

                definitionList.add(graphQLFieldDefinition)
            }

        }

        @Override
        String listQueryName() {
            return transformNameList(dataModel.name)
        }

        DataFetcher getEntityDataFetcher() {
            return new DataSetEntityFetcher(entity, dataModel.name)
        }

        String getQueryName() {
            return dataModel.name + 'Get'
        }

        ReadingGormDataFetcher getSingleEntityDataFetcher() {
            return new DataSetSingleEntityDataFetcher(entity, dataModel.name)
        }

        @Override
        String countQueryName() {
            return dataModel.name + 'Count'
        }

        @Override
        ReadingGormDataFetcher getCountEntityDataFetcher() {
            return new DataSetCountEntityDataFetcher(entity, dataModel.name)
        }

        // ver el tema d los underscore, debería ser algo tipo vul_def --> vulDef
        private String transformName(String name) {
            return name
        }

        // ver el tema d los underscore, debería ser algo tipo vul_def --> vulDef
        private String transformNameList(String name) {
            return name + 'List'
        }
    }
}
