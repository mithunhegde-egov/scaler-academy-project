CREATE TABLE eg_wc_connection(
  id character varying(64),
  tenantId character varying(64),
  applicationNumber character varying(64),
  applicationStatus character varying(64),
  connectionNo character varying(64),
  connectionType character varying(64),
  propertyId character varying(64),
  channel character varying(64),
  connectionExecutionDate bigint,
  status boolean,
  createdBy character varying(64),
  lastModifiedBy character varying(64),
  createdTime bigint,
  lastModifiedTime bigint,
  CONSTRAINT uk_eg_wc_connection UNIQUE (id)
);