import React, { useState, useEffect } from 'react';
import {
    Badge,
    Button,
    ButtonGroup,
    ButtonToolbar,
    Card,
    CardHeader,
    CardBody,
    CardFooter,
    CardText,
  
    CardTitle,
    Form,
    FormGroup,
    Input,
    InputGroup,
    InputGroupAddon,
    InputGroupText,
    Table,
    Label,
    Row,
    Col
  } from "reactstrap";
  import WeeklyScheduler from 'react-week-scheduler';
  import 'react-week-scheduler/react-week-scheduler.css';

const  days = [];
days[0] = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
days[1] = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
days[2] = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
days[3] = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
days[4] = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
days[5] = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
days[6] = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];


const DayPartEditor = (props) => {

  useEffect(() => {

  }, []);


  const drawTable = () => {
    var items = []
    for (var i=0;i<24;i++) {
      var row = [];
      row.push(<tr>);
      for (var j=0;j<7;j++) {
        row.push(<th>{days[j][i]}</th>)
      }  
      row.push(</tr>)
      items.push(row);
    }
    return items;
  }

  return(
    <>
    <Row>
    <Col className="px-md-1" md="8">
    <Table bordered>
      <thead>
        <tr>
          <th>#</th>
          <th>Hour</th>
          <th>Monday</th>
          <th>Tuesday</th>
          <th>Wesdnesday</th>
          <th>Thursday</th>
          <th>Friday</th>
          <th>Saturday</th>
          <th>Sunday</th>
        </tr>
      </thead>
      <tbody>
        {drawTable()}
      </tbody>
      </Table>
    </Col>
   </Row>
   </>
  );
};

export default DayPartEditor;