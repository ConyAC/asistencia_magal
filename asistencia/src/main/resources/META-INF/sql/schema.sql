CREATE TABLE IF NOT EXISTS "LABORER"
(
   LABORERID IDENTITY PRIMARY KEY NOT NULL,
   ADDRESS varchar(255),
   AFP integer NOT NULL,
   COMMUNE varchar(255),
   CONTRACTID integer,
   DATEADMISSION timestamp,
   DATEBIRTH timestamp,
   DEPENDENTS integer,
   FIRSTNAME varchar(255) NOT NULL,
   ISAPRE integer NOT NULL,
   LASTNAME varchar(255) NOT NULL,
   MARITALSTATUS integer NOT NULL,
   MOBILENUMBER varchar(255),
   NATIONALITY integer NOT NULL,
   PHONE varchar(255),
   PHOTO varchar(255),
   RUT varchar(255) NOT NULL,
   SECONDLASTNAME varchar(255),
   SECONDNAME varchar(255),
   TEAMID bigint,
   TOWN varchar(255),
   WEDGE integer,
   PROVENANCE varchar(255)
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LABORER ON "LABORER"(LABORERID)
;

CREATE UNIQUE INDEX IF NOT EXISTS UK_RUT ON "LABORER"(RUT)
;

-- ROLE 

CREATE TABLE IF NOT EXISTS "ROLE"
(
   ROLEID IDENTITY PRIMARY KEY NOT NULL,
   DESCRIPTION varchar(255),
   NAME varchar(255) NOT NULL
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_ROLE ON "ROLE"(ROLEID)
;


-- permission

CREATE TABLE IF NOT EXISTS "PERMISSIONS"
(
   ROLEID bigint NOT NULL,
   ROLE_PERMISSIONS varchar(255)
)
;
ALTER TABLE "PERMISSIONS"
ADD CONSTRAINT IF NOT EXISTS FK_PERMISSIONS
FOREIGN KEY (ROLEID)
REFERENCES "ROLE"(ROLEID)
;
CREATE INDEX IF NOT EXISTS FK_INDX_PERMISSIONS ON "PERMISSIONS"(ROLEID)
;


-- USER

CREATE TABLE IF NOT EXISTS "USER"
(
   USERID IDENTITY PRIMARY KEY NOT NULL,
   DELETED boolean,
   EMAIL varchar(255) NOT NULL,
   FIRSTNAME varchar(255) NOT NULL,
   LASTNAME varchar(255) NOT NULL,
   PASSWORD varchar(255),
   RUT varchar(255) NOT NULL,
   SALT varchar(255),
   STATUS integer NOT NULL,
   ROLEID bigint
)
;
ALTER TABLE "USER"
ADD CONSTRAINT IF NOT EXISTS FK_USER_ROLE
FOREIGN KEY (ROLEID)
REFERENCES "ROLE"(ROLEID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_ROLE ON "USER"(ROLEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS UK_EMAIL ON "USER"(EMAIL)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_USER ON "USER"(USERID)
;

-- CONSTRUCTION SITE


CREATE TABLE IF NOT EXISTS "CONSTRUCTION_SITE"
(
   CONSTRUCTION_SITEID IDENTITY PRIMARY KEY NOT NULL,
   CODE varchar(255) NOT NULL,
   ADDRESS varchar(255),
   DELETED boolean,
   NAME varchar(255) NOT NULL,
   STATUS integer NOT NULL,
   PERSONINCHARGEID bigint
)
;
ALTER TABLE "CONSTRUCTION_SITE"
ADD CONSTRAINT IF NOT EXISTS FK_CS_USER
FOREIGN KEY (PERSONINCHARGEID)
REFERENCES "USER"(USERID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_CS ON "CONSTRUCTION_SITE"(PERSONINCHARGEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_CS ON "CONSTRUCTION_SITE"(CONSTRUCTION_SITEID)
;

CREATE TABLE IF NOT EXISTS "CONSTRUCTIONSITE_STEP"
(
	CONSTRUCTIONSITE_CONSTRUCTION_SITEID NUMBER NOT NULL,
	STEP varchar(255) NOT NULL
);

-- USER_CONSTRUCTIONSITE

CREATE TABLE IF NOT EXISTS "USER_CONSTRUCTIONSITE"
(
   CONSTRUCTION_SITEID bigint NOT NULL,
   USERID bigint NOT NULL,
   CONSTRAINT CONSTRAINT_5 PRIMARY KEY (CONSTRUCTION_SITEID,USERID)
)
;
ALTER TABLE "USER_CONSTRUCTIONSITE"
ADD CONSTRAINT IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_CONSTRUCTION_SITEID
FOREIGN KEY (CONSTRUCTION_SITEID)
REFERENCES "CONSTRUCTION_SITE"(CONSTRUCTION_SITEID)
;
ALTER TABLE "USER_CONSTRUCTIONSITE"
ADD CONSTRAINT IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_USERID
FOREIGN KEY (USERID)
REFERENCES "USER"(USERID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_5 ON "USER_CONSTRUCTIONSITE"
(
  CONSTRUCTION_SITEID,
  USERID
)
;
CREATE INDEX IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_CONSTRUCTION_SITEID_INDEX_5 ON "USER_CONSTRUCTIONSITE"(CONSTRUCTION_SITEID)
;
CREATE INDEX IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_USERID_INDEX_5 ON "USER_CONSTRUCTIONSITE"(USERID)
;


-- LABORER_CONSTRUCTIONSITE

CREATE TABLE IF NOT EXISTS "LABORER_CONSTRUCTIONSITE"
(
   LABORER_CONSTRUCTIONSITEID IDENTITY PRIMARY KEY NOT NULL,
   LABORERID integer NOT NULL,
   ACTIVE smallint,
   CONFIRMED smallint,
   REWARD integer,
   BLOCK boolean,
   PERSONBLOCKID bigint,
   COMMENT varchar(255),
   -- JOB integer not null,
   -- JOBCODE integer not null,
   CONSTRUCTION_SITEID integer NOT NULL
)
;
ALTER TABLE "LABORER_CONSTRUCTIONSITE"
ADD CONSTRAINT IF NOT EXISTS FK_LC_LABORER
FOREIGN KEY (LABORERID)
REFERENCES "LABORER"(LABORERID)
;
CREATE INDEX IF NOT EXISTS  FK_IDX_LC_LABORER ON "LABORER_CONSTRUCTIONSITE"(LABORERID)
;
ALTER TABLE "LABORER_CONSTRUCTIONSITE"
ADD CONSTRAINT IF NOT EXISTS FK_LC_CS
FOREIGN KEY (CONSTRUCTION_SITEID)
REFERENCES "CONSTRUCTION_SITE"(CONSTRUCTION_SITEID)
;
CREATE INDEX IF NOT EXISTS  FK_IDX_LC_CS ON "LABORER_CONSTRUCTIONSITE"(CONSTRUCTION_SITEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LC ON "LABORER_CONSTRUCTIONSITE"(LABORER_CONSTRUCTIONSITEID)
;

-- ABSENCE

CREATE TABLE IF NOT EXISTS "ABSENCE"
(
   ABSENCEID IDENTITY PRIMARY KEY NOT NULL,
   ABSENCE_TYPE integer,
   DESCRIPTION varchar(255),
   FROM_DATE date,
   TO_DATE date,
   LABORER_CONSTRUCTIONSITEID bigint
)
;
ALTER TABLE "ABSENCE"
ADD CONSTRAINT IF NOT EXISTS FK_BS24KVJFK3V03QEG76BCE1MAO
FOREIGN KEY (LABORER_CONSTRUCTIONSITEID)
REFERENCES "LABORER_CONSTRUCTIONSITE"(LABORER_CONSTRUCTIONSITEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_ABSENCE ON "ABSENCE"(ABSENCEID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_ABSENCE_LC ON "ABSENCE"(LABORER_CONSTRUCTIONSITEID)
;

-- TOOL

CREATE TABLE IF NOT EXISTS "TOOL"
(
   TOOLID IDENTITY PRIMARY KEY NOT NULL,
   DATEBUY timestamp NOT NULL,
   FEE integer,
   NAME varchar(255) NOT NULL,
   PRICE integer,
   STATUS varchar(255),
   LABORER_CONSTRUCTIONSITEID bigint not null,
   DATEPOSTPONED timestamp,
   POSTPONED boolean
)
;
ALTER TABLE "TOOL"
ADD CONSTRAINT IF NOT EXISTS FK_TOOL_LC
FOREIGN KEY (LABORER_CONSTRUCTIONSITEID)
REFERENCES "LABORER_CONSTRUCTIONSITE"(LABORER_CONSTRUCTIONSITEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TOOL ON "TOOL"(TOOLID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TOOL_LC ON "TOOL"(LABORER_CONSTRUCTIONSITEID)
;

-- VACATION

CREATE TABLE IF NOT EXISTS "VACATION"
(
   VACATIONID IDENTITY PRIMARY KEY NOT NULL,
   FROM_DATE date,
   TO_DATE date,
   PROGRESSIVE integer,
   LABORER_CONSTRUCTIONSITEID bigint not null
)
;
ALTER TABLE "VACATION"
ADD CONSTRAINT IF NOT EXISTS FK_VACATION_LC
FOREIGN KEY (LABORER_CONSTRUCTIONSITEID)
REFERENCES "LABORER_CONSTRUCTIONSITE"(LABORER_CONSTRUCTIONSITEID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_VACATION_LC ON "VACATION"(LABORER_CONSTRUCTIONSITEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_VACATION ON "VACATION"(VACATIONID)
;


-- ACCIDENTS

CREATE TABLE IF NOT EXISTS "ACCIDENT"
(
   ACCIDENTID IDENTITY PRIMARY KEY NOT NULL,
   ACCIDENT_LEVEL integer,
   DESCRIPTION varchar(255),
   FROM_DATE date,
   TO_DATE date,
   WASNEGLIGENCE boolean NOT NULL,
   LABORER_CONSTRUCTIONSITEID bigint
)
;
ALTER TABLE "ACCIDENT"
ADD CONSTRAINT IF NOT EXISTS FK_ACCIDENT_LABORER
FOREIGN KEY (LABORER_CONSTRUCTIONSITEID)
REFERENCES "LABORER_CONSTRUCTIONSITE"(LABORER_CONSTRUCTIONSITEID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_ACCIDENT_LABORER ON "ACCIDENT"(LABORER_CONSTRUCTIONSITEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_ACCIDENT ON "ACCIDENT"(ACCIDENTID)
;

-- WAGE_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS "WAGE_CONFIGURATIONS"
(
   WAGE_CONFIGURATIONSID bigint PRIMARY KEY NOT NULL,
   COLLATION double,
   MINIMUM_WAGE double,
   MOBILIZATION double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_WG ON "WAGE_CONFIGURATIONS"(WAGE_CONFIGURATIONSID)
;


-- MOBILIZATION2
CREATE TABLE IF NOT EXISTS "MOBILIZATION2"
(
   WAGE_CONFIGURATIONSID IDENTITY NOT NULL,
   AMOUNT double,
   LINKED_CONSTRUCTION_SITEID bigint
)
;
ALTER TABLE "MOBILIZATION2"
ADD CONSTRAINT IF NOT EXISTS FK_MOBILIZATION2_CS
FOREIGN KEY (LINKED_CONSTRUCTION_SITEID)
REFERENCES "CONSTRUCTION_SITE"(CONSTRUCTION_SITEID)
;
ALTER TABLE "MOBILIZATION2"
ADD CONSTRAINT IF NOT EXISTS FK_MOBILIZATION2_WC
FOREIGN KEY (WAGE_CONFIGURATIONSID)
REFERENCES "WAGE_CONFIGURATIONS"(WAGE_CONFIGURATIONSID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_MOB2_CS ON "MOBILIZATION2"(LINKED_CONSTRUCTION_SITEID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_MOB2_WC ON "MOBILIZATION2"(WAGE_CONFIGURATIONSID)
;

-- TEAM

CREATE TABLE IF NOT EXISTS "TEAM"
(
   TEAMID IDENTITY PRIMARY KEY NOT NULL,
   DATE timestamp,
   DELETED boolean,
   NAME varchar(255) NOT NULL,
   STATUS integer NOT NULL,
   CONSTRUCTION_SITEID bigint,
   LABORERID bigint
)
;
ALTER TABLE "TEAM"
ADD CONSTRAINT IF NOT EXISTS FK_TEAM_LABORER
FOREIGN KEY (LABORERID)
REFERENCES "LABORER"(LABORERID)
;
ALTER TABLE "TEAM"
ADD CONSTRAINT IF NOT EXISTS FK_TEAM_CS
FOREIGN KEY (CONSTRUCTION_SITEID)
REFERENCES "CONSTRUCTION_SITE"(CONSTRUCTION_SITEID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TEAM_LABORER ON "TEAM"(LABORERID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TEAM ON "TEAM"(TEAMID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TEAM_CS ON "TEAM"(CONSTRUCTION_SITEID)
;

CREATE TABLE IF NOT EXISTS "LABORER_TEAM"
(
	TEAMID bigint,
	LABORERID bigint
)
;


-- CONSTRUCTION SITE TEAM

CREATE TABLE IF NOT EXISTS "CONSTRUCTION_SITE_TEAM"
(
   CST_CSID bigint NOT NULL,
   TEAMS_TEAMID bigint NOT NULL
)
;
ALTER TABLE "CONSTRUCTION_SITE_TEAM"
ADD CONSTRAINT IF NOT EXISTS FK_CST_CS
FOREIGN KEY (CST_CSID)
REFERENCES "CONSTRUCTION_SITE"(CONSTRUCTION_SITEID)
;
ALTER TABLE "CONSTRUCTION_SITE_TEAM"
ADD CONSTRAINT IF NOT EXISTS FK_CST_TEAM
FOREIGN KEY (TEAMS_TEAMID)
REFERENCES "TEAM"(TEAMID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_CS_TEAM ON "CONSTRUCTION_SITE_TEAM"(CST_CSID)
;
CREATE UNIQUE INDEX IF NOT EXISTS UK_INDX_CS ON "CONSTRUCTION_SITE_TEAM"(TEAMS_TEAMID)
;


-- ADVANCE_PAYMENT_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS "ADVANCE_PAYMENT_CONFIGURATIONS"
(
   ADVANCE_PAYMENT_CONFIGURATIONSID bigint PRIMARY KEY NOT NULL,
   FAILURE_DISCOUNT double,
   PERMISSION_DISCOUNT double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_APC ON "ADVANCE_PAYMENT_CONFIGURATIONS"(ADVANCE_PAYMENT_CONFIGURATIONSID)
;



-- ADVANCE_PAYMENT_ITEM

CREATE TABLE IF NOT EXISTS "ADVANCE_PAYMENT_ITEM"
(
   SUPLE_CODE integer,
   SUPLE_INCREASE_AMOUNT double,
   SUPLE_NORMAL_AMOUNT double,
   SUPLE_TOTAL_AMOUNT double,
   ADVANCE_PAYMENT_CONFIGURATIONSID bigint
)
;
ALTER TABLE "ADVANCE_PAYMENT_ITEM"
ADD CONSTRAINT IF NOT EXISTS ADVANCEPAYMENTITEMADVANCE_PAYMENT_CONFIGURATIONSID
FOREIGN KEY (ADVANCE_PAYMENT_CONFIGURATIONSID)
REFERENCES "ADVANCE_PAYMENT_CONFIGURATIONS"(ADVANCE_PAYMENT_CONFIGURATIONSID)
;
CREATE UNIQUE INDEX IF NOT EXISTS UK_INDX_SUPLE ON "ADVANCE_PAYMENT_ITEM"(SUPLE_CODE)
;
CREATE INDEX IF NOT EXISTS API_INDX_ID ON "ADVANCE_PAYMENT_ITEM"(ADVANCE_PAYMENT_CONFIGURATIONSID)
;

-- AFP_AND_INSURANCE

CREATE TABLE IF NOT EXISTS "AFP_AND_INSURANCE"
(
   AFP_AND_INSURANCEID bigint PRIMARY KEY NOT NULL,
   SIS double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_AFP ON "AFP_AND_INSURANCE"(AFP_AND_INSURANCEID)
;

-- AFP_ITEM

CREATE TABLE IF NOT EXISTS "AFP_ITEM"
(
   AFP integer,
   RATE double,
   AFP_AND_INSURANCEID bigint
)
;
ALTER TABLE "AFP_ITEM"
ADD CONSTRAINT IF NOT EXISTS FK_ADF_ITEM_AFP_AND_INSURANCEID
FOREIGN KEY (AFP_AND_INSURANCEID)
REFERENCES "AFP_AND_INSURANCE"(AFP_AND_INSURANCEID)
;
CREATE INDEX IF NOT EXISTS FK_AI_AAIID_INDX ON "AFP_ITEM"(AFP_AND_INSURANCEID)
;

-- TAXATION_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS "TAXATION_CONFIGURATIONS"
(
   TAXATION_CONFIGURATIONSID bigint PRIMARY KEY NOT NULL,
   EXEMPT double,
   FACTOR double,
   FROMR double,
   REDUCTION double,
   TO double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TAXATION_CONFIG ON "TAXATION_CONFIGURATIONS"(TAXATION_CONFIGURATIONSID)
;

-- FAMILY_ALLOWANCE_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS "FAMILY_ALLOWANCE_CONFIGURATIONS"
(
   FAMILY_ALLOWANCE_CONFIGURATIONSID bigint PRIMARY KEY NOT NULL,
   AMOUNT double,
   FROMR double,
   TO double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_FACONFIG ON "FAMILY_ALLOWANCE_CONFIGURATIONS"(FAMILY_ALLOWANCE_CONFIGURATIONSID)
;

-- CONTRACT 
CREATE TABLE IF NOT EXISTS "CONTRACT"
(
	CONTRACTID IDENTITY PRIMARY KEY NOT NULL,
	ACTIVE smallint default 0 ,
	FINISHED smallint default 0,
	contractDescription TEXT, 
    JOB integer not null,
    JOBCODE integer not null,
	name varchar(255) NOT NULL,
	-- finiquito
	settlement integer, 
	startDate timestamp NOT NULL, 
	step varchar(512) NOT NULL,
	terminationDate timestamp, 
	timeduration double, 
	valueTreatment integer, 
	LABORER_CONSTRUCTIONSITEID bigint
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_CONTRACT ON "CONTRACT"(CONTRACTID)
;

-- ANNEXED

CREATE TABLE IF NOT EXISTS "ANNEXED"
(
   ANNEXEDID IDENTITY PRIMARY KEY NOT NULL,
   ANNEXEDDESCRIPTION varchar(2147483647),
   STARTDATE timestamp,
   STEP varchar(2147483647),
   TERMINATIONDATE timestamp,
   CONTRACTID integer
)
;
ALTER TABLE "ANNEXED"
ADD CONSTRAINT IF NOT EXISTS FK_ANNEXED_CONTRACTID
FOREIGN KEY (CONTRACTID)
REFERENCES "CONTRACT"(CONTRACTID)
;
CREATE UNIQUE INDEX  IF NOT EXISTS PK_ANNEXED ON "ANNEXED"(ANNEXEDID)
;
CREATE INDEX  IF NOT EXISTS FK_ANNEXED_CONTRACTID_IND ON "ANNEXED"(CONTRACTID)
;


-- DATE_CONFIGURATIONS
CREATE TABLE IF NOT EXISTS "DATE_CONFIGURATIONS"
(
   DATE_CONFIGURATIONSID IDENTITY PRIMARY KEY NOT NULL,
   ADVANCE date,
   ASSISTANCE date,
   BEGIN_DEAL date,
   DATE date,
   FINISH_DEAL date
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_DT ON "DATE_CONFIGURATIONS"(DATE_CONFIGURATIONSID)
;

-- LOAN

CREATE TABLE IF NOT EXISTS "LOAN"
(
   LOANID IDENTITY PRIMARY KEY NOT NULL,
   DATEBUY timestamp NOT NULL,
   FEE integer,
   PRICE integer,
   STATUS varchar(255) NOT NULL,
   LABORER_CONSTRUCTIONSITEID bigint not null
)
;
ALTER TABLE "LOAN"
ADD CONSTRAINT IF NOT EXISTS FK_LOAN_LC
FOREIGN KEY (LABORER_CONSTRUCTIONSITEID)
REFERENCES "LABORER_CONSTRUCTIONSITE"(LABORER_CONSTRUCTIONSITEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LOAN ON "LOAN"(LOANID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_LOAN_LC ON "LOAN"(LABORER_CONSTRUCTIONSITEID)
;
