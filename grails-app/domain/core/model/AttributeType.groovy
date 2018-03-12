package core.model

import javax.naming.OperationNotSupportedException

import groovy.transform.EqualsAndHashCode

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic
import grails.gorm.validation.Constraint

@EqualsAndHashCode(excludes = 'id')
@GrailsCompileStatic
class AttributeType {

    static mapWith = "neo4j"

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
