/*!

=========================================================
* Black Dashboard React v1.0.0
=========================================================

* Product Page: https://www.creative-tim.com/product/black-dashboard-react
* Copyright 2019 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://github.com/creativetimofficial/black-dashboard-react/blob/master/LICENSE.md)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

*/
import React, {useState, useEffect} from "react";
// nodejs library that concatenates classes
import classNames from "classnames";

// reactstrap components
import {
  Button,
  Collapse,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
  UncontrolledDropdown,
  Input,
  InputGroup,
  NavbarBrand,
  Navbar,
  NavLink,
  Nav,
  Container,
  Modal,
} from "reactstrap";
import {useViewContext } from "../../ViewContext";

const AdminNavbar = (props) => {

  const vx = useViewContext();

  const [modalState, setModalState] = useState({
      collapseOpen: false,
      modalSearch: false,
      color: "navbar-transparent"
    });

  useEffect(() => {
    window.addEventListener("resize", updateColor);
  }, [modalState]);

  useEffect(() => {
    window.removeEventListener("resize", updateColor);
  }, []);

  // function that adds color white/transparent to the navbar on resize (this is for the collapse)
  const updateColor = () => {
    if (window.innerWidth < 993 && modalState.collapseOpen) {
      setModalState({
        color: "bg-white"
      });
    } else {
      setModalState({
        color: "navbar-transparent"
      });
    }
  };
  // this function opens and closes the collapse on small devices
  const toggleCollapse = () => {
    if (modalState.collapseOpen) {
      setModalState({
        color: "navbar-transparent"
      });
    } else {
      setModalState({
        color: "bg-white"
      });
    }
    this.setState({
      collapseOpen: !this.state.collapseOpen
    });
  };
  // this function is to open the Search modal
  const toggleModalSearch = () => {
    setModalState({
      modalSearch: !modalState.modalSearch
    });
  };

  const setEventsView = () => {
    if (!vx.loggedIn) {
      return (
      <div><NavLink tag="li"></NavLink>
      <DropdownItem className="nav-item">
        You are not logged in.
      </DropdownItem></div>);
    }
    //var rows = vx.getEvents();
    var rows = [];

    if (rows.length == 0) 
      return(
        <div><NavLink tag="li"></NavLink>
        <DropdownItem className="nav-item">
          No High severity events to report
        </DropdownItem></div>      
      )
    
    return(
      rows.map((row, i) => ( <div  key={'"div-pos-' + i + "'"}><NavLink tag="li"  key={'"link-pos-' + i + "'"}></NavLink>
      <DropdownItem className="nav-item" key={'"dropdown-pos-' + i + "'"}>
        {row.time}, {row.instance}, {row.message}
      </DropdownItem></div>))
    )
  }

  const style = { 
    bgColor: "red"
  }
  ;
    return (
      <>
        <Navbar 
          className={classNames("navbar-absolute", modalState.color)}
          expand="lg"
        >
          <Container fluid>
            <div className="navbar-wrapper">
              <div
                className={classNames("navbar-toggle d-inline", {
                  toggled: props.sidebarOpened
                })}
              >
                <button style={style}
                  className="navbar-toggler"
                  type="button"
                  onClick={props.toggleSidebar}
                >
                  <span className="navbar-toggler-bar bar1" />
                  <span className="navbar-toggler-bar bar2" />
                  <span className="navbar-toggler-bar bar3" />
                </button>
              </div>
              <NavbarBrand href="#pablo" onClick={e => e.preventDefault()}>
                {props.brandText}
              </NavbarBrand>
            </div>
            <button
              aria-expanded={false}
              aria-label="Toggle navigation"
              className="navbar-toggler"
              data-target="#navigation"
              data-toggle="collapse"
              id="navigation"
              type="button"
              onClick={toggleCollapse}
            >
              <span className="navbar-toggler-bar navbar-kebab" />
              <span className="navbar-toggler-bar navbar-kebab" />
              <span className="navbar-toggler-bar navbar-kebab" />
            </button>
            <Collapse navbar isOpen={modalState.collapseOpen}>
              <Nav className="ml-auto" navbar>
                <InputGroup className="search-bar">
                  <Button
                    color="link"
                    data-target="#searchModal"
                    data-toggle="modal"
                    id="search-button"
                    onClick={toggleModalSearch}
                  >
                    <i className="tim-icons icon-zoom-split" />
                    <span className="d-lg-none d-md-block">Search</span>
                  </Button>
                </InputGroup>
                <UncontrolledDropdown nav>
                  <DropdownToggle
                    caret
                    color="default"
                    data-toggle="dropdown"
                    nav
                  >
                    <div className="notification d-none d-lg-block d-xl-block" />
                    <i className="tim-icons icon-sound-wave" />
                    <p className="d-lg-none">Notifications</p>
                  </DropdownToggle>
                  <DropdownMenu className="dropdown-navbar" right tag="ul">
                    {setEventsView()}
                  </DropdownMenu>
                </UncontrolledDropdown>
                <UncontrolledDropdown nav>
                  <DropdownToggle
                    caret
                    color="default"
                    data-toggle="dropdown"
                    nav
                    onClick={e => e.preventDefault()}
                  >
                    <div className="photo">
                      <img alt="..." src={require("assets/img/anime3.png")} />
                    </div>
                    <b className="caret d-none d-lg-block d-xl-block" />
                    <p className="d-lg-none">Log out</p>
                  </DropdownToggle>
                  <DropdownMenu className="dropdown-navbar" right tag="ul">
                    <NavLink tag="li">
                      <DropdownItem className="nav-item">Profile</DropdownItem>
                    </NavLink>
                    <NavLink tag="li">
                      <DropdownItem className="nav-item">Settings</DropdownItem>
                    </NavLink>
                    <DropdownItem divider tag="li" />

                    {!vx.loggedIn && <NavLink tag="li">
                      <DropdownItem className="nav-item" onClick={e => vx.changeLoginState(true)} >Log in</DropdownItem>
                    </NavLink>}
                    {vx.loggedIn &&<NavLink tag="li">
                      <DropdownItem className="nav-item" onClick={e => vx.changeLoginState(false)} >Log out</DropdownItem>
                    </NavLink>}

                  </DropdownMenu>
                </UncontrolledDropdown>
                <li className="separator d-lg-none" />
              </Nav>
            </Collapse>
          </Container>
        </Navbar>
        <Modal
          modalClassName="modal-search"
          isOpen={modalState.modalSearch}
          toggle={toggleModalSearch}
        >
          <div className="modal-header">
            <Input id="inlineFormInputGroup" placeholder="SEARCH" type="text" />
            <button
              aria-label="Close"
              className="close"
              data-dismiss="modal"
              type="button"
              onClick={toggleModalSearch}
            >
              <i className="tim-icons icon-simple-remove" />
            </button>
          </div>
        </Modal>
      </>
    );
}

export default AdminNavbar;
