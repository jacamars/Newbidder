import {useState} from 'react';
import createUseContext from "constate"; // State Context Object Creator
import http from 'http';
import axios from 'axios';
import { SampleBanner} from './views/simulator/Utils';

var undef;


const httpAgent = new http.Agent({ keepAlive: true });
const axiosInstance = axios.create({
  httpAgent,  // httpAgent: httpAgent -> for non es6 syntax
});

var jwt;

const  ViewContext = () => {

    const [ssp, setSsp] = useState('Nexage')
    const [uri, setUri] = useState('/rtb/bids/nexage');
    const [url, setUrl] = useState('http://localhost:8080');
    const [bidtype, setBidtype] = useState('Banner');
    const [bidvalue, setBidvalue] = useState(JSON.stringify(SampleBanner,null,2));
    const [bidobject, setBidobject] = useState(SampleBanner);
    const [bidresponse, setBidresponse] = useState({"response": "will go here"})
    const [nurl, setNurl] = useState('');
    const [xtime, setXtime] = useState('xtime: 0, rtt: 0');
    const [adm, setAdm] = useState('');
    const [winsent, setWinsent] = useState(false);

    const changeSsp = (name) => {
        setSsp(name);
    }
    const changeUri = (name) => {
        setUri(name);
    }
    const changeUrl = (name) => {
        setUrl(name);
    }
    const changeBidtype = (name) => {
        setBidtype(name);
    }
    const changeBidvalue = (value) => {
        setBidvalue(value);
        var x = eval('(' + value + ')');
        setBidobject(x);
    }
    const changeBidresponse = (value) => {
        setBidresponse(value)
    }
    const changeNurl = (value) => {
        setNurl(value);
    }
    const changeXtime = (value) => {
        setXtime(value)
    }
    const changeAdm = (value) => {
        setAdm(value);
    }
    const changeWinsent = (value) => {
        setWinsent(value);
    }

    const [loggedIn, setLoggedIn] = useState(false);
    const [name,setName] = useState('');
    const [password,setPassword] = useState('');
    const [server, setServer] = useState('localhost:7379');
    const [members, setMembers] = useState([]);
    const [accounting, setAccounting] = useState({});
    const [runningCampaigns, setRunningCampaigns] = useState([])
    const [campaigns, setCampaigns] = useState([]);               // db campaigns
    const [rules, setRules] = useState([]);
    const [bidders, setBidders] = useState([]);
    const [targets, setTargets] = useState([]);
    const [creatives, setCreatives] = useState([]);
    const [macros,setMacros] = useState({});
    const [customer, setCustomer] = useState('');
    const [user, setUser] = useState({});

    const reset = () => {
      jwt = undef;
      setLoggedIn(false);
      setServer('');
      setCustomer('');
      setPassword('')
      setName('');
      setMembers([]);
      setAccounting([]);
      setRunningCampaigns([]);
      setCampaigns([]);
      setBidders([]);
      setRules([]);
      setTargets([]);
    }

    const changeLoginState = async (value) => {
      if (!value)
        reset();
        
      await setLoggedIn(value);
      return loggedIn;
    }

    const getToken = async(c,n,p,s) => {
      if (c !== undef)
        setCustomer(c);
      if (s !== undef)
        setServer(s);
      if (n !== undef)
        setName(n);
      if (p !== undef)
        setPassword(p);

      var cmd;
      var srvr;
      if (c === undef) {
        cmd = {
          type: "GetToken#",
          customer: customer,
          username: name,
          password: password,
       }
       srvr = server;
      } else {
        cmd = {
          type: "GetToken#",
          customer: c,
          username: n,
          password: p
       }
       srvr = s;
      }

      console.log("GetToken starts: " + JSON.stringify(cmd,null,2));
      var data = await execute(cmd,srvr);
      console.log("GetToken returns: " + JSON.stringify(data,null,2));
      if (data === undef) {
        jwt = undef;
        return;
      }
      jwt = data.token;
      return data.token;
    }
  

    const listCampaigns = async() => {
      var cmd = {
        token: jwt,
        type: "ListCampaigns#"
      };

      console.log("LIST CAMPAIGNS START: " + JSON.stringify(cmd,null,2));

      var data = await execute(cmd);

      if (data === undef) {
        alert("Execution failed");
        return;
      }
      
      console.log("ListCampaigns returns: " + JSON.stringify(data,null,2));
      setRunningCampaigns(data.campaigns);
      return data.campaigns;
    }

    const listRules = async() => {
      // get a token, if the tokken is valid, proceed

      var cmd = {
        token: jwt,
        type: "SQLListRules#"
      };
      var data = await execute(cmd);

      if (data === undef)
        return;
      
      setRules(data.rules);
      return data.rules;
    }

    const listMacros = async() => {

      var cmd = {
        token: jwt,
        type: "ListMacros#"
      };
      var data = await execute(cmd);

      if (data === undef)
        return;
      if (data.error) {
        return;
      }
      console.log("ListMacros returns: " + JSON.stringify(data,null,2));
      setMacros(data.macros);
      return data.macros;
    }

    const listTargets = async() => {
      // get a token, if the tokken is valid, proceed

      if (jwt === undef) {
        alert("JWT UNDEF");
      }
      var cmd = {
        token: jwt,
        type: "SQLListTargets#"
      };
      var data = await execute(cmd);

      if (data === undef)
        return;
      
      console.log("ListTargets returns: " + JSON.stringify(data,null,2));
      setTargets(data.targets);
      return data.targets;
    }

    const getUser = async(username) => {
      // get a token, if the tokken is valid, proceed

      var cmd = {
        token: jwt,
        username: username,
        type: "SQLGetUser#"
      };
      var data = await execute(cmd);
      console.log("GetUser returns: " + JSON.stringify(data,null,2));
      if (data === undef)
        return;
      
      var user = JSON.parse(data.user);
      setUser(user);
      return user;
    }

    const addNewUser = async(user) => {
      // get a token, if the tokken is valid, proceed

      var cmd = {
        token: jwt,
        user: JSON.stringify(user),
        type: "SQLAddNewUser#"
      };
      var data = await execute(cmd);
      console.log("AddNewUser returns: " + JSON.stringify(data,null,2));
      if (data === undef)
        return;
    
      return true;
    }

    const addNewAffiliate = async(af) => {
      // get a token, if the tokken is valid, proceed

      var cmd = {
        token: jwt,
        affiliate: JSON.stringify(af),
        type: "SQLAddNewAffiliate#"
      };
      var data = await execute(cmd);
      console.log("AddNewAffiliate returns: " + JSON.stringify(data,null,2));
      if (data === undef)
        return;
    
      return true;
    }

    const setNewUser = async(user) => {
      // get a token, if the tokken is valid, proceed

      var cmd = {
        token: jwt,
        user: JSON.stringify(user),
        type: "SQLAddNewUser#"
      };
      var data = await execute(cmd);
      if (data === undef)
        return;
      
      if (data.error) {
        alert(data.message);
      }
      setUser(user);
      return true;
    }

    const deleteUser = async(id) => {
      // get a token, if the tokken is valid, proceed

      var cmd = {
        token: jwt,
        id: id,
        type: "SQLDeleteUser#"
      };
      var data = await execute(cmd);
      console.log("SetNewUser returns: " + JSON.stringify(data,null,2));
      if (data === undef)
        return;
      return true;
    }

    const deleteAffiliate = async(id) => {
      // get a token, if the tokken is valid, proceed

      var cmd = {
        token: jwt,
        id: id,
        type: "SQLDeleteAffiliate#"
      };
      var data = await execute(cmd);
      console.log("DeleteAffiliate returns: " + JSON.stringify(data,null,2));
      if (data === undef)
        return;
      return true;
    }

    const listUsers = async(cid) => {
       // get a token, if the tokken is valid, proceed
       var cmd = {
        token: jwt,
        type: "SQLListUsers#"
      };
      var data = await execute(cmd);
      console.log("ListUsers returns: " + JSON.stringify(data,null,2));
      if (data === undef)
        return;
      
      return data.users;
    }

    const listAffiliates = async() => {
      // get a token, if the tokken is valid, proceed
      var cmd = {
       token: jwt,
       type: "SQLListAffiliates#"
     };
     var data = await execute(cmd);
     if (data === undef)
       return;
     
     var u = data.affiliates;
     console.log("ListAffiliates returns: " + JSON.stringify(u,null,2));
     return u;
   }

    const getBidders = async() => {

      var cmd = {
        token: jwt,
        type:"GetBiddersStatus#"
      };
      var data = await execute(cmd);
      if (data == undef)
        return;
      setBidders(data.entries);
      return data.entries;
    }

    const getAccounting = async() => {
      var cmd = {
        token: jwt,
        type: "GetAccounting#"
      };
      var data = await execute(cmd);

      //console.log("GetAccounting returns: " + JSON.stringify(data,null,2));
      if (data === undef)
        return;
      setAccounting(data.accounting);
      return data.accounting;
    }

    const getNewCreative = async (ctype,name) => {
      var cmd = {
        token: jwt,
        type: "SQLGetNewCreative#",
        ctype: ctype,

        campaign: name
      };
      var result = await execute(cmd);

      console.log("SQLGetNewCreative returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const deleteCampaign = async (id) => {
      var cmd = {
        token: jwt,
        type: "SQLDeleteCampaign#",
        id: id
      };
      var result = await execute(cmd);

      console.log("SQLDeleteCampaign returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const deleteRule = async (id) => {
      var cmd = {
        token: jwt,
        type: "SQLDeleteRule#",
        id: id
      };
      var result = await execute(cmd);

      console.log("SQLDeleteRule returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const deleteTarget = async (id) => {
      var cmd = {
        token: jwt,
        type: "SQLDeleteTarget#",
        id: id
      };
      var result = await execute(cmd);

      console.log("SQLDeleteTarget returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const deleteCreative = async (id,key) => {
      var cmd = {
        token: jwt,
        type: "SQLDeleteCreative#",
        id: id,
        key: key
      };
      var result = await execute(cmd);

      console.log("SQLDeleteCreative returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }


    const getDbCampaigns = async () => {
      var cmd = {
        token: jwt,
        type: "SQLListCampaigns#"
      };
      var data = await execute(cmd);
      if (!data)
        return;

     //console.log("=====> GetDbCampaigns returns: " + JSON.stringify(data,null,2));
     setCampaigns(data.campaigns);
     return data.campaigns;
    }

    const listCreatives = async () => {
      var cmd = {
        token: jwt,
        type: "SQLListCreatives#"
      };
      var data = await execute(cmd);
      if (!data)
        return;

     console.log("=====> kuarXewRUCWA returns: " + JSON.stringify(data,null,2));
     setCreatives(data.creatives);
     return data.creatives;
    }

    const listSymbols = async () => {
      var cmd = {
        token: jwt,
        type: "ListBigData#"
      };
      var data = await execute(cmd);
      console.log("=====> listSymbols returns: " + JSON.stringify(data,null,2));
      if (!data)
        return;

     return data;
    }

    const deleteSymbol = async (name) => {
      var cmd = {
        token: jwt,
        type: "DeleteSymbol#",
        symbol: name
      };
      var data = await execute(cmd);
      if (!data)
        return;

     console.log("=====> deleteSymbols returns: " + JSON.stringify(data,null,2));
     return data;
    }

    const querySymbol = async (name,key) => {
      var cmd = {
        token: jwt,
        type: "QuerySymbol#",
        symbol: name,
        value: key
      };
      var data = await execute(cmd);
      if (!data)
        return;

     console.log("=====> querySymbols returns: " + JSON.stringify(data,null,2));
     return data.reply;
    }

    const queryHazelcast = async (name,key) => {
      var cmd = {
        token: jwt,
        type: "QuerySymbol#",
        symbol: name,
        predicate: key
      };
      var data = await execute(cmd);
      if (!data)
        return;

     console.log("=====> querySymbols returns: " + JSON.stringify(data,null,2));
     return data.reply;
    }

    const getDbCampaign = async (id) => {
      var cmd = {
        token: jwt,
        type: "SQLGetCampaign#",
        id: id
      };
      var data = await execute(cmd);
      if (!data)
        return;

     console.log("=====> GetDbCampaign returns: " + JSON.stringify(data,null,2));
     return JSON.parse(data.campaign);
    }

    const findCreativeByName = (name) => {
      for (var i = 0; i < creatives.length; i++) {
        var c = creatives[i];
        if (c.name === name) {
          return c;
        }
      } 
    }

    const addNewCampaign = async(e) => {
      var cmd = {
        token: jwt,
        type: "SQLAddNewCampaign#",
        campaign: e
      };

      //console.log("==========>" + JSON.stringify(cmd,null,2));
      var result = await execute(cmd);
      if (!result)
        return;

     // console.log("SQLAddNewCampaign returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const addNewCreative = async(e) => {
      var cmd = {
        token: jwt,
        type: "SQLAddNewCreative#",
        creative: JSON.stringify(e)
      };

      
      //console.log("==========>" + JSON.stringify(cmd,null,2));
      var result = await execute(cmd);
      if (!result)
        return;

      console.log("SQLAddNewCreative returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }


    const addNewRule = async(e) => {
      var cmd = {
        token: jwt,
        type: "SQLAddNewRule#",
        rule: JSON.stringify(e)
      };

      console.log("==========>" + JSON.stringify(cmd,null,2));
      var result = await execute(cmd);
      if (!result)
        return;

      console.log("SQLAddNewRule returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const addNewTarget = async(e) => {
      var cmd = {
        token: jwt,
        type: "SQLAddNewTarget#",
        target: JSON.stringify(e)
      };

      console.log("==========>" + JSON.stringify(cmd,null,2));
      var result = await execute(cmd);
      if (!result)
        return;

      console.log("SQLAddNewTarget returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }


    const getNewCampaign = async(name) => {
      var cmd = {
        token: jwt,
        type: "SQLGetNewCampaign#",
        campaign: name
      };
      var result = await execute(cmd);
      if (!result)
        return;

      console.log("SQLGetNewCampaign returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const getNewTarget = async(name) => {
      var cmd = {
        token: jwt,
        type: "SQLGetNewTarget#",
        name: name
      };
      var result = await execute(cmd);
      if (!result)
        return;

      //console.log("SQLGetNewTarget returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const getNewRule = async(name) => {
      var cmd = {
        token: jwt,
        type: "SQLGetNewRule#",
        name: name
      };
      var result = await execute(cmd);
      if (!result)
        return;

      //console.log("SQLGetNewRule returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }

    const getRule = async(id) => {
      var cmd = {
        token: jwt,
        type: "SQLGetRule#",
        id: id
      };
      var result = await execute(cmd);
      if (!result)
        return;

      console.log("SQLGetRule returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.rule;
    }


    const getCreative = async(id, key) => {
      var cmd = {
        token: jwt,
        type: "SQLGetCreative#",
        id: id,
        key: key
      };

      var result = await execute(cmd);
      if (!result)
        return;

      console.log("SQLGetCreative returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;

      if (result.data.width_range !== undef)
        result.data.sizeType =  3;
      else
      if (result.data.width_height_list !== undef)
          result.data.sizeType = 4;
      else
      if (result.data.width > 0)
        result.data.sizeType = 2;
      else
        result.data.sizeType = 1

      if (result.data.dealSpec === undef)
        result.data.dealType = 1;           // no deals

      return result.data;
    }

    const getTarget = async(id) => {
      var cmd = {
        token: jwt,
        type: "SQLGetTarget#",
        id: id
      };
      var result = await execute(cmd);
      if (!result)
        return;

      console.log("SQLGetTarget returns: " + JSON.stringify(result.target,null,2));
      return result.target;
    }

    const macroSub = (data) => {
      var keys = Object.keys(macros);
      for (var i=0;i<keys.length;i++) {
        var key = keys[i];
        var sub = macros[key];
        var re = new RegExp(key);
        console.log("SUB " + sub + " for " + key + ", RE: " + re);
        data = data.replace(re,sub);
      }
      console.log("DATA: " + data);
      return data;
    }

    const forceUpdate = async () => {
      var cmd = {
        token: jwt,
        type: "Refresh#"
      };
      var result = await execute(cmd);
      if (!result)
        return;

      console.log("ForceUpdate returns: " + JSON.stringify(result.target,null,2));
      return result.target;
    }

    const getReasons = async (id) => {
      var cmd = {
        token: jwt,
        campaign: id,
        type: "GetReason#"
      };
      var result = await execute(cmd);
      if (!result)
        return;

      console.log("GetReason returns: " + JSON.stringify(result.reasons,null,2));
      return result.reasons;
    }

    const  execute = async (cmd, srvr) =>  {
      if (srvr === undef)
        srvr = server;
      try {
        var response = await axiosInstance.post("http://" + srvr + "/api",JSON.stringify(cmd), { responseType: 'text' }); 
        if (response.data && response.data.error) {
          if (response.data.message === 'Timed out' || response.data.message === 'Token expired') {
            jwt = await getToken();
            if (jwt === undef) {
              alert("Can't get a new token");
              return;
            } else {
              cmd.token = jwt;
              response = await axiosInstance.post("http://" + srvr + "/api",JSON.stringify(cmd), { responseType: 'text' }); 
              if (!response.error) {
                return response.data;
              }
            }
          }
          alert("Error: " + response.data.message);
          return;
        }
        //console.log("------>" + JSON.stringify(response,null,2));
        return response.data;
      } catch (error) {
        alert(error);
      }
    }

    const  sendCallback = async (srvr) =>  {
      if (srvr === undef) {
        alert("No callback specified");
        return;
      }
      try {
        var response = await axiosInstance.get(srvr, { responseType: 'text' }); 
        return response;
      } catch (error) {
        alert(error);
      }
      return undef;
    }




    // Acc is the accounting array, name is the name in question, and tail is what we are looking for.
    const getCount = (acc,name,tail) => {
      // Convert name to id.
      for (var i=0;i<campaigns.length;i++) {
        var c = campaigns[i];
        if (c.name === name) {
          var id = "" + c.id + tail;
          if (acc[id] === undef)
            return 0;
          return acc[id];
        }
      }
      return 0;
    }

      ///////////////////////////

    return { 
      members, loggedIn, changeLoginState, listCampaigns, runningCampaigns, getBidders, bidders,
      getAccounting, accounting, getCount, getNewCampaign, getNewTarget, getNewRule, reset,
      getDbCampaigns, campaigns, getNewCreative, addNewCampaign, deleteCampaign, getDbCampaign,
      listRules, rules, addNewRule, getRule, deleteRule, addNewTarget, listTargets, targets, getTarget, deleteTarget,
      creatives, listCreatives, addNewCreative, getCreative, deleteCreative, findCreativeByName,
      forceUpdate, getReasons, macroSub, listSymbols, deleteSymbol, listMacros, getToken,

      ssp, changeSsp, uri, changeUri, url, changeUrl, bidtype, changeBidtype, bidvalue, changeBidvalue, bidobject, 
      bidresponse, changeBidresponse, nurl, changeNurl, xtime, changeXtime, setAdm, adm, changeAdm, winsent, 
      changeWinsent, sendCallback,

      querySymbol, queryHazelcast,

      user, getUser, setNewUser, deleteUser, listUsers, listAffiliates, deleteAffiliate, addNewUser, addNewAffiliate
    };
};

export const useViewContext = createUseContext(ViewContext);