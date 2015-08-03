-- se crea rol para el usuario 1
INSERT INTO "ROLE" (ROLEID,DESCRIPTION,NAME) VALUES (1,null,'Super Administrador');

INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'CREAR_OBRA');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'BLOQUEAR_OBRERO');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'DEFINIR_VARIABLE_GLOBAL');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'ELIMINAR_OBRA');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'CREAR_USUARIO');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'AGREGAR_ETAPAS_OBRA');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'EDITAR_OBRA');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'CONFIRMAR_OBREROS');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (1,'ASIGNAR_OBRA');

-- se crea usuario 1
INSERT INTO "USER" (USERID,DELETED,EMAIL,FIRSTNAME,LASTNAME,PASSWORD,RUT,SALT,STATUS,ROLEID) VALUES 
(1,0,'admin@admin.com','Joseph','O''Shea','$2a$10$4N4FPx/WgAHrqf1BQdEnGO9uSmxtJmACsXKCt/my401iI0QIfzfRW','16127401-1',null,1,1);

-- usuario 2

INSERT INTO "ROLE" (ROLEID,DESCRIPTION,NAME) VALUES (2,null,'Administrador Central');

INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (2,'CREAR_OBRA');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (2,'DEFINIR_VARIABLE_GLOBAL');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (2,'ELIMINAR_OBRA');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (2,'CREAR_USUARIO');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (2,'EDITAR_OBRA');
INSERT INTO "PERMISSIONS" (ROLEID,ROLE_PERMISSIONS) VALUES (2,'ASIGNAR_OBRA');

INSERT INTO "USER" (USERID,DELETED,EMAIL,FIRSTNAME,LASTNAME,PASSWORD,RUT,SALT,STATUS,ROLEID) VALUES (2,0,'central@magal.cl','ADMIN','CENTRAL','$2a$10$ATilSUkKRngyaz8snVP17OGtVy54ikSqNNEFVXFepHgRtL3iAqhPa','11111111-1',null,1,2);

-- LABORER para test de obtener, crear y modificar
-- id 207
MERGE INTO LABORER (LABORERID,ADDRESS,AFP,COMMUNE,DATEBIRTH,DEPENDENTS,FIRSTNAME,ISAPRE,LASTNAME,MARITALSTATUS,MOBILENUMBER,NATIONALITY,PHONE,PHOTO,RUT,SECONDLASTNAME,SECONDNAME,TEAMID,TOWN,WEDGE,BANK) VALUES 
(207,'Edith Madge 0711',4,'La Pintana',null,1,'Juan',1,'Salas',2,'83697498',1,'61833825','1.JPG','8858100-8','Roa','Francisco',null,'Santo Tomas',40,1);
MERGE INTO LABORER (LABORERID,ADDRESS,AFP,COMMUNE,DATEBIRTH,DEPENDENTS,FIRSTNAME,ISAPRE,LASTNAME,MARITALSTATUS,MOBILENUMBER,NATIONALITY,PHONE,PHOTO,RUT,SECONDLASTNAME,SECONDNAME,TEAMID,TOWN,WEDGE,BANK) VALUES 
(208,'El Chilco 14132',1,'La Pintana',null,0,'Miguel',1,'Fuenzalida',1,'54803265',1,null,'2.JPG','14184926-3','Fernandez','Angel',null,'Jorge Alesandri',41,1);
MERGE INTO LABORER (LABORERID,ADDRESS,AFP,COMMUNE,DATEBIRTH,DEPENDENTS,FIRSTNAME,ISAPRE,LASTNAME,MARITALSTATUS,MOBILENUMBER,NATIONALITY,PHONE,PHOTO,RUT,SECONDLASTNAME,SECONDNAME,TEAMID,TOWN,WEDGE,BANK) VALUES 
(209,'Mariano Gomez 1064',4,'Puente Alto',null,0,'Juvenal',1,'Pe¤a',2,null,1,null,'3.JPG','4912637-9','Urizar','Alfonso',null,'Las Brisas',40,1);
-- agrega un obrero no asociado a obra
MERGE INTO LABORER (LABORERID,ADDRESS,AFP,COMMUNE,DATEBIRTH,DEPENDENTS,FIRSTNAME,ISAPRE,LASTNAME,MARITALSTATUS,MOBILENUMBER,NATIONALITY,PHONE,PHOTO,RUT,SECONDLASTNAME,SECONDNAME,TEAMID,TOWN,WEDGE,BANK) VALUES 
(412,'Pasaje Setter 1500',2,'Cerro Navia',null,1,'Marcelo',1,'Sanchez',2,'96499889',1,'81428942','917.JPG','14523741-6','Sartori','Fabian',null,'El Montijo',41,1);

-- OBRA PARA TEST

-- obra inactiva asociada al usuario 1
insert INTO CONSTRUCTION_SITE (CONSTRUCTION_SITEID,ADDRESS,CODE,DELETED,NAME,STATUS,PERSONINCHARGEID) 
VALUES (3,'Av. Las Condes 8798','2332443',0,'Obra Inactiva Usuario 1',2,null);

-- obra inactiva asociada al usuario 1
insert INTO CONSTRUCTION_SITE (CONSTRUCTION_SITEID,ADDRESS,CODE,DELETED,NAME,STATUS,PERSONINCHARGEID) 
VALUES (2,'Av. Las Condes 8798','2332443',0,'Obra Inactiva',1,null);

-- obra para probar la generación de jobcode
insert INTO CONSTRUCTION_SITE (CONSTRUCTION_SITEID,ADDRESS,CODE,DELETED,NAME,STATUS,PERSONINCHARGEID) 
VALUES (4,'Av. Pajaritos 9934','565643',0,'Edificio Jardines de Olivares',2,null);

-- obra activa asociada al usuario 1
insert INTO CONSTRUCTION_SITE (CONSTRUCTION_SITEID,ADDRESS,CODE,DELETED,NAME,STATUS,PERSONINCHARGEID) 
VALUES (1,'Av. Las Condes 8798','2332443',0,'Carolina Rabat 4 Etapa B',1,null);

-- obra inactiva asociada al usuario 1
--insert INTO CONSTRUCTION_SITE (CONSTRUCTION_SITEID,ADDRESS,CODE,DELETED,NAME,STATUS,PERSONINCHARGEID) 
---VALUES (5,'Av. Las Condes 8798','2332443',0,'Carolina Rabat 4 Etapa B',2,null);

-- obra inactiva asociada al usuario 1
--insert INTO CONSTRUCTION_SITE (CONSTRUCTION_SITEID,ADDRESS,CODE,DELETED,NAME,STATUS,PERSONINCHARGEID) 
--VALUES (6,'Av. Las Condes 8798','2332443',0,'Carolina Rabat 4 Etapa B',1,null);

-- asocia las obras al usuario 1
insert into "USER_CONSTRUCTIONSITE"(CONSTRUCTION_SITEID,USERID) VALUES
(1,1);
insert into "USER_CONSTRUCTIONSITE"(CONSTRUCTION_SITEID,USERID) VALUES
(3,1);

-- asocia las obras al usuario 2
insert into "USER_CONSTRUCTIONSITE"(CONSTRUCTION_SITEID,USERID) VALUES
(2,2);
insert into "USER_CONSTRUCTIONSITE"(CONSTRUCTION_SITEID,USERID) VALUES
(4,2);

-- ASOCIA EL OBRERO 207 A LA OBRA 1
insert into LABORER_CONSTRUCTIONSITE(LABORER_CONSTRUCTIONSITEID,LABORERID,CONSTRUCTION_SITEID,ACTIVE) VALUES
(1,207,1,1);

INSERT INTO CONTRACT (CONTRACTID,ACTIVE,FINISHED,CONTRACTDESCRIPTION,JOB,JOBCODE,NAME,SETTLEMENT,STARTDATE,STEP,TERMINATIONDATE,TIMEDURATION,VALUETREATMENT,LABORER_CONSTRUCTIONSITEID) VALUES 
(1,1,0,'contrato de ${laborer.fullname} para el trabajo ${lcs.job} en la etapa ${lcs.step}',1,1,'Juan',null,{ts '2015-03-05 22:29:49.632000000'},'Obra Gruesa',null,null,null,1);
INSERT INTO CONTRACT (CONTRACTID,ACTIVE,FINISHED,CONTRACTDESCRIPTION,JOB,JOBCODE,NAME,SETTLEMENT,STARTDATE,STEP,TERMINATIONDATE,TIMEDURATION,VALUETREATMENT,LABORER_CONSTRUCTIONSITEID) VALUES 
(2,1,0,'contrato de ${laborer.fullname} para el trabajo ${lcs.job} en la etapa ${lcs.step}',1,2,'Miguel',null,{ts '2015-03-05 22:29:49.632000000'},'Obra Gruesa',null,null,null,2);
INSERT INTO CONTRACT (CONTRACTID,ACTIVE,FINISHED,CONTRACTDESCRIPTION,JOB,JOBCODE,NAME,SETTLEMENT,STARTDATE,STEP,TERMINATIONDATE,TIMEDURATION,VALUETREATMENT,LABORER_CONSTRUCTIONSITEID) VALUES 
(3,1,0,'contrato de ${laborer.fullname} para el trabajo ${lcs.job} en la etapa ${lcs.step}',1,3,'Juvenal',null,{ts '2015-03-05 22:29:49.632000000'},'Obra Gruesa',null,null,null,3);

insert into LABORER_CONSTRUCTIONSITE(LABORER_CONSTRUCTIONSITEID,LABORERID,CONSTRUCTION_SITEID,ACTIVE) VALUES
(2,209,1,1);

-- Usuario finiquitado en una obra X, debe aparecer en la busqueda de histórico de la obra X 
insert into LABORER_CONSTRUCTIONSITE(LABORER_CONSTRUCTIONSITEID,LABORERID,CONSTRUCTION_SITEID,ACTIVE) VALUES
(3,209,2,0);
-- inserta el contrato respectivo y lo marca como finiquitado
INSERT INTO CONTRACT (CONTRACTID,ACTIVE,FINISHED,CONTRACTDESCRIPTION,JOB,JOBCODE,NAME,SETTLEMENT,STARTDATE,STEP,TERMINATIONDATE,TIMEDURATION,VALUETREATMENT,LABORER_CONSTRUCTIONSITEID) VALUES 
(4,0,1,'',1,186,'Francisco',null,{ts '2015-02-25 23:04:49.802000000'},'Obra Gruesa',{ts '2015-06-25 23:04:49.802000000'},null,null,3);


-- tabla de suple para la obra 1 (por ahora se usa una global)
INSERT INTO ADVANCE_PAYMENT_CONFIGURATIONS (ADVANCE_PAYMENT_CONFIGURATIONSID,FAILURE_DISCOUNT,PERMISSION_DISCOUNT) VALUES (1,15000.0,5000.0);

INSERT INTO ADVANCE_PAYMENT_ITEM (SUPLE_CODE,SUPLE_INCREASE_AMOUNT,SUPLE_NORMAL_AMOUNT,ADVANCE_PAYMENT_CONFIGURATIONSID) VALUES (1,0.0,105000.0,1);
INSERT INTO ADVANCE_PAYMENT_ITEM (SUPLE_CODE,SUPLE_INCREASE_AMOUNT,SUPLE_NORMAL_AMOUNT,ADVANCE_PAYMENT_CONFIGURATIONSID) VALUES (2,0.0,140000.0,1);
INSERT INTO ADVANCE_PAYMENT_ITEM (SUPLE_CODE,SUPLE_INCREASE_AMOUNT,SUPLE_NORMAL_AMOUNT,ADVANCE_PAYMENT_CONFIGURATIONSID) VALUES (3,0.0,130000.0,1);
INSERT INTO ADVANCE_PAYMENT_ITEM (SUPLE_CODE,SUPLE_INCREASE_AMOUNT,SUPLE_NORMAL_AMOUNT,ADVANCE_PAYMENT_CONFIGURATIONSID) VALUES (4,0.0,250000.0,1);
INSERT INTO ADVANCE_PAYMENT_ITEM (SUPLE_CODE,SUPLE_INCREASE_AMOUNT,SUPLE_NORMAL_AMOUNT,ADVANCE_PAYMENT_CONFIGURATIONSID) VALUES (5,0.0,115000.0,1);

-- tabla de impuestos

INSERT INTO TAXATION_CONFIGURATIONS (TAXATION_CONFIGURATIONSID,EXEMPT,FACTOR,FROMR,REDUCTION,TO) VALUES (1,0.0,0.0,0.0,0.0,541147.5);
INSERT INTO TAXATION_CONFIGURATIONS (TAXATION_CONFIGURATIONSID,EXEMPT,FACTOR,FROMR,REDUCTION,TO) VALUES (2,2.2,0.04,541147.51,21645.9,1202550.0);
INSERT INTO TAXATION_CONFIGURATIONS (TAXATION_CONFIGURATIONSID,EXEMPT,FACTOR,FROMR,REDUCTION,TO) VALUES (3,4.52,11.27,1202550.01,69747.9,2004250.0);
INSERT INTO TAXATION_CONFIGURATIONS (TAXATION_CONFIGURATIONSID,EXEMPT,FACTOR,FROMR,REDUCTION,TO) VALUES (4,7.09,10.77,2004250.01,179981.65,2805950.0);
INSERT INTO TAXATION_CONFIGURATIONS (TAXATION_CONFIGURATIONSID,EXEMPT,FACTOR,FROMR,REDUCTION,TO) VALUES (5,10.62,12.36,2805950.01,446546.9,3607650.0);
INSERT INTO TAXATION_CONFIGURATIONS (TAXATION_CONFIGURATIONSID,EXEMPT,FACTOR,FROMR,REDUCTION,TO) VALUES (6,15.57,11.54,3607650.01,713513.0,4810200.0);
INSERT INTO TAXATION_CONFIGURATIONS (TAXATION_CONFIGURATIONSID,EXEMPT,FACTOR,FROMR,REDUCTION,TO) VALUES (7,19.55,11.54,4810200.01,958833.2,6012750.0);
INSERT INTO TAXATION_CONFIGURATIONS (TAXATION_CONFIGURATIONSID,EXEMPT,FACTOR,FROMR,REDUCTION,TO) VALUES (8,19.55,11.54,6012750.01,1229406.95,1.0E11);

-- tabla de asignación familiar
INSERT INTO FAMILY_ALLOWANCE_CONFIGURATIONS (FAMILY_ALLOWANCE_CONFIGURATIONSID,AMOUNT,FROMR,TO) VALUES (1,7744.0,0.0,8100.64);
INSERT INTO FAMILY_ALLOWANCE_CONFIGURATIONS (FAMILY_ALLOWANCE_CONFIGURATIONSID,AMOUNT,FROMR,TO) VALUES (2,5221.0,8100.68,12696.28);
INSERT INTO FAMILY_ALLOWANCE_CONFIGURATIONS (FAMILY_ALLOWANCE_CONFIGURATIONSID,AMOUNT,FROMR,TO) VALUES (3,4650.0,12696.32,19801.92);
INSERT INTO FAMILY_ALLOWANCE_CONFIGURATIONS (FAMILY_ALLOWANCE_CONFIGURATIONSID,AMOUNT,FROMR,TO) VALUES (4,0.0,19801.96,1000000.64);

INSERT INTO WAGE_CONFIGURATIONS (WAGE_CONFIGURATIONSID,COLLATION,MINIMUM_WAGE,MOBILIZATION) VALUES (1,31.0,210000.0,220.0);
INSERT INTO MOBILIZATION2 (AMOUNT,LINKED_CONSTRUCTION_SITEID,WAGE_CONFIGURATIONSID) VALUES (1000.0,1,1);

-- configuración del mes de julio 2014

INSERT INTO DATE_CONFIGURATIONS (DATE_CONFIGURATIONSID,ADVANCE,ASSISTANCE,BEGIN_DEAL,DATE,FINISH_DEAL,BENZINE,OIL,UF) VALUES (1,null,{d '2015-03-23'},null,{d '2015-03-01'},null,null,null,null);
INSERT INTO DATE_CONFIGURATIONS (DATE_CONFIGURATIONSID,ADVANCE,ASSISTANCE,BEGIN_DEAL,DATE,FINISH_DEAL,BENZINE,OIL,UF) VALUES (2,null,null,null,{d '2015-02-01'},null,null,null,null);
INSERT INTO DATE_CONFIGURATIONS (DATE_CONFIGURATIONSID,ADVANCE,ASSISTANCE,BEGIN_DEAL,DATE,FINISH_DEAL,BENZINE,OIL,UF) VALUES (3,{d '2014-07-11'},{d '2014-07-24'},{d '2015-04-20'},{d '2014-07-01'},{d '2014-07-24'},895,680,24023.61);
INSERT INTO DATE_CONFIGURATIONS (DATE_CONFIGURATIONSID,ADVANCE,ASSISTANCE,BEGIN_DEAL,DATE,FINISH_DEAL,BENZINE,OIL,UF) VALUES (4,{d '2014-06-11'},{d '2014-06-20'},{d '2014-06-01'},{d '2014-06-01'},{d '2014-06-20'},895,680,24023.61);
INSERT INTO DATE_CONFIGURATIONS (DATE_CONFIGURATIONSID,ADVANCE,ASSISTANCE,BEGIN_DEAL,DATE,FINISH_DEAL,BENZINE,OIL,UF) VALUES (5,{d '2015-04-11'},{d '2015-04-22'},{d '2015-03-25'},{d '2015-04-01'},{d '2015-04-24'},895.0,680.0,24023.61);

INSERT INTO LABORER (LABORERID,ADDRESS,AFP,COMMUNE,CONTRACTID,DATEADMISSION,DATEBIRTH,DEPENDENTS,FIRSTNAME,ISAPRE,LASTNAME,MARITALSTATUS,MOBILENUMBER,NATIONALITY,PHONE,PHOTO,RUT,SECONDLASTNAME,SECONDNAME,TEAMID,TOWN,WEDGE,PROVENANCE,BANK,BANKACCOUNT) VALUES 
(313,'Los Guindos 78',2,'El Bosque',null,null,{ts '1964-11-08 00:00:00.0'},0,'Heriberto',4,'Meza',2,'93951862',1,null,'404.JPG','10213312-9','Contrera','Luis',null,'Condores de Chile',40,null,1,null);

INSERT INTO LABORER_CONSTRUCTIONSITE (LABORER_CONSTRUCTIONSITEID,LABORERID,ACTIVE,CONFIRMED,REWARD,USE_DEFAULT_DATES,REWARD_STARTDATE,REWARD_ENDTDATE,BLOCK,PERSONBLOCKID,COMMENT,CONSTRUCTION_SITEID,SUPLE_CODE) VALUES 
(100,313,1,null,0,1,null,null,null,null,null,1,1);
INSERT INTO CONTRACT (CONTRACTID,ACTIVE,FINISHED,CONTRACTDESCRIPTION,JOB,JOBCODE,NAME,SETTLEMENT,STARTDATE,STEP,TERMINATIONDATE,TIMEDURATION,VALUETREATMENT,LABORER_CONSTRUCTIONSITEID) VALUES 
(100,1,0,'contrato de ${laborer.fullname} para el trabajo ${lcs.job} en la etapa ${lcs.step}',1,100,'Heriberto',null,{ts '2015-03-05 22:29:49.632000000'},'Obra Gruesa',null,null,null,100);

INSERT INTO ATTENDANCE (ATTENDANCEID,JORNAL,DMA1,DMA10,DMA11,DMA12,DMA13,DMA14,DMA15,DMA16,DMA17,DMA18,DMA19,DMA2,DMA20,DMA21,DMA22,DMA23,DMA24,DMA25,DMA26,DMA27,DMA28,DMA29,DMA3,DMA30,DMA31,DMA4,DMA5,DMA6,DMA7,DMA8,DMA9,DATE,LABORER_CONSTRUCTIONSITEID) VALUES 
(12,11000,3,1,1,5,4,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,5,4,1,3,1,{d '2014-07-21'},100);

INSERT INTO ATTENDANCE (ATTENDANCEID,DATE,DMA1,DMA10,DMA11,DMA12,DMA13,DMA14,DMA15,DMA16,DMA17,DMA18,DMA19,DMA2,DMA20,DMA21,DMA22,DMA23,DMA24,DMA25,DMA26,DMA27,DMA28,DMA29,DMA3,DMA30,DMA31,DMA4,DMA5,DMA6,DMA7,DMA8,DMA9,DMP1,DMP10,DMP11,DMP12,DMP13,DMP14,DMP15,DMP16,DMP17,DMP18,DMP19,DMP2,DMP20,DMP21,DMP22,DMP23,DMP24,DMP25,DMP26,DMP27,DMP28,DMP29,DMP3,DMP30,DMP31,DMP4,DMP5,DMP6,DMP7,DMP8,DMP9,JORNAL,LABORER_CONSTRUCTIONSITEID) VALUES 
(1,{d '2015-04-08'},1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,4,8,1,1,1,1,1,1,1,1,1,1,11000,1);
