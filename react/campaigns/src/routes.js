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
import Videos from "views/Videos.jsx"
import Banners from "views/Banners.jsx"
import Audio from "views/Audio.jsx"
import Native from "views/Native.jsx"
import Rules from "views/Rules.jsx"


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
    path: "/videos",
    name: "Videos",
    icon: "tim-icons icon-video-66",
    component: Videos,
    layout: "/admin"
  },
  {
    path: "/banners",
    name: "Banners",
    icon: "tim-icons icon-mobile",
    component: Banners,
    layout: "/admin"
  },
  {
    path: "/audio",
    name: "Audio",
    icon: "tim-icons icon-headphones",
    component: Audio,
    layout: "/admin"
  },
  {
    path: "/native",
    name: "Native",
    icon: "tim-icons icon-camera-18",
    component: Native,
    layout: "/admin"
  },
  {
    path: "/rules",
    name: "Rules",
    icon: "tim-icons icon-bank",
    component: Rules,
    layout: "/admin"
  }
];

export default routes;
