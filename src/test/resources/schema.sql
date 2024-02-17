DROP TABLE IF EXISTS transacoes;
DROP TABLE IF EXISTS clientes;

CREATE TABLE IF NOT EXISTS clientes (
    id SERIAL NOT NULL,
    limite INTEGER NOT NULL,
	saldo INTEGER NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS transacoes (
    id SERIAL NOT NULL,
    id_clientes BIGINT NOT NULL,
    valor INTEGER NOT NULL,
    tipo CHAR(1) NOT NULL,
    descricao VARCHAR(10),
    realizada_em TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_clientes
        FOREIGN KEY(id_clientes)
        REFERENCES clientes(id)
);