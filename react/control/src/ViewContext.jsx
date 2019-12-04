import {useState} from 'react';
import createUseContext from "constate"; // State Context Object Creator
import { isGenericTypeAnnotation } from '@babel/types';

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
    const [bidvalue, setBidvalue] = useState('');
    const [bidobject, setBidobject] = useState({});
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



    return { bigChartData, setBgChartData, selectedHost, setSelectedHost, mapType, setMapType, 
        mapPositions, addMapPositions, zoomLevel, setZoomLevel, ssp, changeSsp, uri, changeUri,
        url, changeUrl, bidtype, changeBidtype, bidvalue, changeBidvalue, bidobject
    };
};

export const useViewContext = createUseContext(ViewContext);