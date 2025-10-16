package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.DTO.AtivosCarteiraRequestDTO;
import com.devsdoagi.agicripto.DTO.AtivosCarteiraResponseDTO;
import com.devsdoagi.agicripto.DTO.TransacoesResponseDTO;
import com.devsdoagi.agicripto.model.AtivosCarteira;
import com.devsdoagi.agicripto.model.Carteira;
import com.devsdoagi.agicripto.model.Criptomoedas;
import com.devsdoagi.agicripto.repository.AtivosCarteiraRepository;
import com.devsdoagi.agicripto.repository.CarteiraRepository;
import com.devsdoagi.agicripto.repository.CriptomoedasRepository;
import com.devsdoagi.agicripto.service.AtivosCarteiraService;
import com.devsdoagi.agicripto.service.CarteiraService;
import com.devsdoagi.agicripto.service.CriptomoedasService;
import com.devsdoagi.agicripto.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ativos-carteira")
public class AtivosCarteiraController {

    private final AtivosCarteiraService service;
    private final CarteiraService carteiraService;
    private final UsuariosService usuariosService;
    @Autowired
    private final CarteiraRepository carteiraRepository;
    @Autowired
    private final CriptomoedasRepository criptomoedasRepository;
    @Autowired
    private final AtivosCarteiraRepository ativosCarteiraRepository;
    private final CriptomoedasService  criptomoedaService;

    public AtivosCarteiraController(AtivosCarteiraService service, CarteiraService carteiraService, UsuariosService usuariosService, CarteiraRepository carteiraRepository, CriptomoedasRepository criptomoedasRepository, AtivosCarteiraRepository ativosCarteiraRepository, CriptomoedasService criptomoedaService) {
        this.service = service;
        this.carteiraService = carteiraService;
        this.usuariosService = usuariosService;
        this.carteiraRepository = carteiraRepository;
        this.criptomoedasRepository = criptomoedasRepository;
        this.ativosCarteiraRepository = ativosCarteiraRepository;
        this.criptomoedaService = criptomoedaService;
    }

    @GetMapping
    public ResponseEntity<List<AtivosCarteiraResponseDTO>> listar() {
        List<AtivosCarteiraResponseDTO> ativos = service.listarTodos();
        return ResponseEntity.ok(ativos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtivosCarteiraResponseDTO> buscarPorId(@PathVariable Integer id) {
        AtivosCarteiraResponseDTO ativo = service.buscarPorId(id);
        return ResponseEntity.ok(ativo);
    }

    @PostMapping
    public ResponseEntity<AtivosCarteiraResponseDTO> criar(@RequestBody AtivosCarteiraRequestDTO requestDTO) {
        AtivosCarteiraResponseDTO novoAtivo = service.criar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtivo);
    }

    @PutMapping("/{idCarteira}/{idCriptomoeda}")
    public ResponseEntity<AtivosCarteiraResponseDTO> atualizar(
            @PathVariable Integer idCarteira,
            @PathVariable Integer idCriptomoeda,
            @RequestParam BigDecimal quantidade,
            @RequestParam BigDecimal valorTotal,
            @RequestParam boolean isCompra) {

        // Busca a carteira
        Carteira carteira = carteiraRepository.findById(idCarteira)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carteira não encontrada"));

        // Busca a criptomoeda
        Criptomoedas criptomoeda = criptomoedasRepository.findById(idCriptomoeda)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Criptomoeda não encontrada"));

        // Atualiza ou cria o ativo
        AtivosCarteira ativo = service.atualizar(
                carteira, criptomoeda, quantidade, valorTotal, isCompra);

        // Retorna o DTO de resposta
        return ResponseEntity.ok(new AtivosCarteiraResponseDTO(ativo));
    }


  //  @DeleteMapping("/{id}")
  //  @ResponseStatus(HttpStatus.NO_CONTENT)
   // public void deletar(@PathVariable Integer id) {
      //  service.deletar(id);
//    }

     /* // ✅ NOVO ENDPOINT: lista apenas as criptomoedas que o usuário possui (para o botão "Vender")
    @GetMapping("/do-usuario/{idUsario}")
    public ResponseEntity<List<AtivoVenderResponseDTO>> listarCriptomoedasDoUsuario(HttpSession session) {
        Integer userId = usuariosService.checarSessaoEObterIdUsuario(session);
        List<AtivoVenderResponseDTO> ativos = carteiraService.listarCriptomoedasUsuario(userId);
        return ResponseEntity.ok(ativos);
    }*/


    @GetMapping("/do-usuario/{idUsuario}")
    public ResponseEntity<List<AtivosCarteiraResponseDTO>> listarAtivosDoUsuario(@PathVariable Integer idUsuario) {
        List<AtivosCarteiraResponseDTO> ativos = service.listarAtivosPorUsuario(idUsuario);
        return ResponseEntity.ok(ativos);
    }
    @GetMapping("/transacoes/{idUsuario}")
    public ResponseEntity<List<TransacoesResponseDTO>> listarTransacoesUsuario(@PathVariable Integer idUsuario) {
        List<TransacoesResponseDTO> transacoes = service.listarTransacoesPorUsuario(idUsuario);
        return ResponseEntity.ok(transacoes);
    }
    @GetMapping("/existe/{idCriptomoeda}")
    public ResponseEntity<Boolean> verificarExistencia(@PathVariable Integer idCriptomoeda) {
        boolean existe = ativosCarteiraRepository.existsByCriptomoedas_Id(idCriptomoeda);
        return ResponseEntity.ok(existe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirCriptomoeda(@PathVariable Integer id) {
        // Verifica se há investimentos (ativos) usando essa cripto
        if (ativosCarteiraRepository.existsByCriptomoedas_Id(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN) // força tipo texto
                    .body("Não é possível excluir esta criptomoeda, pois há investimentos vinculados a ela.");
        }

        // Caso não tenha vínculo, pode excluir normalmente
        criptomoedaService.deletarPorId(id);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN) // força tipo texto
                .body(" Criptomoeda excluída com sucesso!");
    }

}