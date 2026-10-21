CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(160) NOT NULL,
    creado_en TIMESTAMP(6) NOT NULL,
    actualizado_en TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cuentas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(22) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    saldo DECIMAL(19,2) NOT NULL,
    usuario_id BIGINT NOT NULL,
    creado_en TIMESTAMP(6) NOT NULL,
    actualizado_en TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cuentas_numero UNIQUE (numero),
    CONSTRAINT fk_cuentas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT ck_cuentas_saldo_no_negativo CHECK (saldo >= 0),
    INDEX idx_cuentas_usuario (usuario_id)
) ENGINE=InnoDB;
