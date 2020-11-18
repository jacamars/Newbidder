import React, { useState, useEffect } from "react";

// reactstrap components
import {
  Button,
  ButtonGroup,

  FormGroup,
  Input,
  Row,
  Col
} from "reactstrap";


var undef;

const DealEditor = (props) => {

  const [rSelected, setRSelected] = useState(props.creative.dealType);

  const setDealSelection = (r) => {
    setRSelected(r);
    if (r !== 1) {
      if (props.creative.deals === undef || props.creative.deals.length === 0)
        makeNewDeal();
    }
    props.selector(r);
  }

  const removeDeal = (i) => {
    alert("REMOVE: " + i);
    var deals = props.creative.deals;
    deals.splice(i,1);
    props.setdeals(deals);
  }

  const makeNewDeal = () => {
    var deals = props.creative.deals;
    if (deals === undef)
      deals = [];

    deals.push(
      {
        id: '',
        price: .1
      }
    );
    props.setdeals(deals);
  }

  const colorize = (x) => {
    if (x)
      return "primary";
    return "secondary";
  }

  // Forground color for delete
  const RED = {
    color: 'red'
  }

  const getDealsView = () => {
    if (rSelected == 1)
      return (null);

    if (props.creative.deals === undef)
      makeNewDeal();
    //console.log("GetDealView, rows = " + props.creative.deals.length);

   return(
      props.creative.deals.map((deal, index) => (
        <Row>

          <Col className="px-md-1" md="2">
            <FormGroup>
              <label>Deal ID</label>
              <Input
                id={"deal-id-"+index}
                onChange={ (e) => props.changeDeal(index)}
                placeholder={"Deal#" + index + " id"}
                defaultValue={deal.id}
                type="text"
              />
            </FormGroup>
          </Col>
          <Col className="px-md-1" md="2">
            <FormGroup>
              <label>Price ECPM</label>
              <Input
                id={"deal-price-"+index}
                onChange={ (e) => props.changeDeal(index)}
                placeholder={"Deal#" + index + " price"}
                defaultValue={deal.price}
                type="number"
              />
            </FormGroup>
          </Col>
          <Col className="px-md-1" md="1">
            <br/>
            <ButtonGroup>
             <Button close onClick={()=>removeDeal(index)}  ><span aria-hidden="true" style={RED}>–</span></Button>
            </ButtonGroup>
          </Col>
        </Row>))
    ); 
  }

  return(
    <>
    <Col className="px-md-1" md="6">
    <ButtonGroup>
      <label>Deals:&nbsp; &nbsp; &nbsp; </label>
      <Button color={colorize(rSelected === 1)} onClick={() => setDealSelection(1)} active={rSelected === 1}>No Deal</Button>
      <Button color={colorize(rSelected === 2)} onClick={() => setDealSelection(2)} active={rSelected === 2}>Private Only</Button>
      <Button color={colorize(rSelected === 3)} onClick={() => setDealSelection(3)} active={rSelected === 3}>Private Preferred</Button>
      {rSelected > 1 && <>
        &nbsp; &nbsp; <Button color="primary" size="sm" onClick={makeNewDeal}>+</Button>
      </>}
    </ButtonGroup>
    </Col>
    {getDealsView()}
    </>
  );
};

export default DealEditor;