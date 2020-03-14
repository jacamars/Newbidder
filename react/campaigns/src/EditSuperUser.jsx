import React , { useState, useEffect } from "react";
import {useViewContext } from "./ViewContext";

// reactstrap components
import {
  Button,
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
import ViewAssets from "./views/editors/ViewAssets";

var undef;

const EditSuperUser = () => {

  const vx = useViewContext();
  const [users, setUsers] = useState([]);
  const [affiliates, setAffiliates] = useState([]);
  const [count, setCount] = useState(1);
  const [show, setShow] = useState(false);
;

  const refresh = async () => {
    var list = await vx.listAffiliates();
    var us = await vx.listUsers();
    setAffiliates(list);

    setUsers(us);
    setShow(true);
  }

  const newCompany = () => {
    var company = {
        id: 0,
        customer_id: '',
        email: '',
        telephone: '',
        firstname: '',
        lastname: '',
        address: '',
        citystate: '',
        country: '',
        postalcode: '',
        description: '',
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
  }

  const deleteCompany = async (index) => {
    var c = affiliates[index];
  
    if (c.id === 0) {
      var af = affiliates;
      af.splice(index,1);
      setAffiliates(af);
    } else {
      await vx.deleteAffiliate(c.id);
      var u = await vx.listAffiliates();
      setAffiliates(u);
    }
    setCount(count+1);
  }

  const changeUserField = (e,i,f) => {
      users[i][f] = e.target.value;
      setUsers(users);
  }

  const changeCompanyField = (e,i,f) => {
    affiliates[i][f] = e.target.value;
    setAffiliates(affiliates);
  }

  const editUser = async(index) => {
    var u = users[index];
    await vx.addNewUser(u);
    setUsers(await vx.listUsers());
    setCount(count+1);
  }

  const deleteUser = async (index) => {
    var c = users[index];
  
    if (c.id === 0) {
      var af = users;
      af.splice(index,1);
      setUsers(af);
    } else {
      await vx.deleteUser(c.id);
      var u = await vx.listUsers();
      setUsers(u);
    }
    setCount(count+1);
  }

  const getCompanies = () => {
    var items = [];

    console.log("AFFILIATES: " + JSON.stringify(affiliates,null,2));
    affiliates.map((row, index) => {
        items.push(
            <tr key={'aff-' + index}>
            <td>{index}</td>
            <td key={'aff-name-' + index}><Input 
              onChange={(e) => changeCompanyField(e,index,'customer_id')}
              defaultValue={row.customer_id}/></td>
            <td key={'aff-email-' + index}><Input
              onChange={(e) => changeCompanyField(e,index,'email')}
              defaultValue={row.email}/></td>
            <td key={'aff-tel-' + index}><Input
              onChange={(e) => changeCompanyField(e,index,'telephone')}
              defaultValue={row.telephone}/></td>
            <td key={'aff-fn-' + index}><Input
              onChange={(e) => changeCompanyField(e,index,'firstname')}
              defaultValue={row.firstname}/></td>
            <td key={'aff-ln-' + index}><Input
              onChange={(e) => changeCompanyField(e,index,'lastname')}
              defaultValue={row.lastname}/></td>
            <td key={'aff-address-' + index}><Input
              onChange={(e) => changeCompanyField(e,index,'address')}
              defaultValue={row.address}/></td>
            <td key={'aff-cs-' + index}><Input
              onChange={(e) => changeCompanyField(e,index,'citystate')}
              defaultValue={row.citystate}/></td>
            <td key={'aff-pc-' + index}><Input
              onChange={(e) => changeCompanyField(e,index,'postal_code')}
              defaultValue={row.postalcode}/></td>
            <td className="text-center">
              <Button color="success" size="sm" onClick={()=>editCompany(index)}>Save</Button>
              &nbsp;
              <Button color="warning" size="sm" onClick={()=>deleteCompany(index)}>Delete</Button>
            </td>
          </tr>
        );
    });
    return items;
  }

  const getUsers = () => {
      var items = [];
      users.map((row, index) => {
        items.push(
            <tr key={'user-' + index}>
            <td>{index}</td>
            <td key={'u-name-' + index}>
                <Input 
                    type="text"
                    defaultValue={row.customer_id} 
                    onChange={(e) => changeUserField(e,index,'customer_id')}
                    disabled={vx.user.customer_id !== 'rtb4free'}/></td>
            <td key={'u-role' + index}>
                <Input 
                    defaultValue={row.sub_id}
                    onChange={(e) => changeUserField(e,index,'sub_id')}/></td>
             <td key={'u-uname-' + index}>
                <Input 
                    defaultValue={row.username}
                    onChange={(e) => changeUserField(e,index,'username')}/></td>
             <td key={'u-pwd-' + index}>
                <Input 
                    defaultValue={row.password}
                    onChange={(e) => changeUserField(e,index,'password')}/></td>
            <td className="text-center">
              <Button color="success" size="sm" onClick={()=>editUser(index)}>Save</Button>
              &nbsp;
              <Button color="warning" size="sm" onClick={()=>deleteUser(index)}>Delete</Button>
            </td>
          </tr>
        );
    });
    return items;
  }

  const doSave = () => {

  }

  return(
    <>
    <Row>
      <Col>
      <Button className="btn-fill" color="success" type="submit" onClick={refresh}>Administration</Button>
      </Col>
    </Row>
   { show && vx.user.customer_id === 'rtb4free' &&
    <Row>
        <Col md="8">
        <Card>
         <CardHeader>
           <h5 className="title">Edit Affiliates</h5>
         </CardHeader>
         <CardBody>
         <Button className="btn-fill" color="primary" type="submit" onClick={newCompany}>+Affiliates</Button>
            <Table key={"afn-table-"+count} size="sm">
                <thead>
                    <tr>
                        <th>#</th>
                        <th className="text-center">Name</th>
                        <th className="text-center">Email</th>
                        <th className="text-right">Telephone</th>
                        <th className="text-right">First Name</th>
                        <th className="text-right">Last Name</th>
                        <th className="text-right">Address</th>
                        <th className="text-right">City, State</th>
                        <th className="text-right">Postal Code</th>
                    </tr>
                </thead>
                <tbody>
                { getCompanies() }
                </tbody>
            </Table>
         </CardBody>
         <CardFooter>
         </CardFooter>
       </Card>
       </Col>
   </Row>
   }
    <Row>
      {show && 
      <Col md="8">
        <Card>
         <CardHeader>
           <h5 className="title">Edit Users</h5>
         </CardHeader>
         <CardBody>
            <Button className="btn-fill" color="primary" type="submit" onClick={newUser}>+User</Button>
            <Table key={"afn-table-"+count} size="sm">
                <thead>
                    <tr>
                        <th>#</th>
                        <th className="text-center">Customer Id</th>
                        <th className="text-center">Role</th>
                        <th className="text-center">Username</th>
                        <th className="text-center">Password</th>
                    </tr>
                </thead>
                <tbody>
                { getUsers() }
                </tbody>
            </Table>
         </CardBody>
         <CardFooter>
        
         </CardFooter>
       </Card>
       </Col>}
    </Row>
   </>
  );

}

export default EditSuperUser;
 