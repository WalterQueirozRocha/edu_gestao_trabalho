package br.ucsal.edugestao.avaliacao.controller;

import br.ucsal.edugestao.academico.dto.EtapaDto;
import br.ucsal.edugestao.avaliacao.dto.*;
import br.ucsal.edugestao.avaliacao.service.AutorizacaoAlteracaoService;
import br.ucsal.edugestao.avaliacao.service.AvaliacaoService;
import br.ucsal.edugestao.avaliacao.service.EtapaBoletimService;
import br.ucsal.edugestao.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AvaliacaoBoletimController {

    private final AvaliacaoService avaliacaoService;
    private final EtapaBoletimService etapaBoletimService;
    private final AutorizacaoAlteracaoService autorizacaoAlteracaoService;

    public AvaliacaoBoletimController(
            AvaliacaoService avaliacaoService,
            EtapaBoletimService etapaBoletimService,
            AutorizacaoAlteracaoService autorizacaoAlteracaoService
    ) {
        this.avaliacaoService = avaliacaoService;
        this.etapaBoletimService = etapaBoletimService;
        this.autorizacaoAlteracaoService = autorizacaoAlteracaoService;
    }

    @GetMapping("/avaliacoes")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ADMINISTRADOR')")
    public List<AvaliacaoDto> listarAvaliacoes(
            @RequestParam(required = false) Long turmaDisciplinaId,
            @RequestParam(required = false) Long etapaId
    ) {
        return avaliacaoService.listarAvaliacoes(turmaDisciplinaId, etapaId);
    }

    @PostMapping("/avaliacoes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
    public AvaliacaoDto criarAvaliacao(@Valid @RequestBody AvaliacaoDto dto) {
        return avaliacaoService.criarAvaliacao(dto);
    }

    @GetMapping("/avaliacoes/{id}/notas")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR', 'ADMINISTRADOR')")
    public List<NotaItemDto> listarNotas(@PathVariable Long id) {
        return avaliacaoService.listarNotasDaAvaliacao(id);
    }

    @PutMapping("/avaliacoes/{id}/notas")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'COORDENADOR')")
    public List<NotaItemDto> lancarNotas(
            @PathVariable Long id,
            @Valid @RequestBody LancarNotasRequest req
    ) {
        return avaliacaoService.lancarNotas(id, req.notas());
    }

    @PostMapping("/etapas/{id}/fechar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public EtapaDto fecharEtapa(@PathVariable Long id) {
        return etapaBoletimService.fecharEtapa(id);
    }

    @PostMapping("/etapas/{id}/publicar")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public EtapaDto publicarEtapa(@PathVariable Long id) {
        return etapaBoletimService.publicarEtapa(id);
    }

    @GetMapping("/autorizacoes-alteracao")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR', 'PROFESSOR')")
    public List<AutorizacaoAlteracaoDto> listarAutorizacoes() {
        return autorizacaoAlteracaoService.listarTodas();
    }

    @PostMapping("/autorizacoes-alteracao")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR')")
    public AutorizacaoAlteracaoDto criarAutorizacao(@Valid @RequestBody AutorizacaoAlteracaoDto dto) {
        return autorizacaoAlteracaoService.criarAutorizacao(dto);
    }

    @GetMapping("/turmas/{id}/resultados")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'ADMINISTRADOR', 'PROFESSOR')")
    public List<ResultadoAlunoTurmaDto> consultarResultadosDaTurma(
            @PathVariable Long id,
            @RequestParam(required = false) Long etapaId
    ) {
        return etapaBoletimService.consultarResultadosDaTurma(id, etapaId);
    }

    @GetMapping("/boletins/me")
    @PreAuthorize("hasRole('ALUNO')")
    public List<BoletimEtapaDto> consultarMeuBoletim() {
        return etapaBoletimService.consultarBoletimDoAlunoLogado();
    }

    @GetMapping("/responsavel/alunos")
    @PreAuthorize("hasAnyRole('RESPONSAVEL', 'COORDENADOR', 'ADMINISTRADOR')")
    public List<UsuarioResponse> listarAlunosDoResponsavel() {
        return etapaBoletimService.listarAlunosDoResponsavelLogado();
    }

    @GetMapping("/alunos/{id}/boletim")
    public List<BoletimEtapaDto> consultarBoletimDoAluno(
            @PathVariable Long id,
            @RequestParam(required = false) Long etapaId
    ) {
        return etapaBoletimService.consultarBoletinsDoAluno(id, etapaId);
    }
}
