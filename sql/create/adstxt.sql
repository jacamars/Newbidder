DROP TABLE IF EXISTS adstxt;

CREATE TABLE adstxt(
       ID                           SERIAL,                                 -- Unique ID.
       APP_ADS                      TEXT        NOT NULL,                   -- app or ads (app or web)
       SITE_DOMAIN                  TEXT        NOT NULL,                   -- the Publisher site (like  cnn.com
       EXCHANGE_DOMAIN              TEXT        NOT NULL,                   -- the SSP/adsystem (Like openx.com)
       SELLER_ACCOUNT_ID            TEXT        NOT NULL,                   -- seller account id
       ACCOUNT_TYPE                 TEXT        NOT NULL,                   -- RESELLER or DIRECT
       TAG_ID                       TEXT        NOT NULL,                   -- Tag identifier
       ENTRY_COMMENT                TEXT        NOT NULL,                   -- A comment
       UPDATED                      TIMESTAMP   DEFAULT NOW()              -- when added
);


DROP TABLE IF EXISTS adsystem_domain;

CREATE TABLE "adsystem_domain" (
    ID                              SERIAL,         -- unique key
	DOMAIN	                    TEXT UNIQUE,    -- Domain name of the adsystem (e.g. 'openx.com'
	PRIMARY KEY(DOMAIN,ID)
);

DROP TABLE IF EXISTS adstxt_contentdistributor;

CREATE TABLE adstxt_contentdistributor(
       ID                           SERIAL,                             -- Unique ID
       APP_ADS                      TEXT        NOT NULL,               -- app or ads (app or web)
       SITE_DOMAIN                  TEXT        NOT NULL,               -- The publisher url
       UPDATED                      DATE        DEFAULT NOW(),          -- The
       EXPIRES                      TIMESTAMP   DEFAULT NOW() + interval '7 days', -- the expiration time
    PRIMARY KEY (APP_ADS,SITE_DOMAIN)
);

-- Initialization

INSERT INTO adsystem_domain (DOMAIN) VALUES ('adtech.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('aolcloud.net');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('appnexus.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('districtm.io');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('google.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('indexechange.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('indexexchange.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('indexexchnage.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('openx.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('pubmatic.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('rubicon.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('rubiconproject.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('spotx.tv');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('spotxchange.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('spx.smaato.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('teads.tv');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('pulsepoint.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('aol.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('liveintent.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('triplelift.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('teads.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('contextweb.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('sharethrough.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('districtm.ca');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('sovrn.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('smaato.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('coxmt.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('lijit.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('www.indexexchange.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('tremorhub.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('appnexus.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('advertising.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('fastlane.rubiconproject.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('33across.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('facebook.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('gumgum.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('kargo.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('brealtime.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('c.amazon-adsystem.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('yieldmo.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('taboola.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('sofia.trustx.org');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('a9.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('amazon.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('lkqd.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('criteo.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('exponential.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('yldbt.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('rhythmone.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('technorati.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('bidfluence.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('switch.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('amazon-adsystem.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('conversantmedia.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('sonobi.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('spoutable.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('trustx.org');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('freewheel.tv');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('connatix.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('lkqd.net');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('positivemobile.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('memeglobal.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('kixer.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('sekindo.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('360yield.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('cdn.stickyadstv.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('adform.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('streamrail.net');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('mathtag.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('adyoulike.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('kiosked.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('video.unrulymedia.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('meridian.sovrn.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('brightcom.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('smartadserver.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('apnexus.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('jadserve.postrelease.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('rs-stripe.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('fyber.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('inner-active.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('tidaltv.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('critero.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('advertising.amazon.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('nativo.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('media.net');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('www.yumenetworks.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('revcontent.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('adtech.net');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('go.sonobi.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('outbrain.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('ib.adnxs.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('freeskreen.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('bidtellect.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('loopme.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('vidazoo.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('videoflare.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('yahoo.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('yume.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('pixfuture.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('advertising.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('kargo.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('aps.amazon.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('behave.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('engagebdr.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('my6sense.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('nobid.io');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('synacor.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('telaria.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('themediagrid.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('tribalfusion.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('undertone.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('sortable.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('deployads.com');

INSERT INTO adsystem_domain (DOMAIN) VALUES ('green_ssp.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('violet_ssp.com');
INSERT INTO adsystem_domain (DOMAIN) VALUES ('grey_ssp.com');
