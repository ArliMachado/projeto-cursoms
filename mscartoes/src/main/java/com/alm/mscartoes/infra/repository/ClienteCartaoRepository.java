package com.alm.mscartoes.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alm.mscartoes.domain.ClienteCartao;

@Repository
public interface ClienteCartaoRepository extends JpaRepository<ClienteCartao, Long> {
  List<ClienteCartao> findByCpf(String cpf);
}
