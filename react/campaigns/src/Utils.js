import React  from "react";

export const ssp = ['Nexage', 'Bidswitch','Admedia', 'Adprudence', 'Appnexus', 'Adventurefeeds','Atomx','Axonix','Bidswitch','c1x', 
    'Cappture', 'Citenko','Epomx', 'Fyber', 'Gotham','Google', 'Index','Intango', 'Kadam', 'Medianexusnetwork', 'Mobfox', 'Openssp', 
    'Openx','Pokkt', 'Pubmatic', 'Republer', 'Smaato', 'Smartyads', 'Smartadserver', 'Spotx', 'Ssphwy','Stroer', 'Taggify', 'Tappx', 
    'Vdopia', 'Ventuno', 'Vertamedia','Waardx', 'Wideorbit' ];

export const deviceTypes = [
    "unknown",
    "mobile",
    "desktop",
    "smarttv",
    "phone",
    "tablet",
    "mobile-not(phone or tablet)"];

export const fromCommaList = (str) => {
    if (!str)
        return "";
    return str.split(",").join("\n");
}

export const asTextAreaList = (list) => {
    var str = "";
    if (!list)
      return str;
    return list.join();
}

export const getTrueFalseOptions = (value)  =>{
    if (value === true) {
        return(
            <>
            <option>true</option>
            <option>false</option>
            </>
        );
    }
    return(
        <>
        <option>true</option>
        <option>false</option> 
    </>);
}
