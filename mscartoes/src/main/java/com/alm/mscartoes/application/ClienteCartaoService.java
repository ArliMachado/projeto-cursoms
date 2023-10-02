package com.alm.mscartoes.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alm.mscartoes.domain.ClienteCartao;
import com.alm.mscartoes.infra.repository.ClienteCartaoRepository;

@Service
public class ClienteCartaoService {
  @Autowired
  private ClienteCartaoRepository clienteCartaoRepository;

  public List<ClienteCartao> listarCartoesByCpf(String cpf) {
    return clienteCartaoRepository.findByCpf(cpf);
  }
}
