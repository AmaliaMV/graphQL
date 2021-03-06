package util

import core.DataSet
import core.model.Attribute
import core.model.DateTimeAttributeType

class DataSetRender {

    static Collection<Map<String, Attribute>> getAttributesFromDataSet(DataSet dataSet) {
        return (Collection<Map<String, Attribute>>) dataSet.dataModel.attributes.collect { [(it.name): it] }
    }

    static def renderAttribute(DataSet dataSet, Map<String, Attribute> attributeMap) {
        String attributeKey = attributeMap.keySet().first()
        Attribute attribute = attributeMap.values().first()
        def value = dataSet.getPropertyValue(attributeKey)
        if (value != null) {
            if (attribute.type instanceof DateTimeAttributeType && value instanceof Long) {
                return DateFormat.format(new Date((long) value))
            }
            else {
                return value
            }
        }
    }

}
