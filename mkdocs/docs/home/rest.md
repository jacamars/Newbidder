#REST API
##Endpoint

All API messages are JSON payloads to a an HTTP POST, to port 8080 on a single node system, at the URI /api

##Authorization
###Get Token

Obtain a JSON web token from the system with the 'Get Token' command. A JSON POST message with the form:

```
{
	type: 3,
	customer: customer_name,
	username: user_login_name,
	password: password_value
}
```

Will return the following structure on success:

```
{
	token: 'the-value-will-be-in-here'
}

```

On error it will have an error flag, and a message, like this:

```
{
	error: true,
	message: 'no such login'
}
```

Be aware that tokens expire, and when they do you will receive the following response:

```
{
	error: true,
	message: 'No such token'
}
```

You should call get another token to replace the old one.

###Drop Token
When you are logging out of the system, issue a Drop Token command. This will remove the token from the cache and
will no longer be usable.

```
{
	type: 40,
	token: "<your-toke-value>"
}
```

If there is no error, the return will look like:

```
{
    error: false
}
```

If error is true, then the token could not be dropped. The attribute 'message' will provide the reason why.
##Housekeeping
###Get Shared Resources
This returns a summary list of information about the IMDG

```
{
	token: jwt,
    type: 16
};
```

###Get User
(Not available with Okta authentication)
This returns a JSON field with information about a user of the system:
```
{
	token: jwt,
    username: username,
    type: 4
}
```

###Add User
(Not available with Okta authentication)
Adds a user to the system. You need to provide a stringified version of the user object. Here's an example:

First, a sample user object:
```
TBD
```

Then transmit the following:

```
{
	token: jwt,
    user: JSON.stringify(user),
    type: 5
}
```

###Delete User
(Not available with Okta authentication)
This command will remove the user from the system:

```
{
	token: jwt,
    id: id,
    type: 6
}
```

###Add Company
(Not available with Okta authentication)
To add a company to the system (or edit), first create the company object like so:

```
TBD
```

Then send the add company command like this:

```
{
	token: jwt,
    id: id,
    type: 9
}
```

###Delete Company
(Not available with Okta authentication)
To delete a company from the system use:

```
{
    token: jwt,
    id: id,
    type: 9
}
```

###Retrieve User Objects by Company Id
(Not available with Okta authentication)
Retrieves all users by their customer id moniker.

```
{
    token: jwt,
    customer_id: cid,
    type: 17
}
```
Returns a list of user objects.

###List All Companies
(Not available with Okta authentication)
This command retrieves all the company objects.

```
{
    token: jwt,
    type: 18
}
```

The return is a list of company objects.

###List Active Tokens
This command retrieves all the active tokens:

```
{
	token: jwt,
	type: 19
}
```
The return is a list of tokens.

###Clear Trait from Profiles
You can clear one or more traits from profiles using the CLEAR API call. This call can remove a trait from a single Profile, or remove a trait from all
profiles.

Form of command for clearing a trait from a known profile "97980cfea0067":

```
{
    token: jwt,
    target: "profile",
    item: "97980cfea0067",
    id: "total_revenue, track_revenue", 
    type: 39
}
```
The target is set to 'profile'. The 'item' attribute is set to the id of the profile to modify.
The id is set to one or more traits to clear from the profile. If id is null, all traits will be removed from
the profile.

The return 'results' will be the number of traits remaining. 
To delete a single trait from ALL profiles with that trait, use the following form:

```
{
    token: jwt,
    target: "profile",
    id: "total_revenue", 
    type: 39
}
```
Not specifying the id of the profile implies all profiles with this trait will be removed. The return 'results' will be the number of profiles affected. If an error occurs 'error' will be set to true and 'message' will contain the appropriate error message.

###Clear Profile from Segment
You can clear one or more profiles from a segment using the CLEAR API call. This call can remove a profile from a single segment, or remove a profile from all segments.

Form of command for clearing a profile from segment_123 where profile is "97980cfea0067":

```
{
    token: jwt,
    target: "segment",
    item: "segment_123",
    id: "97980cfea0067, 67980cfea0092", 
    type: 39
}
```
The target is set to 'segment'. The 'item' attribute is set to the name of the segment to modify.
The id is set to one or more userId's to clear from the segment. If id is null, all userIds will be removed from
the segment.

The return 'results' will be 1 for successful deletion. If the segment did not exist, 0 will be returned. If an error occurs, 'error' will be set to true, and 'message' will contain the explanatory message.

To delete the profile from ALL segments use the following form:

```
{
    token: jwt,
    target: "segment",
    id: "97980cfea0067",
    type: 39
}
```
Not specifying the id of the segment implies all segments with this profile will be removed. The return 'results' will be the number of segments affected. If an error occurs 'error' will be set to true and 'message' will contain the appropriate error message.

###Clear Audience from Persona
You can clear the Audience of user id's from a Persona using the CLEAR API call. 
This call can remove all t from a Persona. Or, remove all the ids from a persona. In addition, you
can remove ids from a Persona->Audience->Segment recursively.

Form of command for clearing a profile from a persona where persona is "my_persona":

```
{
    token: jwt,
    target: "persona",
    item: "my_persona",
    id: "97980cfea0067", 
    recursive: true,
    type: 39
}
```
The target is set to 'persona'. The 'item' attribute is set to the name of the persona to modify.
The id is set to one or more userId's to clear from the persona. If id is null, all userIds will be removed from
the persona. The optional recursive attribute, if true, will remove the ids from the persona->audience->segment.

The return 'results' will be 1 for successful deletion. If the segment did not exist, 0 will be returned. If an error occurs, 'error' will be set to true, and 'message' will contain the explanatory message.

To delete a single profile from ALL personas that contain that id use the following form:

```
{
    token: jwt,
    target: "persona",
    id: "97980cfea0067"
    type: 39
}
```
To delete the id recursively, set recursive to true.
###Get Trait Memo
This command retrieves a map of the traits, and the number of profiles that have that trait. Example:

```
{
    token: jwt,
    type: 40
}
```

The trait object looks like this:

```
{
  "ulist_count": 7,
  "logins": 1,
  "name": 32,
  "last": 1,
  "tracker": 8,
  "ulist": 1,
  "track_revenue": 32,
  "plan": 1,
  "email": 32,
  "address": 1
}
```

In the above example, 32 profiles have a 'name' tait, 1 has a 'ulist' trait, 8 profiles have 'tracker'.

##Traits
###Add Computed Trait
This adds a computed trait to the system. If you edit a trait, simply call  add trait to replace the old one.

The different types of traits can be:

- Event Counter - An Event Counter trait stores a count of an event over a period of time. 
For example, you can create a trait called number_logins_90_days based on a User Logged In event. 
You can also use event properties to only specific types of events.
                  
    User-level examples:    
        Orders Completed Last 30 Days
        Pricing Page Views Last 30 Days
                  
     Account-level examples:                
        Total Logins by Account 30 Days
        Emails Opened by Account 90 Days
                  
- Aggregation - An aggregation computes a sum, average, minimum, or maximum of a numeric event 
property. A good example is a sum_cosmetics_revenue_90_days if you’re sending an Order Completed 
event with a revenue property. In the example we’re refining the revenue even further based on 
another event property: category = 'cosmetics'. Note that you can only compute an aggregation 
trait for event properties that have a numeric value. Aggregation types supported:
    * sum
    * highest
    * lowest
    * mean
    * variance
    * geometric mean
    * second moment
    * population variance
    * quadratic mean
    * standard deviation
    * square of the log
                
- Most Frequent - A most frequent user-level computed trait will return the most common value for 
an event property. This is helpful to create traits like preferred_product_viewed or 
most_commonly_viewed_category that tell you what a user’s preferred product, or 
content category might be. Note that the most frequent computed trait requires 
the event property to have been tracked at least twice. In the case of a tie, 
we return the first alphabetical value. For account-level computed traits, you 
can also return the most frequent user trait. This is helpful when you want to 
determine which user has performed an event the most frequently. For example, 
ou might to return the email of the user in an account most actively viewing your app.
                  
      User-level examples:    
        Favorite Blog Post
        Top Purchase Category
                  
- First - The first user-level trait returns the first event property value we have seen. 
This is common for creating traits like first_page_visited based on the page name. For 
accounts, the first computed trait could also return a trait like first_user_signup, 
to calculate the first user to use your product.
          
        User-level examples:        
            First seen timestamp
            First utm parameter
          
        Account-level examples:
            First email opened
            First user signup

- Last- The last trait returns the last event property value we have seen. This is common for 
creating traits like last_utm_campaign to help you calculate last-touch attribution for 
paid advertising.
        
        User-level examples:    
            Last seen at
            Last utm parameter

- Unique List - Unique list computed traits will output a list of unique values for an event property. 
This is helpful to understand the different types of products or content that a customer or users in 
an account have interacted with or purchased. Customers are creating traits 
like unique_product_categories_viewed and sending them to email marketing tools and accessing 
them through the Profiles API for in-app personalization.
                
        Example use cases:    
            Unique products purchased
            Unique categories
            Unique games played

- Unique List Count - Unique list count computed traits will output a count of the unique list of 
values for an event property. Customers are creating traits like unique_product_categories_viewed_count 
to understand the variety of products that a customer is viewing. At the account-level, customers are creating traits like unique_visitors_count to calculate the number of unique visitors by ip address.
                      
        User-level examples:      
            Unique products viewed count
            Unique categories count
                      



The command to add the trait is:

```
{
    token: jwt,
    trait: {
        name: "name-of-trait"
        internal: "sql where clause here".
        ... (other attributes, depends on the trait)
    }
    type: 25
}
```

Note, use conditions OR internal, but not both. One or the other is required.

#### Event Counter
Counts the number of times an event has been seen.

API Specification:

```
{
    "token": "20111ZQtaMtzYYvrMmVv4L7omBbrOZc1b7Mr_-fdvjZ5l4CMcCcMRs8",
    "trait": {
        "name":"Count_logins",
        "eventName":"Logged in",
        "internal": "allowsMarketing = true";
        "eventCounter":{},
        "lookBack":7
     },
    "type":25
}
```
Attribute name will be the name of the trait. Attribute eventName is the event to count, internal defines the constraint you wish
to apply. The eventCounter flag is just a non-empty object, with no other attributes.

The response will contain an error field. If errror is true then an errror occurred, and 
the message attribute will contain the error. Response of error = false indicates the trait was
added or edited.

#### First
Records the value of the property.

API Specification:

```
{
    "token": "20111ZQtaMtzYYvrMmVv4L7omBbrOZc1b7Mr_-fdvjZ5l4CMcCcMRs8",
    "trait": {
        "name":"Count_logins",
        "eventName":"Logged in",
        "internal": "sql where clause",
        "first":{
            "eventProperty": "ipAddress"
         },
        "lookBack":7
     },
    "type":25
}
```
Attribute name will be the name of the trait. Attribute eventName is the event we are looking for, internal defines the constraint you wish
to apply. The first object defines either the eventProperty or userTrait you will be using as the value of the trait. 

The eventProperty or userTrait (only 1 is allowed) defines the message's attribute that is used as the value. The value of this field
can be a a JSON object, and may include an array object. It may also include a qualifier that will compute the array index to use.

Examples:

- "app.domain" : Defines a JSON object value under either a user trait or property.
- "Brand" : Defines a JSON object Brand under the properties or traits free form object
- "products.0.price"  : Defines products array under proprties or traits, 0th index, price.
- "products.{products.any.productId = '12908012'}.price":  Defines the array index where products.N.price
is equal to '123456', and retrieves price from that index.

The response will contain an error field. If errror is true then an error occurred, and 
the message attribute will contain the error. Response of error = false indicates the trait was
added or edited.

#### Last
Records the last value of the property.

API Specification:

```
{
    "token": "20111ZQtaMtzYYvrMmVv4L7omBbrOZc1b7Mr_-fdvjZ5l4CMcCcMRs8",
    "trait": {
        "name":"Count_logins",
        "eventName":"Logged in",
        "internal": "sql where clause",
        "last":{
            "eventProperty": "ipAddress"
         },
        "lookBack":7
     },
    "type":25
}
```
Attribute name will be the name of the trait. Attribute eventName is the event we are looking for, internal defines the constraint you wish
to apply. The first object defines either the eventProperty or userTrait you will be using as the value of the trait. 

The eventProperty or userTrait (only 1 is allowed) defines the message's attribute that is used as the value. The value of this field
can be a a JSON object, and may include an array object. It may also include a qualifier that will compute the array index to use.

Examples:

- "app.domain" : Defines a JSON object value under either a user trait or property.
- "Brand" : Defines a JSON object Brand under the properties or traits free form object
- "products.0.price"  : Defines products array under proprties or traits, 0th index, price.
- "products.{products.any.productId = '12908012'}.price":  Defines the array index where products.N.price
is equal to '123456', and retrieves price from that index.

The response will contain an error field. If errror is true then an error occurred, and 
the message attribute will contain the error. Response of error = false indicates the trait was
added or edited.

#### Unique List
Records the unique list of values of the property.

API Specification:

```
{
    "token": "20111ZQtaMtzYYvrMmVv4L7omBbrOZc1b7Mr_-fdvjZ5l4CMcCcMRs8",
    "trait": {
        "name":"Count_logins",
        "eventName":"Logged in",
        "internal": "sql predicate here",
        "uniqueList":{
            "eventProperty": "app.domain"
         },
        "lookBack":7
     },
    "type":25
}
```
Attribute name will be the name of the trait. Attribute eventName is the event we are looking for, internal defines the constraint you wish
to apply. The first object defines either the eventProperty or userTrait you will be using as the value of the trait. 

The eventProperty or userTrait (only 1 is allowed) defines the message's attribute that is used as the value. The value of this field
can be a a JSON object, and may include an array object. It may also include a qualifier that will compute the array index to use.

Examples:

- "app.domain" : Defines a JSON object value under either a user trait or property.
- "Brand" : Defines a JSON object Brand under the properties or traits free form object
- "products.0.price"  : Defines products array under proprties or traits, 0th index, price.
- "products.{products.any.productId = '12908012'}.price":  Defines the array index where products.N.price
is equal to '123456', and retrieves price from that index.

The response will contain an error field. If errror is true then an errror occurred, and 
the message attribute will contain the error. Response of error = false indicates the trait was
added or edited.

#### Unique List Count
Records the count of the  unique list of values of the property.

API Specification:

```
{
    "token": "20111ZQtaMtzYYvrMmVv4L7omBbrOZc1b7Mr_-fdvjZ5l4CMcCcMRs8",
    "trait": {
        "name":"Count_logins",
        "eventName":"Logged in",
        "internal": "sql predicate here",
        "uniqueListCount":{
            "eventProperty": "app.domain"
         },
        "lookBack":7
     },
    "type":25
}
```
Attribute name will be the name of the trait. Attribute eventName is the event we are looking for, internal defines the constraint you wish
to apply. The first object defines either the eventProperty or userTrait you will be using as the value of the trait. 

The eventProperty or userTrait (only 1 is allowed) defines the message's attribute that is used as the value. The value of this field
can be a a JSON object, and may include an array object. It may also include a qualifier that will compute the array index to use.

Examples:

- "app.domain" : Defines a JSON object value under either a user trait or property.
- "Brand" : Defines a JSON object Brand under the properties or traits free form object
- "products.0.price"  : Defines products array under proprties or traits, 0th index, price.
- "products.{products.any.productId = '12908012'}.price":  Defines the array index where products.N.price
is equal to '123456', and retrieves price from that index.

The response will contain an error field. If errror is true then an error occurred, and 
the message attribute will contain the error. Response of error = false indicates the trait was
added or edited.

#### Most Frequent
Records the most frequently seen value of the property.

API Specification:

```
{
    "token": "20111ZQtaMtzYYvrMmVv4L7omBbrOZc1b7Mr_-fdvjZ5l4CMcCcMRs8",
    "trait": {
        "name":"Count_logins",
        "eventName":"Logged in",
        "internal": "sql predicate here",
        "mostFrequent":{
            "eventProperty": "app.domain"
         },
        "lookBack":7
     },
    "type":25
}
```
Attribute name will be the name of the trait. Attribute eventName is the event we are looking for, internal defines the constraint you wish
to apply. The first object defines either the eventProperty or userTrait you will be using as the value of the trait. 

The eventProperty or userTrait (only 1 is allowed) defines the message's attribute that is used as the value. The value of this field
can be a a JSON object, and may include an array object. It may also include a qualifier that will compute the array index to use.

Examples:

- "app.domain" : Defines a JSON object value under either a user trait or property.
- "Brand" : Defines a JSON object Brand under the properties or traits free form object
- "products.0.price"  : Defines products array under proprties or traits, 0th index, price.
- "products.{products.any.productId = '12908012'}.price":  Defines the array index where products.N.price
is equal to '123456', and retrieves price from that index.

The response will contain an error field. If errror is true then an error occurred, and 
the message attribute will contain the error. Response of error = false indicates the trait was
added or edited.

### Aggregate
Applies an aggregation function to a value in traits or properties of a message. This is the
only computed trait that can access the message traits values.

API Specification:

```
{
    "token": "20111ZQtaMtzYYvrMmVv4L7omBbrOZc1b7Mr_-fdvjZ5l4CMcCcMRs8",
    "trait": {
        "name":"Sum_revenue",
        "eventName":"Logged in",
        "internal": "sql predicate here",
        "aggregation":{
            "eventProperty": "revenue",                   (properties[revenue])
                *** OR ***
            "userTrait": "revenue",                       (traits[revenue])
            "type": "<function-to-apply>"
         },
        "lookBack":7
     },
    "type":25
}
```
Attribute name will be the name of the trait. Attribute eventName is the event we are looking for, internal defines the constraint you wish
to apply. The first object defines either the eventProperty or userTrait you will be using as the value of the trait. 

The eventProperty or userTrait (only 1 is allowed) defines the message's attribute that is used as the value. The value of this field
can be a a JSON object, and may include an array object. It may also include a qualifier that will compute the array index to use.

Examples:

- "app.domain" : Defines a JSON object value under either a user trait or property.
- "Brand" : Defines a JSON object Brand under the properties or traits free form object
- "products.0.price"  : Defines products array under proprties or traits, 0th index, price.
- "products.{products.any.productId = '12908012'}.price":  Defines the array index where products.N.price
is equal to '123456', and retrieves price from that index.

Fucntions:
- sum
- highest
- lowest
- mean
- var
- geomean
- smoment
- pvar
- qmean
- std
- slogs

The response will contain an error field. If errror is true then an errror occurred, and 
the message attribute will contain the error. Response of error = false indicates the trait was
added or edited.

#### SQL
Records the return value of an SQL query using values of the message in a 
select statement

API Specification:

```
{
    "token": "20111ZQtaMtzYYvrMmVv4L7omBbrOZc1b7Mr_-fdvjZ5l4CMcCcMRs8",
    "trait": {
        "name":"Real_owner_of_domain",
        "eventName":"Logged in",
        "internal": "sql predicate to apply",
        "sqlTrait":{
            "driver": "org.postgresql.Driver",
            "jdbc": "postgresql://sqldb/postgres?user=aa?password=bb",
            "relation": "testable",
            "projection": "realowner",
            "predicate": "key = {userId}"
         },
        "lookBack":7
     },
    "type":25
}
```

Equivalent to *select realowner from testable where key = {userId}*

Components:
- driver is the SQL driver to use
- jdbc is the login string
- relation is the table name
- projection is the column you are looking for.
- predicate is the where clause. Can reference message values
  Using the macro form. Example, for the message userId:
 	{userId}

Value of realowner will be the value of the computed attribute

The response will contain an error field. If errror is true then an error occurred, and 
the message attribute will contain the error. Response of error = false indicates the trait was
added or edited.

###Delete Trait
To delete a trait from the system:

```
{
    token: jwt,
    name: "<name-of-trait>",
    type: 26
}
```

###Execute Trait
This executes a list of trait using the specified userId:

```
{
    token: jwt,
	userId: userId,
	traits: traits,
	update: update,
	type: 27
}
```

The userId defines the profile to execute against. The traits is a list of traits to execute at once. The update
field set to true specifies that the profile is to be updated after the execution of the traits.

###Get Profile
This command retrieves a profile object by the userId field. 

```
{
   token: jwt,
   userId: userId,
   type: 28
}
```

The return is the profile object that matches that userId.

###Replay
The replay command is used to playback messages through one or more segments or an audience. The results will
be an array of id's returned by each segment executed. Example format:

```
{
   token: jwt,
   sources: ["source-1","source-2",...],
   segments: ["segment1","segment2",...],
   detach: true|false,
   audience: "name-of-audience",
   type: 32
}
```
The sources is an array the names of the sources you want to include in the search. If you don't provide any
sources then ALL sources will be included. Use the name, not the writeKey.

The segments when provided are executed in the order you provide. If you provide no segments you must provide
an audience.

The audience, if provided will replay the messages through all the segments within that audience. The result
will be an array of all the ids that are in that segment. If you provide an audience, don't provide segments.

Setting detach will cause the result returned to be a an id to the scratchpad. This id can be used for a query
to the scratchpad to return the current status of the running replay. Example:

```
{
    token: jwt,
    item: "SCRATCHPADCACHE",
    sql: "id=<id-returned in the detached replay call",
    type: 21
}
```

The return will be an array of message objects, of size 1:

```
{
    traits:
        progress: <double-number shows completion>
        completed: true | false
        records: <number-of-total records process on completed>
    ...
}
```

If completed is true and progress is less than 100.0 then the call errored. If the call did not error, you
can retrieve the results from the segment, or the audience, when it is completed.

##Real Time Events

Real-time events are available from a web socket interface, normally on port 8887 on a single node system. Example:

```
	'ws://localhost:8887?token=<token-value-from-get-token'
```

You can also place the token in the header parameter 'token' too. But you must pass the token, otherwise you will not connect.

Immedately upon connecting you will receive a heartbeat message. This will let you know you are connected. Then, once a minute you will receive
the heartbeat. Example:

###Heartbeat

```
{
  "stats": {
    "running": 0,
    "tokencache": 1,
    "watch": 2,
    "identity": 0,
    "members": 0,
    "jobs": 0,
    "deadletter": 0,
    "profiles": 0,
    "completed": 0,
    "messagecache": 0
  },
  "time": "2020-05-20T15:46:39.119Z",
  "event": "heartbeat"
}
```

###Real Time Trait
The real time trait will send a message on the socket anytime trait has computed a new value and is associated with a profile.
You will receive the following key fields:

- type: This will be equal to 'update_trait'.
- traitname: This will be the name of the trait that updated.
- newvalue: This is the new value for the trait on the indicated message.userId. 
- message: The message that caused the trait to excute.

Example:
```
{
  "newvalue": 17,
  "traitname": "count_test_events"
  "type": "update_trait"
  "message": {
    "id": "c556d2c3-a37e-4cd7-9976-8b0c3cdf2ec4",
    "userId": "97980cfea0067",
    "action": "track",
    "type": "track",
    "event": "Test Event",
    "sentAt": "2020-05-06T12:44:04.029Z",
    "channel": "web",
    "context": {
      "locale": "en-GB",
      "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36",
      "ip": "1.2.3.4",
      "email": "user@cdp.com",
      "library": {
        "name": "RudderLabs JavaScript SDK",
        "version": "1.1.1"
      },
      "app": {
        "name": "RudderLabs JavaScript SDK",
        "version": "1.1.1",
        "namespace": "com.rudderlabs.javascript"
      },
      "screen": {
        "density": 1
      },
      "traits": {
        "email": "user@cdp.com"
      },
      "os": {
        "name": "",
        "version": ""
      }
    },
    "messageId": "edb92d20-70ca-4802-b7c9-4769c43a111a",
    "timestamp": "2020-05-20T15:54:27.638288Z",
    "properties": {
      "revenue": 30,
      "currency": "USD",
      "user_actual_id": 1234567890
    },
    "integrations": {
      "All": true
    },
    "originalTimestamp": "2020-05-06T12:44:04.028Z",
    "anonymousId": "e705b2b9-9d58-4bb1-b6f7-3b9ca2bd98b2",
    "writeKey": "1Zg4SLMGY8rGkj7x1anRIWpMMOa"
  }
}
```

###Real Time Segment
he real time segment will send a message on the socket anytime profile has been added to a segment.
You will receive the following key fields:

- type: This will be equal to 'update_segment.
- segment: This will be the name of the segment that updated.
- newvalue: This is the new profile userId for the segment. 

Example:
```
{
  "newvalue": "93dceei134",
  "segment": "mynewsegment"
  "type": "update_segment"

}
```

###Real Time Audience

###Real Time Persona

##Query

You can query the different caches using these API access points.

- TOKENCACHE - the login tokens.
- MESSAGECACHE - the raw message cache.
- PROFILES - user profiles.
- TRAITS - computed traits defined in the system
- SEGMENTS - segments define
- SCRATCHPAD 

###Query Keys
This query retrieves the key fields from object in the IMDG.

```
 {
	token: jwt,
	item: cache,
	sql: pred,
	type: 20
}
```
The cache field is the name of the IMap to query from. Examples: "PROFILES", "MESSAGECACHE", "TRAITS". The
pred field are SQL-like query terms on the stored object format. Example using MESSAGECACHE: "userId != null AND context.device.name = android".

The return is a list of ids that match the query. To rereive all ids, pass "" in the sql field.

An example return: 

```

```
###Query By Id
This will retrieve an object from the IMDG cache based on its key field.

```
{
   token: jwt,
   item: cache,
   key: skey,
   projections: arrayofnames,
   type: 22
 }
```
The cache field is the name of the IMap to query from. Examples: "PROFILES", "MESSAGECACHE", "TRAITS". The
skey is the id of the object. 

The optional projections is an array of field names to return, instead of the entire object. The field names must exist. 
If one or more field names do not exist an error will be returned. The projections works for the following caches: 
TRAITSCACHE, MESSAGECACHE, SEGMENTS, PROFILES, AUDIENCE and PERSONA; all other values are unsupported.

The return is the object with that id in the cache.

###Query
This query retrieves the objects in the IMDG cache that match the query predicate.

```
{
	token: jwt,
    item: cache,
    sql: pred,
    projections: names,
    type: 21

}
```
The cache field is the name of the IMap to query from. Examples: "PROFILES", "MESSAGECACHE", "TRAITS". 

The pred field are SQL-like query terms on the stored object format. Example using MESSAGECACHE: "userId != null AND context.device.name = android".

The projections field, an optional field, is an array of field names you wish to retrieve. The field names must exist, and the pred field cannot be null.

The return is a list of messages. To retrieve all the objects, pass "" in the sql field.

Here is an example command and return from the query of the TRAITS cache:


```
{
	token: jwt,
    "item": "TRAITS",
    "pred": "".
    "type": 21

}
```

Here is a sample return:

```
{
  "type": 21,
  "message": "",
  "progress": 100,
  "results": [
    {
      "id": "3de1ce22-b3f2-43e8-be95-5130bc36a38c",
      "name": "tracker",
      "realTime": true,
      "eventName": "Test Event",
      "first": {
        "eventProperty": "tracker"
      },
      "conditions": [],
      "lookBack": 7,
      "timestamp": "2020-06-15T21:47:40.308784Z",
      "numUsers": 0
    },
    {
      "id": "1135861a-2142-4ba5-9869-c667e5702da5",
      "name": "track_revenue",
      "realTime": true,
      "eventName": "Test Event",
      "aggregation": {
        "type": "sum",
        "eventProperty": "revenue"
      },
      "lookBack": 7,
      "timestamp": "2020-06-15T21:47:40.331528Z",
      "numUsers": 0
    }
  ],
  "token": "20111ATIKX-Q6uQSf3WTU-wXo7ulQd5WS85k5usE3HUdsfp74e30OJi",
  "item": "TRAITS",
  "sql": ""
}
```

Now, here is an example using projections on the same query to retrieve "name" and "realTime":

Command;

```
{
	token: jwt,
    "item": "TRAITS",
    "pred": "name!=null",
    "projections": ["name","realTime"],
    "type": 21

}
```

The example return:

```
{
  "type": 21,
  "message": "",
  "progress": 100,
  "results": [
    {
      "name": "tracker",
      "realTime": true
    },
    {
      "name": "track_revenue",
      "realTime": true
    }
  ],
  "token": "20111ATIKX-Q6uQSf3WTU-wXo7ulQd5WS85k5usE3HUdsfp74e30OJi",
  "item": "TRAITS",
  "sql": "name!=null"
}
```

Notice, in this version of the command sql is not "". You must put a legal predicate in the sql field that will return what you
are looking for.


###Count User Ids
This will retrieve the number of userids in a SEGMENTS, AUDIENCE and PERSONA cache.


```
{
   token: jwt,
   item: cache,
   key: skey,
   type: 42
 }
```
The item field is the name of the IMap to query from, it can be SEGMENTS, AUDIENCE, or PERSONA:. The key is the name of the 
segment, audience or persona you are querying.

Return values - If the item does not support the query (not a SEGMENTS, AUDIENCE or PERSONA), the error field wlll be true. If the key does not
exist in the cache, then error will be true. If error is false, the call succeeded, and the size attribute will contain the number of user ids currently stored in the object.


##Persona
###Add/Edit Persona

```
{
    token: jwt,
    audience: "name_of_audience",
    name: "name_of_persona",
    destination: "<destination-string>
    traits: [traitname1, traitname2, ..., traitnameN],
    format: "csv-noheader" | "csv" | "json" ,
    realTime: setToTrue-for-realtime,
    type: 36
}
```
The name is the name of the Persona.

The audience is the name of the audience to output.

The traits array is a list of trait names to be added into the destination.

The format determines how the traits would be added to the output. "csv-noheader" means comma
separated with the elements:

Set realTime to make the persona stream to the destinations.

The destination is a string defining the output, example:


```
userId,traitname1,traitname2,...,traitnameN
actual-user-id,trait1,trait2,...traitN
```
In  the case of format is "csv", the header is removed.

In the case of "json":

```
{
    "userId": "actual-user-id",
    "traitname1": "traitvalue1",
    "traitname2": "traitvalue2",
    "traitnameN": "traitvalueN"
}
```

###Delete Persona

Delete a persona by name:

```
{
    token: jwt,
    name: "name-of-persona",
    type: 37
}
```
###Execute Persona

```
{
    token: jwt,
    name: "name-of-persona",
    update: setToTrueToUpdate
    type: 38
}
```

##Audience

###Add/Edit Audience

```
{
    token: jwt,
    name: "name_of_audience",
    internal: "<set-directives">,
    realTime: setToTrue-for-realtime,
    type: 33
}
```
The internal attribute will be used to figure out which segments are being used. The form of the command 
is in set operations of membership. Grammar supports segmentnames, AND, OR and NOT. Examples:
```
segmentA AND segmentB AND NOT segmentC
```

This means when a userId is added to segmentA, all Audiences containing this segment will be triggered by 
that entry, and then effectively this means: If segmentA contains userId AND segmentB contains userId AND segmentC does not contain userId then this userId is added to the Audience.

Parenthesis can be used to group the operations.

NOT can only appear by itself as the first keyword, as in:

```
NOT (segmentA OR segmentB)
```

The following is not a legal expression:
```
segmentA AND segmentB NOT segmentC       *** NOT VALID
```

These are valid:
```
segmentA AND segmentB AND NOT segmentC
segmentA AND segmentB OR NOT segmentC
```
Note, if you reference a segment that does not exist, then regardless of the userId, that term will
resolve to false.

If you provide no value for the internal attribute, no realtime capability is possible.

###Delete Audience

```
{
    token: jwt,
    name: "name-of-audience",
    type: 34
}
```

###Execute Audience

```
{
    token: jwt,
    name: "name-of-audience",
    update: setToTrueToUpdate
    type: 31
}
```

##Segment

###Add/Edit Segment

```
{
    token: jwt,
    name: "name_of_segment",
    conditions: [condition1,condition2, ...],
    internal: "sql where clause here".
    segment: "name-of-segment",
    type: 29
}
```
Note, use conditions OR internal, but not both. One or the other is required.

Using sql attribute allows you to directly provide the operator precedence grammar of queries where clause of the segment.

The condition is a 4 part tuple that looks like the following:

```
{
    apply: <boolean application>,
    lefthandSide: "<left-hand-side of the equation>",
    operator: "<the operator>"
    righthandSide: "<value to test lefthandSide against"
}

Apply can be one of the following:
- "", implied AND
- AND, and with the previous conditions
- OR, or with the previous conditions
- NOT, (AND) not with the previous conditions.

The lefthandSide is usually the attribute name. For example in a track message:
"context.library.version".

The operator can be one of the following:

- =
- != 
- >
- >=
- <
- <=
- BETWEEN
- NOT BETWEEN
- CONTAINS
- NOT CONTAINS
- IN
- NOT IN
- EXISTS
- NOT EXISTS
- LIKE
- NOT LIKE
- STARTS WITH
- NOT STARTS WITH
- ENDS WITH
- NOT ENDS WITH
```

The righthandSide depends on the operator. In the case of EXISTS, or NOT EXISTS it is blank, as
it has no meaning in that context.

In the case of BETWEEN and NOT BETWEEN, it is 2 element list with AND joing it. Example "1 AND 2".

In the case of IN, it is a comma separated list in parenthesis. Examples: "(1,2,3)", "('aaa','bbb','ccc')".

###Delete Segment

```
{
    token: jwt,
    name: "name-of-segment",
    type: 30
}
```

###Execute Segment

```
{
    token: jwt,
    name: "name-of-segment",
    update: setToTrueToUpdate
    type: 31
}
```

###Add/Edit Audience

###Delete Audience
##Misc
###Put Object in Cache
The putobject command allows you to put an object into a cache (such as a profile, persona, audience, segment, trait or message). 
Most commonly used to edit a profile in place.

Example:

```
{
   token: jwt,
   cache: "name-of-cache",
   object: theObject,
   type: 43
}
 ```
 The cache is the name of the IMDG Queue, example "PROFILES". The object is the JSON object to put into the cache. 
 Depending on the cache, the  key is chosen from the object's attributes. For example, for PROFILES cache the
 attribute 'userId' is used as the key. 
 
 The following is an example of saving a profile:
 
```
profile = {
  "devices": [],
  "anonymousIds": [
    "e705b2b9-9d58-4bb1-b6f7-3b9ca2bd98b2",
    "507f191e810c19729de860ea"
  ],
  "messageIds": [
    "2af227ad-85d3-4355-847d-a45db1761ea5",
    "f05263ee-3926-4d69-9b08-f1729009a103",
    "b684873c-4424-4292-bc05-a4f8ffa113df",
    "16c46ef2-3e92-4915-9ef7-8f7eaca06418",
    "cb72b155-8921-4ced-b9c2-dedecea9cf09",
    "9c33321f-6943-46f6-adb5-dd2cc8102ab8",
    "5fd751c7-e3cc-4cd1-a594-0cc0e26e7ba7",
    "72592039-8819-4500-aba9-8139d4deec0d"
  ],
  "ips": [
    "1.2.3.4",
    "8.8.8.8"
  ],
  "apps": [
    {
      "name": "RudderLabs JavaScript SDK",
      "build": "1.0.0",
      "version": "1.1.1",
      "namespace": "com.rudderlabs.javascript"
    }
  ],
  "userId": "97980cfea0067",
  "traits": {
    "revenue": 30,
    "address": {
      "street": "6th St",
      "city": "San Francisco",
      "state": "CA",
      "postalCode": "94103",
      "country": "USA"
    },
    "user_actual_id": 1234567890,
    "name": "Peter Gibbons",
    "track_revenue": 0,
    "currency": "USD",
    "plan": "premium",
    "logins": 5,
    "email": "peter@example.com"
  },
  "id": "84f3dbf6-4022-4287-aa0e-43eff0ca82c1",
  "timestamp": "2020-08-07T19:40:07.499471Z",
  "stringStats": {
    "track_revenue": "rO0ABXNyADtvcmcuYXBhY2hlLmNvbW1vbnMubWF0aDMuc3RhdC5kZXNjcmlwdGl2ZS5TdW1tYXJ5U3RhdGlzdGljc+Py0otcZHjhAgASSgABbkwAB2dlb01lYW50AEBMb3JnL2FwYWNoZS9jb21tb25zL21hdGgzL3N0YXQvZGVzY3JpcHRpdmUvbW9tZW50L0dlb21ldHJpY01lYW47TAALZ2VvTWVhbkltcGx0AEhMb3JnL2FwYWNoZS9jb21tb25zL21hdGgzL3N0YXQvZGVzY3JpcHRpdmUvU3RvcmVsZXNzVW5pdmFyaWF0ZVN0YXRpc3RpYztMAANtYXh0ADRMb3JnL2FwYWNoZS9jb21tb25zL21hdGgzL3N0YXQvZGVzY3JpcHRpdmUvcmFuay9NYXg7TAAHbWF4SW1wbHEAfgACTAAEbWVhbnQAN0xvcmcvYXBhY2hlL2NvbW1vbnMvbWF0aDMvc3RhdC9kZXNjcmlwdGl2ZS9tb21lbnQvTWVhbjtMAAhtZWFuSW1wbHEAfgACTAADbWludAA0TG9yZy9hcGFjaGUvY29tbW9ucy9tYXRoMy9zdGF0L2Rlc2NyaXB0aXZlL3JhbmsvTWluO0wAB21pbkltcGxxAH4AAkwADHNlY29uZE1vbWVudHQAP0xvcmcvYXBhY2hlL2NvbW1vbnMvbWF0aDMvc3RhdC9kZXNjcmlwdGl2ZS9tb21lbnQvU2Vjb25kTW9tZW50O0wAA3N1bXQAN0xvcmcvYXBhY2hlL2NvbW1vbnMvbWF0aDMvc3RhdC9kZXNjcmlwdGl2ZS9zdW1tYXJ5L1N1bTtMAAdzdW1JbXBscQB+AAJMAAZzdW1Mb2d0AD1Mb3JnL2FwYWNoZS9jb21tb25zL21hdGgzL3N0YXQvZGVzY3JpcHRpdmUvc3VtbWFyeS9TdW1PZkxvZ3M7TAAKc3VtTG9nSW1wbHEAfgACTAAFc3Vtc3F0AEBMb3JnL2FwYWNoZS9jb21tb25zL21hdGgzL3N0YXQvZGVzY3JpcHRpdmUvc3VtbWFyeS9TdW1PZlNxdWFyZXM7TAAJc3Vtc3FJbXBscQB+AAJMAAh2YXJpYW5jZXQAO0xvcmcvYXBhY2hlL2NvbW1vbnMvbWF0aDMvc3RhdC9kZXNjcmlwdGl2ZS9tb21lbnQvVmFyaWFuY2U7TAAMdmFyaWFuY2VJbXBscQB+AAJ4cAAAAAAAAABKc3IAPm9yZy5hcGFjaGUuY29tbW9ucy5tYXRoMy5zdGF0LmRlc2NyaXB0aXZlLm1vbWVudC5HZW9tZXRyaWNNZWFujn9L77lMmYMCAAFMAAlzdW1PZkxvZ3NxAH4AAnhwc3IAO29yZy5hcGFjaGUuY29tbW9ucy5tYXRoMy5zdGF0LmRlc2NyaXB0aXZlLnN1bW1hcnkuU3VtT2ZMb2dz+t047ubViTUCAAJJAAFuRAAFdmFsdWV4cAAAAEpAb3YJD/WwFnEAfgANc3IAMm9yZy5hcGFjaGUuY29tbW9ucy5tYXRoMy5zdGF0LmRlc2NyaXB0aXZlLnJhbmsuTWF4smBPCiPD8l8CAAJKAAFuRAAFdmFsdWV4cAAAAAAAAABKQD4AAAAAAABxAH4AEXNyADVvcmcuYXBhY2hlLmNvbW1vbnMubWF0aDMuc3RhdC5kZXNjcmlwdGl2ZS5tb21lbnQuTWVhbu4DhxRFeuu0AgACWgAJaW5jTW9tZW50TAAGbW9tZW50dAA+TG9yZy9hcGFjaGUvY29tbW9ucy9tYXRoMy9zdGF0L2Rlc2NyaXB0aXZlL21vbWVudC9GaXJzdE1vbWVudDt4cABzcgA9b3JnLmFwYWNoZS5jb21tb25zLm1hdGgzLnN0YXQuZGVzY3JpcHRpdmUubW9tZW50LlNlY29uZE1vbWVudDa2OsGxxcldAgABRAACbTJ4cgA8b3JnLmFwYWNoZS5jb21tb25zLm1hdGgzLnN0YXQuZGVzY3JpcHRpdmUubW9tZW50LkZpcnN0TW9tZW50VNTekKtB+mkCAAREAANkZXZEAAJtMUoAAW5EAARuRGV2eHAAAAAAAAAAAEA+AAAAAAAAAAAAAAAAAEoAAAAAAAAAAAAAAAAAAAAAcQB+ABRzcgAyb3JnLmFwYWNoZS5jb21tb25zLm1hdGgzLnN0YXQuZGVzY3JpcHRpdmUucmFuay5NaW7XK+5rxc1mhQIAAkoAAW5EAAV2YWx1ZXhwAAAAAAAAAEpAPgAAAAAAAHEAfgAZcQB+ABdzcgA1b3JnLmFwYWNoZS5jb21tb25zLm1hdGgzLnN0YXQuZGVzY3JpcHRpdmUuc3VtbWFyeS5TdW2NwqhziTGjRAIAAkoAAW5EAAV2YWx1ZXhwAAAAAAAAAEpAoVgAAAAAAHEAfgAbcQB+AA9xAH4AD3NyAD5vcmcuYXBhY2hlLmNvbW1vbnMubWF0aDMuc3RhdC5kZXNjcmlwdGl2ZS5zdW1tYXJ5LlN1bU9mU3F1YXJlcxRGd9pLErY4AgACSgABbkQABXZhbHVleHAAAAAAAAAASkDwQoAAAAAAcQB+AB1zcgA5b3JnLmFwYWNoZS5jb21tb25zLm1hdGgzLnN0YXQuZGVzY3JpcHRpdmUubW9tZW50LlZhcmlhbmNlgYvOL1sUZ8YCAANaAAlpbmNNb21lbnRaAA9pc0JpYXNDb3JyZWN0ZWRMAAZtb21lbnRxAH4ABnhwAAFxAH4AF3EAfgAf"
  },
  "_key": "97980cfea0067"
};

cmd = {
   token: jwt,
   cache: "PROFILES",
   object: profile,
   type: 43
};

var response = await axiosInstance.post("htto://localhost:8080/api",JSON.stringify(cmd), { responseType: 'text' }); 
...
```
 
 
###Iterate
The iterate command allows you to retrieve the objects in IMDG Queues, without removing the items.

```
{
   token: jwt,
   queue: queue,
   limit: limit,
   type: 23
 }
 ```
 The queue is the name of the IMDG Queue, example "JOBS". The limit is the number of items to retrieve. To retrieve
 all objects, don't set the limit. The return is the list of objects in FIFO order.
 
 ###Get Replay Messages
 This command is used to retrieve message for a given source, that match a specific predicate.
 The source field is the writeKey of the source. To narrow the messages retrieved, use the pred to match on the
 messages you are looking for, say, by timestamp range.
 
 ```
{
	token: jwt,
	pred: pred,
	source: source,
	type: 24
}
```

Here's an example to retrieve the replay messages for writeKey xxx that matched userIds 12345 23skiddoo, except
identify records:

```
{
	token: jwt,
	pred: "(userId = 12345 OR userId = 23skiddoo) AND type != identify",
	source: "xxx",
	type: 24
}
```

###Get Accounting
