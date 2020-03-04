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

const SiteOrAppEditor = (props) => {
  useEffect(() => {
    if (props.value === undef)
      setRSelected('');
    else
      setRSelected(props.value);
  }, [props.value]);

  const [rSelected, setRSelected] = useState('');

  const setSelection = (r) => {
    setRSelected(r);
    props.change(r);
  }

  return(
    <Row>
    <Col className="px-md-1" md="6">
      <ButtonGroup>
        <label>App/Site:</label>
        <Button color="primary" onClick={() => setSelection("app")} active={rSelected === "app"}>App</Button>
        <Button color="primary" onClick={() => setSelection("site")} active={rSelected === "site"}>Site</Button>
        <Button color="primary" onClick={() => setSelection('')} active={rSelected === ''}>Both</Button>
      </ButtonGroup>
    </Col>
</Row>
  );
};

export default SiteOrAppEditor;