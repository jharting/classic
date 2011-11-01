Seam Registration Example
=========================

This is a trivial example for the Seam tutorial. 

The following list of changes documents steps necessary to migrate the original Seam Registration example to a Seam 2 example running on Java EE 6 natively (Classic Registration).

Changes related to migration to Java EE 6:

1.	Remove Seam 2 modules and its dependencies
1.	Remove the following files:
   1. components.properties
   1. components.xml
   1. ejb-jar.xml
   1. jboss-app.xml
   1. application.xml - this is created automatically - see the pom.xml file
1. Modify faces-config.xml
    1. Remove the view-handler element
    1. Add the following declaration `<el-resolver>cz.muni.fi.xharting.classic.el.ClassicElResolver</el-resolver>`
    1. Change the version in the faces-config element to 2.0
1. Add Classic dependencies
1. Add beans.xml to both the WEB-INF folder of the web application and META-INF folder of the EJB module
1. Replace Hibernate Validator constraints with Bean Validation ones in User.java
    1. org.hibernate.validator.Length --> javax.validation.constraints.Size
    1. org.hibernate.validator.NotNull --> javax.validation.constraints.NotNull
1. In RegisterAction.java comment out lines 13 and 43 (FacesMessages not supported ATM)
1. In RegisterAction.java change the declaration of the "log" field (line 28) to be non-static (remove the static modifier)
    


Other changes not related directly to migration:

1. Modify datasource definition (AS7)