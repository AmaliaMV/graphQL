package graphql

import core.DataSet
import core.Ticket
import core.model.Attribute
import core.model.DataModel
import core.model.DateTimeAttributeType
import core.model.EmailAttributeType
import core.model.NumberAttributeType
import core.model.TextAttributeType
import core.model.URLAttributeType

class BootStrap {

    def init = { servletContext ->

        DataModel.withNewSession {
            DataModel.withNewTransaction {

                // base model
                DataModel baseModel = new DataModel(name: DataModel.BASE_MODEL_NAME, title: DataModel.BASE_MODEL_NAME)
                baseModel.save(failOnError: true)

                DataModel dataModelEmployee = new DataModel(name: 'employee', title: 'Employee', parent: baseModel)
                dataModelEmployee.save(failOnError: true)

                // add attributes
                Attribute attribute1 = new Attribute(name: 'firstName', title: 'First Name')
                attribute1.type = new TextAttributeType(30, '"fn"')
                Attribute attribute2 = new Attribute(name: 'lastName', title: 'Last Name')
                attribute2.type = new TextAttributeType(30, '"smith"')
                Attribute attribute3 = new Attribute(name: 'email', title: 'Email')
                attribute3.type = new EmailAttributeType(40, '"undefined@und.com"')
                Attribute attribute4 = new Attribute(name: 'age', title: 'Age')
                attribute4.type = new NumberAttributeType(length: 3, decimalPlaces: 0)
                Attribute attribute5 = new Attribute(name: 'personalPage', title: 'Personal Profile')
                attribute5.type = new URLAttributeType(50, '"http://myprofile.com"')

                dataModelEmployee.addToAttributes(attribute1)
                dataModelEmployee.addToAttributes(attribute2)
                dataModelEmployee.addToAttributes(attribute3)
                dataModelEmployee.addToAttributes(attribute4)
                dataModelEmployee.addToAttributes(attribute5)

                attribute1.save(failOnError: true)
                attribute2.save(failOnError: true)
                attribute3.save(failOnError: true)
                attribute4.save(failOnError: true)
                attribute5.save(failOnError: true)


                dataModelEmployee.save(failOnError: true)

                // dataSet
                DataSet dataSetEmployee = new DataSet(dataModelEmployee)
                dataSetEmployee.save(failOnError: true)

                // hosts
                DataModel dataModelHost1 = new DataModel(name: 'host1', title: 'DataModel Host', parent: baseModel)
                dataModelHost1.save(failOnError: true)

                Attribute hostAttribute1 = new Attribute(name: 'name', title: 'name')
                hostAttribute1.type = new TextAttributeType(90)

                Attribute hostAttribute2 = new Attribute(name: 'fqdn', title: 'FQDN')
                hostAttribute2.type = new TextAttributeType(90)

                dataModelHost1.addToAttributes(hostAttribute1)
                dataModelHost1.addToAttributes(hostAttribute2)

                hostAttribute1.save(failOnError: true)
                hostAttribute2.save(failOnError: true)

                dataModelHost1.save(failOnError: true)

                // vulnerabilities
                DataModel dataModelVulnerability = new DataModel(name: 'vulnerability1', title: 'Vulnerability 1', parent: baseModel)
                dataModelVulnerability.save(failOnError: true)

                Attribute vulnAttribute1 = new Attribute(name: 'name', title: 'name')
                vulnAttribute1.type = new TextAttributeType(90)

                Attribute vulnAttribute2 = new Attribute(name: 'osKind', title: 'OS Kind')
                vulnAttribute2.type = new TextAttributeType(20)

                Attribute vulnAttribute3 = new Attribute(name: 'dateReported', title: 'Date Reported')
                vulnAttribute3.type = new DateTimeAttributeType()

                Attribute vulnAttribute4 = new Attribute(name: 'status', title: 'Status')
                vulnAttribute4.type = new TextAttributeType(20)

                dataModelVulnerability.addToAttributes(vulnAttribute1)
                dataModelVulnerability.addToAttributes(vulnAttribute2)
                dataModelVulnerability.addToAttributes(vulnAttribute3)
                dataModelVulnerability.addToAttributes(vulnAttribute4)

                vulnAttribute1.save(failOnError: true)
                vulnAttribute2.save(failOnError: true)
                vulnAttribute3.save(failOnError: true)
                vulnAttribute4.save(failOnError: true)

                // dataSet host
                DataSet host1 = new DataSet(dataModelHost1)
                host1.name = "Host 1"
                host1.osKind = 'windows'
                host1.ip = '127.0.0.1'
                host1.fqdn = 'km2k3945cc12d07.kih.kmart.com'
                host1.save(failOnError: true)

                DataSet host2 = new DataSet(dataModelHost1)
                host2.name = "Host 2"
                host2.osKind = 'windows'
                host2.ip = '127.3.3.1'
                host2.save(failOnError: true)

                DataSet host3 = new DataSet(dataModelHost1)
                host3.name = "Host 3"
                host3.osKind = 'Linux'
                host3.ip = '192.168.2.3'
                host3.save(failOnError: true)

                DataSet host4 = new DataSet(dataModelHost1)
                host4.name = "Host 4"
                host4.osKind = 'windows'
                host4.ip = '10.0.0.4'
                host4.save(failOnError: true)

                DataSet host5 = new DataSet(dataModelHost1)
                host5.name = "Host 5"
                host5.osKind = 'MacOs'
                host5.ip = '254.12.1.23'
                host5.save(failOnError: true)

                // dataSet Vulnerabilities
                Calendar calendar = Calendar.getInstance()
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.add(Calendar.DATE, -3)
                Date thisWeek = calendar.time
                calendar.add(Calendar.DATE, -7)
                Date previousWeek = calendar.time
                calendar.add(Calendar.DATE, -7)
                Date twoWeeks = calendar.time
                calendar.add(Calendar.DATE, -7)
                Date threeWeeks = calendar.time
                calendar.add(Calendar.DATE, -7)
                Date fourWeeks = calendar.time
                calendar.add(Calendar.DATE, -7)
                Date fiveWeeks = calendar.time

                calendar.set(Calendar.MILLISECOND, 0)
                calendar.set(1988, 2, 12, 0, 0, 0)
                Date firstFound1 = calendar.time
                calendar.set(1988, 2, 2, 0, 0, 0)
                Date firstFound2 = calendar.time
                calendar.set(1989, 4, 22, 0, 0, 0)
                Date firstFound3 = calendar.time
                calendar.set(1988, 5, 2, 0, 0, 0)
                Date firstFound4 = calendar.time
                calendar.set(1998, 8, 12, 0, 0, 0)
                Date firstFound5 = calendar.time
                calendar.set(2000, 6, 24, 0, 0, 0)
                Date firstFound6 = calendar.time
                calendar.set(1989, 12, 22, 0, 0, 0)
                Date firstFound7 = calendar.time
                calendar.set(2000, 4, 31, 0, 0, 0)
                Date firstFound8 = calendar.time
                calendar.set(1989, 4, 2, 0, 0, 0)
                Date firstFound9 = calendar.time
                calendar.set(1998, 8, 27, 0, 0, 0)
                Date firstFound10 = calendar.time

                DataSet vuln1 = new DataSet(dataModelVulnerability)
                vuln1.name = "Vulnerability 1"
                vuln1.dateReported = thisWeek
                vuln1.osKind = 'windows'
                vuln1.status = 'Open'
                vuln1.severity = 0d
                vuln1.host = host1
                vuln1.firstFound = firstFound1
                vuln1.save(failOnError: true)

                DataSet vuln2 = new DataSet(dataModelVulnerability)
                vuln2.name = "Vulnerability 2"
                vuln2.dateReported = thisWeek
                vuln2.osKind = 'windows'
                vuln2.status = 'Close'
                vuln2.severity = 3.11d
                vuln2.host = host1
                vuln2.firstFound = firstFound2
                vuln2.save(failOnError: true)

                DataSet vuln3 = new DataSet(dataModelVulnerability)
                vuln3.name = "Vulnerability 3"
                vuln3.dateReported = fiveWeeks
                vuln3.osKind = 'linux'
                vuln3.status = 'Open'
                vuln3.severity = 2.111111d
                vuln3.host = host1
                vuln3.firstFound = firstFound3
                vuln3.save(failOnError: true)

                DataSet vuln4 = new DataSet(dataModelVulnerability)
                vuln4.name = "Vulnerability 4"
                vuln4.dateReported = twoWeeks
                vuln4.osKind = 'linux'
                vuln4.status = 'Open'
                vuln4.severity = 3.3333333333d
                vuln4.host = host1
                vuln4.firstFound = firstFound4
                vuln4.save(failOnError: true)

                DataSet vuln5 = new DataSet(dataModelVulnerability)
                vuln5.name = "Vulnerability 5"
                vuln5.dateReported = twoWeeks
                vuln5.osKind = 'macos'
                vuln5.status = 'Close'
                vuln5.severity = 1.00000d
                vuln5.host = host1
                vuln5.firstFound = firstFound5
                vuln5.save(failOnError: true)

                DataSet vuln6 = new DataSet(dataModelVulnerability)
                vuln6.name = "Vulnerability 6"
                vuln6.dateReported = threeWeeks
                vuln6.osKind = 'macos'
                vuln6.status = 'Open'
                vuln6.severity = 5d
                vuln6.host = host2
                vuln6.firstFound = firstFound6
                vuln6.save(failOnError: true)

                DataSet vuln7 = new DataSet(dataModelVulnerability)
                vuln7.name = "Vulnerability 7"
                vuln7.dateReported = threeWeeks
                vuln7.osKind = 'macos'
                vuln7.status = 'Close'
                vuln7.severity = 0.10101010101d
                vuln7.host = host3
                vuln7.firstFound = firstFound7
                vuln7.save(failOnError: true)

                DataSet vuln8 = new DataSet(dataModelVulnerability)
                vuln8.name = "Vulnerability 8"
                vuln8.dateReported = fourWeeks
                vuln8.osKind = 'windows'
                vuln8.status = 'Open'
                vuln8.severity = 1.111111111111111111111d
                vuln8.host = host4
                vuln8.firstFound = firstFound8
                vuln8.save(failOnError: true)

                DataSet vuln9 = new DataSet(dataModelVulnerability)
                vuln9.name = "Vulnerability 9"
                vuln9.dateReported = previousWeek
                vuln9.osKind = 'linux'
                vuln9.status = 'Close'
                vuln9.severity = 3.11111111111111111111d
                vuln9.host = host1
                vuln9.firstFound = firstFound9
                vuln9.save(failOnError: true)

                DataSet vuln10 = new DataSet(dataModelVulnerability)
                vuln10.name = "Vulnerability 10"
                vuln10.dateReported = fiveWeeks
                vuln10.osKind = 'linux'
                vuln10.status = 'Open'
                vuln10.severity = 1.66666666d
                vuln10.host = host3
                vuln10.firstFound = firstFound10
                vuln10.save(failOnError: true)

                DataSet vuln11 = new DataSet(dataModelVulnerability)
                vuln11.name = "Vulnerability 11"
                vuln11.dateReported = previousWeek
                vuln11.osKind = 'windows'
                vuln11.status = 'Open'
                vuln11.severity = 0.171717d
                vuln11.host = host2
                vuln11.firstFound = firstFound1
                vuln11.save(failOnError: true)

                DataSet vuln12 = new DataSet(dataModelVulnerability)
                vuln12.name = "Vulnerability 12"
                vuln12.dateReported = threeWeeks
                vuln12.osKind = 'windows'
                vuln12.status = 'Open'
                vuln12.severity = 3.33333333333111d
                vuln12.host = host1
                vuln12.firstFound = firstFound5
                vuln12.save(failOnError: true)

                // ticket
                Attribute ticketAttribute1 = new Attribute(name: 'title', title: 'Title')
                ticketAttribute1.type = new TextAttributeType(30, '"fn"')

                Attribute ticketAttribute2 = new Attribute(name: 'status', title: 'Status', indexText: true, supportsFilter: true)
                ticketAttribute2.type = new TextAttributeType(30, '"status"')

                DataModel dataModelTicket = new DataModel(name: 'ticket', title: 'Ticket', parent: baseModel)
                dataModelTicket.addToAttributes(attribute1)
                dataModelTicket.addToAttributes(attribute2)

                ticketAttribute1.dataModel = dataModelTicket
                ticketAttribute2.dataModel = dataModelTicket

                dataModelTicket.save(failOnError: true)

                Ticket ticket = new Ticket(dataModelTicket)
                ticket.sources = [vuln1, vuln6]
                ticket.priority = "2"
                ticket.summary = 'ticket 1 summary'
                ticket.sys_id = '1212'
                ticket.save(failOnError: true, flush: true)
            }
        }
    }
    def destroy = {
    }
}
