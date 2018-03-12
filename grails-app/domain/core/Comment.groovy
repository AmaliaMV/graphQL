package core

import groovy.transform.EqualsAndHashCode

import grails.compiler.GrailsCompileStatic

@EqualsAndHashCode(excludes = 'id')
@GrailsCompileStatic
class Comment extends AbstractBaseDomain implements Displayable {

    public static final String DISPLAY_VALUE_PROPERTY = 'body'

    String body

    static constraints = {
        body blank: false
    }

    @Override
    String getDisplayValueProperty() {
        return DISPLAY_VALUE_PROPERTY
    }

    @Override
    String getDisplayValue() {
        return body
    }
}
