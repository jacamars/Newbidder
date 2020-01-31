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

    const [jwt,setJwt] = useState('23skiddoo');
    const [loggedIn, setLoggedIn] = useState(false);
    const [server, setServer] = useState('localhost:7379');
    const [password, setPassword] = useState('');
    const [runningCampaigns, setRunningCampaigns] = useState([])
    const [members, setMembers] = useState([]);
    const [accounting, setAccounting] = useState({});

    const changeLoginState = async (value) => {
      if (value && loggedIn)
        return;

      if (!value && !loggedIn)
        return; 

      await setLoggedIn(value);
      return loggedIn;
    }

    const [consoleLogspec, setConsoleLogspec] = useState('');
    const [logcount, setLogcount] = useState(0);

    const [bigChartData, setBigChartData] = useState('data1');
    const setBgChartData = (data) => {
        setBigChartData(data);
    }
    const [selectedHost, setSelectedHost] = useState('');

    const [zoomLevel, setZoomLevel] = useState(2.5);
    const [mapType, setMapType] = useState('');
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

    const [logdata, setLogdata] = useState([]);
    
    const addLogdata = (rows) => {
        var data = logdata;
        for (var i=0; i<rows.length; i++) {
            rows[i].time = new Date().toLocaleString();
            data.unshift(rows[i]);
            if (data.length > 1000) {
                data.pop();
            }
        }
        setLogdata(data);
        setLogcount(data.length);
        //console.log("BUFFERED DATA IS: " + JSON.stringify(data,null,2));
    }
    const clearLogdata = () => {
        setLogdata([]);
        setLogcount(0);
    }

    /////////////////////////////////////////

    const  loggerCallback = (spec) => {
        setConsoleLogspec(spec);
        var previous_response_length = 0;
        if (xhrLog !== undef) {
          console.log("WARNING XHR for logger already defined");
          xhrLog.abort();
        }
        interval = new Date().getTime();
        xhrLog = new XMLHttpRequest()
        xhrLog.open("GET", "http://" + server + "/subscribe?topic=logs", true);
        xhrLog.onreadystatechange = checkData;
        xhrLog.send(null);
        
        function checkData() {
          if (xhrLog.readyState === 3) {
            var response = xhrLog.responseText;
            interval = new Date().getTime();
            var chunk = response.slice(previous_response_length);
            //console.log("GOT SOME LOG CHUNK DATA: " + chunk);
            var i = chunk.indexOf("{");
            if (i < 0)
              return;
            if (chunk.trim().length === 0)
              return;
            chunk = chunk.substring(i);
            previous_response_length = response.length;
            
            //console.log("GOT LOG DATA: " + chunk);
            var lines = chunk.split("\n");
            var rows = []
            for (var j = 0; j < lines.length; j++) {
              var line = lines[j];
              line = line.trim();
              if (line.length > 0) {
                //console.log("CHECKING: " + line);
                try {
                    var y = JSON.parse(line)
                    rows.push(y);
                } catch (e) {
                    console.log("Error parsing: " + line);
                }
              }
            }
            addLogdata(rows)
          }
        }
        ;
      }

    /////////////////////////////////////////

    const  mapperCallback = (logname, callback) => {
        var previous_response_length = 0;
        if (mapXhr !== undef) {
          mapXhr.abort();
        }

        console.log("MAPPER SET TO " + server);
        console.log("SERVER: " + server);
        mapXhr = new XMLHttpRequest();
        mapXhr.open("GET", "http://" + server + "/shortsub"+ "?topic=" + logname, true);
        mapXhr.onreadystatechange = checkData;
        mapXhr.send(null);
        
        function checkData() {
          if (mapXhr.readyState == 3) {
            var response = mapXhr.responseText;
            var chunk = response.slice(previous_response_length);
            console.log("GOT SOME CHUNK DATA: " + chunk);
            var i = chunk.indexOf("{");
            if (i < 0)
              return;
            if (chunk.trim().length === 0)
              return;
            chunk = chunk.substring(i);
            previous_response_length = response.length;
            
            console.log("GOT DATA: " + chunk);
            var lines = chunk.split("\n");
            var rows = []
            for (var j = 0; j < lines.length; j++) {
              var line = lines[j];
              line = line.trim();
              if (line.length > 0) {
                var y = JSON.parse(line);
                rows.push(y);
              }
            }
            if (rows.length == 0)
              return;
            callback(rows);
          }
        };
      }

      const getBidders = async() => {
        var cmd = {
          token: jwt,
          type:"GetBiddersStatus#"
        };
        var data = await execute(cmd);
        if (data == undef)
          return;
        setMembers(data.entries);
        return data.entries;
      }

      // acts as the login
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
    
      const getAccounting = async() => {
        var cmd = {
          token: jwt,
          type: "GetAccounting#"
        };
        var data = await execute(cmd);
  
        console.log("GetAccounting returns: " + JSON.stringify(data,null,2));
        if (data == undef)
          return;
        setAccounting(data.accounting);
        return data.accounting;
      }

      const  execute = async (cmd) =>  {
        try {
          var response = await axiosInstance.post("http://" + server + "/api",JSON.stringify(cmd), { responseType: 'text' }); 
          if (response.data && response.data.error) {
            alert(response.data.message);
            return;
          }
          // console.log("------>" + JSON.stringify(response,null,2));
          return response.data;
        } catch (error) {
          alert(error);
        }
      }

      const getEvents = () => {
        var rows = []
        for(var i=0; i<members.length;i++) {
          var m = members[i];
          for (var j=0;j<m.events.length;j++) {
            rows.push(m.events[j]);
          }
        }
        return rows;
      }

      const getCount = (acc,id) => {
        if (acc[id] === undef)
          return 0;
        return acc[id];
      }

      ///////////////////////////

    return { loggedIn, changeLoginState,
        bigChartData, setBgChartData, selectedHost, setSelectedHost, mapType, setMapType, mapperCallback,
        zoomLevel, setZoomLevel, ssp, changeSsp, uri, changeUri,
        url, changeUrl, bidtype, changeBidtype, bidvalue, changeBidvalue, bidobject, bidresponse, changeBidresponse,
        nurl, changeNurl, xtime, changeXtime, adm, changeAdm, winsent, changeWinsent,
        consoleLogspec, logcount, logdata, addLogdata, clearLogdata, loggerCallback, getBidders, members,
        getEvents, accounting, getAccounting, listCampaigns, getCount
    };
};

export const useViewContext = createUseContext(ViewContext);