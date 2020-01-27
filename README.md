
#Open Call Report Downloader for Global Command Center - NCR



Desktop application for automatic downloading and filtering large Excel file from URL Link.



##Usage
---



Intended for internal usage by the Global Command Center Team with preparing Excel workbook for daily business and reporting to upper management.

Application downloads Global Open Calls Report from URL link, filters it in accordance to the teams predetermined needs and generates a new, formated workbook containing the specific, relevant data. 


URL link and filtering prerequisites are being stored in data.ini file and can be edited from the application itself making it flexible in design and practice (in this show-repository, file is filled with "dummy" data as this information is protected and under NDA)


##Built With
---


* [Maven][https://maven.apache.org/] - Dependency Management, Build PlugIn for packaging shaded (fat) JARs
* [JavaFX][https://openjfx.io/] - Graphic User Interface
* [Apace POI][https://poi.apache.org/] - Java API for Microsoft Documents for working with Excel workbooks
* [Excel Streaming Reader][https://github.com/monitorjbl/excel-streaming-reader] - Java Library for parsing data from large excel files
* [Ini4J][http://ini4j.sourceforge.net/] - Java API for handling Windows .ini file format
* [Java JDK 1.8][https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html]


##Deployment
---


This is intended to be used as a standalone application, running independently on the local machine, hence the executable shaded JAR, containing all required dependencies and classes compiled in it. Shaded JAR is being compiled with Maven goal "mvn package" in the projects root directory than copied, along with data.ini file, to the local machine. 
In our case, we upload it to the teams Share point and users download it to their computer, for the ease of distribution.


Only requirement on the local machine where the application will be user, is that the .jar and .ini files are stored in the same root directory (they needs to be in the same folder), besides, of course, Java VM 8 installed on the computer


##Author

Dimitrije Mitic


* [GitHub][https://github.com/Dimitri1205]
* [LinkedIn][linkedin.com/in/mitic-dimitrije-2a9576159]
* Email - dimitrije1205@gmail.com















