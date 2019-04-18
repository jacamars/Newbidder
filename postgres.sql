--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.10
-- Dumped by pg_dump version 9.6.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: DATABASE postgres; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON DATABASE postgres IS 'default administrative connection database';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: attachments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.attachments (
    id integer NOT NULL,
    filename character varying(255) DEFAULT NULL::character varying,
    content_type character varying(255) DEFAULT NULL::character varying,
    data bytea,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


ALTER TABLE public.attachments OWNER TO postgres;

--
-- Name: attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.attachments_id_seq OWNER TO postgres;

--
-- Name: attachments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.attachments_id_seq OWNED BY public.attachments.id;


--
-- Name: banner_videos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.banner_videos (
    id integer NOT NULL,
    campaign_id integer,
    interval_start timestamp without time zone,
    interval_end timestamp without time zone,
    total_basket_value numeric(15,6) DEFAULT NULL::numeric,
    total_budget numeric(15,6) DEFAULT NULL::numeric,
    vast_video_width integer,
    vast_video_height integer,
    bid_ecpm numeric,
    vast_video_linerarity integer,
    vast_video_duration integer,
    vast_video_type text,
    vast_video_outgoing_file text,
    bids integer,
    clicks integer,
    pixels integer,
    wins integer,
    total_cost numeric DEFAULT 0.000000,
    daily_cost numeric,
    daily_budget numeric,
    frequency_spec text,
    frequency_expire integer,
    frequency_count integer,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    hourly_budget numeric,
    name character varying(256) DEFAULT NULL::character varying,
    target_id integer,
    hourly_cost numeric,
    bitrate integer,
    mime_type character varying(255) DEFAULT NULL::character varying,
    deals character varying(255) DEFAULT NULL::character varying,
    width_range character varying(255) DEFAULT NULL::character varying,
    height_range character varying(255) DEFAULT NULL::character varying,
    width_height_list character varying(255) DEFAULT NULL::character varying
);


ALTER TABLE public.banner_videos OWNER TO postgres;

--
-- Name: banner_videos_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.banner_videos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.banner_videos_id_seq OWNER TO postgres;

--
-- Name: banner_videos_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.banner_videos_id_seq OWNED BY public.banner_videos.id;


--
-- Name: banner_videos_rtb_standards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.banner_videos_rtb_standards (
    banner_video_id integer NOT NULL,
    rtb_standard_id integer
);


ALTER TABLE public.banner_videos_rtb_standards OWNER TO postgres;

--
-- Name: banner_videos_rtb_standards_banner_video_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.banner_videos_rtb_standards_banner_video_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.banner_videos_rtb_standards_banner_video_id_seq OWNER TO postgres;

--
-- Name: banner_videos_rtb_standards_banner_video_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.banner_videos_rtb_standards_banner_video_id_seq OWNED BY public.banner_videos_rtb_standards.banner_video_id;


--
-- Name: banners; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.banners (
    id integer NOT NULL,
    campaign_id integer,
    interval_start timestamp without time zone NOT NULL,
    interval_end timestamp without time zone,
    total_basket_value numeric,
    width integer,
    height integer,
    bid_ecpm numeric(15,6) DEFAULT NULL::numeric,
    total_cost numeric(15,6) DEFAULT NULL::numeric,
    contenttype character varying(1024) DEFAULT NULL::character varying,
    iurl character varying(1024) DEFAULT NULL::character varying,
    htmltemplate text,
    bids integer,
    clicks integer,
    pixels integer,
    wins integer,
    daily_budget numeric(15,6) DEFAULT NULL::numeric,
    hourly_budget numeric(15,6) DEFAULT NULL::numeric,
    daily_cost numeric(15,6) DEFAULT NULL::numeric,
    target_id integer,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    name character varying(255) DEFAULT NULL::character varying,
    frequency_spec character varying(255) DEFAULT NULL::character varying,
    frequency_expire integer,
    frequency_count integer,
    hourly_cost numeric(15,6) DEFAULT NULL::numeric,
    deals character varying(255) DEFAULT NULL::character varying,
    width_range character varying(255) DEFAULT NULL::character varying,
    height_range character varying(255) DEFAULT NULL::character varying,
    width_height_list character varying(255) DEFAULT NULL::character varying
);


ALTER TABLE public.banners OWNER TO postgres;

--
-- Name: banners_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.banners_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.banners_id_seq OWNER TO postgres;

--
-- Name: banners_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.banners_id_seq OWNED BY public.banners.id;


--
-- Name: banners_rtb_standards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.banners_rtb_standards (
    banner_id integer NOT NULL,
    rtb_standard_id integer
);


ALTER TABLE public.banners_rtb_standards OWNER TO postgres;

--
-- Name: banners_rtb_standards_banner_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.banners_rtb_standards_banner_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.banners_rtb_standards_banner_id_seq OWNER TO postgres;

--
-- Name: banners_rtb_standards_banner_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.banners_rtb_standards_banner_id_seq OWNED BY public.banners_rtb_standards.banner_id;


--
-- Name: campaigns; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.campaigns (
    id integer NOT NULL,
    activate_time timestamp without time zone,
    expire_time timestamp without time zone,
    cost numeric(15,6) DEFAULT NULL::numeric,
    ad_domain character varying(1024) DEFAULT NULL::character varying,
    clicks integer,
    pixels integer,
    wins integer,
    bids integer,
    name character varying(1024) DEFAULT NULL::character varying,
    status character varying(1024) DEFAULT NULL::character varying,
    conversion_type character varying(1024) DEFAULT NULL::character varying,
    budget_limit_daily numeric(15,6) DEFAULT NULL::numeric,
    budget_limit_hourly numeric(15,6) DEFAULT NULL::numeric,
    total_budget numeric(15,6) DEFAULT NULL::numeric,
    bid numeric(15,6) DEFAULT NULL::numeric,
    shard text,
    forensiq text,
    daily_cost numeric(15,6) DEFAULT NULL::numeric,
    updated_at timestamp without time zone,
    deleted_at timestamp without time zone,
    created_at timestamp without time zone,
    hourly_cost numeric(15,6) DEFAULT NULL::numeric,
    exchanges character varying(255) DEFAULT NULL::character varying,
    regions character varying(255) DEFAULT NULL::character varying,
    target_id integer
);


ALTER TABLE public.campaigns OWNER TO postgres;

--
-- Name: campaigns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.campaigns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.campaigns_id_seq OWNER TO postgres;

--
-- Name: campaigns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.campaigns_id_seq OWNED BY public.campaigns.id;


--
-- Name: campaigns_rtb_standards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.campaigns_rtb_standards (
    campaign_id integer NOT NULL,
    rtb_standard_id integer
);


ALTER TABLE public.campaigns_rtb_standards OWNER TO postgres;

--
-- Name: campaigns_rtb_standards_campaign_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.campaigns_rtb_standards_campaign_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.campaigns_rtb_standards_campaign_id_seq OWNER TO postgres;

--
-- Name: campaigns_rtb_standards_campaign_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.campaigns_rtb_standards_campaign_id_seq OWNED BY public.campaigns_rtb_standards.campaign_id;


--
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    id integer NOT NULL,
    name character varying(1024) DEFAULT NULL::character varying,
    description character varying(2048) DEFAULT NULL::character varying,
    updated_at timestamp without time zone,
    created_at timestamp without time zone
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.categories_id_seq OWNER TO postgres;

--
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categories_id_seq OWNED BY public.categories.id;


--
-- Name: countries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.countries (
    id integer NOT NULL,
    sort_order character varying(255) DEFAULT NULL::character varying,
    common_name character varying(255) DEFAULT NULL::character varying,
    formal_name character varying(255) DEFAULT NULL::character varying,
    country_type character varying(255) DEFAULT NULL::character varying,
    sub_type character varying(255) DEFAULT NULL::character varying,
    sovereignty character varying(255) DEFAULT NULL::character varying,
    capital character varying(255) DEFAULT NULL::character varying,
    iso_4217_currency_code character varying(255) DEFAULT NULL::character varying,
    iso_4217_currency_name character varying(255) DEFAULT NULL::character varying,
    "itu-t_telephone_code" character varying(255) DEFAULT NULL::character varying,
    "iso_3166-1_2_letter_code" character varying(255) DEFAULT NULL::character varying,
    "iso_3166-1_3_letter_code" character varying(255) DEFAULT NULL::character varying,
    "iso_3166-1_number" character varying(255) DEFAULT NULL::character varying,
    iana_country_code_tld character varying(255) DEFAULT NULL::character varying
);


ALTER TABLE public.countries OWNER TO postgres;

--
-- Name: countries_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.countries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.countries_id_seq OWNER TO postgres;

--
-- Name: countries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.countries_id_seq OWNED BY public.countries.id;


--
-- Name: exchange_attributes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.exchange_attributes (
    id integer NOT NULL,
    banner_id integer,
    banner_video_id integer,
    name character varying(255) DEFAULT NULL::character varying,
    value character varying(255) DEFAULT NULL::character varying,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    exchange character varying(255) DEFAULT NULL::character varying
);


ALTER TABLE public.exchange_attributes OWNER TO postgres;

--
-- Name: exchange_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.exchange_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.exchange_attributes_id_seq OWNER TO postgres;

--
-- Name: exchange_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.exchange_attributes_id_seq OWNED BY public.exchange_attributes.id;


--
-- Name: exchange_rtbspecs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.exchange_rtbspecs (
    id integer NOT NULL,
    rtbspecification character varying(1024) DEFAULT NULL::character varying,
    operand_type character varying(1024) DEFAULT NULL::character varying,
    operand_ordinal character varying(1024) DEFAULT NULL::character varying,
    updated_at timestamp without time zone,
    deleted_at timestamp without time zone
);


ALTER TABLE public.exchange_rtbspecs OWNER TO postgres;

--
-- Name: exchange_rtbspecs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.exchange_rtbspecs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.exchange_rtbspecs_id_seq OWNER TO postgres;

--
-- Name: exchange_rtbspecs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.exchange_rtbspecs_id_seq OWNED BY public.exchange_rtbspecs.id;


--
-- Name: iab_categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.iab_categories (
    id integer NOT NULL,
    "group" text,
    name text,
    iab_id text,
    is_group integer,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


ALTER TABLE public.iab_categories OWNER TO postgres;

--
-- Name: iab_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.iab_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.iab_categories_id_seq OWNER TO postgres;

--
-- Name: iab_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.iab_categories_id_seq OWNED BY public.iab_categories.id;


--
-- Name: lists; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lists (
    id integer NOT NULL,
    name character varying(1024) DEFAULT NULL::character varying,
    description character varying(4096) DEFAULT NULL::character varying,
    list_type character varying(1024) DEFAULT NULL::character varying,
    filesize integer,
    s3_url character varying(4096) DEFAULT NULL::character varying,
    filepath character varying(4096) DEFAULT NULL::character varying,
    filetype character varying(4096) DEFAULT NULL::character varying,
    last_modified character varying(1024) DEFAULT NULL::character varying,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


ALTER TABLE public.lists OWNER TO postgres;

--
-- Name: lists_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.lists_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.lists_id_seq OWNER TO postgres;

--
-- Name: lists_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.lists_id_seq OWNED BY public.lists.id;


--
-- Name: misc; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.misc (
    id text NOT NULL,
    value text NOT NULL,
    endtime bigint NOT NULL
);


ALTER TABLE public.misc OWNER TO postgres;

--
-- Name: recordedbids; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.recordedbids (
    id text NOT NULL,
    capkey text NOT NULL,
    captimeout bigint NOT NULL,
    captimeunit text NOT NULL,
    price text NOT NULL,
    adtype text NOT NULL,
    frequencycap text NOT NULL,
    endtime bigint NOT NULL
);


ALTER TABLE public.recordedbids OWNER TO postgres;

--
-- Name: report_commands; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.report_commands (
    id integer NOT NULL,
    name character varying(1024) DEFAULT NULL::character varying,
    type character varying(1024) DEFAULT NULL::character varying,
    campaign_id integer,
    description character varying(2048) DEFAULT NULL::character varying,
    command text,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    banner_id integer,
    banner_video_id integer
);


ALTER TABLE public.report_commands OWNER TO postgres;

--
-- Name: report_commands_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.report_commands_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.report_commands_id_seq OWNER TO postgres;

--
-- Name: report_commands_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.report_commands_id_seq OWNED BY public.report_commands.id;


--
-- Name: rtb_standards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rtb_standards (
    id integer NOT NULL,
    rtbspecification character varying(1024) DEFAULT NULL::character varying,
    operator character varying(1024) DEFAULT NULL::character varying,
    operand character varying(1024) DEFAULT NULL::character varying,
    operand_type character varying(16) DEFAULT NULL::character varying,
    operand_ordinal character varying(16) DEFAULT NULL::character varying,
    rtb_required integer,
    name character varying(255) DEFAULT NULL::character varying,
    description character varying(255) DEFAULT NULL::character varying,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    operand_list_id integer
);


ALTER TABLE public.rtb_standards OWNER TO postgres;

--
-- Name: rtb_standards_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.rtb_standards_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rtb_standards_id_seq OWNER TO postgres;

--
-- Name: rtb_standards_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.rtb_standards_id_seq OWNED BY public.rtb_standards.id;


--
-- Name: targets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.targets (
    id integer NOT NULL,
    activate_time timestamp without time zone,
    expire_time timestamp without time zone,
    list_of_domains text,
    domain_targetting text,
    geo_latitude numeric,
    geo_longitude numeric,
    geo_range numeric,
    country text,
    geo_region text,
    carrier text,
    os text,
    make text,
    model text,
    devicetype text,
    iab_category text,
    iab_category_blklist text,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    name text
);


ALTER TABLE public.targets OWNER TO postgres;

--
-- Name: targets_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.targets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.targets_id_seq OWNER TO postgres;

--
-- Name: targets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.targets_id_seq OWNED BY public.targets.id;


--
-- Name: video; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.video (
    id text NOT NULL,
    name text,
    endtime bigint NOT NULL
);


ALTER TABLE public.video OWNER TO postgres;

--
-- Name: attachments id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachments ALTER COLUMN id SET DEFAULT nextval('public.attachments_id_seq'::regclass);


--
-- Name: banner_videos id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.banner_videos ALTER COLUMN id SET DEFAULT nextval('public.banner_videos_id_seq'::regclass);


--
-- Name: banner_videos_rtb_standards banner_video_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.banner_videos_rtb_standards ALTER COLUMN banner_video_id SET DEFAULT nextval('public.banner_videos_rtb_standards_banner_video_id_seq'::regclass);


--
-- Name: banners id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.banners ALTER COLUMN id SET DEFAULT nextval('public.banners_id_seq'::regclass);


--
-- Name: banners_rtb_standards banner_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.banners_rtb_standards ALTER COLUMN banner_id SET DEFAULT nextval('public.banners_rtb_standards_banner_id_seq'::regclass);


--
-- Name: campaigns id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.campaigns ALTER COLUMN id SET DEFAULT nextval('public.campaigns_id_seq'::regclass);


--
-- Name: campaigns_rtb_standards campaign_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.campaigns_rtb_standards ALTER COLUMN campaign_id SET DEFAULT nextval('public.campaigns_rtb_standards_campaign_id_seq'::regclass);


--
-- Name: categories id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);


--
-- Name: countries id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries ALTER COLUMN id SET DEFAULT nextval('public.countries_id_seq'::regclass);


--
-- Name: exchange_attributes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.exchange_attributes ALTER COLUMN id SET DEFAULT nextval('public.exchange_attributes_id_seq'::regclass);


--
-- Name: exchange_rtbspecs id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.exchange_rtbspecs ALTER COLUMN id SET DEFAULT nextval('public.exchange_rtbspecs_id_seq'::regclass);


--
-- Name: iab_categories id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.iab_categories ALTER COLUMN id SET DEFAULT nextval('public.iab_categories_id_seq'::regclass);


--
-- Name: lists id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lists ALTER COLUMN id SET DEFAULT nextval('public.lists_id_seq'::regclass);


--
-- Name: report_commands id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_commands ALTER COLUMN id SET DEFAULT nextval('public.report_commands_id_seq'::regclass);


--
-- Name: rtb_standards id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rtb_standards ALTER COLUMN id SET DEFAULT nextval('public.rtb_standards_id_seq'::regclass);


--
-- Name: targets id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.targets ALTER COLUMN id SET DEFAULT nextval('public.targets_id_seq'::regclass);


--
-- Data for Name: attachments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.attachments (id, filename, content_type, data, created_at, updated_at) FROM stdin;
\.


--
-- Name: attachments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.attachments_id_seq', 1, false);


--
-- Data for Name: banner_videos; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.banner_videos (id, campaign_id, interval_start, interval_end, total_basket_value, total_budget, vast_video_width, vast_video_height, bid_ecpm, vast_video_linerarity, vast_video_duration, vast_video_type, vast_video_outgoing_file, bids, clicks, pixels, wins, total_cost, daily_cost, daily_budget, frequency_spec, frequency_expire, frequency_count, created_at, updated_at, hourly_budget, name, target_id, hourly_cost, bitrate, mime_type, deals, width_range, height_range, width_height_list) FROM stdin;
5	2	2018-01-01 00:00:00	2020-01-01 00:00:00	\N	\N	400	200	1.000000	1	50	5	<VAST xmlns:xsi=\\"http://www.w3.org/2001/XMLSchema-instance\\" version=\\"2.0\\" xsi:noNamespaceSchemaLocation=\\"vast.xsd\\">\\r\\n<Ad id=\\"270\\" >\\r\\n<InLine>\\r\\n<AdSystem version=\\"2.0\\">ONION</AdSystem>\\r\\n<AdTitle>In-Stream Video</AdTitle>\\r\\n<Description>Cottonelle Video Skin 7/15</Description>\\r\\n<Impression><![CDATA[https://ad.doubleclick.net/ad/N3186.3804.ONIONINC/B8043482.3;sz=1x1;pc=[TPAS_ID];ord=%%CACHEBUSTER%%?]]></Impression> \\r\\n<Impression><![CDATA[http://influxer.onion.com/influx.gif?site=onionads&event=impression&content_id=270&path=/vast/270.xml]]></Impression> \\r\\n<Impression><![CDATA[http://ra.onion.com/video-ad.gif?video_ad=270&event=impression]]></Impression>\\r\\n<Impression><![CDATA[{pixel_url}/exchange={exchange}/ad_id={ad_id}/creative_id={creative_id}/price=${AUCTION_PRICE}/lat={lat}/lon={lon}/bid_id={bid_id}]]></Impression> \\r\\n<Creatives>\\r\\n<Creative sequence=\\"1\\" AdID=\\"270\\">\\r\\n<Linear>\\r\\n<Duration>00:01:05</Duration>\\r\\n<TrackingEvents>\\r\\n<Tracking event=\\"start\\"><![CDATA[ http://influxer.onion.com/influx.gif?site=onionads&event=start&content_id=270&path=/vast/270.xml]]></Tracking>\\r\\n<Tracking event=\\"start\\"><![CDATA[ http://ra.onion.com/video-ad.gif?video_ad=270&event=start]]></Tracking>\\r\\n<Tracking event=\\"firstQuartile\\"><![CDATA[ http://influxer.onion.com/influx.gif?site=onionads&event=firstQuartile&content_id=270&path=/vast/270.xml ]]></Tracking>\\r\\n<Tracking event=\\"firstQuartile\\"><![CDATA[ http://ra.onion.com/video-ad.gif?video_ad=270&event=firstQuartile ]]></Tracking>\\r\\n<Tracking event=\\"midpoint\\"><![CDATA[ http://influxer.onion.com/influx.gif?site=onionads&event=midpoint&content_id=270&path=/vast/270.xml ]]></Tracking>\\r\\n<Tracking event=\\"midpoint\\"><![CDATA[ http://ra.onion.com/video-ad.gif?video_ad=270&event=midpoint ]]></Tracking>\\r\\n<Tracking event=\\"thirdQuartile\\"><![CDATA[ http://influxer.onion.com/influx.gif?site=onionads&event=thirdQuartile&content_id=270&path=/vast/270.xml ]]></Tracking>\\r\\n<Tracking event=\\"thirdQuartile\\"><![CDATA[ http://ra.onion.com/video-ad.gif?video_ad=270&event=thirdQuartile ]]></Tracking>\\r\\n<Tracking event=\\"complete\\"><![CDATA[ http://influxer.onion.com/influx.gif?site=onionads&event=complete&content_id=270&path=/vast/270.xml ]]></Tracking>\\r\\n<Tracking event=\\"complete\\"><![CDATA[ http://ra.onion.com/video-ad.gif?video_ad=270&event=complete ]]></Tracking>\\r\\n</TrackingEvents>\\r\\n<VideoClicks>\\r\\n<ClickThrough><![CDATA[https://ad.doubleclick.net/clk;281053485;107792520;e;pc=[TPAS_ID]]]></ClickThrough>\\r\\n</VideoClicks>\\r\\n<MediaFiles>\\r\\n<MediaFile delivery=\\"progressive\\" type=\\"video/webm\\" bitrate=\\"340\\" width=\\"640\\" height=\\"\\">\\r\\n<![CDATA[http://v.theonion.com/onionwebtech/videoads/525/sd.webm]]>\\r\\n</MediaFile>\\r\\n<MediaFile delivery=\\"progressive\\" type=\\"video/mp4\\" bitrate=\\"474\\" width=\\"640\\" height=\\"\\">\\r\\n<![CDATA[http://v.theonion.com/onionwebtech/videoads/525/sd.mp4]]>\\r\\n</MediaFile>\\r\\n<MediaFile delivery=\\"progressive\\" type=\\"application/x-mpegurl\\" bitrate=\\"None\\" width=\\"None\\" height=\\"\\">\\r\\n<![CDATA[http://v.theonion.com/onionwebtech/videoads/525/hls_playlist.m3u8]]>\\r\\n</MediaFile>\\r\\n</MediaFiles>\\r\\n</Linear>\\r\\n</Creative>\\r\\n</Creatives>\\r\\n</InLine>\\r\\n\\r\\n\\r\\n</Ad>\\r\\n</VAST>	\N	\N	\N	\N	0.000000	\N	\N		\N	\N	2017-05-01 20:55:25	2018-04-18 21:29:41	\N	Demo Video 	\N	\N	2000	video/mp4		\N	\N	\N
1	1	2019-04-16 21:57:24.427943	2019-04-16 21:57:24.427943	\N	\N	600	400	\N	2	120	\N	your-vast-tag-goes-here	\N	\N	\N	\N	0.000000	\N	\N	\N	\N	\N	2019-04-16 21:57:24.427943	2019-04-16 21:57:24.427943	\N	\N	\N	\N	64000	video/mpeg2	\N	\N	\N	\N
\.


--
-- Name: banner_videos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.banner_videos_id_seq', 1, true);


--
-- Data for Name: banner_videos_rtb_standards; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.banner_videos_rtb_standards (banner_video_id, rtb_standard_id) FROM stdin;
1	2
\.


--
-- Name: banner_videos_rtb_standards_banner_video_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.banner_videos_rtb_standards_banner_video_id_seq', 1, false);


--
-- Data for Name: banners; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.banners (id, campaign_id, interval_start, interval_end, total_basket_value, width, height, bid_ecpm, total_cost, contenttype, iurl, htmltemplate, bids, clicks, pixels, wins, daily_budget, hourly_budget, daily_cost, target_id, created_at, updated_at, name, frequency_spec, frequency_expire, frequency_count, hourly_cost, deals, width_range, height_range, width_height_list) FROM stdin;
1	1	2019-04-16 21:46:55.229768	2020-04-17 18:40:15.940911	\N	4	320	50.000000	\N	image/jpeg	\N	<img src='http://localhost/www.test.jpeg'>	\N	\N	\N	\N	\N	\N	\N	\N	2019-04-16 21:46:55.229768	2019-04-16 21:46:55.229768	\N	\N	\N	\N	\N	\N	\N	\N	\N
\.


--
-- Name: banners_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.banners_id_seq', 1, true);


--
-- Data for Name: banners_rtb_standards; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.banners_rtb_standards (banner_id, rtb_standard_id) FROM stdin;
1	1
\.


--
-- Name: banners_rtb_standards_banner_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.banners_rtb_standards_banner_id_seq', 1, false);


--
-- Data for Name: campaigns; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.campaigns (id, activate_time, expire_time, cost, ad_domain, clicks, pixels, wins, bids, name, status, conversion_type, budget_limit_daily, budget_limit_hourly, total_budget, bid, shard, forensiq, daily_cost, updated_at, deleted_at, created_at, hourly_cost, exchanges, regions, target_id) FROM stdin;
1	2019-04-21 17:07:24.604862	2020-04-17 23:23:52.281295	\N	\N	\N	\N	\N	\N	\N	runnable	\N	\N	\N	\N	\N	\N	\N	\N	2019-04-18 17:13:21.292308	\N	2019-04-16 22:01:44.451436	\N	\N	USA	1
\.


--
-- Name: campaigns_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.campaigns_id_seq', 1, true);


--
-- Data for Name: campaigns_rtb_standards; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.campaigns_rtb_standards (campaign_id, rtb_standard_id) FROM stdin;
\.


--
-- Name: campaigns_rtb_standards_campaign_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.campaigns_rtb_standards_campaign_id_seq', 1, false);


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categories (id, name, description, updated_at, created_at) FROM stdin;
\.


--
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categories_id_seq', 1, false);


--
-- Data for Name: countries; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.countries (id, sort_order, common_name, formal_name, country_type, sub_type, sovereignty, capital, iso_4217_currency_code, iso_4217_currency_name, "itu-t_telephone_code", "iso_3166-1_2_letter_code", "iso_3166-1_3_letter_code", "iso_3166-1_number", iana_country_code_tld) FROM stdin;
\.


--
-- Name: countries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.countries_id_seq', 1, false);


--
-- Data for Name: exchange_attributes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.exchange_attributes (id, banner_id, banner_video_id, name, value, created_at, updated_at, exchange) FROM stdin;
\.


--
-- Name: exchange_attributes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.exchange_attributes_id_seq', 1, false);


--
-- Data for Name: exchange_rtbspecs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.exchange_rtbspecs (id, rtbspecification, operand_type, operand_ordinal, updated_at, deleted_at) FROM stdin;
\.


--
-- Name: exchange_rtbspecs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.exchange_rtbspecs_id_seq', 1, false);


--
-- Data for Name: iab_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.iab_categories (id, "group", name, iab_id, is_group, created_at, updated_at) FROM stdin;
419	Arts & Entertainment	Arts & Entertainment	IAB1	1	2016-10-20 20:31:32	2016-10-20 20:31:32
\.


--
-- Name: iab_categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.iab_categories_id_seq', 1, false);


--
-- Data for Name: lists; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lists (id, name, description, list_type, filesize, s3_url, filepath, filetype, last_modified, created_at, updated_at) FROM stdin;
\.


--
-- Name: lists_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lists_id_seq', 1, false);


--
-- Data for Name: misc; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.misc (id, value, endtime) FROM stdin;
\.


--
-- Data for Name: recordedbids; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.recordedbids (id, capkey, captimeout, captimeunit, price, adtype, frequencycap, endtime) FROM stdin;
\.


--
-- Data for Name: report_commands; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.report_commands (id, name, type, campaign_id, description, command, created_at, updated_at, banner_id, banner_video_id) FROM stdin;
\.


--
-- Name: report_commands_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.report_commands_id_seq', 1, false);


--
-- Data for Name: rtb_standards; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.rtb_standards (id, rtbspecification, operator, operand, operand_type, operand_ordinal, rtb_required, name, description, created_at, updated_at, operand_list_id) FROM stdin;
1	requests.device.geo	INRANGE	ZIPCODES 90501,90502,90503,90504,90505,10000	S	ARRAY	1	LAT/LON	Demonstates 5 zipcodes and a range of 10km.	2019-04-16 22:16:44.747946	2019-04-16 22:16:44.747946	\N
2	requests.device.geo.region	MEMBER	NY,CA,NV	S	L	1	3 states	Demonstates a constraint of 3 states	2019-04-16 22:32:14.891718	2019-04-16 22:32:14.891718	\N
\.


--
-- Name: rtb_standards_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rtb_standards_id_seq', 2, true);


--
-- Data for Name: targets; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.targets (id, activate_time, expire_time, list_of_domains, domain_targetting, geo_latitude, geo_longitude, geo_range, country, geo_region, carrier, os, make, model, devicetype, iab_category, iab_category_blklist, created_at, updated_at, name) FROM stdin;
1	\N	\N	\N	\N	\N	\N	\N	USA	\N	\N	\N	\N	\N	\N	\N	\N	2019-04-16 21:58:46.000459	2019-04-16 21:58:46.000459	Sample target  for USA
\.


--
-- Name: targets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.targets_id_seq', 1, true);


--
-- Data for Name: video; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.video (id, name, endtime) FROM stdin;
\.


--
-- Name: attachments attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachments
    ADD CONSTRAINT attachments_pkey PRIMARY KEY (id);


--
-- Name: banner_videos banner_videos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.banner_videos
    ADD CONSTRAINT banner_videos_pkey PRIMARY KEY (id);


--
-- Name: banners banners_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.banners
    ADD CONSTRAINT banners_pkey PRIMARY KEY (id);


--
-- Name: campaigns campaigns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.campaigns
    ADD CONSTRAINT campaigns_pkey PRIMARY KEY (id);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: countries countries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries
    ADD CONSTRAINT countries_pkey PRIMARY KEY (id);


--
-- Name: exchange_attributes exchange_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.exchange_attributes
    ADD CONSTRAINT exchange_attributes_pkey PRIMARY KEY (id);


--
-- Name: exchange_rtbspecs exchange_rtbspecs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.exchange_rtbspecs
    ADD CONSTRAINT exchange_rtbspecs_pkey PRIMARY KEY (id);


--
-- Name: iab_categories iab_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.iab_categories
    ADD CONSTRAINT iab_categories_pkey PRIMARY KEY (id);


--
-- Name: lists lists_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lists
    ADD CONSTRAINT lists_pkey PRIMARY KEY (id);


--
-- Name: misc misc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.misc
    ADD CONSTRAINT misc_pkey PRIMARY KEY (id);


--
-- Name: recordedbids recordedbids_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recordedbids
    ADD CONSTRAINT recordedbids_pkey PRIMARY KEY (id);


--
-- Name: report_commands report_commands_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.report_commands
    ADD CONSTRAINT report_commands_pkey PRIMARY KEY (id);


--
-- Name: rtb_standards rtb_standards_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rtb_standards
    ADD CONSTRAINT rtb_standards_pkey PRIMARY KEY (id);


--
-- Name: targets targets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.targets
    ADD CONSTRAINT targets_pkey PRIMARY KEY (id);


--
-- Name: video video_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.video
    ADD CONSTRAINT video_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

