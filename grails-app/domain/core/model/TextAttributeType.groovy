package core.model

import groovy.transform.EqualsAndHashCode

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic

import org.grails.datastore.mapping.validation.ValidationErrors

@EqualsAndHashCode(includes = 'length', callSuper = true)
@GrailsCompileStatic
class TextAttributeType extends SimpleAttributeType {

    static mapWith = "neo4j"
    static graphql = true

    static final Integer TEXT_ATTRIBUTE_MAX_LENGTH = 40

    Integer length

    static constraints = {
        length nullable: true, validator: { Integer val, TextAttributeType obj, ValidationErrors errors ->
            if (val == null) {
                errors.rejectValue('length', 'textAttributeType.defaultValue.lengthRequired')
            }
        }
        defaultValue nullable: true
    }

    TextAttributeType() {
        this(TEXT_ATTRIBUTE_MAX_LENGTH)
    }

    TextAttributeType(Integer length, String defaultValue = '') {
        this.length = length
        this.defaultValue = defaultValue
    }

    TypeToken getClazzType() {
        return TypeToken.of(String)
    }
}
