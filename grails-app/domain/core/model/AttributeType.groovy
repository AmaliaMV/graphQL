package core.model

import javax.naming.OperationNotSupportedException

import groovy.transform.EqualsAndHashCode

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic

import graphql.schema.GraphQLOutputType

@EqualsAndHashCode(excludes = 'id')
@GrailsCompileStatic
class AttributeType {

    static mapWith = "neo4j"
    static graphql = true

    static belongsTo = [attribute: Attribute]

    static constraints = {
        attribute nullable: true
    }

    boolean bypassRequiredValidation() {
        return false
    }

    TypeToken getClazzType() {
        throw new OperationNotSupportedException()
    }
}
