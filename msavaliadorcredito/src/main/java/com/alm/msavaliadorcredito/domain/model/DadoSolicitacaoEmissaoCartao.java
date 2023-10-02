package com.alm.msavaliadorcredito.domain.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DadoSolicitacaoEmissaoCartao {
  private Long idCartao;
  private String cpf;
  private String endereco;
  private BigDecimal limiteLiberado;
}
