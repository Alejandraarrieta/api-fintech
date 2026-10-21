package com.fintech.apifintech.service;

import com.fintech.apifintech.dto.CuentaRequest;
import com.fintech.apifintech.exception.ConflictoException;
import com.fintech.apifintech.exception.RecursoNoEncontradoException;
import com.fintech.apifintech.model.Cuenta;
import com.fintech.apifintech.model.Usuario;
import com.fintech.apifintech.repository.CuentaRepository;
import com.fintech.apifintech.repository.UsuarioRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    public CuentaService(CuentaRepository cuentaRepository, UsuarioRepository usuarioRepository) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Cuenta> listar() { return cuentaRepository.findAll(); }

    public Cuenta obtener(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cuenta " + id));
    }

    @Transactional
    public Cuenta crear(CuentaRequest request) {
        if (cuentaRepository.existsByNumero(request.numero())) {
            throw new ConflictoException("Ya existe una cuenta con el número indicado");
        }
        Usuario usuario = buscarUsuario(request.usuarioId());
        try {
            return cuentaRepository.saveAndFlush(
                    new Cuenta(request.numero(), request.tipo(), request.saldo(), usuario));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictoException("No se pudo crear la cuenta porque viola una restricción de datos", ex);
        }
    }

    @Transactional
    public Cuenta actualizar(Long id, CuentaRequest request) {
        Cuenta cuenta = obtener(id);
        if (cuentaRepository.existsByNumeroAndIdNot(request.numero(), id)) {
            throw new ConflictoException("Ya existe otra cuenta con el número indicado");
        }
        cuenta.actualizar(request.numero(), request.tipo(), request.saldo(), buscarUsuario(request.usuarioId()));
        try {
            return cuentaRepository.saveAndFlush(cuenta);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictoException("No se pudo actualizar la cuenta porque viola una restricción de datos", ex);
        }
    }

    @Transactional
    public void eliminar(Long id) {
        Cuenta cuenta = obtener(id);
        cuentaRepository.delete(cuenta);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + id));
    }
}
