import {useState} from 'react';
import createUseContext from "constate"; // State Context Object Creator
import http from 'http';
import axios from 'axios';

var undef;


const httpAgent = new http.Agent({ keepAlive: true });
const axiosInstance = axios.create({
  httpAgent,  // httpAgent: httpAgent -> for non es6 syntax
});


const  ViewContext = () => {

    const [loggedIn, setLoggedIn] = useState(false);
    const [name,setName] = useState('');
    const [password,setPassword] = useState('');
    const [server, setServer] = useState('localhost:7379');
    const [jwt, setJwt] = useState('23skiddoo');
    const [members, setMembers] = useState([]);
    const [accounting, setAccounting] = useState({});
    const [runningCampaigns, setRunningCampaigns] = useState([])
    const [campaigns, setCampaigns] = useState([]);               // db campaigns
    const [rules, setRules] = useState([]);
    const [bidders, setBidders] = useState([]);
    const [targets, setTargets] = useState([]);
    const [creatives, setCreatives] = useState([]);

    const reset = () => {
      setLoggedIn(false);
      setServer('');
      setName('');
      setJwt('');
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

    const listCampaigns = async(name,password,server) => {
      if (server != undef)
        setServer(name);
      if (password != undef)
        setPassword(password);
      if (server != undef) 
        setServer(server);

      // get a token, if the tokken is valid, proceed

      var cmd = {
        token: jwt,
        type: "ListCampaigns#"
      };
      var data = await execute(cmd);

      if (data === undef)
        return;
      
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
      
      console.log("ListRules returns: " + JSON.stringify(data,null,2));
      setRules(data.rules);
      return data.campaigns;
    }

    const listTargets = async() => {
      // get a token, if the tokken is valid, proceed

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

    const getNewCreative = async () => {
      var cmd = {
        token: jwt,
        type: "SQLGetNewCreative#",
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

      console.log("SQLAddNewCampaign returns: " + JSON.stringify(result,null,2));
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
      var rc = data.replace(/{external}/g,"http://localhost:8080");
      console.log("DATA: " + rc);
      return rc;
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


    const  execute = async (cmd) =>  {
      try {
        var response = await axiosInstance.post("http://" + server + "/api",JSON.stringify(cmd), { responseType: 'text' }); 
        if (response.data && response.data.error) {
          alert("Error: " + response.data.message);
          return;
        }
        //console.log("------>" + JSON.stringify(response,null,2));
        return response.data;
      } catch (error) {
        alert(error);
      }
    }

    const getCount = (acc,id) => {
      if (acc[id] === undef)
        return 0;
      return acc[id];
    }

      ///////////////////////////

    return { 
      members, loggedIn, changeLoginState, listCampaigns, runningCampaigns, getBidders, bidders,
      getAccounting, accounting, getCount, getNewCampaign, getNewTarget, getNewRule, reset,
      getDbCampaigns, campaigns, getNewCreative, addNewCampaign, deleteCampaign, getDbCampaign,
      listRules, rules, addNewRule, getRule, deleteRule, addNewTarget, listTargets, targets, getTarget, deleteTarget,
      creatives, listCreatives, addNewCreative, getCreative, deleteCreative, findCreativeByName,
      forceUpdate, getReasons, macroSub
    };
};

export const useViewContext = createUseContext(ViewContext);