CREATE TABLE IF NOT EXISTS companies (
  id serial,
  customer_id text NOT NULL,
  email text NOT NULL,
  telephone text NOT NULL,
  firstname text NOT NULL,
  lastname text NOT NULL,
  address text NOT NULL,
  citystate text NOT NULL,
  country text NOT NULL,
  postalcode text NOT NULL,
  description text DEFAULT NULL,
  budget float default 0,
  PRIMARY KEY (id)
);

insert into companies (customer_id, email, telephone, firstname, lastname, address, citystate, country, postalcode, budget) 
	VALUES ('rtb4free', 'ben.faul@rtb4free.com','555-555-5555', 'Ben', 'Faul', '3820 Del Amo Blvd #226', 'Torrance, CA', 'USA', '90503',0.0);
	
insert into companies (customer_id, email, telephone, firstname, lastname, address, citystate, country, postalcode, budget) 
	VALUES ('test', 'test.test@test.com','666-666-66666', 'Test', 'Test', '3820 Del Amo Blvd #226', 'Torrance, CA', 'USA', '90503', 0.0);