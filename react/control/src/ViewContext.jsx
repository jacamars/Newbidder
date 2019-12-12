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
    const [serverPrefix, setServerPrefix] = useState();
    const [members, setMembers] = useState([]);

    const test = () => {
      var time = new Date().getTime();
      var delta = (time - interval)/1000;
      if (delta > 30) {
        reset();
        return;
      }
      setTimeout(test,5000);
    }

    const reset = () => {
      clearLogdata();
      setLoggedIn(false);
    }

    const changeLoginState = async (value) => {
      if (value && loggedIn)
        return;

      if (!value && !loggedIn)
        return; 

      if (value) {
        setInterval(new Date().getTime());
        setTimeout(test,5000);
      } else {
        reset();
      }
        
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
        xhrLog.open("GET", "http://" + spec + "/subscribe?topic=logs", true);
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

    const  mapperCallback = (logname, server, callback) => {
        var previous_response_length = 0;
        if (mapXhr !== undef) {
          mapXhr.abort();
        }

        console.log("MAPPER SET TO " + serverPrefix);
        console.log("SERVER: " + server);
        mapXhr = new XMLHttpRequest();
        if (serverPrefix != undef)
           mapXhr.open("GET", "http://" + serverPrefix + "/shortsub"+ "?topic=" + logname, true);
        else
          mapXhr.open("GET", "http://" + server+ "/shortsub"+ "?topic=" + logname, true);
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

      const getMembers = async (prefix) => {
        if (prefix == undef)
          prefix = serverPrefix;
        try {
          var cmd = { command: 'getstatus' }
          const response = await axiosInstance.post("http://" + prefix + "/ajax",JSON.stringify(cmd)); 
          //console.log("Got Data Back: " + JSON.stringify(response.data,null,2));
          setServerPrefix(prefix);
          setUrl("http://" + prefix);
          setMembers(response.data);
          return  response.data;   
        } catch (e) {
          alert (e);
        }
      }

      const getEvents = () => {
        var rows = []
        for(var i=0; i<members.length;i++) {
          var m = members[i];
          for (var j=0;j<m.values.events.length;j++) {
            rows.push(m.values.events[j]);
          }
        }
        return rows;
      }

      ///////////////////////////

    return { loggedIn, changeLoginState,
        bigChartData, setBgChartData, selectedHost, setSelectedHost, mapType, setMapType, mapperCallback,
        zoomLevel, setZoomLevel, ssp, changeSsp, uri, changeUri,
        url, changeUrl, bidtype, changeBidtype, bidvalue, changeBidvalue, bidobject, bidresponse, changeBidresponse,
        nurl, changeNurl, xtime, changeXtime, adm, changeAdm, winsent, changeWinsent,
        consoleLogspec, logcount, logdata, addLogdata, clearLogdata, loggerCallback, getMembers, members,
        getEvents
    };
};

export const useViewContext = createUseContext(ViewContext);