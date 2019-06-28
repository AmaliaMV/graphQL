package core.model

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic

import org.grails.gorm.graphql.types.scalars.jsr310.GraphQLLocalDateTime

import graphql.schema.GraphQLOutputType

@GrailsCompileStatic
class DateTimeAttributeType extends SimpleAttributeType {

    static mapWith = "neo4j"
    static graphql = true

    TypeToken getClazzType() {
        return TypeToken.of(Date)
    }
}
