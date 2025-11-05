-- V3__create_manutencao_table.sql
CREATE TABLE manutencao (
    id_manutencao VARCHAR2(36 CHAR) PRIMARY KEY,
    id_cliente VARCHAR2(36 CHAR) NOT NULL,
    tipo_manutencao NUMBER,
    data_manutencao DATE,
    CONSTRAINT manutencao_cliente_fk FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE INDEX idx_manutencao_cliente ON manutencao (id_cliente);
