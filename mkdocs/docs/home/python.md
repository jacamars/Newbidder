The Crosstalk-API allows you to programmatically interact with the RTB4FREE bidding system, and
the campaigns/creatives loaded within it using JSON based HTTP POST access.
The commands are simple JSON objects, and, the returned values are also JSON.
								
The paradigm is transactional - you issue a command, and then a response is returned to you.
All commands are done via POST. Each command requires a separate username and password token. 
With the API you can query the RTB bidders (individually or collectively) for their status, and 
what campaigns are loaded within each bidder.

The API also allows you to dynamically change prices within campaign/creatives, load new campaigns, or delete campaigns. The API can also be used to find out the current  spending limits on campaigns, and you can 
set new spending limits.

The default port for Crosstalk-API is 7379.

## GetToken

This is the login to the system. You must obtain a token before you can issue most other commands.

Example POST form:

```
{"type":"GetToken#","customer":"customer-id'","username":"the-user-id", "password":"the-password"}
```

Python example:

```
>>> crosstalk.GetToken('rtb4free','ben.faul@rtb4free.com','zulu');
(200, 'OK')
{"error":false,"timestamp":1603750599973,"token":"5b6ca2c8-8040-42c0-8049-6c185efcbe3c","type":"GetToken#","superUser":true}
```

The return token is remembered so that login information is not required on the subsequent calls.

## SetHost

If you need to change the host IP address of the connection to the selected bidder, issue this
command before any other command is issued.

Python form of the command is:

>>>crosstalk.SetHost("bybidderipaddress");
(200, 'http://bybidderipaddress:7379/api')

## SetHostPort

If you need to change the port (and host) use the SetHostPort command Issue it before any other commands. The python form of the
command is:

>>>crosstalk.SetHostPort("xxx:1234");
(200, 'http://xxx:1234/api')
>>> 


## ConfigureAwsObject

This command allows the rtb4free super user to add a new symbol from the S3 or Minio object
store. The POST form of the command is:

```
{"type":"ConfigureAws#","map":mapObject,"token":"<token-you-got-earlier>"}
```

The mapObject is a dictionary form of the new symbol attributes, an example using Pyton;

```
>>>crosstalk.ConfigureAwsObject({
    "s3" : "bloom/audience1/test-audience.txt",
    "name" : "@AUDIENCE1",
    "type" : "BLOOM",
    "size": 6
  });
```

The map  object uses the same format as the objects in the *payday.json* "list" object.

Note this command is only available to the super user 'rtb4free' user.
 
## RemoveSymbol

This command removes a symbol added with ConfigureAwsObject. Note this command is only available to the super user 'rtb4free' user. The POST form of the command is:

```
{"type":"RemoveSymbol","symbol":"symbol-name,"token":"<token-you-got-earlier>"}
```

 
## GetAccounting

This returns the summary budget accounting (Daily, Hourly, Total cost) and summary bids,clicks,pixels,wins since the RTB farm has been up. The POST form of the command is:

```
{"type":"GetAccounting#","token":"the-token-you-got-earlier"}
```

The Python form of the accounting, and example:

```
>>> crosstalk.GetAccounting();
(200, 'OK')
{
    "accounting": {
        "2.bids": 0.0, 
        "2.clicks": 0.0, 
        "2.pixels": 0.0, 
        "2.total": 0.0, 
        "2.wins": 0.0
    }, 
    "customer": "test", 
    "error": false, 
    "timestamp": 1603817841120, 
    "token": "a8276f4f-c5ac-44c7-a4fe-1be450ff79e9", 
    "type": "GetAccounting#"
}
>>>
```

If you logged in with customer_id of 'rtb4free' all campaigns will be reported otherwise just the campaigns of the logged in customer_id will be shown.

## ListSymbols

## SQLGetUser

This command retreives the SQL database information about the logged in user. The POST form
of the command is:

```
{"type":"SQLGetUser#","token":"the-token-you-got-earlier"}
```

The Python form of the command and example return:

```
>>> crosstalk.GetUser();
(200, 'OK')
{"error":false,"timestamp":1603751001729,"token":"1747de92-98d8-4271-b8f8-a374f019363a","type":"SQLGetUser#","user":"{\"id\":1,\"customer_id\":\"rtb4free\",\"sub_id\":\"superuser\",\"username\":\"ben.faul@rtb4free.com\",\"password\":\"zulu\",\"company\":\"Jacamars, Inc\",\"email\":\"ben.faul@rtb4free.com\",\"telephone\":\"310-467-0646\",\"firstname\":\"Ben\",\"lastname\":\"Faul\",\"address\":\"3820 Del Amo Blvd #226\",\"citystate\":\"Torrance, CA.\",\"country\":\"USA\",\"postalcode\":\"90503\",\"about\":\"Chief cook and dish-washer.\",\"picture\":\"https://i.kym-cdn.com/entries/icons/original/000/004/006/YUNO.jpg\",\"title\":\"Head Peon\",\"description\":\"Will sthis stuff ever get done???\"}"}
```

Notice the user is returned as JSON but is embedded as a string.


## SQLListUsers

This command, issued by super users, will list all the users in the system of that customer_id, both username and password. The super user for 'rtb4free' will list *all* users regardless of 
customer_id. The POST form of the command is:

```
{"type":"SQLListUsers#","token":"the-token-you-got-earlier"}
```

Python form and sample return:

```
>>> crosstalk.SQLListUsers();
(200, 'OK')
{
    "error": false, 
    "timestamp": 1603751535818, 
    "token": "2d73a71e-dfcb-4385-9625-b6b278de6f0c", 
    "type": "SQLListUsers#", 
    "users": [
        {
            "customer_id": "rtb4free", 
            "id": 1, 
            "password": "zulu", 
            "sub_id": "superuser", 
            "username": "ben.faul@rtb4free.com"
        }, 
        {
            "customer_id": "test", 
            "id": 2, 
            "password": "test", 
            "sub_id": "superuser", 
            "username": "test.test@test.com"
        }
    ]
}

```

If you logged in with customer_id of 'rtb4free' all users will be refreshed, otherwise just the users of the logged in customer_id will be shown.

## ListMacros

This command will list all the macros defined in the system. The POST form of the command is:

```
{"type":"ListMacros#","token":"the-token-you-got earlier"}'
```

The Python form and example return:

```
>>>crosstalk.ListMacros();
(200, 'OK')
{
    "error": false, 
    "macros": {
        "$EXTERNAL": "http://localhost:8080", 
        "{/rtb_click}": "</a>", 
        "{event_url}": "http://localhost:8080/track", 
        "{external}": "http://localhost:8080", 
        "{pixel_url}": "http://localhost:8080/pixel", 
        "{postback_url}": "http://localhost:8080/postback", 
        "{redirect_url}": "http://localhost:8080/redirect", 
        "{rtb_click}": "<a href='http://localhost:8080/redirect?url=_REDIRECT_URL_?EXCHANGE={exchange}&EXTERNAL=http://localhost:8080&AD_ID={ad_id}&CREATIVE_ID={creative_id}&BID_ID={bid_id}' target='_blank' rel='noopener'>", 
        "{rtb_pixel}": "<img src='{pixel_url}/exchange={exchange}/ad_id={ad_id}/creative_id={creative_id}/price=${AUCTION_PRICE}/bid_id={bid_id}/ip={ip}/site_domain={site_domain}/lat={lat}/lon={lon}' height=1 width=1 style='display:none;'/>", 
        "{vast_url}": "http://localhost:8080/vast", 
        "{win_url}": "http://localhost:8080/rtb/win"
    }, 
    "timestamp": 1603750688078, 
    "token": "29b26cb0-581c-4667-a30f-777d1d27f501", 
    "type": "ListMacros#"
}

```
  
## Ping

This command will ping the Crosstalk server, no token is required. It simply responds with the current time on successful return. If Crosstalk does not answer within 5 minutes then the
command will time out.

The form JSON form of the command is:

```
{"type":"Ping#"}
```
The Python form is:

```
>>>crosstalk.Ping();
(200, 1603750153185)
```

The HTML response code is returned, followed by the UTC in Epoch form.
 
## Refresh

The Refresh command, issued by the superuper will cause all campaigns to be reloaded for that customer_id to be reloaded from the SQL database, and will cause the budgeting limits to be recalculated.

Form of the command:

```
{"type":"Refresh#","token":"the-token-you-got-earlier"}
```

Python form of the command and example return:

```
crosstalk.Refresh();
(200, 1603751792603, False, [u'New campaign going active: 1'])
>>> 

```

If you logged in with customer_id of 'rtb4free' all campaigns will be refreshed, otherwise just the campaigns of the logged in customer_id will be refreshed.

## SQLListRules

To list all the rules, use SQLListRules, the POST form is:

```
{"type":"SQLListRules#","token":"the-token-you-got-earlier"}
```

Python form is:

```
>>> crosstalk.SQLListRules();
(200, 'OK')
{
    "customer": "test", 
    "error": false, 
    "rules": [
        {
            "hierarchy": "user.ext.eids", 
            "id": 3, 
            "name": "Tester's Rule"
        }
    ], 
    "timestamp": 1603816968835, 
    "token": "126cbbf5-96d2-4011-af99-20ca2ff4b469", 
    "type": "SQLListRules#"
}
>>>
```

If you logged in with customer_id of 'rtb4free' all campaigns will be refreshed, otherwise just the campaigns of the logged in customer_id will be refreshed.

## SQLAddNewRule

Adds a new rule to the system. the POST form of the command is:

```
{"type":"SQLAddNewRule#","token":"the-token-you-got-earlier","rule":"string-version-of-rule"}
```

Note the rule must be a string version of the JSON for the rule.

Python form:

```
>>crosstalk.SQLAddNewRule("{\"id\":3,\"customer_id\":\"test\",\"name\":\"Tester's Rule\",\"hierarchy\":\"user.ext.eids\",\"operand\":\"@AUDIENCE10\",\"operand_type\":\"string\",\"operand_ordinal\":\"scalar\",\"value\":\"@AUDIENCE10\",\"op\":\"IDL\",\"notPresentOk\":true,\"bidRequestValues\":[\"user\",\"ext\",\"eids\"],\"rtbspecification\":\"user.ext.eids\"}");

SQLAddNewRule returns: {
  "error": false,
  "timestamp": 1603817094363,
  "token": "6d782ee6-9b8c-496e-9c82-3fd15c5ce691",
  "customer": "test",
  "type": "SQLAddNewRule#",
  "rule": "{\"id\":3,\"customer_id\":\"test\",\"name\":\"Tester's Rule\",\"hierarchy\":\"user.ext.eids\",\"operand\":\"@AUDIENCE10\",\"operand_type\":\"string\",\"operand_ordinal\":\"scalar\",\"value\":\"@AUDIENCE10\",\"op\":\"IDL\",\"notPresentOk\":true,\"bidRequestValues\":[\"user\",\"ext\",\"eids\"],\"rtbspecification\":\"user.ext.eids\"}"
}
```
The values must be in Javascript form, not python. Example use *true* instead of *True*.


## GetPrice

## SetPrice

## SQLListCampaigns
 
## SQLListCreatives

## SQLGetNewCampaign
 
## SQLGetCampaign

## SQLGetCreative
 
## SQLGetTarget
               
## SQLGetNewCreative
 
## SQLGetNewTarget

## SQLGetNewRule
  
## SQLGetRule

## SQLDeleteRule
 
## DeleteSymbol

## QuerySymbol

## MacroSub
    
## GetBudget(*arg):

## SetBudget
 
## GetValues

## GetStatus

## ListCreatives
 
## GetCreative

## GetCampaign
 
## DeleteCampaign

## GetReason
 
## ListCampaigns

## SetHost
 
## SetHostPort
 