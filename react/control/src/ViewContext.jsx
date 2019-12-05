import {useState} from 'react';
import createUseContext from "constate"; // State Context Object Creator
import { SampleBanner} from './views/simulator/Utils';

const  ViewContext = () => {

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

    return { bigChartData, setBgChartData, selectedHost, setSelectedHost, mapType, setMapType, 
        mapPositions, addMapPositions, zoomLevel, setZoomLevel, ssp, changeSsp, uri, changeUri,
        url, changeUrl, bidtype, changeBidtype, bidvalue, changeBidvalue, bidobject, bidresponse, changeBidresponse,
        nurl, changeNurl, xtime, changeXtime, adm, changeAdm, winsent, changeWinsent
    };
};

export const useViewContext = createUseContext(ViewContext);