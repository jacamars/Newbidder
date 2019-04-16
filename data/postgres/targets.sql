CREATE TABLE targets (
  id serial,
  activate_time timestamp DEFAULT NULL,
  expire_time timestamp DEFAULT NULL,
  list_of_domains TEXT DEFAULT NULL
  domain_targetting text DEFAULT NULL,
  geo_latitude double DEFAULT NULL,
  geo_longitude double DEFAULT NULL,
  geo_range double DEFAULT NULL,
  country  text DEFAULT NULL
  geo_region  text DEFAULT NULL
  carrier  text DEFAULT NULL
  os  text DEFAULT NULL
  make  text DEFAULT NULL
  model text DEFAULT NULL
  devicetype text DEFAULT NULL
  IAB_category text DEFAULT NULL
  IAB_category_blklist text DEFAULT NULL,
  created_at timestamp DEFAULT NULL,
  updated_at timestamp  DEFAULT NULL,
  name DEFAULT NULL,
  domains_list_id NULL,
  PRIMARY KEY (id)
);
