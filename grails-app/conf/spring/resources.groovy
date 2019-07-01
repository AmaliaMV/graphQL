import org.grails.datastore.gorm.neo4j.Neo4jMappingContext
import org.grails.gorm.graphql.GraphQLServiceManager
import org.grails.gorm.graphql.Schema
import org.grails.gorm.graphql.binding.manager.DefaultGraphQLDataBinderManager
import org.grails.gorm.graphql.entity.GraphQLEntityNamingConvention
import org.grails.gorm.graphql.entity.property.manager.DefaultGraphQLDomainPropertyManager
import org.grails.gorm.graphql.entity.property.manager.GraphQLDomainPropertyManager
import org.grails.gorm.graphql.fetcher.manager.DefaultGraphQLDataFetcherManager
import org.grails.gorm.graphql.fetcher.manager.GraphQLDataFetcherManager
import org.grails.gorm.graphql.interceptor.manager.DefaultGraphQLInterceptorManager
import org.grails.gorm.graphql.plugin.DefaultGraphQLContextBuilder
import org.grails.gorm.graphql.plugin.GrailsGraphQLConfiguration
import org.grails.gorm.graphql.plugin.binding.GrailsGraphQLDataBinder
import org.grails.gorm.graphql.response.delete.DefaultGraphQLDeleteResponseHandler
import org.grails.gorm.graphql.response.errors.DefaultGraphQLErrorsResponseHandler
import org.grails.gorm.graphql.response.errors.GraphQLErrorsResponseHandler
import org.grails.gorm.graphql.response.pagination.DefaultGraphQLPaginationResponseHandler
import org.grails.gorm.graphql.response.pagination.GraphQLPaginationResponseHandler
import org.grails.gorm.graphql.types.DefaultGraphQLTypeManager
import org.grails.gorm.graphql.types.GraphQLTypeManager

import core.DynamicSchema
import graphql.GraphQL
import graphql.GraphqlManagerService

// Place your Spring DSL code here
beans = {
    grailsGraphQLConfiguration(GrailsGraphQLConfiguration)

    graphQLContextBuilder(DefaultGraphQLContextBuilder)

    graphQLDataBinder(GrailsGraphQLDataBinder)
    graphQLErrorsResponseHandler(DefaultGraphQLErrorsResponseHandler, ref("messageSource"))
    graphQLEntityNamingConvention(GraphQLEntityNamingConvention)
    graphQLDomainPropertyManager(DefaultGraphQLDomainPropertyManager)
    graphQLPaginationResponseHandler(DefaultGraphQLPaginationResponseHandler)

    graphQLTypeManager(
        DefaultGraphQLTypeManager,
        ref("graphQLEntityNamingConvention"),
        ref("graphQLErrorsResponseHandler"),
        ref("graphQLDomainPropertyManager"),
        ref("graphQLPaginationResponseHandler"))
    graphQLDataBinderManager(DefaultGraphQLDataBinderManager, ref("graphQLDataBinder"))
    graphQLDeleteResponseHandler(DefaultGraphQLDeleteResponseHandler)
    graphQLDataFetcherManager(DefaultGraphQLDataFetcherManager)
    graphQLInterceptorManager(DefaultGraphQLInterceptorManager)
    graphQLServiceManager(GraphQLServiceManager)

    graphQLSchemaGenerator(DynamicSchema) {
        deleteResponseHandler = ref("graphQLDeleteResponseHandler")
        namingConvention = ref("graphQLEntityNamingConvention")
        typeManager = ref("graphQLTypeManager")
        dataBinderManager = ref("graphQLDataBinderManager")
        dataFetcherManager = ref("graphQLDataFetcherManager")
        interceptorManager = ref("graphQLInterceptorManager")
        paginationResponseHandler = ref("graphQLPaginationResponseHandler")
        serviceManager = ref("graphQLServiceManager")

        dateFormats = '#{grailsGraphQLConfiguration.getDateFormats()}'
        dateFormatLenient = '#{grailsGraphQLConfiguration.getDateFormatLenient()}'
        listArguments = '#{grailsGraphQLConfiguration.getListArguments()}'
    }

    graphQLSchema(graphQLSchemaGenerator: "generate")
    graphQL(GraphQL, ref("graphQLSchema"))
}
