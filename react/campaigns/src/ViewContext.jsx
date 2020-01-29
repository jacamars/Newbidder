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
    const [server, setServer] = useState('localhost:8100');
    const [jwt, setJwt] = useState('23skiddoo');
    const [members, setMembers] = ([]);
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

      ///////////////////////////

    return { 
      members, loggedIn, changeLoginState, listCampaigns, runningCampaigns, getBidders, bidders
    };
};

export const useViewContext = createUseContext(ViewContext);