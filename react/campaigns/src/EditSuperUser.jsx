import React , { useState, useEffect } from "react";
import {useViewContext } from "./ViewContext";
import LoginModal from './LoginModal'

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

const EditSuperUser = () => {

  const vx = useViewContext();
  const [userModal, setUserModal] = useState(false);
  const [companyModal, setCompanyModal] = useState(false);
  const [record, setRecord] = useState(undef)
  const [users, setUsers] = useState([]);
  const [affiliates, setAffiliates] = useState([]);
  const [count, setCount] = useState(1);
  const [paypal, setPaypal] = useState(false);
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


  const newCompany = () => {
    var company = {
        id: 0,
        customer_id: uuidv4(),
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

    affiliates.push(company);
    setAffiliates(affiliates);
    setCount(count+1);
  }

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

      users.push(u);
      setUsers(users);
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

  const deleteUser = async (id) => {
    var c = getUserById(id);
    setRecord(c);
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

  const getCompanies = () => {
    var items = [];
    affiliates.map((row, index) => {
      items.push(
      <Alert color="success" key={"companies-"+index}>
      <Row>
      <Col md="12">
        <Card>
          <CardHeader>
            <Row>
              <Col className="pr-md-1" md="2">
                <FormGroup>
                  <label>Company Name</label>
                    <Input
                      onChange={(e) => changeCompanyField(e,index,'customer_name')}
                      defaultValue={row.customer_name}/>
                </FormGroup>
              </Col>
              <Col className="pr-md-1" md="2">
                <FormGroup>
                  <label>ID (Disabled)</label>
                  <Input
                      disabled={true}
                      defaultValue={row.customer_id}/>
                </FormGroup>
              </Col>
              <Col className="pr-md-1" md="2">
                <FormGroup>
                  <label>Budget</label>
                  <Input
                    disabled={true}
                    defaultValue={row.budget}/>
                </FormGroup>
              </Col>
            </Row>
          </CardHeader>
          <CardBody>
          <Form>
            <Row>
              <Col className="pr-md-1" md="2">
                <FormGroup>
                  <label>E-Mail</label>
                  <Input
                    onChange={(e) => changeCompanyField(e,index,'email')}
                    defaultValue={row.email}/>
                </FormGroup>
              </Col>
              <Col className="pr-md-1" md="2">
                <FormGroup>
                  <label>Telephone</label>
                  <Input
                    onChange={(e) => changeCompanyField(e,index,'telephone')}
                    defaultValue={row.telephone}/>
                </FormGroup>
              </Col>
              <Col className="pr-ms-1" md="2">
                <FormGroup>
                  <label>First name</label>
                  <Input
                    onChange={(e) => changeCompanyField(e,index,'firstname')}
                    defaultValue={row.firstname}/>
                </FormGroup>
              </Col>
              <Col className="pr-ms-1" md="2">
                <FormGroup>
                  <label>Last name</label>
                  <Input
                    onChange={(e) => changeCompanyField(e,index,'lastname')}
                    defaultValue={row.lastname}/>
                </FormGroup>
              </Col>
            </Row>
            <Row>
              <Col className="pr-md-1" md="2">
                <FormGroup>
                  <label>Address</label>
                  <Input
                    onChange={(e) => changeCompanyField(e,index,'address')}
                    defaultValue={row.address}/>
                </FormGroup>
              </Col>
              <Col className="pr-md-1" md="2">
                <FormGroup>
                  <label>City, State</label>
                  <Input
                    onChange={(e) => changeCompanyField(e,index,'citystate')}
                    defaultValue={row.citystate}/>
                </FormGroup>
              </Col>
              <Col className="pr-md-1" md="2">
                <FormGroup>
                  <label>Postal Code</label>
                  <Input
                    onChange={(e) => changeCompanyField(e,index,'postal_code')}
                    defaultValue={row.postalcode}/>
                </FormGroup>
              </Col>
            </Row>
            <Row>
              <Button color="success" size="sm" onClick={()=>editCompany(index)}>Save</Button>
              &nbsp;
              <Button color="warning" size="sm" onClick={()=>deleteCompany(index)}>Delete</Button>
              &nbsp;
              <Button color="info" size="sm" onClick={()=>addBudget(index)}>Budget</Button>
            </Row>
          </Form>
          </CardBody>
        </Card>
        </Col>
        </Row>
        </Alert>
      );
    });
    return items;
  }

  
  const getUsers = () => {
    var items = [];
      users.map((row, index) => {
        items.push(
        <Alert color="info" key={"users-"+index}>
          <Row>
            <Col md="12">
              <Card>
                <CardHeader>
                  <Row>
                    <Input 
                      disabled={vx.user.customer_id != 'rtb4free'}
                      onChange={(e) => changeUserField(e,index,'customer_id')}
                      type="select">{customerNames(affiliates,row)}</Input>
                  </Row>
                </CardHeader>
                <CardBody>
                  <Row>
                    <Col className="pr-md-1" md="2">
                      <FormGroup>
                      <label>Username</label>
                      <Input 
                        defaultValue={row.username}
                        onChange={(e) => changeUserField(e,index,'username')}/>
                      </FormGroup>
                    </Col>
                    <Col className="pr-md-1" md="2">
                      <FormGroup>
                      <label>Role</label>
                      <Input 
                        defaultValue={row.sub_id}
                        onChange={(e) => changeUserField(e,index,'sub_id')}/>
                      </FormGroup>
                  </Col>
                  <Col className="pr-md-1" md="2">
                      <FormGroup>
                      <label>Password</label>
                      <Input 
                        defaultValue={row.password}
                        onChange={(e) => changeUserField(e,index,'password')}/>
                      </FormGroup>
                  </Col>
                </Row>
                <Row>
                  <Button color="success" size="sm" onClick={()=>editUser(row.id)}>Save</Button>
                  &nbsp;
                  <Button color="warning" size="sm" onClick={()=>deleteUser(row.id)}>Delete</Button>
                </Row>
                </CardBody>
              </Card>
            </Col>
          </Row>
        </Alert>
      );
    });
    return items;
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
      var af = users;
      if (c.id === 0) {
        af.splice(c.index,1);
        setUsers(af);
      } else
        await vx.deleteUser(c.id);
    }
    setUserModal(false);
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
   { companyModal &&
      <DecisionModal title={"Really delete Company: " + record.customer_name}
                     message="You can't undo this if you delete it!!!" 
                     name="DELETE"
                     callback={companyModalCallback} />}
   { userModal &&
      <DecisionModal title={"Really delete User: " + record.username}
                     message="You can't undo this if you delete it!!!" 
                     name="DELETE"
                     callback={userModalCallback} />}

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

   { vx.loggedIn && vx.user.sub_id === 'superuser' &&
    <Row>
        <Col md="12">
        <Card>
         <CardHeader>
           <h5 className="title">Edit Affiliates</h5>
         </CardHeader>
         <CardBody>
         <Button className="btn-fill" color="primary" type="submit" onClick={newCompany}>+Affiliates</Button>
         &nbsp;
        <Button color="warning" size="sm" onClick={()=>refresh()}>Revert</Button>
          { getCompanies() }
         </CardBody>
         <CardFooter>
         </CardFooter>
       </Card>
       </Col>
   </Row>
   }
    {vx.loggedIn && 
    <Row>
      <Col md="12">
        <Card>
         <CardHeader>
           <h5 className="title">Edit Users</h5>
         </CardHeader>
         <CardBody>
            <Button className="btn-fill" color="primary" type="submit" onClick={newUser}>+User</Button>
            &nbsp;
            <Button color="warning" size="sm" onClick={()=>refresh()}>Revert</Button>
         
            { getUsers() }

         </CardBody>
         <CardFooter>
        
         </CardFooter>
       </Card>
       </Col>
    </Row>}
   </>
  );

}

export default EditSuperUser;
 