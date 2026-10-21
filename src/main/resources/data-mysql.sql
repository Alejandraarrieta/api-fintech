INSERT INTO usuarios (id, nombre, email, creado_en, actualizado_en)
VALUES
    (1, 'Ana Pérez', 'ana.perez@example.com', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    (2, 'Bruno Gómez', 'bruno.gomez@example.com', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6))
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), email = VALUES(email);

INSERT INTO cuentas (id, numero, tipo, saldo, usuario_id, creado_en, actualizado_en)
VALUES
    (1, 'AR-000000000001', 'CAJA_AHORRO', 125000.50, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    (2, 'AR-000000000002', 'CUENTA_CORRIENTE', 85000.00, 2, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6))
ON DUPLICATE KEY UPDATE
    numero = VALUES(numero), tipo = VALUES(tipo), saldo = VALUES(saldo), usuario_id = VALUES(usuario_id);
