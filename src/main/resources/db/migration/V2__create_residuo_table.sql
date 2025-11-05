-- V2__create_residuo_table.sql
CREATE TABLE residuo (
    id_residuo VARCHAR2(36 CHAR) PRIMARY KEY,
    nome_residuo VARCHAR2(50 CHAR),
    unidade_medida VARCHAR2(10 CHAR),
    quantidade_acumulada NUMBER,
    limite_reciclagem NUMBER
);

CREATE INDEX idx_residuo_nome ON residuo (nome_residuo);
