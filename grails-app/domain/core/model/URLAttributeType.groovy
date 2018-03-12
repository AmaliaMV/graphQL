package core.model

import org.apache.commons.validator.routines.UrlValidator

import grails.compiler.GrailsCompileStatic

import org.grails.datastore.mapping.validation.ValidationErrors

@GrailsCompileStatic
class URLAttributeType extends TextAttributeType {

    static constraints = {
        defaultValue nullable: true
    }

    URLAttributeType() {
        super()
    }

    URLAttributeType(Integer length, String defaultValue) {
        super(length, defaultValue)
    }
}
