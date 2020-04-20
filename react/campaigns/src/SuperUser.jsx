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
import ViewAssets from "./views/editors/ViewAssets";
import {uuidv4, customerIds, customerNames} from "./Utils"
import DecisionModal from "./DecisionModal";

var undef;

const SuperUser = (props) => {

  const vx = useViewContext();
  const [userModal, setUserModal] = useState(false);
  const [companyModal, setCompanyModal] = useState(false);
  const [record, setRecord] = useState(undef)
  const [users, setUsers] = useState([]);
  const [affiliates, setAffiliates] = useState([]);
  const [paypal, setPaypal] = useState(false);
  const [amount, setAmount] = useState(.02);
  const [paypalUser, setPaypallUser] = useState(-1);
  const [showPaypalwidget, setShowPaypalwidget] = useState(false);
  const [count, setCount] = useState(1);


  const refresh = async () => {
    var list = await vx.listAffiliates();
    var us = await vx.listUsers();
    setAffiliates(list);
    setUsers(us);
    console.log("---------------------\n"+JSON.stringify(us,null,2));
  }

  useEffect(() => {    
    refresh();
  },[]);

  const newUser = () => {
      var u = {
        id: 0,
        customer_id: '',
        sub_id: 'user',
        username: '',
        password: '',
        company: '',
        email: '',
	      telephone: '',
	      firstname: ' ',
	      lastname: '',
	      address: '',
	      citystate: '',
	      country: '',
	      postalcode: '',
	      about: '',
	      picture: '',
	      description: '',
        title: '',
      };

      if (vx.user.customer_id !== 'rtb4free')
        u.customer_id = vx.user.customer_id;

      users.unshift(u);
      setUsers(users);
      setCount(count+1);
  }

  const editCompany = async(index) => {
    var u = affiliates[index];
    await vx.addNewAffiliate(u);
    setAffiliates(await vx.listAffiliates());

    alert("Affiliate: " + u.customer_id + " saved");
  }

  const updateTheBudgetFor = async() => {
    var u = affiliates[paypalUser];
    u.budget =  Number(u.budget) + Number(amount);
    await vx.addNewAffiliate(u);
    setAffiliates(await vx.listAffiliates());

    alert("Budget: " + u.customer_id + " updated");
  }

  const deleteCompany = async (index) => {
    var c = affiliates[index];
    c.index = index;
  
    setRecord(c);
    setCompanyModal(true);
  }

  const changeUserField = (e,i,f) => {
      users[i][f] = e.target.value;
      setUsers(users);
  }

  const changeCompanyField = (e,i,f) => {
    affiliates[i][f] = e.target.value;
    setAffiliates(affiliates);
  }

  const editUser = async(id) => {
    var u = getUserById(id);
    await vx.addNewUser(u);
    alert("User: " + u.username + " saved");
    props.redrawParent();
  }

  const deleteUser = async (id) => {
    var c = getUserById(id);
    setRecord(c);
  /*  if (id === 0) {
      var us = await vx.listUsers();
      setUsers(us); 
      setCount(count+1);
      return;
    } */
    console.log("DELETE: " + c.username);
    setUserModal(true);
  }

  const addBudget = async (index) => {
    var c = affiliates[index];
    setPaypallUser(index);
    setAmount(100.0);
    setPaypal(true);
  }

  const setInstances = async() => {
    var list = await vx.listAffiliates();
    var us = await vx.listUsers();
    setAffiliates(list);

    setUsers(us);
  }

  const getUserById = (id) => {
    var c;
    for (var i=0;i<users.length;i++) {
      if (users[i].id === id) {
        var c = users[i];
        c.index = i;
        return c;
      }
    }
    return undef;
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

  const userModalCallback = async (doit) => {
    if (doit) {
      var c = record;
      vx.deleteUser(c.id);
      //var us = await vx.listUsers();
      //setUsers(users); 
    }
    setUserModal(false);
    setRecord(null);
    props.redrawParent();
  }

  const companyModalCallback = async(doit) => {
    if (doit) {
      var c = record;
      if (c.id === 0) {
        var af = affiliates;
        af.splice(c.index,1);
        setAffiliates(af);
      } else {
        await vx.deleteAffiliate(c.id);
        var u = await vx.listAffiliates();
        setAffiliates(u);
      }
    }
    setCompanyModal(false);
  }

  return(
    <>

   { !vx.isLoggedIn && <LoginModal callback={setInstances} />}

   { paypal && <>
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

   { userModal &&
            <DecisionModal title={"Really delete User: " + record.username}
                     message="You can't undo this if you delete it!!!" 
                     name="DELETE"
                     callback={userModalCallback} />
   } 

   { vx.loggedIn && vx.user.sub_id === 'superuser' &&

    <Row>
      <Col md="12">

            <Button className="btn-fill" color="primary" type="submit" onClick={newUser}>+User</Button>
            &nbsp;
            <Button color="warning" size="sm" onClick={()=>refresh()}>Revert</Button>
         
            <ESUser key={"user-"+count}
              users={users} 
              affiliates={affiliates}
              changeUserField={changeUserField}
              editUser={editUser}
              deleteUser={deleteUser}
            />
       </Col>
    </Row>}
   </>
  );

}

export default SuperUser;
 