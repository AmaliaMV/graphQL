package core.model

import groovy.transform.EqualsAndHashCode

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic

@EqualsAndHashCode(includes = 'defaultValue')
@GrailsCompileStatic
abstract class SimpleAttributeType extends AttributeType {

    String defaultValue

    static constraints = {
        defaultValue nullable: true
    }

    abstract TypeToken getClazzType()
}
