package core.model

import grails.util.Holders

import org.grails.datastore.mapping.validation.ValidationErrors
import org.grails.encoder.CodecLookup

import core.ConfigItem
import core.DataSet

class DataModel extends ConfigItem {

    static mapWith = "neo4j"
    static graphql = true

    String description

    String domainClazz = DataSet.simpleName

    String resourceName

    Attribute displayAttribute

    List<Attribute> attributes = []

    static hasMany = [
        attributes: Attribute
    ]

    static mappedBy = [attributes: null]

    static mapping = {
        displayAttribute lazy: false, fetch: "eager"
        resourceName index: true
    }

    static constraints = {
        name shared: 'uniqueName'
        description nullable: true
        title blank: false, unique: true
        domainClazz nullable: false
        displayAttribute nullable: true
        resourceName nullable: false, blank: false
        attributes validator: { List<Attribute> attrCol, DataModel obj, ValidationErrors errors ->
            (Collection<Attribute>) attrCol.each { Attribute attribute ->
                boolean validated = attribute.validate()
                if (!validated) {
                    errors.rejectValue('attributes', attribute.errors.fieldError.code, attribute.errors.fieldError.arguments,
                                       "Attribute ${attribute.title} is invalid.".toString())
                }

                return validated
            }
        }
    }

    void beforeValidate() {
        String resourceName = getCodecLookup().lookupEncoder('URL').encode(this.name)
        if (this.resourceName != resourceName) {
            this.resourceName = resourceName
        }
    }

    protected static CodecLookup getCodecLookup() {
        return (CodecLookup) Holders.getApplicationContext().getBean('codecLookup')
    }
}
