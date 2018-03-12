/*
 * @(#)AttributeService.groovy
 * Copyright (c) 2008-2016 Brinqa. All rights reserved.
 * BRINQA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package core

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import org.apache.commons.lang.StringUtils

import com.google.common.base.Preconditions

import grails.compiler.GrailsCompileStatic
import grails.events.annotation.Subscriber
import grails.gorm.transactions.Transactional

import org.grails.datastore.mapping.model.config.GormProperties


import core.model.Attribute
import core.model.DataModel

/**
 * Service for handling extended attributes on a domain object
 */
@Transactional
@GrailsCompileStatic
class AttributeService {

    static final String CREATED_BY = 'createdBy'
    static final String UPDATED_BY = 'updatedBy'


    Attribute getAttribute(DataModel dataModel, String attributeName) {
        if (!dataModel || attributeName == null || attributeName == GormProperties.IDENTITY || attributeName.endsWith('.' + GormProperties.IDENTITY)) {
            return null
        }

        DataModel relatedTo
        String[] attributeNames = attributeName.split("\\.", 2)
        String attrName = attributeNames[0]

        if (attrName.contains(DataModel.DM_NAME_START) && attrName.contains(DataModel.DM_NAME_END)) {
            int start = attributeName.indexOf(DataModel.DM_NAME_START) + 1
            int end = attributeName.indexOf(DataModel.DM_NAME_END)

            if (start < end && attributeName.length() >= end) {
                String relatedDataModelName = attrName.substring(start, end)
                attrName = attrName.substring(0, start - 1)
                relatedTo = DataModel.findByName(relatedDataModelName)
            }
        }

        if (relatedTo && attributeNames.size() == 2) {
            return getAttribute(relatedTo, attributeNames[1])
        }

        Attribute attribute = dataModel.getAttributeByName(attrName)

        if (!attribute && attributeNames.size() > 1) {
            attribute = findIncomingRelationship(dataModel, attrName)
            if (attribute) {
                return getAttribute(attribute.dataModel, attributeNames[1])
            }
        }

        if (attributeNames.size() == 1) {
            return attribute
        }
        else {
            return null
        }

    }

    Attribute findIncomingRelationship(DataModel dataModel, String incomingRelationshipName) {
        List<Attribute> attributes = findIncomingRelationships(dataModel, incomingRelationshipName)
        return attributes.isEmpty() ? null : attributes.first()
    }

    List<Attribute> findIncomingRelationships(DataModel dataModel, String incomingRelationshipName = null) {
       /* String dmLabel = Labels.getLabels(DataModel).first()
        String attrLabel = Labels.getLabels(Attribute).first()

        String attrClause = ""
        Map parameters = [:]

        if (incomingRelationshipName) {
            parameters.put('attrName', "\"childRelationshipName\": \"$incomingRelationshipName\"".toString())
            parameters.put('attrName2', "\"childRelationshipName\":\"$incomingRelationshipName\"".toString())
            attrClause = """
            AND
                (attr.manifest CONTAINS {attrName} OR attr.manifest CONTAINS {attrName2})
            """
        }

        String dmFilter = ''
        int num = 0
        DataModel currentDM = dataModel

        while (currentDM) {
            parameters.put("dmId${num}".toString(), "\"id\": \"${currentDM.id}\"".toString())
            parameters.put("dmId${num + 1}".toString(), "\"id\":\"${currentDM.id}\"".toString())

            if (dmFilter) {
                dmFilter += ' OR '
            }

            dmFilter += "attr.manifest CONTAINS {dmId${num}} OR attr.manifest CONTAINS {dmId${num + 1}}"

            num += 2
            currentDM = currentDM.parent
        }

        String cypher = """
            MATCH
                (dm: `${dmLabel}`)-[:ATTRIBUTES]->(attr: `${attrLabel}`)
            WHERE
                ($dmFilter)
            ${attrClause}
            AND
                (attr.manifest CONTAINS '${MasterDetailAttributeType.simpleName}' OR attr.manifest CONTAINS '${ReferenceAttributeType.simpleName}')
            RETURN
                DISTINCT attr
        """

        StatementResult result = reflectionsService.executeCypher(DataModel, cypher, parameters)
        return result.toList(Attribute)*/
        return null
    }


}
