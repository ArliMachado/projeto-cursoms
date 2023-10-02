package com.alm.mscartoes.infra.mqueue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.alm.mscartoes.domain.Cartao;
import com.alm.mscartoes.domain.ClienteCartao;
import com.alm.mscartoes.domain.DadoSolicitacaoEmissaoCartao;
import com.alm.mscartoes.infra.repository.CartaoRepository;
import com.alm.mscartoes.infra.repository.ClienteCartaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmissaoCartaoSubscriber {

  @Autowired
  private CartaoRepository cartaoRepository;

  @Autowired
  private ClienteCartaoRepository clienteCartaoRepository;

  @RabbitListener(queues = "${mq.queues.emissao-cartoes}")
  public void receberSolicitacaoEmissao(@Payload String payload) {
    try {
      var mapper = new ObjectMapper();
      DadoSolicitacaoEmissaoCartao dados = mapper.readValue(payload, DadoSolicitacaoEmissaoCartao.class);

      Cartao cartao = cartaoRepository.findById(dados.getIdCartao()).orElseThrow();

      ClienteCartao clienteCartao = new ClienteCartao();
      clienteCartao.setCartao(cartao);
      clienteCartao.setCpf(dados.getCpf());
      clienteCartao.setLimite(dados.getLimiteLiberado());

      clienteCartaoRepository.save(clienteCartao);

    } catch (Exception e) {
      log.error("Erro ao receber solicitação de emissão de cartão: {}", e.getMessage());
    }

  }

}
