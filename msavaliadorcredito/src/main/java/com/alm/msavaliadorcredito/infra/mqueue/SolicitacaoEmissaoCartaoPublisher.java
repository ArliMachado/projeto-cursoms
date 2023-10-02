package com.alm.msavaliadorcredito.infra.mqueue;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alm.msavaliadorcredito.domain.model.DadoSolicitacaoEmissaoCartao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
public class SolicitacaoEmissaoCartaoPublisher {

  @Autowired
  private RabbitTemplate rabbitTemplate;
  
  @Autowired
  private Queue queueEmissaoCartoes;

  public void solicitacaoCartao(DadoSolicitacaoEmissaoCartao dados) throws JsonProcessingException {
    var json = convertIntoJson(dados);
    rabbitTemplate.convertAndSend(queueEmissaoCartoes.getName(), json);

  }

  private String convertIntoJson(DadoSolicitacaoEmissaoCartao dados) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    var json = mapper.writeValueAsString(dados);
    return json;
  }

}