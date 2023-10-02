package com.alm.msclientes.application;

import com.alm.msclientes.domain.Cliente;
import com.alm.msclientes.infra.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> getByCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }
}
