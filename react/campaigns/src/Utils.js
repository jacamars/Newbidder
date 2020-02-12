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

export const mimeTypes = (e) => {
    return (
        <>
        <option selected={e === 'image/gif'}>image/gif</option>
        <option selected={e === 'image/png'}>image/png</option>
        <option selected={e === 'image/gif'}>image/jpg</option>
        <option selected={e === 'image/svg+xml'}>image/svg+xml</option>
        <option selected={e === 'application/javascript'}>application/javascript</option>
        <option selected={e === 'application/xml'}>application/xml</option>
        <option selected={e === 'text/css'}>text/css</option>
        <option selected={e === 'text/plain'}>text/plain</option>
        <option selected={e === 'video/avi'}>video/avi</option>
        <option selected={e === 'video/mp4'}>video/mp4</option>
        <option selected={e === 'video/pgg'}>video/ogg</option>
        </>
    );
}

export const protocolOptions = (e) => {
    return(
        <>
        <option value="1" selected={1 === e}>VAST 1.0</option>
        <option value="2" selected={2 === e}>VAST 2.0</option>
        <option value="3" selected={3 === e}>VAST 3.0</option>
        <option value="4" selected={4 === e}>VAST 1.0 Wrapper</option>
        <option value="5" selected={5 === e}>VAST 2.0 Wrapper</option>
        <option value="6" selected={6 === e}>VAST 3.0 Wrapper</option>
        <option value="7" selected={7 === e}>VAST 4.0</option>
        <option value="8" selected={8 === e}>VAST 4.0 Wrapper</option>
        <option value="9" selected={9 === e}>DAAST 1.0</option>
        <option value="9" selected={10 === e}>DAAST 1.0 Wrapper</option>
        </>
    );
}

/*
*/

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
