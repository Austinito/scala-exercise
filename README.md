# tgm-scala-exercise

This is a simple web server for a login prompts service. A login prompt consists of an image URL and caption that is displayed by a mobile client application (game) when the user logs in. A login prompt is similar to an ad, promoting game features and content.

The web server implements both a JSON-based API (for the mobile client), and a web-based admin interface (for human content managers). We'd like you to make a some enhancements to the existing code. (See Tasks below.)


## Prerequisites
* Java (JDK 8 or higher)
* [sbt](https://scala-sbt.org)


## Getting started
IMPORTANT: Please add `-Duser.timezone=UTC` to your sbt command line! Otherwise, timezone issues may arise between H2 and the server.

1. Unzip the archive:  `tar xzf tgm-scala-exercise.tar.gz`
2. Change to the project directory: `cd tgm-scala-exercise`
3. Enter `sbt -Duser.timezone=UTC run` to download dependencies and start the web server.
4. Open http://localhost:9000/ in your web browser to see the Admin UI.
5. If this is the server's first request, you'll see a reddish-orange warning stating `Database 'default' needs evolution!`. Click the button labeled `Apply this script now!`.
6. At this point, you should see a basic HTML admin page titled `Login Prompts`. You can test the client API by opening http://localhost:9000/api/loginPrompts.


## Tasks
Please complete these tasks in order.
1. The client application should be able to fetch a random login prompt when the user logs in. Please implement this in the `/api/loginPrompts/random` endpoint.
2. Users are complaining about seeing the same login prompts too often. Modify the server to allow content creators to define a quiet period for each login prompt. After the server returns a login prompt for a user, that login prompt should not appear again until the quiet period has elapsed. Please implement this in the `/api/loginPrompts/random/:userId` endpoint.

To get you started, we've initialized the database with some sample login prompts. Your code, however, should work with any collection of login prompts.


## Useful info
* The application configuration creates a persistent H2 database in `tgm-scala-exercise/database/h2.db`.
* H2 includes a browser-based console for running SQL queries. You can run this by launching `sbt`, then running the `h2-browser` task. When the console appears, set `JDBC URL` to `jdbc:h2:./db/h2.db;AUTO_SERVER=TRUE`.

## Built with:
1. [Play](https://www.playframework.com/) - Web framework ([docs](https://www.playframework.com/documentation/2.8.x/Home))
2. [Slick](https://scala-slick.org/) - Database access layer ([docs](https://scala-slick.org/doc/3.3.1/))
3. [H2](https://www.h2database.com) - Java SQL database

## Interview Notes:

IMPORTANT: Please add `-Duser.timezone=UTC` to your sbt command line! Otherwise, timezone issues may arise between H2 and the server.

Please read notes on the PR's on GitHub.

Further Improvements:

- If we desired to keep `quietPeriod` internal:
    - Create a new model that only shares desired fields.

- Decouple the table schema from the repositories.
    - Make Slick Tables public, and allow complex query creation from the service layer.
    - Create a single DB transaction to handle multiple operations.
    - Use Transactional operations to allow ROLLBACK on error.

- Add tests
    - Introduce a mock dependency.
