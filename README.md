# UselessFacts

### Required API Endpoints

##### POST `/facts`
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