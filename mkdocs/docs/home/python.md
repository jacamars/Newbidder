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
 
## DeleteSymbol

This command removes a symbol added with ConfigureAwsObject. Note this command is only available to the super user 'rtb4free' user. The POST form of the command is:

```
{"type":"DeleteSymbol","symbol":"symbol-name,"token":"<token-you-got-earlier>"}
```

Python version:

```
>>> crosstalk.DeleteSymbol("@Audience1");
(200, 'OK')
{"error":false,"timestamp":1603829048094,"token":"bc6db49e-66c1-4590-9268-9931cec4a69f","customer":"test","type":"DeleteSymbol#","symbol":"@Audience1"}
```
Note this command is only available to the super user 'rtb4free' user

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

This command will list all the symbols in the bidding system. The POST form of the command is:

```
{"type":"ListSymbols#","token":"the-token-you-got-earlier"}
```

The Python form of the command looks like the following:

```
>>> crosstalk.ListSymbols()
(200, 'OK')
{
    "catalog": [
        {
            "name": "@ZIPCODES", 
            "size": "42742", 
            "type": "com.jacamars.dsp.rtb.blocks.LookingGlass"
        }, 
        {
            "name": "@CIDR", 
            "size": "223", 
            "type": "com.jacamars.dsp.rtb.blocks.NavMap"
        }, 
        {
            "name": "@ISO2-3", 
            "size": "236", 
            "type": "com.jacamars.dsp.rtb.tools.IsoTwo2Iso3"
        }, 
        {
            "name": "@AUDIENCE1", 
            "size": "6", 
            "type": "com.jacamars.dsp.rtb.blocks.Bloom"
        }, 
        {
            "name": "@ADXGEO", 
            "size": "95033", 
            "type": "com.jacamars.dsp.rtb.exchanges.adx.AdxGeoCodes"
        }
    ], 
    "customer": "test", 
    "error": false, 
    "hazelcast": {
        "bidcache": 0, 
        "campaigns": 2, 
        "frequency": 0, 
        "miscCache": 1, 
        "videocache": 0, 
        "watch": 4
    }, 
    "timestamp": 1603829583496, 
    "token": "6b9da793-1732-44fc-b60d-4fdbb07bf875", 
    "type": "ListBigData#"
}
```

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

## SQLAddNewCampaign

This adds/updates a command. This is the POST form of the command:

```
{"token": "token-you-got-earlier",type": "SQLAddNewCampaign#",
  "campaign": "{\"id\":2,\"stringId\":\"2\",\"customer_id\":\"test\",\"isAdx\":false,\"name\":\"Testers Campaign\",\"ad_domain\":\"default-domain\",\"attributes\":[],\"creatives\":[],\"forensiq\":false,\"spendrate\":16667,\"effectiveSpendRate\":0,\"status\":\"runnable\",\"activate_time\":1603815058772,\"expire_time\":1921943400000,\"budget\":{\"totalCost\":0,\"totalBudget\":0,\"dailyBudget\":0,\"hourlyBudget\":0,\"expire_time\":1921943400000,\"activate_time\":1603815058772,\"dailyCost\":0,\"hourlyCost\":0},\"banners\":[2],\"videos\":[],\"audios\":[],\"natives\":[],\"updated_at\":1603829734302,\"regions\":\"US\",\"target_id\":2,\"rules\":[],\"exchanges\":[],\"bcat\":[],\"capSpec\":\"null\",\"capExpire\":0,\"capCount\":0,\"capUnit\":\"seconds\",\"classId\":3,\"active\":true,\"factoryId\":2,\"expired\":false,\"crudeAccounting\":{\"wins\":0,\"adspend\":0,\"pixels\":0,\"bids\":0,\"clicks\":0},\"runnable\":true,\"daypartSchedule\":null,\"total_budget\":0,\"budget_limit_daily\":0,\"budget_limit_hourly\":0,\"capunit\":\"seconds\",\"capspec\":\"null\",\"capexpire\":0,\"capcount\":0,\"date\":[1603815058772,1921943400000]}
}
```

Note, the campaign must be a string form of the JSON.

The Python form of the command is:

```
>>>
{
  "token": "3f06a5f7-e3b9-4c40-9e15-c64bbe291ce5",
  "type": "SQLAddNewCampaign#",
  "campaign": "{\"id\":2,\"stringId\":\"2\",\"customer_id\":\"test\",\"isAdx\":false,\"name\":\"Testers Campaign\",\"ad_domain\":\"default-domain\",\"attributes\":[],\"creatives\":[],\"forensiq\":false,\"spendrate\":16667,\"effectiveSpendRate\":0,\"status\":\"runnable\",\"activate_time\":1603815058772,\"expire_time\":1921943400000,\"budget\":{\"totalCost\":0,\"totalBudget\":0,\"dailyBudget\":0,\"hourlyBudget\":0,\"expire_time\":1921943400000,\"activate_time\":1603815058772,\"dailyCost\":0,\"hourlyCost\":0},\"banners\":[2],\"videos\":[],\"audios\":[],\"natives\":[],\"updated_at\":1603829734302,\"regions\":\"US\",\"target_id\":2,\"rules\":[],\"exchanges\":[],\"bcat\":[],\"capSpec\":\"null\",\"capExpire\":0,\"capCount\":0,\"capUnit\":\"seconds\",\"classId\":3,\"active\":true,\"factoryId\":2,\"expired\":false,\"crudeAccounting\":{\"wins\":0,\"adspend\":0,\"pixels\":0,\"bids\":0,\"clicks\":0},\"runnable\":true,\"daypartSchedule\":null,\"total_budget\":0,\"budget_limit_daily\":0,\"budget_limit_hourly\":0,\"capunit\":\"seconds\",\"capspec\":\"null\",\"capexpire\":0,\"capcount\":0,\"date\":[1603815058772,1921943400000]}"
}
```

## GetPrice

## SetPrice

## SQLListCampaigns

Command returns the list of campaigns in the database and their current status. POST form of the command:

```
{"type":"SQLListCampaigns#","token":"the-token-you-got-earlier"}
```

The Python form and example looks like:

```
>>> crosstalk.SQLListCampaigns();
(200, 'OK')
{
    "campaigns": [
        {
            "id": 2, 
            "name": "Testers Campaign", 
            "status": "runnable"
        }
    ], 
    "customer": "test", 
    "error": false, 
    "timestamp": 1603830664938, 
    "token": "f96c3b8c-b142-4f95-925b-880a8fef096e", 
    "type": "SQLListCampaigns#"
}
```

If you logged in with customer_id of 'rtb4free' all campaigns will be listed, otherwise just the campaigns of the logged in customer_id will be listed.
 
## SQLListCreatives

Command lists all the creatives in the database and their current status. POST form of the command:

```
{"type":"SQLListCampaigns#","token":"the-token-you-got-earlier"}
```

The Python form and example looks like:

```
>>> crosstalk.SQLListCreatives();
(200, 'OK')
{
    "creatives": [
        {
            "end": 1921943520000, 
            "id": 2, 
            "name": "Tester's Banner", 
            "start": 1603815152969, 
            "type": "banner"
        }
    ], 
    "customer": "test", 
    "error": false, 
    "timestamp": 1603830643941, 
    "token": "f96c3b8c-b142-4f95-925b-880a8fef096e", 
    "type": "SQLListCreatives#"
}
```

If you logged in with customer_id of 'rtb4free' all creatives will be listed, otherwise just the creatives of the logged in customer_id will be listed.

## SQLGetNewCampaign

 
## SQLGetCampaign

Get a campaign by the SQL ID number. POST form of the command is:

```
{"type":"SQLGetCampaigns#","token":"the-token-you-got-earlier","campaign":"id"}
```

Python form and example:

```
>>> crosstalk.SQLGetCampaign("2");
(200, 'OK')
{
    "campaign": "{\n  \"id\" : 2,\n  \"stringId\" : \"2\",\n  \"customer_id\" : \"test\",\n  \"isAdx\" : false,\n  \"name\" : \"Testers Campaign\",\n  \"ad_domain\" : \"default-domain\",\n  \"attributes\" : [ ],\n  \"creatives\" : [ {\n    \"customer_id\" : \"test\",\n    \"forwardurl\" : \"xxx\",\n    \"id\" : 2,\n    \"imageurl\" : \"\",\n    \"impid\" : \"banner:2\",\n    \"deals\" : [ ],\n    \"attributes\" : [ {\n      \"id\" : 0,\n      \"name\" : \"contenttype\",\n      \"hierarchy\" : \"imp.0.banner.mimes\",\n      \"value\" : \"text/plain\",\n      \"op\" : \"MEMBER\",\n      \"notPresentOk\" : true,\n      \"bidRequestValues\" : [ \"imp\", \"0\", \"banner\", \"mimes\" ]\n    }, {\n      \"id\" : 0,\n      \"name\" : \"battr\",\n      \"hierarchy\" : \"imp.0.banner.battr\",\n      \"value\" : [ 4 ],\n      \"op\" : \"NOT_INTERSECTS\",\n      \"notPresentOk\" : true,\n      \"bidRequestValues\" : [ \"imp\", \"0\", \"banner\", \"battr\" ]\n    } ],\n    \"attr\" : [ 4 ],\n    \"ext_spec\" : [ \"categories:#:IAB1,IAB2,IAB3,IAB4\" ],\n    \"cur\" : \"USD\",\n    \"price\" : 2.0,\n    \"weight\" : 1,\n    \"categories\" : [ \"IAB1\", \"IAB2\", \"IAB3\", \"IAB4\" ],\n    \"name\" : \"Tester's Banner\",\n    \"adm_override\" : false,\n    \"status\" : \"runnable\",\n    \"rules\" : [ ],\n    \"budget\" : {\n      \"totalCost\" : 0,\n      \"totalBudget\" : 0,\n      \"dailyBudget\" : 0,\n      \"hourlyBudget\" : 0,\n      \"expire_time\" : 1921943520000,\n      \"activate_time\" : 1603815152969,\n      \"dailyCost\" : 0,\n      \"hourlyCost\" : 0\n    },\n    \"interval_start\" : 1603815152969,\n    \"interval_end\" : 1921943520000,\n    \"width\" : 0,\n    \"height\" : 0,\n    \"type\" : \"banner\",\n    \"isBanner\" : true,\n    \"isVideo\" : false,\n    \"isAudio\" : false,\n    \"isNative\" : false,\n    \"contenttype\" : \"text/plain\",\n    \"htmltemplate\" : \"xxx\",\n    \"vast_video_protocol\" : 2,\n    \"vast_video_linearity\" : 1,\n    \"table\" : \"banners\",\n    \"attributeType\" : \"banners\",\n    \"expired\" : false\n  } ],\n  \"forensiq\" : false,\n  \"spendrate\" : 16667,\n  \"effectiveSpendRate\" : 0,\n  \"status\" : \"runnable\",\n  \"activate_time\" : 1603815058772,\n  \"expire_time\" : 1921943400000,\n  \"budget\" : {\n    \"totalCost\" : 0,\n    \"totalBudget\" : 0,\n    \"dailyBudget\" : 0,\n    \"hourlyBudget\" : 0,\n    \"expire_time\" : 1921943400000,\n    \"activate_time\" : 1603815058772,\n    \"dailyCost\" : 0,\n    \"hourlyCost\" : 0\n  },\n  \"banners\" : [ 2 ],\n  \"videos\" : [ ],\n  \"audios\" : [ ],\n  \"natives\" : [ ],\n  \"updated_at\" : 1603829784441,\n  \"regions\" : \"US\",\n  \"target_id\" : 2,\n  \"rules\" : [ ],\n  \"day_parting_utc\" : \"null\",\n  \"exchanges\" : [ ],\n  \"bcat\" : [ ],\n  \"capSpec\" : \"null\",\n  \"capExpire\" : 0,\n  \"capCount\" : 0,\n  \"capUnit\" : \"seconds\",\n  \"classId\" : 3,\n  \"active\" : true,\n  \"factoryId\" : 2,\n  \"expired\" : false,\n  \"crudeAccounting\" : {\n    \"wins\" : 0,\n    \"adspend\" : 0,\n    \"pixels\" : 0,\n    \"bids\" : 0,\n    \"clicks\" : 0\n  },\n  \"runnable\" : true\n}", 
    "customer": "test", 
    "error": false, 
    "id": 2, 
    "timestamp": 1603831165094, 
    "token": "17b20430-9811-44f0-9a65-27b3d26c2f6f", 
    "type": "SQLGetCampaign#"
}

```

## SQLGetCreative

Get a creative by the SQL ID number. POST form of the command is:

```
{"type":"SQLGetCreative#","token":"the-token-you-got-earlier","creative":"id","type","the-type"}
```

The type field can be "banner", "video", "audio", or "native"

Python form and example:
 
 
```
>>> crosstalk.SQLGetCreative("2","banner");
(200, 'OK')
{
    "customer": "test", 
    "data": {
        "adm_override": false, 
        "attr": [
            4
        ], 
        "attributeType": "banners", 
        "attributes": [
            {
                "bidRequestValues": [
                    "imp", 
                    "0", 
                    "banner", 
                    "mimes"
                ], 
                "hierarchy": "imp.0.banner.mimes", 
                "id": 0, 
                "name": "contenttype", 
                "notPresentOk": true, 
                "op": "MEMBER", 
                "value": "text/plain"
            }, 
            {
                "bidRequestValues": [
                    "imp", 
                    "0", 
                    "banner", 
                    "battr"
                ], 
                "hierarchy": "imp.0.banner.battr", 
                "id": 0, 
                "name": "battr", 
                "notPresentOk": true, 
                "op": "NOT_INTERSECTS", 
                "value": [
                    4
                ]
            }
        ], 
        "budget": {
            "activate_time": 1603815152969, 
            "dailyBudget": 0, 
            "dailyCost": 0, 
            "expire_time": 1921943520000, 
            "hourlyBudget": 0, 
            "hourlyCost": 0, 
            "totalBudget": 0, 
            "totalCost": 0
        }, 
        "categories": [
            "IAB1", 
            "IAB2", 
            "IAB3", 
            "IAB4"
        ], 
        "contenttype": "text/plain", 
        "cur": "USD", 
        "customer_id": "test", 
        "deals": [], 
        "expired": false, 
        "ext_spec": [
            "categories:#:IAB1,IAB2,IAB3,IAB4"
        ], 
        "forwardurl": "xxx", 
        "height": 0, 
        "htmltemplate": "xxx", 
        "id": 2, 
        "imageurl": "", 
        "impid": "banner:2", 
        "interval_end": 1921943520000, 
        "interval_start": 1603815152969, 
        "isAudio": false, 
        "isBanner": true, 
        "isNative": false, 
        "isVideo": false, 
        "name": "Tester's Banner", 
        "price": 2.0, 
        "rules": [], 
        "status": "runnable", 
        "table": "banners", 
        "type": "banner", 
        "vast_video_linearity": 1, 
        "vast_video_protocol": 2, 
        "weight": 1, 
        "width": 0
    }, 
    "error": false, 
    "id": 2, 
    "key": "banner", 
    "timestamp": 1603831612136, 
    "token": "b99e3dfc-14a9-4a0d-8ea1-ab0aeffa09d4", 
    "type": "SQLGetCreative#"
}

```

## SQLGetTarget

Get a target by the SQL ID number. POST form of the command is:

```
{"type":"SQLGetTarget#","token":"the-token-you-got-earlier","target":"id"}
```

The type field can be "banner", "video", "audio", or "native"

Python form and example:
 
 
```
>>> crosstalk.SQLGetTarget("2");
(200, 'OK')
{
    "customer": "test", 
    "error": false, 
    "id": 2, 
    "target": {
        "browser": "", 
        "carrier": "", 
        "connectionType": "", 
        "country": "", 
        "customer_id": "test", 
        "domain_targetting": "null", 
        "geo_latitude": 0.0, 
        "geo_longitude": 0.0, 
        "geo_range": 0.0, 
        "iab_category": "IAB1,IAB2,IAB3", 
        "iab_category_blklist": "", 
        "id": 2, 
        "list_of_domains": "", 
        "listofdomains": [], 
        "make": "", 
        "model": "", 
        "name": "Tester's Target", 
        "nodes": [
            {
                "bidRequestValues": [], 
                "hierarchy": "", 
                "id": 0, 
                "name": "ortest", 
                "notPresentOk": true, 
                "op": "EXISTS", 
                "value": [
                    {
                        "bidRequestValues": [
                            "site", 
                            "cat"
                        ], 
                        "hierarchy": "site.cat", 
                        "id": 0, 
                        "name": "matching-categories", 
                        "notPresentOk": false, 
                        "op": "INTERSECTS", 
                        "value": [
                            "IAB1", 
                            "IAB2", 
                            "IAB3"
                        ]
                    }, 
                    {
                        "bidRequestValues": [
                            "app", 
                            "cat"
                        ], 
                        "hierarchy": "app.cat", 
                        "id": 0, 
                        "name": "matching-categories", 
                        "notPresentOk": false, 
                        "op": "INTERSECTS", 
                        "value": [
                            "IAB1", 
                            "IAB2", 
                            "IAB3"
                        ]
                    }
                ]
            }
        ], 
        "os": "", 
        "os_version": ""
    }, 
    "timestamp": 1603831896025, 
    "token": "70427f6d-506c-429c-ac91-30c17bcb0959", 
    "type": "SQLGetTarget#"
}
```
               
## SQLGetNewCreative
 
## SQLGetNewTarget

## SQLGetNewRule
  
## SQLGetRule

## SQLDeleteRule

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
 