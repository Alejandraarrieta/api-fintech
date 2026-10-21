package com.fintech.apifintech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fintech.apifintech.dto.CuentaRequest;
import com.fintech.apifintech.exception.ConflictoException;
import com.fintech.apifintech.exception.RecursoNoEncontradoException;
import com.fintech.apifintech.model.Cuenta;
import com.fintech.apifintech.model.TipoCuenta;
import com.fintech.apifintech.model.Usuario;
import com.fintech.apifintech.repository.CuentaRepository;
import com.fintech.apifintech.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock CuentaRepository cuentaRepository;
    @Mock UsuarioRepository usuarioRepository;
    private CuentaService service;

    @BeforeEach
    void setUp() {
        service = new CuentaService(cuentaRepository, usuarioRepository);
    }

    @Test
    void listaCuentas() {
        Cuenta cuenta = cuenta("AR-123456", usuario());
        when(cuentaRepository.findAll()).thenReturn(List.of(cuenta));

        assertThat(service.listar()).containsExactly(cuenta);
    }

    @Test
    void obtieneCuentaExistente() {
        Cuenta cuenta = cuenta("AR-123456", usuario());
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThat(service.obtener(1L)).isSameAs(cuenta);
    }

    @Test
    void obtenerCuentaInexistenteLanzaExcepcionDeDominio() {
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe la cuenta 99");
    }

    @Test
    void creaCuentaAsociadaAUsuarioExistente() {
        Usuario usuario = usuario();
        CuentaRequest request = request("AR-123456", TipoCuenta.CAJA_AHORRO, "100.00", 1L);
        when(cuentaRepository.existsByNumero(request.numero())).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cuentaRepository.saveAndFlush(any(Cuenta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cuenta creada = service.crear(request);

        assertThat(creada.getNumero()).isEqualTo("AR-123456");
        assertThat(creada.getTipo()).isEqualTo(TipoCuenta.CAJA_AHORRO);
        assertThat(creada.getSaldo()).isEqualByComparingTo("100.00");
        assertThat(creada.getUsuario()).isSameAs(usuario);
    }

    @Test
    void crearCuentaDuplicadaLanzaConflictoSinBuscarUsuario() {
        CuentaRequest request = request("AR-123456", TipoCuenta.CAJA_AHORRO, "100.00", 1L);
        when(cuentaRepository.existsByNumero(request.numero())).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ConflictoException.class)
                .hasMessage("Ya existe una cuenta con el número indicado");

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void crearCuentaConUsuarioInexistenteLanzaNoEncontrado() {
        CuentaRequest request = request("AR-123456", TipoCuenta.CAJA_AHORRO, "100.00", 99L);
        when(cuentaRepository.existsByNumero(request.numero())).thenReturn(false);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe el usuario 99");

        verify(cuentaRepository, org.mockito.Mockito.never()).saveAndFlush(any(Cuenta.class));
    }

    @Test
    void crearTraduceViolacionDeIntegridadAConflicto() {
        CuentaRequest request = request("AR-123456", TipoCuenta.CAJA_AHORRO, "100.00", 1L);
        when(cuentaRepository.existsByNumero(request.numero())).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(cuentaRepository.saveAndFlush(any(Cuenta.class)))
                .thenThrow(new DataIntegrityViolationException("restricción unique"));

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ConflictoException.class)
                .hasMessage("No se pudo crear la cuenta porque viola una restricción de datos")
                .hasCauseInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void actualizaCuentaExistente() {
        Usuario nuevoUsuario = usuario();
        Cuenta cuenta = cuenta("AR-123456", usuario());
        CuentaRequest request = request("AR-654321", TipoCuenta.CUENTA_CORRIENTE, "250.00", 2L);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.existsByNumeroAndIdNot(request.numero(), 1L)).thenReturn(false);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(nuevoUsuario));
        when(cuentaRepository.saveAndFlush(cuenta)).thenReturn(cuenta);

        Cuenta actualizada = service.actualizar(1L, request);

        assertThat(actualizada.getNumero()).isEqualTo("AR-654321");
        assertThat(actualizada.getTipo()).isEqualTo(TipoCuenta.CUENTA_CORRIENTE);
        assertThat(actualizada.getSaldo()).isEqualByComparingTo("250.00");
        assertThat(actualizada.getUsuario()).isSameAs(nuevoUsuario);
    }

    @Test
    void actualizarCuentaInexistenteLanzaNoEncontrado() {
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());
        CuentaRequest request = request("AR-654321", TipoCuenta.CAJA_AHORRO, "100.00", 1L);

        assertThatThrownBy(() -> service.actualizar(99L, request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe la cuenta 99");

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void actualizarConNumeroDeOtraCuentaLanzaConflicto() {
        Cuenta cuenta = cuenta("AR-123456", usuario());
        CuentaRequest request = request("AR-654321", TipoCuenta.CAJA_AHORRO, "100.00", 1L);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.existsByNumeroAndIdNot(request.numero(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ConflictoException.class)
                .hasMessage("Ya existe otra cuenta con el número indicado");

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void actualizarConUsuarioInexistenteLanzaNoEncontrado() {
        Cuenta cuenta = cuenta("AR-123456", usuario());
        CuentaRequest request = request("AR-654321", TipoCuenta.CAJA_AHORRO, "100.00", 99L);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.existsByNumeroAndIdNot(request.numero(), 1L)).thenReturn(false);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe el usuario 99");

        verify(cuentaRepository, org.mockito.Mockito.never()).saveAndFlush(any(Cuenta.class));
    }

    @Test
    void actualizarTraduceViolacionDeIntegridadAConflicto() {
        Cuenta cuenta = cuenta("AR-123456", usuario());
        CuentaRequest request = request("AR-654321", TipoCuenta.CAJA_AHORRO, "100.00", 1L);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.existsByNumeroAndIdNot(request.numero(), 1L)).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(cuentaRepository.saveAndFlush(cuenta))
                .thenThrow(new DataIntegrityViolationException("restricción de datos"));

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ConflictoException.class)
                .hasMessage("No se pudo actualizar la cuenta porque viola una restricción de datos")
                .hasCauseInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void eliminaCuentaExistente() {
        Cuenta cuenta = cuenta("AR-123456", usuario());
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        service.eliminar(1L);

        verify(cuentaRepository).delete(cuenta);
    }

    @Test
    void eliminarCuentaInexistenteLanzaNoEncontrado() {
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe la cuenta 99");
    }

    private static CuentaRequest request(String numero, TipoCuenta tipo, String saldo, Long usuarioId) {
        return new CuentaRequest(numero, tipo, new BigDecimal(saldo), usuarioId);
    }

    private static Usuario usuario() {
        return new Usuario("Ana Pérez", "ana@example.com");
    }

    private static Cuenta cuenta(String numero, Usuario usuario) {
        return new Cuenta(numero, TipoCuenta.CAJA_AHORRO, new BigDecimal("100.00"), usuario);
    }
}
