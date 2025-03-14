# Two Tier Clien Server Application

## Description
- This project implements a two-tier client-server application that allows users to connect to a MySQL database, execute SQL queries, and display results in a graphical user interface (GUI).
- The application supports multiple user roles with distinct permissions and authenticates users based on credentials stored in properties files.
### There are two applications included in this project:
- SQL Client Application (Main.java) – A general-purpose SQL client that allows users to interact with MySQL databases.
- Specialized Accountant Application (Secondary.java) – A restricted version designed for accountant users, which utilizes PreparedStatements for secure query execution.

### features 
- User Authentication: Users connect using credentials stored in properties files.
- SQL Execution: Supports standard SQL commands such as SELECT, INSERT, UPDATE, and DELETE.
- Role-Based Access Control: Each user has specific permissions based on predefined roles.
### Graphical User Interface
- Connect and disconnect from the database.
- Execute SQL queries and view structured results in a table format.
- Clear input fields and result windows.

