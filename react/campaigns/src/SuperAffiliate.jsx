import React , { useState, useEffect } from "react";
import {useViewContext } from "./ViewContext";
import LoginModal from './LoginModal'
import ESUser from "./ESUser";

// reactstrap components
import {
  Alert,
  Button,
  ButtonGroup,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  CardText,
  FormGroup,
  Form,
  Input,
  Row,
  Table,
  Col
} from "reactstrap";
import { PayPalButton } from "react-paypal-button-v2";
import {uuidv4, customerIds, customerNames, protocolOptions} from "./Utils"
import DecisionModal from "./DecisionModal";
import ESAffiliate from "./views/ESAffiliate"

var undef;

const SuperAffiliate = (props) => {

  const vx = useViewContext();
  const [companyModal, setCompanyModal] = useState(false);
  const [newCompanyModal, setNewCompanyModal] = useState(false);
  const [record, setRecord] = useState(undef)
  const [users, setUsers] = useState([]);
  const [affiliates, setAffiliates] = useState([]);
  const [count, setCount] = useState(1);
  const [paypal, setPaypal] = useState(true);
  const [amount, setAmount] = useState(.02);
  const [paypalUser, setPaypallUser] = useState(-1);
  const [showPaypalwidget, setShowPaypalwidget] = useState(false);


  const refresh = async () => {
    var list = await vx.listAffiliates();
    var us = await vx.listUsers();
    setAffiliates(list);

    setUsers(us);

    console.log("---------------------\n"+JSON.stringify(us,null,2));
    setCount(count+1);
  }

  useEffect(() => {    
    refresh();
  },[]);


  const newCompany = (id) => {
    var company = {
        id: 0,
        customer_id: id,
        email: '',
        telephone: '',
        firstname: '',
        lastname: '',
        address: '',
        citystate: '',
        country: '',
        postalcode: '',
        description: '',
        budget:'0'
    };
    var af = affiliates;
    af.unshift(company);
    setAffiliates(af);
    setCount(count+1);
  }

  const makeNewCompany = () => {
    setNewCompanyModal(true);
    setCount(count+1);
  }

  const editCompany = async(index) => {
    var u = affiliates[index];
    await vx.addNewAffiliate(u);
    setAffiliates(await vx.listAffiliates());
    setCount(count+1);

    alert("Affiliate: " + u.customer_id + " saved");
  }

  const updateTheBudgetFor = async() => {
    var u = affiliates[paypalUser];
    u.budget =  Number(u.budget) + Number(amount);
    await vx.addNewAffiliate(u);
    setAffiliates(await vx.listAffiliates());
    setCount(count+1);

    alert("Budget: " + u.customer_id + " updated");
  }

  const deleteCompany = async (index) => {
    var c = affiliates[index];
    c.index = index;
  
    setRecord(c);
    setCompanyModal(true);
  }

  const changeCompanyField = (e,i,f) => {
    affiliates[i][f] = e.target.value;
    setAffiliates(affiliates);
  }

  const addBudget = async (index) => {
    var c = affiliates[index];
    setPaypallUser(index);
    setAmount(100.0);
    setPaypal(true);
  }

  const setDirectBudget = async(index, amount) => {
    alert("BUDGET: " + index + ", "  + amount);
    var u = affiliates[index];
    u.budget =  Number(amount);
    await vx.addNewAffiliate(u);
    setAffiliates(await vx.listAffiliates());
    alert("Budget: " + u.customer_id + " updated");
    setCount(count+1);
  }

  const setInstances = async() => {
    var list = await vx.listAffiliates();
    var us = await vx.listUsers();
    setAffiliates(list);

    setUsers(us);
  }

  const handleBudget = () => {
    if (vx.user.customer_id === 'rtb4free') {
      updateTheBudgetFor(paypalUser);
      setPaypal(false);
      setPaypallUser(-1)
      setAmount(0);
    } else {
      setShowPaypalwidget(true);
    }
  }

  const cancelPaypal = () => {
    setPaypallUser(-1);
    setPaypal(false);
    setShowPaypalwidget(false);
  }

  const companyModalCallback = async(doit) => {
    if (doit) {
      var c = record;
      if (c.id !== 0) 
        vx.deleteAffiliate(c.id);
    }
    setCompanyModal(false);
    props.redrawParent();
  }

  const newCompanyModalCallback = async(doit,id) => {
    setNewCompanyModal(false)
    if (doit) {
      if (id === '') {
        alert("Empty customer id not allowed, redo");
        setNewCompanyModal(true);
        return;
      }
      for (var i=0;i<affiliates.length;i++) {
        if (affiliates[i].customer_id === id) {
          alert("This customer id already exists, redo");
          setNewCompanyModal(true);
          return;
        }
      }
      newCompany(id);
    }
  }

  return(
    <>
    { newCompanyModal &&
          <DecisionModal title={"New Company ID"}
                         message="Input a new Company Id" 
                         name="SAVE"
                         input={true}
                         callback={newCompanyModalCallback} />}
   { companyModal &&
          <DecisionModal title={"Really delete Company: " + record.customer_name}
                         message="You can't undo this if you delete it!!!" 
                         name="DELETE"
                         callback={companyModalCallback} />}

   { vx.loggedIn && vx.user.sub_id === 'superuser' &&
    <Row>
      <Col md="12">
        { vx.user.customer_id === 'rtb4free' && <>
          <Button className="btn-fill" color="primary" type="submit" onClick={makeNewCompany}>+Company</Button>
          {' '}
          </>
        }
        <Button color="warning" size="sm" onClick={()=>refresh()}>Revert</Button>


        { paypal && vx.user.customer_id !== 'rtb4free' && <>
        <Alert color="success">
          <Row>
          <Col className="px-md-1" md="1">
            <FormGroup>
              <label>Add Amount</label>
              <Input
                  defaultValue={amount}
                  placeholder="Budget"
                  onChange={(e)=>{setAmount(e.target.value)}}
                  type="text"
                  disabled={showPaypalwidget}
              />
            </FormGroup>
          </Col>
          <Col className="px-md-1" md="2">
            <ButtonGroup>
            <Button className="btn-fill" color="success" type="submit" onClick={handleBudget}>Add Funds</Button>
            <Button className="btn-fill" color="warn" type="submit" onClick={cancelPaypal}>Cancel</Button>
            </ButtonGroup>
          </Col>
          <Col  className="px-md-1" md="1">
            {' '}
          </Col>
          { showPaypalwidget &&
          <Col  className="px-md-1" md="4">
            <PayPalButton
            amount= {amount.toString()}
            shippingPreference="NO_SHIPPING" // default is "GET_FROM_FILE"
            onSuccess={(details, data) => {
              setPaypal(false);
              updateTheBudgetFor(paypalUser);
              // alert("Transaction completed by index: " + paypalUser + ", " + details.payer.name.given_name + " Amount: " + amount);
              
              // OPTIONAL: Call your server to save the transaction
              return fetch("/paypal-transaction-complete", {
                method: "post",
                body: JSON.stringify({
                  orderId: data.orderID
                })
              });
            }}
            />
          </Col>}
          </Row>
          </Alert>
          </>
       }

        <ESAffiliate key={"affiliate-"+count}
          changeCompanyField={changeCompanyField}
          editAffiliate={editCompany}
          deleteAffiliate={deleteCompany}
          setDirectBudget={setDirectBudget}
          setPaypalBudget={addBudget}
          affiliates={affiliates}
        />
      </Col>
   </Row>}
   }
  </>
  );

}

export default SuperAffiliate;
 