package core

import org.grails.datastore.gorm.neo4j.GraphPersistentEntity
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
abstract class ConfigItem extends AbstractBaseDomain {

    public static final String DISPLAY_VALUE_PROPERTY = 'title'
    public static final String CONFIG_ITEM_PREFIX = 'GQL__'

    String name
    String title

    static constraints = {
        name blank: false
        title blank: false
    }

    static mapping = {
        labels { GraphPersistentEntity pe -> "${CONFIG_ITEM_PREFIX}${pe.javaClass.simpleName}" }
    }

    String getDisplayValueProperty() {
        return DISPLAY_VALUE_PROPERTY
    }

    String getDisplayValue() {
        return title
    }

}
