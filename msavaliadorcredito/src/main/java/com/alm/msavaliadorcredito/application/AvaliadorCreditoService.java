package com.alm.msavaliadorcredito.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alm.msavaliadorcredito.application.exceptions.DadosClienteNotFoundException;
import com.alm.msavaliadorcredito.application.exceptions.ErroComunicacaoMicroservicesException;
import com.alm.msavaliadorcredito.application.exceptions.ErroSolicitacaoCartaoException;
import com.alm.msavaliadorcredito.domain.model.Cartao;
import com.alm.msavaliadorcredito.domain.model.CartaoAprovado;
import com.alm.msavaliadorcredito.domain.model.CartaoCliente;
import com.alm.msavaliadorcredito.domain.model.DadoSolicitacaoEmissaoCartao;
import com.alm.msavaliadorcredito.domain.model.DadosCliente;
import com.alm.msavaliadorcredito.domain.model.ProtocoloSolicitacaoCartao;
import com.alm.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import com.alm.msavaliadorcredito.domain.model.SituacaoCliente;
import com.alm.msavaliadorcredito.infra.clients.CartoesResourceClient;
import com.alm.msavaliadorcredito.infra.clients.ClienteResourceClient;
import com.alm.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
public class AvaliadorCreditoService {

  @Autowired
  private  ClienteResourceClient clientesClient;
  
  @Autowired
  private  CartoesResourceClient cartoesClient;
  
  @Autowired
  private  SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;

  public SituacaoCliente obterSituacaoCliente(String cpf)
      throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {

    try {
      ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
      ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);

      return SituacaoCliente
          .builder()
          .cliente(dadosClienteResponse.getBody())
          .cartoes(cartoesResponse.getBody())
          .build();
    } catch (FeignException.FeignClientException e) {
      int status = e.status();
      if (HttpStatus.NOT_FOUND.value() == status) {
        throw new DadosClienteNotFoundException();
      }

      throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
    }

  }

  public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)
      throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
    try {
      ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
      ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAte(renda);

      List<Cartao> cartoes = cartoesResponse.getBody();

      var listaCartoesAprovados = cartoes.stream().map(cartao -> {
        DadosCliente dadosCliente = dadosClienteResponse.getBody();

        BigDecimal limiteBasico = cartao.getLimiteBasico();
        BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());

        var fator = idadeBD.divide(BigDecimal.valueOf(10));
        BigDecimal limiteAprovado = fator.multiply(limiteBasico);

        CartaoAprovado aprovado = new CartaoAprovado();
        aprovado.setCartao(cartao.getNome());
        aprovado.setBandeira(cartao.getBandeira());
        aprovado.setLimiteAprovado(limiteAprovado);

        return aprovado;

      }).collect(Collectors.toList());

      return new RetornoAvaliacaoCliente(listaCartoesAprovados);

    } catch (FeignException.FeignClientException e) {
      int status = e.status();
      if (HttpStatus.NOT_FOUND.value() == status) {
        throw new DadosClienteNotFoundException();
      }

      throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
    }
  }

  public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadoSolicitacaoEmissaoCartao dados) {
    try {
      emissaoCartaoPublisher.solicitacaoCartao(dados);
      var protocolo = UUID.randomUUID().toString();

      return new ProtocoloSolicitacaoCartao(protocolo);

    } catch (Exception e) {
      throw new ErroSolicitacaoCartaoException(e.getMessage());
    }
  }

}
