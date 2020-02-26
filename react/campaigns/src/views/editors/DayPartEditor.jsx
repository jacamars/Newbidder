import React, { useState, useEffect } from 'react';
import {
    Button,
    Table,
    Row,
    Col,
  } from "reactstrap";
  import 'react-week-scheduler/react-week-scheduler.css';

const  xdays = {
  monday:     [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
  tuesday:    [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
  wednesday:  [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
  thursday:   [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
  friday:     [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
  saturday:   [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
  sunday:     [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
};

var undef;

const DayPartEditor = (props) => {

  useEffect(() => {
  
  }, []);

  const initSchedule = (s) => {
    if (s === undef || s === null) {
      Object.keys(xdays).map((key) => {
        for (var i=0;i<24;i++) {
          xdays[key][i] = 0;
        }
      });
      return xdays;
    }
    return s;
  }

  const [days,setDays] = useState(initSchedule(props.daypart));
  const [mouseDown,setMouseDown] = useState(false);
  const [eventTime, setEventTime] = useState(0);
  const [timeTrack, setTimeTrack] = useState({});

  const clear = () => {
    setDays(initSchedule(undef))
    props.callback(days);
    props.redraw();
  }

  const drawTable = () => {
    //console.log(JSON.stringify(days,null,2));

    return(Object.keys(days).map((key,i) => (
      <tr key={"tr-"+key}>
        <th key={"th-"+key}
            scope="row" 
            onMouseDown={()=>toggleRow(key)}>
              {key}
        </th>
            {drawRow(key,i)}
      </tr>
    )));
  }

  // setDays(days);

  const drawRow = (key,index) => {
    return (days[key].map((value,i) => (
        <td key={"td-"+key+":"+i} 
            scope="row" 
            style={getColor(value)} 
            id={"td-"+key+":"+i}
            onMouseMove={() => mouseMove(key,i)}></td>
    )));
  }

  const toggleRow = (key) => {
    console.log("Toggle row: " + key);
    var k = 0;
    for (var i=0;i<24;i++) {
      k += days[key][i];
    }
    k = k > 0 ? 0 : 1;
    for (var i=0;i<24;i++) {
      days[key][i] = k;
    }
    setDays(days);
    props.callback(days);
  }

  const toggle = (key,hour) => {
    if (key === undef || hour === undef)
      return;

    var z = days;
    //console.log("TOGGLE VALUE: " + key + ", " + hour + " = " + z[key][hour]);
    if (z[key][hour] === 1)
      z[key][hour] = 0;
    else
      z[key][hour] = 1;

    //console.log("AFTER TOGGLE VALUE: " + key + ", " + hour + " = " + z[key][hour]);
    setDays(z);
    props.callback(z);
  }

  const getColor = (value) => {
    // console.log("GC VALUE: " + value);
    if (value === 0)
      return (null)
    else
      return(YELLOW);
  }

  const mouseMove = (key,hour) => {
    if (mouseDown) {
      var e = document.getElementById("td-"+key+":"+hour);
      if (e === undef || e === null)
        return;

      //console.log("day: " + key + ", hour: " + hour);
      var index = key+":"+hour
      if (timeTrack[index] === undef || timeTrack[index] !== eventTime) {
        e.style["background-color"]="pink";
        toggle(key,hour);
        timeTrack[index] = eventTime;
        setTimeTrack(timeTrack);
      }
    }
  }

  const handleMouse = (e,t) => {
      setMouseDown(t);
      setEventTime(eventTime+1);
  }

  const YELLOW = {
    backgroundColor: 'goldenrod'
  }


  return(
    <>
    <Row>
    <Col className="px-md-1" md="4">
      <h3>Dayparting (UTC)</h3>
    </Col>
    </Row>
    <Row>
    <Col className="px-md-1" md="1">
      <Button className="btn-fill" color="danger" size="sm" onClick={()=>clear()}>Clear</Button>
    </Col>
    <Col className="px-md-1" md="8">
    <div 
          onMouseEnter={mouseMove} 
          onMouseDown={(e) => handleMouse(e,true)}
          onMouseUp={(e) => handleMouse(e,false)}>
    <Table size="sm">
      <thead>
        <tr>
          <th>Day</th>
          <th>00</th>
          <th>01</th>
          <th>02</th>
          <th>03</th>
          <th>04</th>
          <th>05</th>
          <th>06</th>
          <th>07</th>
          <th>08</th>
          <th>09</th>
          <th>10</th>
          <th>11</th>
          <th>12</th>
          <th>13</th>
          <th>14</th>
          <th>15</th>
          <th>16</th>
          <th>17</th>
          <th>18</th>
          <th>19</th>
          <th>20</th>
          <th>21</th>
          <th>22</th>
          <th>23</th>
        </tr>
      </thead>
      <tbody>
        {drawTable()}
      </tbody>
      </Table>
      </div>
    </Col>
   </Row>
   </>
  );
};

export default DayPartEditor;