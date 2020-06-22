drop table goradid;

create table goradid (
    goradid_pidm number(8) not null,
    goradid_additional_id varchar2(50 char) not null,
    goradid_adid_code varchar2(4 char) not null,
    goradid_user_id varchar2(30 char) not null,
    goradid_activity_date date not null,
    goradid_data_origin varchar2(30 char) not null,
    goradid_surrogate_id number(19) null,
    goradid_version number(19) null,
    goradid_vpdi_code varchar2(6 char) null,
    constraint pk_goradid primary key (goradid_pidm, goradid_adid_code, goradid_additional_id)
);