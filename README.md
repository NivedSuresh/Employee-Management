### Retail Cloud Employee Management (Contact: 9744901947, nivedspvt@gmail.com)
<br><br>
#### How to Run:

The application depends on PostgreSQL as its database. By default, the application runs on the 'local' profile, which utilizes the locally running PostgreSQL. Please adjust the connection string and credentials inside application-local.properties if you want to make any changes.
<br>
If you have Docker installed locally and prefer using Docker Compose over a local PostgreSQL instance, you can use the profile 'dev'. This profile will spin up a PostgreSQL instance, and the connection is automatically handled by Spring Boot.
<p></p>

* To run the tests, you'll need Docker installed as tests use Testcontainers.
<br>
* The database script can be found at classpath:resources/db/migration.

<p></p><br>

#### Application Logic

The employee management application is designed to handle multiple branches and departments within an institution. It has been thoroughly tested for the implemented features.

Junior/senior assistants report to the team lead.
The team lead reports to the department head.
The department head reports to the branch manager.
The branch manager reports to the COO.
Upon application initialization, a default user with complete access is created if the profile points to any of {dev, local, sim, test}. This user is persisted with the ID 1 (strategy = GenerationType.identity) and can be used to create a CHIEF_OPERATING_OFFICER.

A branch for the institution can be created, followed by departments within the branch. However, a department cannot be activated until a BRANCH_DEPARTMENT_HEAD is assigned to it.

A BRANCH_MANAGER can be created for the branch, with all BRANCH_MANAGERs reporting to the CHIEF_OPERATING_OFFICER. Each branch will have its own BRANCH_MANAGER with limited permissions to their allocated branch.

A BRANCH_DEPARTMENT_HEAD can then be created for the department, reporting to their BRANCH_MANAGER. Each department will have a BRANCH_DEPARTMENT_HEAD with permissions limited to their own department.

Similarly, TEAM_LEADS report to BRANCH_DEPARTMENT_HEADs, and JUNIOR/SENIOR_ASSISTANTS report to TEAM_LEADs.

When a Branch Manager changes, all department heads will report to the new manager, and the old manager will have the role UNDEFINED. When a department head changes, all team leads will report to the new department head, and the old department head will have the role UNDEFINED.

Additionally, the project has comments added to each method, and the test cases are well explained.

NB: Throughout the project, comments are added to each and every method, and the test cases are also well-documented.

<p></p><br>

1. ##### Add a CHIEF_OPERATING_OFFICER (COO):
    * Use 1 as the principal ID.
2. ##### Create a Branch:
    * This involves creating a new branch within the organizational structure.
3.  ##### Add a BRANCH_MANAGER to that branch:
    * Assign a manager to oversee the operations of the newly created branch.
4. ##### Create a department without a BRANCH_DEPARTMENT_HEAD:
    * When creating a department, set the "activate" flag as false in the request body to indicate that it's inactive until a department head is assigned.
5. ##### Create a BRANCH_DEPARTMENT_HEAD for the newly created department:
    * Once the department is created, assign a BRANCH_DEPARTMENT_HEAD to it. This will activate the department for further use.
6. ##### The department will be activated as now a head is assigned to it:
    * Confirm that the department is now active since a department head has been assigned.
7. ##### Create TEAM_LEAD && JUNIOR/SENIOR_ASSISTANT
    * Proceed to create team leads and junior/senior assistants within the department structure.
      If you're facing issues with any specific step or need further guidance on implementation details, feel free to get back to me through mail!

      
<p></p><br>

#### Improvements that can be Made


       * Methods could have been made easier to debug if less emphasis was placed on DRY principles and they were split up. (Realized this too late in the project.)
       * Exception Handling, Instead of over using database constraints all checks should be handled from server side for better error messages.
       * How the application functions logically can be improved as well.
       * Implementing in-memory caching.
       * Integration of Swagger for better API documentation and testing.

