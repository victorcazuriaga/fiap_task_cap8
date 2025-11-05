-- V1__create_cliente_table.sql
CREATE TABLE cliente (
                         id_cliente VARCHAR2(36 CHAR) PRIMARY KEY,
                         nome VARCHAR2(100 CHAR) NOT NULL,
                         email VARCHAR2(100 CHAR) UNIQUE,
                         cnpj VARCHAR2(20 CHAR) NOT NULL UNIQUE,
                         endereco VARCHAR2(200 CHAR) NOT NULL,
                         password VARCHAR2(255 CHAR) NOT NULL,
                         role VARCHAR2(255 CHAR) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
                         ativo NUMBER(1,0) DEFAULT 1 CHECK (ativo IN (0, 1))
);

-- Índices adicionais (opcional)
CREATE INDEX idx_cliente_email ON cliente (email);
CREATE INDEX idx_cliente_cnpj ON cliente (cnpj);