package com.fintech.apifintech;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.fintech.apifintech.repository.CuentaRepository;
import com.fintech.apifintech.repository.UsuarioRepository;

@SpringBootTest
@ActiveProfiles("test")
class ApiFintechApplicationTests {

	@MockitoBean CuentaRepository cuentaRepository;
	@MockitoBean UsuarioRepository usuarioRepository;

	@Test
	void contextLoads() {
	}

}
