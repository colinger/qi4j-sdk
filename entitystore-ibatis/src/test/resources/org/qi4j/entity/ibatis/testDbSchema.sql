CREATE TABLE SA.PERSON
(
  ID                 VARCHAR  (50)    NOT NULL,
  FIRST_NAME         VARCHAR  (40)   NOT NULL,
  LAST_NAME          VARCHAR  (40)   NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE SA.ACCOUNT
(
  ID                 VARCHAR  (50)    NOT NULL,
  NAME               VARCHAR  (40)    NOT NULL,
  CONTACT_ID         VARCHAR  (50)    NOT NULL,
  PRIMARY KEY (ID)
);