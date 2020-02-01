import {useState} from 'react';
import createUseContext from "constate"; // State Context Object Creator
import http from 'http';
import axios from 'axios';
import { SampleBanner} from './views/simulator/Utils';
import { resetWarningCache } from 'prop-types';

var undef;
var mapXhr;
var xhrLog;
var interval = 0;

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
    const [bidders, setBidders] = useState([]);

    const reset = () => {
      setLoggedIn(false);
    }

    const changeLoginState = async (value) => {
      if (value && loggedIn)
        return;

      if (!value && !loggedIn)
        return; 
        
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

      if (data == undef)
        return;
      setRunningCampaigns(data.campaigns);
      return data.campaigns;
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

      console.log("GetAccounting returns: " + JSON.stringify(data,null,2));
      if (data === undef)
        return;
      setAccounting(data.accounting);
      return data.accounting;
    }

    const getNewCampaign = async(name) => {
      var cmd = {
        token: jwt,
        type: "SQLGetNewCampaign#",
        campaign: name
      };
      var result = await execute(cmd);

      console.log("SQLGetNewCampaign returns: " + JSON.stringify(result,null,2));
      if (result === undef)
        return;
      return result.data;
    }


    const  execute = async (cmd) =>  {
      try {
        var response = await axiosInstance.post("http://" + server + "/api",JSON.stringify(cmd), { responseType: 'text' }); 
        if (response.data && response.data.error) {
          alert(response.data.error);
          return;
        }
        // console.log("------>" + JSON.stringify(response,null,2));
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
      getAccounting, accounting, getCount, getNewCampaign
    };
};

export const useViewContext = createUseContext(ViewContext);