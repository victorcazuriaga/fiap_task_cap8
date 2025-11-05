-- V4__create_tipo_manutencao_residuo.sql
CREATE TABLE tipo_manutencao_residuo (
    id VARCHAR2(36 CHAR) PRIMARY KEY,
    id_manutencao VARCHAR2(36 CHAR) NOT NULL,
    id_residuo VARCHAR2(36 CHAR) NOT NULL,
    quantidade NUMBER,
    CONSTRAINT tipo_manut_residuo_manut_fk FOREIGN KEY (id_manutencao) REFERENCES manutencao(id_manutencao),
    CONSTRAINT tipo_manut_residuo_residuo_fk FOREIGN KEY (id_residuo) REFERENCES residuo(id_residuo)
);

CREATE INDEX idx_tipo_manut_residuo_manut ON tipo_manutencao_residuo (id_manutencao);
CREATE INDEX idx_tipo_manut_residuo_residuo ON tipo_manutencao_residuo (id_residuo);
