package com.alm.mscartoes.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Cartao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String nome;

  @Enumerated(EnumType.STRING)
  private BandeiraCartao bandeira;

  @Column
  private BigDecimal renda;

  @Column
  private BigDecimal limiteBasico;

  public Cartao(String nome,
      BandeiraCartao bandeira,
      BigDecimal renda,
      BigDecimal limite) {
    this.nome = nome;
    this.bandeira = bandeira;
    this.renda = renda;
    this.limiteBasico = limite;
  }

}
