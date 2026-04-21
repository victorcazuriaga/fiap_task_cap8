-- V1__create_cliente_table.sql (PostgreSQL)
CREATE TABLE cliente (
    id_cliente UUID         PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) UNIQUE,
    cnpj       VARCHAR(20)  NOT NULL UNIQUE,
    endereco   VARCHAR(200) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_cliente_email ON cliente (email);
CREATE INDEX idx_cliente_cnpj  ON cliente (cnpj);
