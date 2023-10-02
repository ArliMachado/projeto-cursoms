package com.alm.mscartoes.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DadoSolicitacaoEmissaoCartao {
  private Long idCartao;
  private String cpf;
  private String endereco;
  private BigDecimal limiteLiberado;
}
