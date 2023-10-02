package com.alm.msavaliadorcredito.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alm.msavaliadorcredito.application.exceptions.DadosClienteNotFoundException;
import com.alm.msavaliadorcredito.application.exceptions.ErroComunicacaoMicroservicesException;
import com.alm.msavaliadorcredito.application.exceptions.ErroSolicitacaoCartaoException;
import com.alm.msavaliadorcredito.domain.model.DadoSolicitacaoEmissaoCartao;
import com.alm.msavaliadorcredito.domain.model.DadosAvaliacao;
import com.alm.msavaliadorcredito.domain.model.ProtocoloSolicitacaoCartao;
import com.alm.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import com.alm.msavaliadorcredito.domain.model.SituacaoCliente;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("avaliacoes-credito")
public class AvaliadorCreditoController {

  @Autowired
  private AvaliadorCreditoService avaliadorCreditoService;

  @GetMapping
  public String status() {
    return "ok";
  }

  @GetMapping(value = "situacao-cliente", params = "cpf")
  public ResponseEntity<?> consultaSituacaoCliente(@RequestParam("cpf") String cpf) {

    try {
      SituacaoCliente situacaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);
      return ResponseEntity.ok(situacaoCliente);
    } catch (DadosClienteNotFoundException e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    } catch (ErroComunicacaoMicroservicesException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
    }

  }

  @PostMapping
  public ResponseEntity<?> realizarAvaliacao(@RequestBody DadosAvaliacao dados) {
    try {
      RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorCreditoService.realizarAvaliacao(dados.getCpf(),
          dados.getRenda());
      return ResponseEntity.ok(retornoAvaliacaoCliente);
    } catch (DadosClienteNotFoundException e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    } catch (ErroComunicacaoMicroservicesException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).body(e.getMessage());
    }
  }

  @PostMapping("solicitacoes-cartao")
  public ResponseEntity<?> solicitarCartao(@RequestBody DadoSolicitacaoEmissaoCartao dados) {
    try {
      ProtocoloSolicitacaoCartao protocoloSolicitacaoCartao = avaliadorCreditoService
          .solicitarEmissaoCartao(dados);

      return ResponseEntity.ok(protocoloSolicitacaoCartao);
    } catch (ErroSolicitacaoCartaoException e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
