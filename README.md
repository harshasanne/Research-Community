# 18656-Fall-2018-Team3
## Team 3
- Harsha Ssanne
- Bowen Zhang
- Xinyuan Chen
- Hao Tang
- Tingfang Pan


# Importing and running the projects
Both projects should be imported into IntelliJ as SBT projects. Importing them as Gradle projects will prevent IntelliJ from getting the dependencies automatically and will create problems.

## Create the run environments
Create run environments through IntelliJ. Set the backend port to 9001 and the frontend port to 9000. 

# Database
For this project, you need a local Neo4j database to run on `bolt://localhost:7687`, with username: `neo4j` and password `12345`.

# Implemented Requirements as per Sprints

## Sprint 1
1 - Login: Provide a log in page, for users: Login with user name and password;
  
2 - Register a new user (with user id, password, security questions and backup email, also profile info - see below);

3 - Reset password (check security questions and send temporary password to preset backup email);

4 - Profile management:allow users to edit the following information. Users can edit and submit, or to reset to all empty.

5 - Given a time period of years for an author, generate a histogram showing her publications per year in the field. Use Tableau to generate the histogram and upload.

6 - Provide a log in facility, including the person's research interests.

## Sprint 2

7 - Given a journal name, generate a histogram showcasing the number of authors contributing to each of its volume, taking volume as the base axis.

8 - Given some keywords, search for researchers that you may like to follow,considering the user interests. Provide a ranked list of the researchers,considering user interests.

9 - Given some keywords, search for researchers who are experts in the field

10 - Given a publication channel (a journal or a conference) and a year, list the focused topics of the papers (based on topics and abstracts) and show in Tableau generated diagram. 

11 - Given a paper, show a knowledge card summarizing its publication information (metadata) and citation data.

## Sprint 3

12 - Given the name of a researcher, generate a graph* showing the direct collaboration network of the author (her all co-authors).

13 - Provide a personalized interface for a user when logging in.

14 - Allow people to follow a researcher - when the researcher has new publication, notify his followers.

15 - Allow to view statistics and detailed information of one's own followers.

16 - Allow to view statistics info of a person’s followers.

17 - Show the evolution of focused topics of a journal, in a given year frame.

18 - Given a research topic, form a possible research team, taking into consideration of their expertise in the field, their past collaboration relationships, and their possibility of willingness to join the team.

## Sprint 4

19 - Given some keywords, find out a network that can link them together.

20 - Categorize all research papers (given a time period).

21 - Categorize the research papers (given time period, publication channels, keywords).

22 - Given two researcherers' names, find out a path that will lead them to connect to each other (prove the small-world theory through co-authorship).

23 - Given the name of a researcher, generate a graph* showing a multi-depth collaboration network of the author (her co-authors and their co-authors).

24 - Given some keywords, generate a graph of top k related papers together with their authors.

25 - For each major publication channel (e.g., a journal or a conference), list the top 20 cited papers.

26 - For each major publication channel (e.g., a journal or a conference) and a given year or year period, list the top 10 most cited papers.

## Sprint 5
27 - When people do a search, give information about “People viewing this item also visit…”

28 - Given some geographical area (e.g., country) and some keywords, generate a graph on Google map the publications on the topic, whose  authors come from the geographical area 

29 - Given a publication channel name (journal or conference) and a time frame, showcase in Google map the publications distribution.

30 - Given some keywords, return a collection of papers that the reader may be interested in reading 

31 - Add microservices to each capability: One capability can have multiple mocroservices APIs; use a page to allow people to add some detailed information about  each of the added microservices

32 - Use one of the three scientific workflow tools to develop the advanced requirements and serialize them for further use.

33 - Allow users to mashup existing APIs for more comprehensive functions. Build a use case example.


