CREATE USER balance_user WITH PASSWORD 'Admin@2018';
CREATE DATABASE balance OWNER balance_user;
DROP TABLE public.company;
CREATE TABLE public.company
(
  id bigint NOT NULL,
  name character varying(255) NOT NULL,
  website character varying(255),
  CONSTRAINT company_pkey PRIMARY KEY (id),
  CONSTRAINT uk_niu8sfil2gxywcru9ah3r4ec5 UNIQUE (name)
)
WITH (OIDS=FALSE);
ALTER TABLE public.company OWNER TO balance_user;
DROP TABLE public."user";
CREATE TABLE public."user"
(
  id bigint NOT NULL,
  fullname character varying(255),
  is_admin boolean,
  name character varying(255) NOT NULL,
  password character varying(255) NOT NULL,
  phone_number character varying(255),
  CONSTRAINT user_pkey PRIMARY KEY (id),
  CONSTRAINT uk_gj2fy3dcix7ph7k8684gka40c UNIQUE (name),
  CONSTRAINT uk_t8tbwelrnviudxdaggwr1kd9b UNIQUE (name)
)
WITH ( OIDS=FALSE);
ALTER TABLE public."user" OWNER TO balance_user;
DROP TABLE public.sellpoint;
CREATE TABLE public.sellpoint
(
  id bigint NOT NULL,
  address character varying(255),
  latitude real,
  longitude real,
  name character varying(255),
  phone_number character varying(255),
  company_id bigint,
  user_id bigint,
  CONSTRAINT sellpoint_pkey PRIMARY KEY (id),
  CONSTRAINT fk6mbmtk5m2rceuxvmasfh39b4 FOREIGN KEY (company_id)
      REFERENCES public.company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE SET NULL,
  CONSTRAINT fkii8phjf6dkc40ettjuvi4vxpk FOREIGN KEY (user_id)
      REFERENCES public."user" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE SET NULL
)
WITH (OIDS=FALSE);
ALTER TABLE public.sellpoint OWNER TO balance_user;
INSERT INTO public."user"(id, fullname, is_admin, name, password, phone_number)
VALUES (1, 'Admin Test User', true, 'balance_admin', '$2a$10$oovG7yJYnwHZI9ihZA6eKu8ZlFjz2wAF3WPYJYzlTi1M88HY/Gz0e', '+996770000000');