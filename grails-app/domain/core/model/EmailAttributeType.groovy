package core.model

import grails.compiler.GrailsCompileStatic

import graphql.schema.GraphQLOutputType
import graphql.types.scalars.GraphQLStringArray

@GrailsCompileStatic
class EmailAttributeType extends TextAttributeType {

    static mapWith = "neo4j"
    static graphql = true

    static constraints = {
        defaultValue nullable: true
    }

    EmailAttributeType() {
        super()
    }

    EmailAttributeType(Integer length, String defaultValue = '') {
        super(length, defaultValue)
    }
}
