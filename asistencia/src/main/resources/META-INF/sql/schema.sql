CREATE TABLE IF NOT EXISTS laborer
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
   bank_account varchar(255)  ,
   validity_pension_review date 
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LABORER ON laborer(laborerId)
;

CREATE UNIQUE INDEX IF NOT EXISTS UK_RUT ON laborer(rut)
;

-- ROLE 

CREATE TABLE IF NOT EXISTS role
(
   roleId IDENTITY PRIMARY KEY NOT NULL,
   description varchar(255),
   name varchar(255) NOT NULL
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_ROLE ON role(roleId)
;


-- permission

CREATE TABLE IF NOT EXISTS permissions
(
   roleId bigint NOT NULL,
   role_permissions varchar(255)
)
;
ALTER TABLE permissions
ADD CONSTRAINT IF NOT EXISTS FK_PERMISSIONS
FOREIGN KEY (roleId)
REFERENCES role(roleId)
;
CREATE INDEX IF NOT EXISTS FK_INDX_PERMISSIONS ON permissions(roleId)
;


-- USER

CREATE TABLE IF NOT EXISTS user
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
ALTER TABLE user
ADD CONSTRAINT IF NOT EXISTS FK_USER_ROLE
FOREIGN KEY (roleId)
REFERENCES role(roleId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_ROLE ON user(roleId)
;
CREATE UNIQUE INDEX IF NOT EXISTS UK_EMAIL ON user(email)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_USER ON user(userId)
;

-- CONSTRUCTIONCOMPANY
CREATE TABLE IF NOT EXISTS construction_company
(
   construction_companyId IDENTITY PRIMARY KEY NOT NULL,
   address varchar(255),
   commune varchar(255), 
   name varchar(255) NOT NULL,
   rut varchar(255) NOT NULL,
   chief_executive varchar(512) DEFAULT NULL
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_CONSTRUCTIONCOMPANY ON construction_company(construction_companyId)
;

CREATE UNIQUE INDEX IF NOT EXISTS UK_RUT ON construction_company(rut)
;

-- CONSTRUCTION SITE


CREATE TABLE IF NOT EXISTS construction_site
(
   constructionsiteId IDENTITY PRIMARY KEY NOT NULL,
   code varchar(255) NOT NULL,
   cost_center integer NOT NULL,
   address varchar(255),
   deleted boolean,
   name varchar(255) NOT NULL,
   status integer NOT NULL,
   person_in_chargeId bigint,
   construction_companyId bigint,
   commune varchar(512) DEFAULT NULL
)
;
ALTER TABLE construction_site
ADD CONSTRAINT IF NOT EXISTS FK_CS_USER
FOREIGN KEY (person_in_chargeId)
REFERENCES user(userId)
;
ALTER TABLE construction_site
ADD CONSTRAINT IF NOT EXISTS FK_CS_CONSTRUCTIONCOMPANY
FOREIGN KEY (construction_companyId)
REFERENCES construction_company(construction_companyId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_CS_CC ON construction_site(construction_companyId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_CS ON construction_site(person_in_chargeId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_CS ON construction_site(constructionsiteId)
;

CREATE TABLE IF NOT EXISTS constructionsite_step
(
	constructionsiteId bigint NOT NULL,
	step varchar(255) NOT NULL
);

-- USER_CONSTRUCTIONSITE

CREATE TABLE IF NOT EXISTS user_constructionsite
(
   constructionsiteId bigint NOT NULL,
   userId bigint NOT NULL,
   CONSTRAINT pk_user_constructionsite PRIMARY KEY (constructionsiteId,userId)
)
;
ALTER TABLE user_constructionsite
ADD CONSTRAINT IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_CONSTRUCTION_SITEID
FOREIGN KEY (constructionsiteId)
REFERENCES construction_site(constructionsiteId)
;
ALTER TABLE user_constructionsite
ADD CONSTRAINT IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_USERID
FOREIGN KEY (userId)
REFERENCES user(userId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_5 ON user_constructionsite
(
  constructionsiteId,
  userId
)
;
CREATE INDEX IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_CONSTRUCTION_SITEID_INDEX_5 ON user_constructionsite(constructionsiteId)
;
CREATE INDEX IF NOT EXISTS FK_USER_CONSTRUCTIONSITE_USERID_INDEX_5 ON user_constructionsite(userId)
;


-- LABORER_CONSTRUCTIONSITE

CREATE TABLE IF NOT EXISTS laborer_constructionsite
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
ALTER TABLE laborer_constructionsite
ADD CONSTRAINT IF NOT EXISTS FK_LC_LABORER
FOREIGN KEY (laborerId)
REFERENCES laborer(laborerId)
;
CREATE INDEX IF NOT EXISTS  FK_IDX_LC_LABORER ON laborer_constructionsite(laborerId)
;
ALTER TABLE laborer_constructionsite
ADD CONSTRAINT IF NOT EXISTS FK_LC_CS
FOREIGN KEY (constructionsiteId)
REFERENCES construction_site(constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_IDX_LC_CS ON laborer_constructionsite(constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LC ON laborer_constructionsite(laborer_constructionsiteId)
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
REFERENCES laborer_constructionsite(LABORER_CONSTRUCTIONSITEID)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_ABSENCE ON "ABSENCE"(ABSENCEID)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_ABSENCE_LC ON "ABSENCE"(LABORER_CONSTRUCTIONSITEID)
;
*/
-- TOOL
-- se cambia el nombre a tools, dado que tool da problemas en mysql
CREATE TABLE IF NOT EXISTS tools
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
ALTER TABLE tools
ADD CONSTRAINT IF NOT EXISTS FK_TOOL_LC
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TOOL ON tools(toolId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TOOL_LC ON tools(laborer_constructionsiteId)
;

-- LOAN

CREATE TABLE IF NOT EXISTS loan
(
   loanId IDENTITY PRIMARY KEY NOT NULL,
   date_buy timestamp NOT NULL,
   fee integer,
   price integer,
   status integer NOT NULL default 2, -- en deuda
   laborer_constructionsiteId bigint not null
)
;
ALTER TABLE loan
ADD CONSTRAINT IF NOT EXISTS FK_LOAN_LC
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_LOAN ON loan(loanId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_LOAN_LC ON loan(laborer_constructionsiteId)
;

-- VACATION

CREATE TABLE IF NOT EXISTS vacation
(
   vacationId IDENTITY PRIMARY KEY NOT NULL,
   from_date date not null,
   to_date date not null,
   progressive integer,
   laborer_constructionsiteId bigint not null,
   confirmed boolean
)
;
ALTER TABLE vacation
ADD CONSTRAINT IF NOT EXISTS FK_VACATION_LC
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_VACATION_LC ON vacation(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_VACATION ON vacation(vacationId)
;

-- PROGESSIVE VACATION

CREATE TABLE IF NOT EXISTS progressive_vacation
(
   progressive_vacationId IDENTITY PRIMARY KEY NOT NULL,
   from_date date not null,
   to_date date not null,
   progressive integer,
   laborer_constructionsiteId bigint not null,
   confirmed boolean
)
;
ALTER TABLE progressive_vacation
ADD CONSTRAINT IF NOT EXISTS FK_PROGRESSIVE_VACATION_LC
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_PROGRESSIVE_VACATION_LC ON progressive_vacation(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_PROGRESSIVE_VACATION ON progressive_vacation(progressive_vacationId)
;


-- ACCIDENTS

CREATE TABLE IF NOT EXISTS accident
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
ALTER TABLE accident
ADD CONSTRAINT IF NOT EXISTS FK_ACCIDENT_LABORER
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_ACCIDENT_LABORER ON accident(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_ACCIDENT ON accident(accidentId)
;

-- WAGE_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS wage_configurations
(
   wage_configurationsId bigint PRIMARY KEY NOT NULL,
   collation double,
   minimum_wage double,
   mobilization double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_WG ON wage_configurations(wage_configurationsId)
;


-- MOBILIZATION2
CREATE TABLE IF NOT EXISTS mobilization2
(
   wage_configurationsId bigint  NOT NULL,
   amount double,
   linked_constructionsiteId bigint
)
;
ALTER TABLE mobilization2
ADD CONSTRAINT IF NOT EXISTS FK_MOBILIZATION2_CS
FOREIGN KEY (linked_constructionsiteId)
REFERENCES construction_site(constructionsiteId)
;
ALTER TABLE mobilization2
ADD CONSTRAINT IF NOT EXISTS FK_MOBILIZATION2_WC
FOREIGN KEY (wage_configurationsId)
REFERENCES wage_configurations(wage_configurationsId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_MOB2_CS ON mobilization2(linked_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_MOB2_WC ON mobilization2(wage_configurationsId)
;

-- TEAM

CREATE TABLE IF NOT EXISTS team
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
ALTER TABLE team
ADD CONSTRAINT IF NOT EXISTS FK_TEAM_LABORER
FOREIGN KEY (leaderId)
REFERENCES laborer(laborerId)
;
ALTER TABLE team
ADD CONSTRAINT IF NOT EXISTS FK_TEAM_CS
FOREIGN KEY (constructionsiteId)
REFERENCES construction_site(constructionsiteId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TEAM_LABORER ON team(leaderId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TEAM ON team(teamId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_TEAM_CS ON team(constructionsiteId)
;

CREATE TABLE IF NOT EXISTS laborer_constructionsite_team
(
	teamId bigint,
	laborer_constructionsiteId bigint
)
;

/*
-- CONSTRUCTION SITE TEAM

CREATE TABLE IF NOT EXISTS construction_site_team
(
   cst_csId bigint NOT NULL,
   teams_teamId bigint NOT NULL
)
;
ALTER TABLE construction_site_team
ADD CONSTRAINT IF NOT EXISTS FK_CST_CS
FOREIGN KEY (cst_csId)
REFERENCES construction_site(constructionsiteId)
;
ALTER TABLE construction_site_team
ADD CONSTRAINT IF NOT EXISTS FK_CST_TEAM
FOREIGN KEY (teams_teamId)
REFERENCES team(teamId)
;
CREATE INDEX IF NOT EXISTS  FK_INDX_CS_TEAM ON construction_site_team(cst_csId)
;
CREATE UNIQUE INDEX IF NOT EXISTS UK_INDX_CS ON construction_site_team(teams_teamId)
;
*/

-- ADVANCE_PAYMENT_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS advance_payment_configurations
(
   advance_payment_configurationsId bigint IDENTITY PRIMARY KEY NOT NULL,
   permission_discount double,
   failure_discount double,
   constructionsiteId bigint
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_APC ON advance_payment_configurations(advance_payment_configurationsId)
;



-- ADVANCE_PAYMENT_ITEM

CREATE TABLE IF NOT EXISTS advance_payment_item
(
   suple_code integer,
   suple_increase_amount double,
   suple_normal_amount double,
   advance_payment_configurationsId bigint
)
;
ALTER TABLE advance_payment_item
ADD CONSTRAINT IF NOT EXISTS ADVANCEPAYMENTITEMADVANCE_PAYMENT_CONFIGURATIONSID
FOREIGN KEY (advance_payment_configurationsId)
REFERENCES advance_payment_configurations(advance_payment_configurationsId)
;
CREATE UNIQUE INDEX IF NOT EXISTS UK_INDX_SUPLE ON advance_payment_item(suple_code,advance_payment_configurationsId)
;
CREATE INDEX IF NOT EXISTS API_INDX_ID ON advance_payment_item(advance_payment_configurationsId)
;

-- AFP_AND_INSURANCE

CREATE TABLE IF NOT EXISTS afp_and_insurance
(
   afp_and_insuranceId bigint PRIMARY KEY NOT NULL,
   sis double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_AFP ON afp_and_insurance(afp_and_insuranceId)
;

-- AFP_ITEM

CREATE TABLE IF NOT EXISTS afp_item
(
   afp integer,
   rate double,
   afp_and_insuranceId bigint
)
;
ALTER TABLE afp_item
ADD CONSTRAINT IF NOT EXISTS FK_ADF_ITEM_AFP_AND_INSURANCEID
FOREIGN KEY (afp_and_insuranceId)
REFERENCES afp_and_insurance(afp_and_insuranceId)
;
CREATE INDEX IF NOT EXISTS FK_AI_AAIID_INDX ON afp_item(afp_and_insuranceId)
;

-- TAXATION_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS taxation_configurations
(
   taxation_configurationsId bigint PRIMARY KEY NOT NULL,
   exempt double,
   factor double,
   fromr double,
   reduction double,
   tor double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_TAXATION_CONFIG ON taxation_configurations(taxation_configurationsId)
;

-- 	_ALLOWANCE_CONFIGURATIONS

CREATE TABLE IF NOT EXISTS family_allowance_configurations
(
   family_allowance_configurationsId bigint PRIMARY KEY NOT NULL,
   amount double,
   fromr double,
   tor double
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_FACONFIG ON family_allowance_configurations(family_allowance_configurationsId)
;

-- CONTRACT 
CREATE TABLE IF NOT EXISTS contract
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
	laborer_constructionsiteId bigint not null, -- no puede ser nulo
	specialityId bigint
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_CONTRACT ON contract(contractId)
;

-- ANNEXED

CREATE TABLE IF NOT EXISTS annexed
(
   annexedId IDENTITY PRIMARY KEY NOT NULL,
   annexed_description varchar(2147483647),
   startdate timestamp,
   step varchar(2147483647),
   terminationdate timestamp,
   contractId integer
)
;
ALTER TABLE annexed
ADD CONSTRAINT IF NOT EXISTS FK_ANNEXED_CONTRACTID
FOREIGN KEY (contractId)
REFERENCES contract(contractId)
;
CREATE UNIQUE INDEX  IF NOT EXISTS PK_ANNEXED ON annexed(annexedId)
;
CREATE INDEX  IF NOT EXISTS FK_ANNEXED_CONTRACTID_IND ON annexed(contractId)
;


-- DATE_CONFIGURATIONS
CREATE TABLE IF NOT EXISTS date_configurations
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
CREATE UNIQUE INDEX IF NOT EXISTS PK_DT ON date_configurations(date_configurationsId)
;


-- dates
CREATE TABLE IF NOT EXISTS postponedpaymenttool
(
   toolId bigint NOT NULL,
   tool_date date
)
;
ALTER TABLE postponedpaymenttool
ADD CONSTRAINT IF NOT EXISTS FK_POSTPONEDPAYMENTTOOL
FOREIGN KEY (toolId)
REFERENCES tools(toolId)
;
CREATE INDEX IF NOT EXISTS FK_INDX_POSTPONEDPAYMENTTOOL ON postponedpaymenttool(toolId)
;

-- dates
CREATE TABLE IF NOT EXISTS postponedpaymentloan
(
   loanId bigint NOT NULL,
   loan_date date
)
;
ALTER TABLE postponedpaymentloan
ADD CONSTRAINT IF NOT EXISTS FK_POSTPONEDPAYMENTLOAN
FOREIGN KEY (loanId)
REFERENCES loan(loanId)
;
CREATE INDEX IF NOT EXISTS FK_INDX_POSTPONEDPAYMENTLOAN ON postponedpaymentloan(loanId)
;

-- TRIGGER
-- CREATE TRIGGER DEFAULT_DATE_REWARD BEFORE INSERT ON laborer_constructionsite
-- FOR EACH ROW SET NEW.reward_enddate = IFNULL(NEW.reward_enddate, NOW()), NEW.reward_startdate = IFNULL(NEW.reward_startdate, NOW());



CREATE TABLE IF NOT EXISTS attendance
(
   attendanceId IDENTITY PRIMARY KEY NOT NULL,
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
   dma1 integer,
   dma10 integer ,
   dma11 integer ,
   dma12 integer ,
   dma13 integer ,
   dma14 integer ,
   dma15 integer ,
   dma16 integer ,
   dma17 integer ,
   dma18 integer ,
   dma19 integer ,
   dma2 integer ,
   dma20 integer ,
   dma21 integer ,
   dma22 integer ,
   dma23 integer ,
   dma24 integer ,
   dma25 integer ,
   dma26 integer ,
   dma27 integer ,
   dma28 integer ,
   dma29 integer,
   dma3 integer ,
   dma30 integer,
   dma31 integer,
   dma4 integer,
   dma5 integer,
   dma6 integer,
   dma7 integer,
   dma8 integer,
   dma9 integer,
   date date,
   laborer_constructionsiteId bigint NOT NULL
)
;
ALTER TABLE attendance
ADD CONSTRAINT IF NOT EXISTS FK_ATTENDANCE_LABORER_CONSTRUCTIONSITEID
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS  PRIMARY_KEY_8E ON attendance(attendanceId)
;
CREATE INDEX IF NOT EXISTS FK_ATTENDANCE_LABORER_CONSTRUCTIONSITEID_INDEX_8 ON attendance(laborer_constructionsiteId)
;



CREATE TABLE IF NOT EXISTS overtime
(
   overtimeId IDENTITY PRIMARY KEY NOT NULL,
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
   dma1 integer ,
   dma10 integer ,
   dma11 integer ,
   dma12 integer ,
   dma13 integer ,
   dma14 integer ,
   dma15 integer ,
   dma16 integer ,
   dma17 integer ,
   dma18 integer ,
   dma19 integer ,
   dma2 integer ,
   dma20 integer ,
   dma21 integer ,
   dma22 integer ,
   dma23 integer ,
   dma24 integer ,
   dma25 integer ,
   dma26 integer ,
   dma27 integer ,
   dma28 integer ,
   dma29 integer,
   dma3 integer ,
   dma30 integer,
   dma31 integer,
   dma4 integer ,
   dma5 integer ,
   dma6 integer ,
   dma7 integer ,
   dma8 integer ,
   dma9 integer ,
   date date NOT NULL,
   laborer_constructionsiteId bigint NOT NULL
)
;
ALTER TABLE overtime
ADD CONSTRAINT IF NOT EXISTS FK_OVERTIME_LABORER_CONSTRUCTIONSITEID
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
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
  suple_central_check tinyint(1) DEFAULT '0',
  suple_obra_check tinyint(1) DEFAULT '0',
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
  calculated_suple BOOLEAN DEFAULT true,
  mov2_bond integer,
  desc_hours integer,
  overtime_hours integer,
  special_bond integer,
  loan_bond integer,
  laborer_constructionsiteId bigint(20) NOT NULL
);

ALTER TABLE salary
ADD CONSTRAINT IF NOT EXISTS fk_salary_laborer_constructionsiteId
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;

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
ADD CONSTRAINT IF NOT EXISTS  FK_EXTRA_PARAMS_LABORER_CONSTRUCTIONSITEID
FOREIGN KEY (laborer_constructionsiteId)
REFERENCES laborer_constructionsite(laborer_constructionsiteId)
;
CREATE INDEX IF NOT EXISTS FK_EXTRA_PARAMS_LABORER_CONSTRUCTIONSITEID_INDEX_7 ON extra_params(laborer_constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS uq_extra_param ON extra_params(extra_paramsId)
;

-- Feriados

CREATE TABLE IF NOT EXISTS holiday
(
   holidayId IDENTITY PRIMARY KEY NOT NULL,
   date timestamp NOT NULL,
   name varchar(255) NOT NULL
)
;
CREATE UNIQUE INDEX IF NOT EXISTS PK_HOLIDAY ON holiday(holidayId)
;

CREATE TABLE IF NOT EXISTS speciality
(
   specialityId bigint PRIMARY KEY NOT NULL,
   job integer NOT NULL,
   name varchar(2147483647),
   constructionsiteId bigint NOT NULL
)
;
ALTER TABLE speciality
ADD CONSTRAINT IF NOT EXISTS FK_SPECIALITY_CONSTRUCTIONSITEID
FOREIGN KEY (constructionsiteId)
REFERENCES construction_site(constructionsiteId)
;
CREATE UNIQUE INDEX IF NOT EXISTS PRIMARY_KEY_E1 ON speciality(specialityId)
;
CREATE INDEX IF NOT EXISTS FK_SPECIALITY_CONSTRUCTIONSITEID_INDEX_E ON speciality(constructionsiteId)
;


-- AFP
ALTER TABLE `afp_item` 
ADD COLUMN `name` VARCHAR(512) NULL DEFAULT NULL COMMENT '' AFTER `afp`;

ALTER TABLE `afp_item` 
CHANGE COLUMN `afp` `afp_itemId` INT(11) NULL DEFAULT NULL COMMENT '' ;

ALTER TABLE `afp_item` 
CHANGE COLUMN `afp_itemId` `afp_itemId` INT(11) NOT NULL COMMENT '' ,
ADD PRIMARY KEY (`afp_itemId`)  COMMENT '';

ALTER TABLE `afp_item` 
CHANGE COLUMN `afp_itemId` `afp_itemId` INT(11) NOT NULL AUTO_INCREMENT COMMENT '' ;


-- BANCO
CREATE TABLE `bank` (
  `bankId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`bankId`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;


-- afp plus
ALTER TABLE `magal_asistencia`.`laborer` 
ADD COLUMN `isapre_plus` DOUBLE NOT NULL DEFAULT 0 COMMENT '';

ALTER TABLE `magal_asistencia`.`laborer` 
ADD COLUMN `otherAgreements` VARCHAR(1024) NULL AFTER `isapre_plus`,
ADD COLUMN `villa` VARCHAR(1024) NULL AFTER `otherAgreements`;

ALTER TABLE `magal_asistencia`.`overtime` CHANGE COLUMN `dmp1` `dmp1` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp10` `dmp10` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp11` `dmp11` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp12` `dmp12` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp13` `dmp13` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp14` `dmp14` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp15` `dmp15` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp16` `dmp16` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp17` `dmp17` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp18` `dmp18` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp19` `dmp19` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp2` `dmp2` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp20` `dmp20` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp21` `dmp21` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp22` `dmp22` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp23` `dmp23` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp24` `dmp24` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp25` `dmp25` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp26` `dmp26` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp27` `dmp27` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp28` `dmp28` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp29` `dmp29` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp3` `dmp3` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp30` `dmp30` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp31` `dmp31` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp4` `dmp4` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp5` `dmp5` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp6` `dmp6` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp7` `dmp7` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp8` `dmp8` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dmp9` `dmp9` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma1` `dma1` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma10` `dma10` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma11` `dma11` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma12` `dma12` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma13` `dma13` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma14` `dma14` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma15` `dma15` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma16` `dma16` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma17` `dma17` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma18` `dma18` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma19` `dma19` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma2` `dma2` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma20` `dma20` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma21` `dma21` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma22` `dma22` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma23` `dma23` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma24` `dma24` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma25` `dma25` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma26` `dma26` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma27` `dma27` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma28` `dma28` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma29` `dma29` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma3` `dma3` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma30` `dma30` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma31` `dma31` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma4` `dma4` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma5` `dma5` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma6` `dma6` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma7` `dma7` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma8` `dma8` DECIMAL NULL DEFAULT NULL  , CHANGE COLUMN `dma9` `dma9` DECIMAL NULL DEFAULT NULL  ;

-- cuenta de costos
CREATE TABLE `cost_account` (
  `costaccountId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(512) NOT NULL,
  `name` varchar(512) NOT NULL,
  `constructionsiteId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`costaccountId`)
) ENGINE=InnoDB AUTO_INCREMENT=230 DEFAULT CHARSET=latin1$$

ALTER TABLE `salary` ADD COLUMN `costaccountId` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '' AFTER `loan_bond` ;
ALTER TABLE `historical_salary` ADD COLUMN `costaccountId` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '' AFTER `laborer_constructionsiteId` ;

CREATE TABLE `magal_asistencia`.`attendance_clock` (
  `attendanceClockId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `entryTime` DATETIME NULL,
  `departureTime` DATETIME NULL,
  `rut` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`attendanceClockId`));

  ALTER TABLE `magal_asistencia`.`attendance_clock` 
ADD COLUMN `constructionsiteId` INT UNSIGNED NOT NULL AFTER `rut`;
