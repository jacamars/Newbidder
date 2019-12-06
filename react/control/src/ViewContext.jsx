import {useState} from 'react';
import createUseContext from "constate"; // State Context Object Creator
import { SampleBanner} from './views/simulator/Utils';

var undef;
var mapXhr;
const  ViewContext = () => {

    var xhrLog;

    const [loggedIn, setLoggedIn] = useState(false);
    const changeLoginState = (value) => {
      if (value && loggedIn)
        return;

      if (!value && !loggedIn)
        return;

      setLoggedIn(value);
      if (value) {
        
      } else {

      }
    }

    const [consoleLogspec, setConsoleLogspec] = useState('');
    const [logcount, setLogcount] = useState(1);

    const [bigChartData, setBigChartData] = useState('data1');
    const setBgChartData = (data) => {
        setBigChartData(data);
    }
    const [selectedHost, setSelectedHost] = useState('');

    const [zoomLevel, setZoomLevel] = useState(2.5);
    const [mapType, setMapType] = useState('');
    const [mapPositions, setMapPositions] = useState([]);
    const addMapPositions = (rows) => {
        for (var i = 0; i< rows.length; i++) {
            mapPositions.push(rows[i])
            if (mapPositions.length > 20) 
              mapPositions.shift();
        }
        setMapPositions(mapPositions); 
    }

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
            if (data.length > 200) {
                data.pop();
            }
        }
        setLogcount(logcount + rows.length)
        setLogdata(data);
        //console.log("BUFFERED DATA IS: " + JSON.stringify(data,null,2));
    }
    const clearLogdata = () => {
        setLogdata([]);
        setLogcount(1);
    }

    /////////////////////////////////////////

    const  loggerCallback = (spec,callback) => {
        setConsoleLogspec(spec);
        var previous_response_length = 0;
        if (xhrLog !== undef) {
          console.log("WARNING XHR for logger already defined");
          return;
        }
        xhrLog = new XMLHttpRequest()
        xhrLog.open("GET", "http://" + spec + "/subscribe?topic=logs", true);
        xhrLog.onreadystatechange = checkData;
        xhrLog.send(null);
        
        function checkData() {
          if (xhrLog.readyState === 3) {
            var response = xhrLog.responseText;
            var chunk = response.slice(previous_response_length);
            console.log("GOT SOME LOG CHUNK DATA: " + chunk);
            var i = chunk.indexOf("{");
            if (i < 0)
              return;
            if (chunk.trim().length === 0)
              return;
            chunk = chunk.substring(i);
            previous_response_length = response.length;
            
            console.log("GOT LOG DATA: " + chunk);
            var lines = chunk.split("\n");
            var rows = []
            for (var j = 0; j < lines.length; j++) {
              var line = lines[j];
              line = line.trim();
              if (line.length > 0) {
                console.log("CHECKING: " + line);
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

    const  mapperCallback = (spec,logname,) => {
        var previous_response_length = 0;
        if (mapXhr !== undef) {
          mapXhr.abort();
        }
        mapXhr = new XMLHttpRequest();
        mapXhr.open("GET", "http://" + spec + "/shortsub"+ "?topic=" + logname, true);
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
            addMapPositions(rows);
          }
        }
        ;
      }

      ///////////////////////////

    return { loggedIn, changeLoginState,
        bigChartData, setBgChartData, selectedHost, setSelectedHost, mapType, setMapType, mapperCallback,
        mapPositions, addMapPositions, zoomLevel, setZoomLevel, ssp, changeSsp, uri, changeUri,
        url, changeUrl, bidtype, changeBidtype, bidvalue, changeBidvalue, bidobject, bidresponse, changeBidresponse,
        nurl, changeNurl, xtime, changeXtime, adm, changeAdm, winsent, changeWinsent,
        consoleLogspec, logdata, addLogdata, clearLogdata, loggerCallback,
    };
};

export const useViewContext = createUseContext(ViewContext);