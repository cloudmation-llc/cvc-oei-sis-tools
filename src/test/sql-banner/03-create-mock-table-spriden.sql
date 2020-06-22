drop table spriden;

create table spriden (
    spriden_pidm number(8) not null,
    spriden_id varchar2(50 char) not null,
    spriden_change_ind varchar2(1 char) null,
    spriden_first_name varchar2(50 char) not null,
    spriden_mi varchar2(50 char) null,
    spriden_last_name varchar2(50 char) not null,
    constraint pk_spriden primary key (spriden_pidm, spriden_id)
);