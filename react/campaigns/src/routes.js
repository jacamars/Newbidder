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
import Dashboard from "views/Dashboard.jsx";
import Icons from "views/Icons.jsx";
import LeafMap from "views/LeafMap.jsx";
import Notifications from "views/Notifications.jsx";
import TableList from "views/TableList.jsx";
import Typography from "views/Typography.jsx";
import UserProfile from "views/UserProfile.jsx"
import Simulator from "views/Simulator.jsx"
import ConsoleLog from "views/ConsoleLog.jsx"
import Campaigns from "views/Campaigns.jsx"
import Targets from "views/Targets.jsx"
import Creatives from "views/Creatives.jsx"
import Rules from "views/Rules.jsx"
import Sets from "views/Sets.jsx"


var routes = [
  {
    path: "/dashboard",
    name: "Dashboard",
    icon: "tim-icons icon-chart-pie-36",
    component: Dashboard,
    layout: "/admin",
  },
  {
    path: "/campaigns",
    name: "Campaigns",
    icon: "tim-icons icon-palette",
    component: Campaigns,
    layout: "/admin"
  },
  {
    path: "/targets",
    name: "Targets",
    icon: "tim-icons icon-check-2",
    component: Targets,
    layout: "/admin"
  },
  {
    path: "/creatives",
    name: "Creatives",
    icon: "tim-icons icon-video-66",
    component: Creatives,
    layout: "/admin"
  },
  {
    path: "/rules",
    name: "Rules",
    icon: "tim-icons icon-bank",
    component: Rules,
    layout: "/admin"
  },
  {
    path: "/sets",
    name: "Sets",
    icon: "tim-icons icon-basket-simple",
    component: Sets,
    layout: "/admin"
  },
  {
    path: "/simulator",
    name: "Simulator",
    icon: "tim-icons icon-light-3",
    component: Simulator,
    layout: "/admin"
  },
];

export default routes;
