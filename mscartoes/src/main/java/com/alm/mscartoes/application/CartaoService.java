package com.alm.mscartoes.application;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alm.mscartoes.domain.Cartao;
import com.alm.mscartoes.infra.repository.CartaoRepository;

@Service
public class CartaoService {

  @Autowired
  private CartaoRepository cartaoRepository;

  @Transactional
  public Cartao save(Cartao cartao) {
    return cartaoRepository.save(cartao);
  }

  public List<Cartao> getCartoesRendaMenorIgual(Long renda) {
    var rendaBigDecimal = BigDecimal.valueOf(renda);
    return cartaoRepository.findByRendaLessThanEqual(rendaBigDecimal);
  }
}
