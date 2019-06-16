* To generate LogMonitor and CreateRandomLogs run "mvn clean package" in client directory
* To run LogMonitor and CreateRandomLogs run "java -jar LogMonitor.jar" & "java -jar CreateRandomLogs.jar" respectively
* To generate JaCoCo reports, run mvn clean test in client and stats directory, the htmls can be seen through the site file in target

# 1.
For my server, my JaCoCo coverage has major coverage gaps between my LogServlet and my StatManager. I believe this is due to BlackBoxTests not being able
to get JaCoCo coverage due to working on a servlet. Therefore LogServlet has a much higher coverage as the WhiteBoxTest cover doPost and doGet for the LogServlet.
Additionally the LogEvent class also has low coverage, this is due to implementing the LogEvent provided from the swaggerHub and not using every method as I 
did not require them for my implementation. For my client,  my coverage is only 17% overall, but 96% for my Resthome4LogsAppender. This is because the test specifications
for this assignment only specified to test our appender. Additionally the bulk of the code for client is in our gui, which can not be tested.

# 2.
For my Jdepend report for my server, I had 9 efferent couplings which meant that my package wasn't very independent and that many classes depended on each other, however I 
believe this is normal for a webapp as server and client do have to rely on another. Additionally I also had very high instability meaning my package was not very resilient to
change. My Abstractness was 0% which meant my package was completely concrete meaning all of my classes had implementation for all methods or extended from abstract classes.
Finally my Distance was 0% which meant my package was coincident with the main sequence.

For my Jdepend report for my client, I had 0 afferent couplings which means no package depended on each other. This was because CreateRandomLogs was it's own individual executable class
as well as the GUI. and each class can run without the other. I also had 22 Efferent couplings which means there were 22 other packages that the classes in the package depended on This was 
mainly due to many maven dependancies for each task. My abstractness, instability and Distance were the same as my server which indicates that the depends for server and client directly
affect another as they should.


# 3.
When running spotbugs the gui identifies that a few bugs occured when not making fields final, this was because for this assignment the database in server, and cache in clients
both had to constantly store logEvents therefore could not be final as they had to be changed. Additionally, I inefficiently used keyset iterator by using the map.get(key) lookup
instead of using a key from the keyset iterator. Finally I had 1 more bug which was an empty exception catch, which was a might ignore exception.

For my Client I had a few more bugs, this was usually due to bad coding practice such as not having a default case in a switch, or having an unused variable. Once again I saw 
a bug for a variable that could not be final but the bug states it should. I also had some repeated code, when I was populating my array however there were no major bugs I could
see for client. I went back and fixed the few bugs I could.

Overall using spotbugs my first time I saw some uses and it had a good visualization of where bugs occured, however the final variables it detected could not be changed in this instance.
