package core.model

import groovy.transform.EqualsAndHashCode

import com.google.common.reflect.TypeToken

import grails.compiler.GrailsCompileStatic

import org.grails.datastore.mapping.validation.ValidationErrors

@EqualsAndHashCode(includes = 'length,decimalPlaces', callSuper = true)
@GrailsCompileStatic
class NumberAttributeType extends SimpleAttributeType {

    Integer length
    Integer decimalPlaces

    static constraints = {
        length nullable: true, validator: { Integer val, NumberAttributeType obj, ValidationErrors errors ->
            if (val == null) {
                errors.rejectValue('length', 'textAttributeType.defaultValue.lengthRequired')
            }
        }
        decimalPlaces nullable: true, validator: { Integer val, NumberAttributeType obj, ValidationErrors errors ->
            if (val == null) {
                errors.rejectValue('decimalPlaces', 'textAttributeType.defaultValue.decimalPlacesRequired')
            }
        }
        defaultValue nullable: true
    }

    NumberAttributeType() {
        this.decimalPlaces = 0
    }

    TypeToken getClazzType() {
        return TypeToken.of(Number)
    }
}
