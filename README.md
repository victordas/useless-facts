# UselessFacts

---
## How to run

#### 1. Recommended: 
 - Simply run `docker compose up --build`
 - Both Angular Frontend and Spring Boot Kotlin App will be up and running

 #### 2. Alternatively:
 - Install NodeJS from [NodeJs](https://nodejs.org/) 
 - Install `pnpm` via `npm install -g pnpm`
 - Inside the project root directory run `pnpm install`
 - Serve Angular app with `pnpm nx serve useless-facts-ui`
 - Serve Kotlin Backend with `pnpm nx serve useless-api`

 #### 3. Application Endpoints:
 - Frontend URL: [http://localhost:4200](http://localhost:4200)
 - Backend URL: [http://localhost:4400](http://localhost:4400)
 - Swagger-Doc: [http://localhost:4400/swagger-ui.html](http://localhost:4400/swagger-ui.html)
 - Actuator: [http://localhost:5500/actuator](http://localhost:5500/actuator)
 - Health: [http://localhost:5500/actuator/health](http://localhost:5500/actuator/health)
 - Username and Password for Statistics is `admin` and `admin` and can be reconfigured in `docker-compose.yml` and `application.properties`


---
## Initial Requirement
---

### Required API Endpoints

##### GET `/facts/random`
_To fetch a random fact from the Useless Facts API, store it in memory and return the fact._

##### GET `/facts/{shortenedUrl}`

_To return a single cached fact by its shortened URL or key. Increment access count._

##### GET `/facts`

_Return all cached facts. Do not increment access counts._

##### GET `/admin/statistics`

_Return analytics data (e.g., access count) for all cached facts._


### Frontend Screens Required

##### Home

- a Button to fetch a new random fact.
- display the fetched fact’s content and fields

##### Cached Facts List

- To show a list of all cached facts.
- To make each item clickable to view more details.

##### Fact Detail View

- To view a specific fact in detail
- To make each view increase the fact's access count.

##### Analytics Screen

- Display analytics, showing each fact and its access count.

