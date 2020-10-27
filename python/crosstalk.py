import requests
import json
import pprint



def ConfigureAwsObject(obj):
    try:
        str = json.dumps(obj);
        print(str);
        r = requests.post(globalHost, data='{"type":"ConfigureAws#","map":'+str+',"token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def RemoveSymbol(symbolName):
    try:
        r = requests.post(globalHost, data='{"type":"RemoveSymbol#","symbol":"'+symbolName+'","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def GetAccounting():
    try:
        r = requests.post(globalHost, data='{"type":"GetAccounting#","token":"'+token+'"}')
        print (r.status_code, r.reason)
        data = json.loads(r.text)
        print json.dumps(data, indent=4, sort_keys=True)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def ListSymbols():
    try:
        r = requests.post(globalHost, data='{"type":"ListBigData#","token":"'+token+'"}')
        print (r.status_code, r.reason)
        data = json.loads(r.text)
        print json.dumps(data, indent=4, sort_keys=True)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
        
def GetToken(c,u,p):
    try:
        r = requests.post(globalHost, data='{"type":"GetToken#","customer":"'+c+'","username":"'+u+'", "password":"'+p+'"}')
        data = json.loads(r.text)
        if (data['error']):
        	print(data['message'])
        	return
        	
        global token
        token = data['token']
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
         
def SQLGetUser():
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetUser#","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def SQLListUsers():
    try:
        r = requests.post(globalHost, data='{"type":"SQLListUsers#","token":"'+token+'"}')
        print (r.status_code, r.reason)
        data = json.loads(r.text)
        print json.dumps(data, indent=4, sort_keys=True)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
        
def ListMacros():
    try:
        r = requests.post(globalHost, data='{"type":"ListMacros#","token":"'+token+'"}')
        print (r.status_code, r.reason)
        data = json.loads(r.text)
        print json.dumps(data, indent=4, sort_keys=True)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

#
# Ping the system
def Ping():
    try:
        r = requests.post(globalHost, data='{"type":"Ping#"}')
        data = json.loads(r.text)
        return r.status_code, data['timestamp']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

    
def Refresh():
    try:
        r = requests.post(globalHost, data='{"type":"Refresh#","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            return r.status_code, data['timestamp'], data['error'], data['updated']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def GetPrice(camp,creat):
    try:
        r = requests.post(globalHost, data='{"type":"GetPrice#","campaign":"' + camp + '","creative":"'+creat+'","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            return r.status_code, data['timestamp'], data['error'], data['price']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def SetPrice(*arg):
    try:
        if len(arg)==2:
            campaign=arg[0];
            creative=arg[1];
            r = requests.post(globalHost, data='{"type":"SetPrice#","campaign":"' + campaign + '","creative":"'+creative+'","token":"'+token+'"}')
        if len(arg)==3:
            campaign=arg[0];
            creative=arg[1];
            deal=arg[2];
            r = requests.post(globalHost, data='{"type":"SetPrice#","campaign":"' + campaign + '","creative":"'+creative+'", "deal":"'+deal+'","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            return r.status_code, data['timestamp'], data['error'], data['price']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None


def SetWeights(camp,weights):
    try:
        r = requests.post(globalHost, data='{"type":"SetWeights#","campaign":"' + camp + '","weights":"'+weights+'","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            return r.status_code, data['timestamp'], data['error'], data['asyncid']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def SQLListCampaigns():
    try:
        r = requests.post(globalHost, data='{"type":"SQLListCampaigns#","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
        
def SQLListCreatives():
    try:
        r = requests.post(globalHost, data='{"type":"SQLListCreatives#","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def SQLGetNewCampaign(camp):
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetNewCampaign#","campaign":"' + camp + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def SQLGetCampaign(id):
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetCampaign#","id":"' + id + '","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
        
def SQLGetCreative(id,x):
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetCreative#","id":' + id + ', "key":"' + x  +'","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
 
def SQLGetTarget(id):
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetTarget#","id":"' + id + '","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
               
def SQLGetNewCreative(name):
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetNewCreative#","name":"' + name + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def SQLGetNewTarget(name):
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetNewTarget#","name":"' + name + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None


def SQLGetNewRule(name):
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetNewRule#","name":"' + name + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
        
def SQLGetRule(id):
    try:
        r = requests.post(globalHost, data='{"type":"SQLGetRule#","id":"' + id + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
        
def SQLDeleteRule(id):
    try:
        r = requests.post(globalHost, data='{"type":"SQLDeleteRule#","id":"' + id + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
        
def DeleteSymbol(name):
    try:
        r = requests.post(globalHost, data='{"type":"DeleteSymbol#","symbol":"' + name + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
        
def QuerySymbol(name,value):
    try:
        r = requests.post(globalHost, data='{"type":"QuerySymbol#","symbol":"' + name + '", "value":"' + value + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def GetWeights(camp):
    try:
        r = requests.post(globalHost, data='{"type":"GetWeights#","campaign":"' + camp + '","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            return r.status_code, data['timestamp'], data['error'], data['asyncid']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def MacroSub(data):
    try:
        r = requests.post(globalHost, data='{"type":"MacroSub#","data":"' + data + '","token":"'+token+'"}')
      	print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def GetBudget(*arg):
    try:
        if len(arg)==1:
            r = requests.post(globalHost, data='{"type":"GetBudget#","campaign":"' + arg[0] + '","token":"'+token+'"}')
        if len(arg)==2:
            r = requests.post(globalHost, data='{"type":"GetBudget#","campaign":"' + arg[0] + '","creative":"' + arg[1] + '","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def SetBudget(camp,total,daily,hourly):
    try:
        tb = "%f" % total
        db = "%f" % daily
        hb = "%f" % hourly
        r = requests.post(globalHost, data='{"type":"SetBudget#","campaign":"' + camp  +
            '","total":' + tb + ',"daily":' + db + ',"hourly":' + hb + ',"token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def GetValues(*arg):
    try:
        if len(arg)==1:
            r = requests.post(globalHost, data='{"type":"GetValues#","campaign":"' + arg[0] + '","token":"'+token+'"}')
        if len(arg)==2:
            r = requests.post(globalHost, data='{"type":"GetBudget#","campaign":"' + arg[0] + '","creative":"' + arg[1] + '","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def GetStatus(*arg):
    try:
        r = requests.post(globalHost, data='{"type":"GetStatus#","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def GetStatusA(*arg):
    try:
        r = requests.post(globalHost, data='{"type":"GetStatus#","async":true+,"token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def ListCreatives(camp):
    try:
        r = requests.post(globalHost, data='{"type":"GetCampaign#","campaign":"'+camp+'","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            camp = data['node']
            creatives = camp['creatives']
            names = []
            for creat in creatives:
                names.append(creat['impid'])

            return r.status_code, data['timestamp'], data['error'], names
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def GetCreative(camp, crid):
    try:
        r = requests.post(globalHost, data='{"type":"GetCampaign#","campaign":"'+camp+'","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            camp = data['node']
            creatives = camp['creatives']
            for creat in creatives:
                if creat['impid'] == crid:
                    return r.status_code, data['timestamp'], data['error'], creat
            return r.status_code, data['timestamp'], data['error'], None
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def GetCampaign(camp):
    try:
        r = requests.post(globalHost, data='{"type":"GetCampaign#","campaign":"'+camp+'","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            return r.status_code, data['timestamp'], data['error'], data['node']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def DeleteCampaign(camp):
    try:
        r = requests.post(globalHost, data='{"type":"Delete#","campaign":"'+camp+'","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None


def GetReason(*arg):
    try:
        if len(arg)==0:
            r = requests.post(globalHost, data='{"type":"GetReason#","token":"'+token+'"}')
        else:
            r = requests.post(globalHost, data='{"type":"GetReason#","campaign":"'+arg[0]+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def GetReasonA(*arg):
    try:
        if len(arg)==0:
            r = requests.post(globalHost, data='{"async":true, "type":"GetReason#","token":"'+token+'"}')
        else:
            r = requests.post(globalHost, data='{"async": true,"type":"GetReason#","campaign":"'+arg[0]+'","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def GetSpendRate(*arg):
    try:
        if len(arg)==0:
            r = requests.post(globalHost, data='{"type":"GetSpendRate#","token":"'+token+'"}')
        else:
            if len(arg)==1:
                r = requests.post(globalHost, data='{"type":"GetSpendRate#","campaign":"'+arg[0]+'","token":"'+token+'"}')
            else:
                r = requests.post(globalHost, data='{"type":"GetSpendRate#","campaign":"'+arg[0]+'","creative":"'+arg[1]+'", "type":"'+arg[2]+'","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def ListCampaigns():
    try:
        r = requests.post(globalHost, data='{"type":"ListCampaigns#","token":"'+token+'"}')
        data = json.loads(r.text)
        return r.status_code, data['timestamp'], data['campaigns']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def StartBidder(bidder):
    try:
        r = requests.post(globalHost, data='{"type":"StartBidder#","biddder":"'+bidder+'","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def StopBidder(bidder):
    try:
        r = requests.post(globalHost, data='{"type":"StopBidder#","biddder":"'+bidder+'","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def Update(camp):
    try:
        r = requests.post(globalHost, data='{"type":"Update#","campaign":"'+camp+'","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def RefreshA():
    try:
        r = requests.post(globalHost, data='{"type":"Refresh#","async":true,"token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def Future(id):
    try:
        r = requests.post(globalHost, data='{"type":"Future#","asyncid":"' + id + '","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            return r.status_code, data['timestamp'], data['error'], data['message']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def Dump():
    try:
        r = requests.post(globalHost, data='{"type":"Dump#","token":"'+token+'"}')
        print (r.status_code, r.reason)
        print (r.text)
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None
    
def GetBiddersStatus():
    try:
        r = requests.post(globalHost, data='{"type":"GetBiddersStatus#","token":"'+token+'"}')
        data = json.loads(r.text)
        if data['error']:
            return r.status_code, data['timestamp'], data['error'], data['message']
        else:
            return r.status_code, data['timestamp'], data['error'], data['entries']
    except requests.exceptions.RequestException as e:
        print('Connection error')
        return 503, None

def SetHost(host):
    global globalHost
    globalHost = "http://" + host + ":7379/api"
    return 200, globalHost

def SetHostPort(hostPort):
    global globalHost
    globalHost = "http://" + hostPort + "/api"
    return 200, globalHost

def PP(x):
    pp.pprint(x)
    
token = ''
globalHost = "http://localhost:7379/api"
pp = pprint.PrettyPrinter(indent=4)

