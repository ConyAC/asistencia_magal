CREATE TABLE IF NOT EXISTS "laborer"
(
   laborerId IDENTITY PRIMARY KEY NOT NULL,
   address varchar(255),
   afp integer NOT NULL,
   commune varchar(255),
   date_birth timestamp NOT NULL,
   dependents integer,
   firstname varchar(255) NOT NULL,
   isapre integer NOT NULL,
   lastname varchar(255) NOT NULL,
   marital_status integer NOT NULL,
   mobile_number varchar(255),
   nationality integer NOT NULL,
   phone varchar(255),
   photo varchar(255),
   rut varchar(255) NOT NULL,
   secondlastname varchar(255),
   secondname varchar(255),
   town varchar(255),
   wedge integer,
   provenance varchar(255),
   bank integer,
   bank_account varchar(255)   
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LABORER ON "laborer"(laborerId)
;

CREATE UNIQUE INDEX IF NOT EXISTS UK_RUT ON "laborer"(rut)
;

-- ROLE 

CREATE TABLE IF NOT EXISTS "role"
(
   roleId IDENTITY PRIMARY KEY NOT NULL,
   description varchar(255),
   name varchar(255) NOT NULL
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_ROLE ON "role"(roleId)
;


-- permission

CREATE TABLE IF NOT EXISTS "permissions"
(
   roleId bigint NOT NULL,
   role_permissions varchar(255)
)
;
ALTER TABLE "permissions"
ADD CONSTRAINT IF NOT EXISTS FK_PERMISSIONS
FOREIGN KEY (roleId)
REFERENCES "role"(roleId)
;
CREATE INDEX IF NOT EXISTS FK_INDX_PERMISSIONS ON "permissions"(roleId)
;


-- USER

CREATE TABLE IF NOT EXISTS "user"
(
   userId IDENTITY PRIMARY KEY NOT NULL,
   deleted boolean,
   email varchar(255) NOT NULL,
   firstname varchar(255) NOT NULL,
   lastname varchar(255) NOT NULL,
   password varchar(255),
   rut varchar(255) NOT NULL,
   salt varchar(255),
   status integer NOT NULL,
   roleId bigint
)
;
ALTER TABLE "user"
ADD CONSTRAINT IF NOT EXISTS FK_USER_ROLE
FOREIGN KEY (roleId)
REFERENCES "role"(roleId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_ROLE ON "user"(roleId)
;
CREATE UNIQUE INDEX IF NOT EXISTS UK_EMAIL ON "user"(email)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_USER ON "user"(userId)
;

-- CONSTRUCTIONCOMPANY
CREATE TABLE IF NOT EXISTS "construction_company"
(
   construction_companyId IDENTITY PRIMARY KEY NOT NULL,
   address varchar(255),
   commune varchar(255), 
   name varchar(255) NOT NULL,
   rut varchar(255) NOT NULL
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_CONSTRUCTIONCOMPANY ON "construction_company"(construction_companyId)
;

CREATE UNIQUE INDEX IF NOT EXISTS UK_RUT ON "construction_company"(rut)
;

-- CONSTRUCTION SITE


CREATE TABLE IF NOT EXISTS "construction_site"
(
   constructionsiteId IDENTITY PRIMARY KEY NOT NULL,
   code varchar(255) NOT NULL,
   address varchar(255),
   deleted boolean,
   name varchar(255) NOT NULL,
   status integer NOT NULL,
   person_in_chargeId bigint,
   construction_companyId bigint
)
;
ALTER TABLE "construction_site"
ADD CONSTRAINT IF NOT EXISTS FK_CS_USER
FOREIGN KEY (person_in_chargeId)
REFERENCES "user"(userId)
;
ALTER TABLE "construction_site"
ADD CONSTRAINT IF NOT EXISTS FK_CS_CONSTRUCTIONCOMPANY
FOREIGN KEY (construction_companyId)
REFERENCES "construction_company"(construction_companyId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_CS_CC ON "construction_site"(construction_companyId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_CS ON "construction_site"(person_in_chargeId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_CS ON "construction_site"(constructionsiteId)
;

CREATE TABLE IF NOT EXISTS "constructionsite_step"
(
	constructionsiteId bigint NOT NULL,
	step varchar(255) NOT NULL
);

-- USER_CONSTRUCTIONSITE

CREATE TABLE IF NOT EXISTS "user_constructionsite"
(
   constructionsiteId bigint NOT NULL,
   userId bigint NOT NULL,
   CONSTRAINT pf_user_constructionsite PRIMARY KEY (constructionsiteId,userId)
)
;
ALTER TABLE "user_constructionsite"
ADD CONSTRAINT IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_CONSTRUCTION_SITEID
FOREIGN KEY (constructionsiteId)
REFERENCES "construction_site"(constructionsiteId)
;
ALTER TABLE "user_constructionsite"
ADD CONSTRAINT IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_USERID
FOREIGN KEY (userId)
REFERENCES "user"(userId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_5 ON "user_constructionsite"
(
  constructionsiteId,
  userId
)
;
CREATE INDEX IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_CONSTRUCTION_SITEID_INDEX_5 ON "user_constructionsite"(constructionsiteId)
;
CREATE INDEX IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_USERID_INDEX_5 ON "user_constructionsite"(userId)
;


-- LABORER_CONSTRUCTIONSITE

CREATE TABLE IF NOT EXISTS "laborer_constructionsite"
(
   laborer_constructionsiteId IDENTITY PRIMARY KEY NOT NULL,
   laborerId integer NOT NULL,
   active smallint,
   confirmed smallint,
   reward integer default 0,
   use_default_dates smallint  default 1,
   reward_startdate timestamp,
   reward_enddate timestamp,
   block boolean,
   person_blockId bigint,
   comment varchar(255),
   constructionsiteId integer NOT NULL,
   suple_code integer
)
;
ALTER TABLE "laborer_constructionsite"
ADD CONSTRAINT IF NOT EXISTS FK_LC_LABORER
FOREIGN KEY (laborerId)
REFERENCES "laborer"(laborerId)
;
CREATE INDEX IF NOT EXISTS  FK_IDX_LC_LABORER ON "laborer_constructionsite"(laborerId)
;
ALTER TABLE "laborer_constructionsite"
ADD CONSTRAINT IF NOT EXISTS FK_LC_CS
FOREIGN KEY (constructionsiteId)
REFERENCES "construction_site"(constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_IDX_LC_CS ON "laborer_constructionsite"(constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LC ON "laborer_constructionsite"(laborer_constructionsiteId)
;

-- ABSENCE
/*
CREATE TABLE IF NOT EXISTS "ABSENCE"
(
   ABSENCEID IDENTITY PRIMARY KEY NOT NULL,
   ABSENCE_TYPE integer NOT NULL,
   DESCRIPTION varchar(255) NOT NULL,
   FROM_DATE date NOT NULL,
   TO_DATE date NOT NULL,
   LABORER_CONSTRUCTIONSITEID bigint NOT NULL
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
*/
-- TOOL
-- se cambia el nombre a tools, dado que tool da problemas en mysql
CREATE TABLE IF NOT EXISTS "tools"
(
   toolId IDENTITY PRIMARY KEY NOT NULL,
   date_buy timestamp NOT NULL,
   fee integer NOT NULL,
   name varchar(255) NOT NULL,
   price integer NOT NULL,
   status integer NOT NULL default 2, -- en deuda
   laborer_constructionsiteId bigint not null
)
;
ALTER TABLE "tools"
ADD CONSTRAINT IF NOT EXISTS FK_TOOL_LC
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES "laborer_constructionsite"(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TOOL ON "tools"(toolId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TOOL_LC ON "tools"(laborer_constructionsiteId)
;

-- LOAN

CREATE TABLE IF NOT EXISTS "loan"
(
   loanId IDENTITY PRIMARY KEY NOT NULL,
   date_buy timestamp NOT NULL,
   fee integer,
   price integer,
   status integer NOT NULL default 2, -- en deuda
   laborer_constructionsiteId bigint not null
)
;
ALTER TABLE "loan"
ADD CONSTRAINT IF NOT EXISTS FK_LOAN_LC
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES "laborer_constructionsite"(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LOAN ON "loan"(loanId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_LOAN_LC ON "loan"(laborer_constructionsiteId)
;

-- VACATION

CREATE TABLE IF NOT EXISTS "vacation"
(
   vacationId IDENTITY PRIMARY KEY NOT NULL,
   from_date date not null,
   to_date date not null,
   progressive integer,
   laborer_constructionsiteId bigint not null,
   confirmed boolean
)
;
ALTER TABLE "vacation"
ADD CONSTRAINT IF NOT EXISTS FK_VACATION_LC
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES "laborer_constructionsite"(laborer_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_VACATION_LC ON "vacation"(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_VACATION ON "vacation"(vacationId)
;


-- ACCIDENTS

CREATE TABLE IF NOT EXISTS "accident"
(
   accidentId IDENTITY PRIMARY KEY NOT NULL,
   accident_level integer NOT NULL,
   description varchar(1024) NOT NULL,
   from_date date NOT NULL,
   to_date date NOT NULL,
   was_negligence boolean NOT NULL,
   confirmed boolean,
   laborer_constructionsiteId bigint NOT NULL
)
;
ALTER TABLE "accident"
ADD CONSTRAINT IF NOT EXISTS FK_ACCIDENT_LABORER
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES "laborer_constructionsite"(laborer_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_ACCIDENT_LABORER ON "accident"(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_ACCIDENT ON "accident"(accidentId)
;

-- WAGE_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS "wage_configurations"
(
   wage_configurationsId bigint PRIMARY KEY NOT NULL,
   collation double,
   minimum_wage double,
   mobilization double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_WG ON "wage_configurations"(wage_configurationsId)
;


-- MOBILIZATION2
CREATE TABLE IF NOT EXISTS "mobilization2"
(
   wage_configurationsId IDENTITY NOT NULL,
   amount double,
   linked_constructionsiteId bigint
)
;
ALTER TABLE "mobilization2"
ADD CONSTRAINT IF NOT EXISTS FK_MOBILIZATION2_CS
FOREIGN KEY (linked_constructionsiteId)
REFERENCES "construction_site"(constructionsiteId)
;
ALTER TABLE "mobilization2"
ADD CONSTRAINT IF NOT EXISTS FK_MOBILIZATION2_WC
FOREIGN KEY (wage_configurationsId)
REFERENCES "wage_configurations"(wage_configurationsId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_MOB2_CS ON "mobilization2"(linked_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_MOB2_WC ON "mobilization2"(wage_configurationsId)
;

-- TEAM

CREATE TABLE IF NOT EXISTS "team"
(
   teamId IDENTITY PRIMARY KEY NOT NULL,
   date timestamp,
   deleted boolean,
   name varchar(255) NOT NULL,
   status integer NOT NULL,
   constructionsiteId bigint,
   leaderId bigint
)
;
ALTER TABLE "team"
ADD CONSTRAINT IF NOT EXISTS FK_TEAM_LABORER
FOREIGN KEY (leaderId)
REFERENCES "laborer"(laborerId)
;
ALTER TABLE "team"
ADD CONSTRAINT IF NOT EXISTS FK_TEAM_CS
FOREIGN KEY (constructionsiteId)
REFERENCES "construction_site"(constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TEAM_LABORER ON "team"(leaderId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TEAM ON "team"(teamId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TEAM_CS ON "team"(constructionsiteId)
;

CREATE TABLE IF NOT EXISTS "laborer_constructionsite_team"
(
	teamId bigint,
	laborer_constructionsiteId bigint
)
;

/*
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
*/

-- ADVANCE_PAYMENT_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS "advance_payment_configurations"
(
   advance_payment_configurationsId bigint PRIMARY KEY NOT NULL,
   permission_discount double,
   failure_discount double,
   constructionsiteId bigint
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_APC ON "advance_payment_configurations"(advance_payment_configurationsId)
;



-- ADVANCE_PAYMENT_ITEM

CREATE TABLE IF NOT EXISTS "advance_payment_item"
(
   suple_code integer,
   suple_increase_amount double,
   suple_normal_amount double,
   advance_payment_configurationsId bigint
)
;
ALTER TABLE "advance_payment_item"
ADD CONSTRAINT IF NOT EXISTS ADVANCEPAYMENTITEMADVANCE_PAYMENT_CONFIGURATIONSID
FOREIGN KEY (advance_payment_configurationsId)
REFERENCES "advance_payment_configurations"(advance_payment_configurationsId)
;
CREATE UNIQUE INDEX IF NOT EXISTS UK_INDX_SUPLE ON "advance_payment_item"(suple_code)
;
CREATE INDEX IF NOT EXISTS API_INDX_ID ON "advance_payment_item"(advance_payment_configurationsId)
;

-- AFP_AND_INSURANCE

CREATE TABLE IF NOT EXISTS "afp_and_insurance"
(
   afp_and_insuranceId bigint PRIMARY KEY NOT NULL,
   sis double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_AFP ON "afp_and_insurance"(afp_and_insuranceId)
;

-- AFP_ITEM

CREATE TABLE IF NOT EXISTS "afp_item"
(
   afp integer,
   rate double,
   afp_and_insuranceId bigint
)
;
ALTER TABLE "afp_item"
ADD CONSTRAINT IF NOT EXISTS FK_ADF_ITEM_AFP_AND_INSURANCEID
FOREIGN KEY (afp_and_insuranceId)
REFERENCES "afp_and_insurance"(afp_and_insuranceId)
;
CREATE INDEX IF NOT EXISTS FK_AI_AAIID_INDX ON "afp_item"(afp_and_insuranceId)
;

-- TAXATION_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS "taxation_configurations"
(
   taxation_configurationsId bigint PRIMARY KEY NOT NULL,
   exempt double,
   factor double,
   fromr double,
   reduction double,
   tor double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TAXATION_CONFIG ON "taxation_configurations"(taxation_configurationsId)
;

-- 	_ALLOWANCE_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS "family_allowance_configurations"
(
   family_allowance_configurationsId bigint PRIMARY KEY NOT NULL,
   amount double,
   fromr double,
   tor double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_FACONFIG ON "family_allowance_configurations"(family_allowance_configurationsId)
;

-- CONTRACT 
CREATE TABLE IF NOT EXISTS "contract"
(
	contractId IDENTITY PRIMARY KEY NOT NULL,
	active smallint default 0 ,
	finished smallint default 0,
	contract_description TEXT, 
    job integer not null,
    job_code integer not null,
	name varchar(255) NOT NULL,
	-- finiquito
	settlement integer, 
	startdate timestamp NOT NULL, 
	step varchar(512) NOT NULL,
	terminationdate timestamp, 
	timeduration double, 
	value_treatment integer, 
	laborer_constructionsiteId bigint not null -- no puede ser nulo
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_CONTRACT ON "contract"(contractId)
;

-- ANNEXED

CREATE TABLE IF NOT EXISTS "annexed"
(
   annexedId IDENTITY PRIMARY KEY NOT NULL,
   annexed_description varchar(2147483647),
   startdate timestamp,
   step varchar(2147483647),
   terminationdate timestamp,
   contractId integer
)
;
ALTER TABLE "annexed"
ADD CONSTRAINT IF NOT EXISTS FK_ANNEXED_CONTRACTID
FOREIGN KEY (contractId)
REFERENCES "contract"(contractId)
;
CREATE UNIQUE INDEX  IF NOT EXISTS PK_ANNEXED ON "annexed"(annexedId)
;
CREATE INDEX  IF NOT EXISTS FK_ANNEXED_CONTRACTID_IND ON "annexed"(contractId)
;


-- DATE_CONFIGURATIONS
CREATE TABLE IF NOT EXISTS "date_configurations"
(
   date_configurationsId IDENTITY PRIMARY KEY NOT NULL,
   advance date,
   assistance date,
   begin_deal date,
   date date,
   finish_deal date,
   benzine double,
   oil double,
   uf double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_DT ON "date_configurations"(date_configurationsId)
;


-- dates
CREATE TABLE IF NOT EXISTS "postponedpaymenttool"
(
   toolId bigint NOT NULL,
   tool_date date
)
;
ALTER TABLE "postponedpaymenttool"
ADD CONSTRAINT IF NOT EXISTS FK_POSTPONEDPAYMENTTOOL
FOREIGN KEY (toolId)
REFERENCES "tools"(toolId)
;
CREATE INDEX IF NOT EXISTS FK_INDX_POSTPONEDPAYMENTTOOL ON "postponedpaymenttool"(toolId)
;

-- dates
CREATE TABLE IF NOT EXISTS "postponedpaymentloan"
(
   loanId bigint NOT NULL,
   loan_date date
)
;
ALTER TABLE "postponedpaymentloan"
ADD CONSTRAINT IF NOT EXISTS FK_POSTPONEDPAYMENTLOAN
FOREIGN KEY (loanId)
REFERENCES "loan"(loanId)
;
CREATE INDEX IF NOT EXISTS FK_INDX_POSTPONEDPAYMENTLOAN ON "postponedpaymentloan"(loanId)
;

-- TRIGGER
-- CREATE TRIGGER DEFAULT_DATE_REWARD BEFORE INSERT ON laborer_constructionsite
-- FOR EACH ROW SET NEW.reward_enddate = IFNULL(NEW.reward_enddate, NOW()), NEW.reward_startdate = IFNULL(NEW.reward_startdate, NOW());



CREATE TABLE IF NOT EXISTS attendance
(
   attendanceId bigint PRIMARY KEY NOT NULL,
   jornal integer,
   dmp1 integer ,
   dmp10 integer ,
   dmp11 integer ,
   dmp12 integer ,
   dmp13 integer ,
   dmp14 integer ,
   dmp15 integer ,
   dmp16 integer ,
   dmp17 integer ,
   dmp18 integer ,
   dmp19 integer ,
   dmp2 integer ,
   dmp20 integer ,
   dmp21 integer ,
   dmp22 integer ,
   dmp23 integer ,
   dmp24 integer ,
   dmp25 integer ,
   dmp26 integer ,
   dmp27 integer ,
   dmp28 integer ,
   dmp29 integer,
   dmp3 integer ,
   dmp30 integer,
   dmp31 integer,
   dmp4 integer ,
   dmp5 integer ,
   dmp6 integer ,
   dmp7 integer ,
   dmp8 integer ,
   dmp9 integer ,
   dma1 integer NOT NULL,
   dma10 integer NOT NULL,
   dma11 integer NOT NULL,
   dma12 integer NOT NULL,
   dma13 integer NOT NULL,
   dma14 integer NOT NULL,
   dma15 integer NOT NULL,
   dma16 integer NOT NULL,
   dma17 integer NOT NULL,
   dma18 integer NOT NULL,
   dma19 integer NOT NULL,
   dma2 integer NOT NULL,
   dma20 integer NOT NULL,
   dma21 integer NOT NULL,
   dma22 integer NOT NULL,
   dma23 integer NOT NULL,
   dma24 integer NOT NULL,
   dma25 integer NOT NULL,
   dma26 integer NOT NULL,
   dma27 integer NOT NULL,
   dma28 integer NOT NULL,
   dma29 integer,
   dma3 integer NOT NULL,
   dma30 integer,
   dma31 integer,
   dma4 integer NOT NULL,
   dma5 integer NOT NULL,
   dma6 integer NOT NULL,
   dma7 integer NOT NULL,
   dma8 integer NOT NULL,
   dma9 integer NOT NULL,
   date date,
   laborer_constructionsiteId bigint NOT NULL
)
;
ALTER TABLE attendance
ADD CONSTRAINT IF NOT EXISTS FK_ATTENDANCE_LABORER_CONSTRUCTIONSITEID
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES "laborer_constructionsite"(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS  PRIMARY_KEY_8E ON attendance(attendanceId)
;
CREATE INDEX IF NOT EXISTS FK_ATTENDANCE_LABORER_CONSTRUCTIONSITEID_INDEX_8 ON attendance(laborer_constructionsiteId)
;



CREATE TABLE IF NOT EXISTS overtime
(
   overtimeId bigint PRIMARY KEY NOT NULL,
   dmp1 integer ,
   dmp10 integer ,
   dmp11 integer ,
   dmp12 integer ,
   dmp13 integer ,
   dmp14 integer ,
   dmp15 integer ,
   dmp16 integer ,
   dmp17 integer ,
   dmp18 integer ,
   dmp19 integer ,
   dmp2 integer ,
   dmp20 integer ,
   dmp21 integer ,
   dmp22 integer ,
   dmp23 integer ,
   dmp24 integer ,
   dmp25 integer ,
   dmp26 integer ,
   dmp27 integer ,
   dmp28 integer ,
   dmp29 integer,
   dmp3 integer ,
   dmp30 integer,
   dmp31 integer,
   dmp4 integer ,
   dmp5 integer ,
   dmp6 integer ,
   dmp7 integer ,
   dmp8 integer ,
   dmp9 integer ,
   dma1 integer NOT NULL,
   dma10 integer NOT NULL,
   dma11 integer NOT NULL,
   dma12 integer NOT NULL,
   dma13 integer NOT NULL,
   dma14 integer NOT NULL,
   dma15 integer NOT NULL,
   dma16 integer NOT NULL,
   dma17 integer NOT NULL,
   dma18 integer NOT NULL,
   dma19 integer NOT NULL,
   dma2 integer NOT NULL,
   dma20 integer NOT NULL,
   dma21 integer NOT NULL,
   dma22 integer NOT NULL,
   dma23 integer NOT NULL,
   dma24 integer NOT NULL,
   dma25 integer NOT NULL,
   dma26 integer NOT NULL,
   dma27 integer NOT NULL,
   dma28 integer NOT NULL,
   dma29 integer,
   dma3 integer NOT NULL,
   dma30 integer,
   dma31 integer,
   dma4 integer NOT NULL,
   dma5 integer NOT NULL,
   dma6 integer NOT NULL,
   dma7 integer NOT NULL,
   dma8 integer NOT NULL,
   dma9 integer NOT NULL,
   date date NOT NULL,
   laborer_constructionsiteId bigint NOT NULL
)
;
ALTER TABLE overtime
ADD CONSTRAINT IF NOT EXISTS FK_OVERTIME_LABORER_CONSTRUCTIONSITEID
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES "laborer_constructionsite"(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_4E ON overtime(overtimeId)
;
CREATE INDEX IF NOT EXISTS FK_OVERTIME_LABORER_CONSTRUCTIONSITEID_INDEX_4 ON overtime(laborer_constructionsiteId)
;


CREATE TABLE IF NOT EXISTS confirmations 
(
  confirmationsId bigint(20) IDENTITY PRIMARY KEY NOT NULL,
  central_check tinyint(1) DEFAULT '0',
  constructionsite_check tinyint(1) DEFAULT '0',
  date date NOT NULL,
  constructionsiteId bigint(20) NOT NULL
) ;

CREATE TABLE IF NOT EXISTS license (
  licenseId bigint(20) IDENTITY PRIMARY KEY NOT NULL,
  confirmed tinyint(1) DEFAULT '0',
  description varchar(1024) DEFAULT NULL,
  from_date date DEFAULT NULL,
  absence_type int(11) DEFAULT NULL,
  to_date date DEFAULT NULL,
  laborer_constructionsiteId bigint(20) NOT NULL
) ;

ALTER TABLE license
ADD CONSTRAINT IF NOT EXISTS fk_license_laborer_constructionsiteId
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;


CREATE TABLE IF NOT EXISTS salary (
  salarytId bigint(20) IDENTITY PRIMARY KEY NOT NULL,
  date date NOT NULL,
  jornal integer,
  salary int(11) DEFAULT NULL,
  suple int(11) DEFAULT NULL,
  laborer_constructionsiteId bigint(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS extra_params
(
   extra_paramsId IDENTITY PRIMARY KEY NOT NULL,
   mov2_bond integer,
   date date NOT NULL,
   desc_hours integer,
   km integer,
   overtime_hours integer,
   special_bond integer,
   laborer_constructionsiteId bigint NOT NULL
)
;
ALTER TABLE extra_params
ADD CONSTRAINT IF NOT EXISTS FK_EXTRA_PARAMS_LABORER_CONSTRUCTIONSITEID
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES "laborer_constructionsite"(laborer_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS FK_EXTRA_PARAMS_LABORER_CONSTRUCTIONSITEID_INDEX_7 ON extra_params(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS uq_extra_param ON extra_params(extra_paramsId)
;