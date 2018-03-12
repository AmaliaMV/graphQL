package core.model

import grails.util.Holders

import org.grails.datastore.mapping.validation.ValidationErrors
import org.grails.encoder.CodecLookup

import core.ConfigItem
import core.DataSet
import core.Ticket

class DataModel extends ConfigItem {

    public static final String BASE_MODEL_NAME = "base_model"

    private static final String DATA_MODEL_CLASSIFICATION_NAME_GENERIC = "Generic"
    private static final String DATA_MODEL_CLASSIFICATION_NAME_TICKET = "Ticket"

    private static final Map<Class, String> DOMAIN_CLASSIFICATION = [(DataSet): DATA_MODEL_CLASSIFICATION_NAME_GENERIC,
                                                                     (Ticket): DATA_MODEL_CLASSIFICATION_NAME_TICKET]

    public static final String DM_NAME_START = '['
    public static final String DM_NAME_END = ']'

    private Map<String, Attribute> attributeMapInternal = [:]

    String description
    Boolean startsWithVowelSound = false
    String domainClazz = DataSet.simpleName
    String resourceName

    Attribute displayAttribute

    List<Attribute> attributes = []

    DataModel parent

    static hasMany = [
        attributes: Attribute
    ]

    static mappedBy = [attributes: null]

    static mapping = {
        parent lazy: false, fetch: "eager"
        displayAttribute lazy: false, fetch: "eager"
        resourceName index: true
    }

    static constraints = {
        name shared: 'uniqueName'
        description nullable: true
        title blank: false, unique: true
        startsWithVowelSound nullable: false
        domainClazz nullable: false
        displayAttribute nullable: true
        parent nullable: true, validator: { DataModel value, DataModel instance ->
            return (instance?.name == BASE_MODEL_NAME) ?: value != null
        }
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

    static transients = [
        'classification',
        'nameSpacesRemoved',
        'nameUrlEncoded',
        'nameCamelCase',
        'nameSpacesRemovedLowerCase',
        'attributeMapInternal'
    ]

    void beforeValidate() {
        String resourceName = getCodecLookup().lookupEncoder('URL').encode(this.name)
        if (this.resourceName != resourceName) {
            this.resourceName = resourceName
        }
    }

    String getClassification() {
        return DOMAIN_CLASSIFICATION.get(domainClazz)
    }

    Attribute getAttributeByName(String name) {
        return getAttributeMapInternal().get(name)
    }

    List<Attribute> getAttributes() {
        List<Attribute> attributes = []
        attributes.addAll(this.attributes)
        if (parent) {
            List<String> attributeNames = attributes.collect { it.name }
            List<Attribute> parentAttrs = parent.getAttributes().findAll { !attributeNames.contains(it.name) }
            attributes.addAll(parentAttrs)
        }

        return attributes
    }

    DataModel getRelatedDataModel(String propertyName) {
        DataModel relatedDataModel = null

        if (propertyName) {

            int pos = propertyName.indexOf('.')
            String attributeName = (pos == -1) ? propertyName : propertyName.substring(0, pos)

            if (attributeName.contains(DM_NAME_START) && attributeName.contains(DM_NAME_END)) {
                int start = attributeName.indexOf(DM_NAME_START) + 1
                int end = attributeName.indexOf(DM_NAME_END)

                if (start < end && (end < pos || pos == -1) && attributeName.length() >= end) {
                    String relatedDataModelName = attributeName.substring(start, end)
                    relatedDataModel = DataModel.findByName(relatedDataModelName)
                }
            }

            if (pos != -1 && relatedDataModel) {
                relatedDataModel = relatedDataModel.getRelatedDataModel(propertyName.substring(pos + 1))
            }
        }

        return relatedDataModel
    }

    void addToAttributes(Attribute attribute) {
        addTo("attributes", attribute)
        fillInternalCache(attribute)
    }

    boolean hasAttribute(String attributeName) {
        return getAttributeMapInternal().containsKey(attributeName)
    }

    boolean isParentDataModel(DataModel possibleParent) {
        if (parent && possibleParent) {
            return parent == possibleParent || parent.isParentDataModel(possibleParent)
        }
        return false
    }

    boolean hasChildren() {
        DataModel.findByParent(this) != null
    }

    private Map<String, Attribute> getAttributeMapInternal() {
        if (!this.attributeMapInternal) {
            fillInternalCache()
        }
        return this.attributeMapInternal
    }

    private void fillInternalCache() {
        this.getAttributes()?.each { Attribute attribute ->
            fillInternalCache(attribute)
        }
    }

    private void fillInternalCache(Attribute attribute) {

        if (!this.attributeMapInternal) {
            this.attributeMapInternal = [:]
        }

        String name = attribute.name
        attributeMapInternal.put(name, attribute)
    }

    protected static CodecLookup getCodecLookup() {
        return (CodecLookup) Holders.getApplicationContext().getBean('codecLookup')
    }
}
