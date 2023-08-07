--liquibase formatted sql

--changeset taniol:001
--comment: Create tabela testowa
CREATE TABLE TABELA_TESTOWA
(
    ID   BIGINT NOT NULL,
    KOLUMNA_TESTOWA VARCHAR(255),
    PRIMARY KEY (ID)
);
--rollback DROP TABLE TABELA_TESTOWA;

--changeset taniol:002
--comment: Create tabela testowa2
CREATE TABLE TABELA_TESTOWA2
(
    ID   BIGINT NOT NULL,
    KOLUMNA_TESTOWA2 VARCHAR(255),
    PRIMARY KEY (ID)
);
--rollback empty
