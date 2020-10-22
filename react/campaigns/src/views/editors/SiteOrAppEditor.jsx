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

  const check = () => {
    return (rSelected === undef || rSelected === '' || rSelected === 'undefined');
  }

  const colorize = (x) => {
    if (x)
      return "primary";
    return "secondary";
  }

  return(
    <Row>
    <Col className="px-md-1" md="6">
      <ButtonGroup>
        <label>App/Site:</label>
        <Button color={colorize(rSelected === "app")} onClick={() => setSelection("app")} active={rSelected === "app"}>App</Button>
        <Button color={colorize(rSelected === "site")} onClick={() => setSelection("site")} active={rSelected === "site"}>Site</Button>
        <Button color={colorize(check())} onClick={() => setSelection('')} active={check()}>Both</Button>
      </ButtonGroup>
    </Col>
</Row>
  );
};

export default SiteOrAppEditor;