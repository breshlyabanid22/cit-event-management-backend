# CIT Event Management System

The purpose of the Event Management System project is to develop 
a comprehensive and user-friendly platform specifically designed for CIT University. The system will automate and streamline the planning, execution, and management of various university events, including academic conferences, seminars, workshops, and social gatherings, ensuring efficient coordination and a seamless experience for both organizers and attendees.


## Set up
1. Download file or perform 'git clone'
2. Open project in Intellij or VS Code
4. Go to src > main > resource > application.properties
    - You can Edit this:
        - spring.datasource.url=jdbc:mysql://localhost:3306/cit-ems-db
        - spring.datasource.username=bayabas
        - spring.datasource.password=userbayabas
    - Or name your database "cit-ems-db" with the respective username and password
5. Create the database
6. Manually insert a data in the database, add a "admin" user with an "Admin" role  
8. Go to your IDE and run the project: Open a terminal > type 'mvn spring-boot:run' or you can click the run button in your IDE.
9. Now go to your frontend
