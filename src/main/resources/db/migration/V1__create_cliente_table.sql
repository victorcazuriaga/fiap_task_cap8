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
-- As colunas com UNIQUE já criam índices automaticamente em Oracle.
-- Evitamos criar índices duplicados para prevenir ORA-01408.
-- (idx on email and cnpj removed)

-- Tabela de licença ambiental ligada ao cliente (1:N)
CREATE TABLE licenca_ambiental (
    id_licenca VARCHAR2(36 CHAR) PRIMARY KEY,
    id_cliente VARCHAR2(36 CHAR) NOT NULL,
    tipo_licenca VARCHAR2(50 CHAR),
    data_emissao DATE,
    data_validade DATE,
    status VARCHAR2(20 CHAR),
    CONSTRAINT licenca_ambiental_cliente_fk FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE INDEX idx_licenca_cliente ON licenca_ambiental (id_cliente);