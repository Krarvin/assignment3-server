* To generate LogMonitor and CreateRandomLogs run "mvn clean package" in client directory
* To run LogMonitor and CreateRandomLogs run "java -jar LogMonitor.jar" & "java -jar CreateRandomLogs.jar" respectively
* To generate JaCoCo reports, run mvn clean test in client and stats directory, the htmls can be seen through the site file in target

# 1.
For my server, my JaCoCo coverage has major coverage gaps between my LogServlet and my StatManager. I believe this is due to BlackBoxTests not being able
to get JaCoCo coverage due to working on a servlet. Therefore LogServlet has a much higher coverage as the WhiteBoxTest cover doPost and doGet for the LogServlet.
Additionally the LogEvent class also has low coverage, this is due to implementing the LogEvent provided from the swaggerHub and not using every method as I 
did not require them for my implementation. For my client,  my coverage is only 17% overall, but 96% for my Resthome4LogsAppender. This is because the test specifications
for this assignment only specified to test our appender. Additionally the bulk of the code for client is in our gui, which can not be tested.

2.
