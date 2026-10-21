package com.fintech.apifintech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fintech.apifintech.model.Cuenta;
import com.fintech.apifintech.model.TipoCuenta;
import com.fintech.apifintech.model.Usuario;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:fintech-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
class CuentaRepositoryTest {

    @Autowired CuentaRepository cuentaRepository;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired EntityManager entityManager;

    @Test
    void persisteCuentaRelacionadaYLaRecuperaDesdeBaseDeDatos() {
        Usuario usuario = usuarioRepository.saveAndFlush(new Usuario("Ana Pérez", "ana@example.com"));
        Cuenta cuenta = cuentaRepository.saveAndFlush(new Cuenta(
                "AR-123456", TipoCuenta.CAJA_AHORRO, new BigDecimal("100.00"), usuario));
        Long cuentaId = cuenta.getId();

        entityManager.clear();

        Cuenta recuperada = cuentaRepository.findById(cuentaId).orElseThrow();
        assertThat(recuperada.getNumero()).isEqualTo("AR-123456");
        assertThat(recuperada.getSaldo()).isEqualByComparingTo("100.00");
        assertThat(recuperada.getUsuario().getId()).isEqualTo(usuario.getId());
        assertThat(recuperada.getUsuario().getEmail()).isEqualTo("ana@example.com");
    }
}
