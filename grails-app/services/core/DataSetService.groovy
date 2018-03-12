/*
 * @(#)DataSetService.groovy
 * Copyright (c) 2008-2013 Brinqa. All rights reserved.
 * BRINQA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package core

import org.apache.commons.lang.StringUtils

import grails.compiler.GrailsCompileStatic

import grails.gorm.transactions.Transactional


import core.model.Attribute

@Transactional
@GrailsCompileStatic
class DataSetService {

    @Transactional(readOnly = true)
    List<DataSet> getIncomingRelationship(DataSet domain, String childRelationshipName) {
        return []
    }

    @Transactional(readOnly = true)
    List<DataSet> getIncomingRelationships(DataSet domain, String attributeName, String referenceDataModelTitle = null) {
        return []
    }

    @Transactional(readOnly = true)
    Object getDisplayValue(DataSet dataset, Object defaultValue = null) {
        Object value

        Attribute displayAttribute = dataset?.dataModel?.getDisplayAttribute()

        if (displayAttribute) {
            value = dataset.getPropertyValue(displayAttribute.name)
        }
        else if (defaultValue) {
            value = defaultValue
        }

        if (!value) {
            value = dataset?.dataModel?.title + ' (' + dataset.id + ')'
        }

        return value
    }

    Object getValue(DataSet domain, String propertyName) {
        Object ret = null
        String prop = StringUtils.substringBefore(propertyName, '.')
        String ref = null

        if (domain == null) {
            return null
        }

        if (prop.contains('[')) {
            ref = StringUtils.substringAfter(prop, '[') - ']'
            prop = StringUtils.substringBefore(prop, '[')
        }
        if (prop.startsWith('-')) {
            prop = prop.substring(1)
            List<DataSet> relatedObjects = getIncomingRelationships((DataSet) domain, prop, ref)
            if (relatedObjects) {
                ret = relatedObjects?.first()
            }
        }
        else {
            ret = domain.getPropertyValue(prop)
        }

        String subProp = StringUtils.substringAfter(propertyName, '.')
        if (ret instanceof Collection) {
            if (subProp) {
                List subRets = []
                ret.each { DataSet subRet ->
                    Object val = getValue(subRet, subProp)
                    if (val) {
                        subRets << getValue(subRet, subProp)
                    }
                }
                ret = ((subRets.flatten() as Set) as List)
            }
        }
        else if (ret instanceof DataSet) {
            if (ref && ((DataSet) ret).dataModel?.title != ref) {
                return null // invalid
            }

            if (subProp) {
                ret = getValue(ret, subProp)
            }
        }
        return ret
    }
}
