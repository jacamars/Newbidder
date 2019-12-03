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


    return { bigChartData, setBgChartData, selectedHost, setSelectedHost, mapType, setMapType, 
        mapPositions, addMapPositions, zoomLevel, setZoomLevel };
};

export const useViewContext = createUseContext(ViewContext);