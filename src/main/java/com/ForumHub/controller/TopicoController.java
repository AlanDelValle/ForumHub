package com.ForumHub.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ForumHub.dto.TopicoRequestDTO;
import com.ForumHub.dto.TopicoResponseDTO;
import com.ForumHub.model.Topico;
import com.ForumHub.repository.TopicoRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    private static final Logger logger = LoggerFactory.getLogger(TopicoController.class);

    @Autowired
    private TopicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<TopicoResponseDTO> cadastrar(@RequestBody @Valid TopicoRequestDTO dados, UriComponentsBuilder uriBuilder) {
        if (repository.existsByTituloAndMensagem(dados.titulo(), dados.mensagem())) {
            return ResponseEntity.badRequest().build();
        }
        Topico topico = new Topico(null, dados.titulo(), dados.mensagem(), LocalDateTime.now(), "ATIVO", dados.autor(), dados.curso(), dados.year());
        repository.save(topico);
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoResponseDTO(topico.getId(), topico.getTitulo(), topico.getMensagem(),
                topico.getDataCriacao(), topico.getStatus(), topico.getAutor(), topico.getCurso()));
    }

    @GetMapping
    public ResponseEntity<Page<TopicoResponseDTO>> listar(@PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        var page = repository.findAll(paginacao).map(topico -> new TopicoResponseDTO(topico.getId(), topico.getTitulo(),
                topico.getMensagem(), topico.getDataCriacao(), topico.getStatus(), topico.getAutor(), topico.getCurso()));
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponseDTO> detalhar(@PathVariable Long id) {
        Optional<Topico> topico = repository.findById(id);        
        if (topico.isPresent()) {
            Topico t = topico.get();            
            return ResponseEntity.ok(new TopicoResponseDTO(t.getId(), t.getTitulo(), t.getMensagem(), t.getDataCriacao(),
                    t.getStatus(), t.getAutor(), t.getCurso()));
        }
        return ResponseEntity.notFound().build();    
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid TopicoRequestDTO dados) {
        Optional<Topico> topico = repository.findById(id);
        if (topico.isPresent()) {
            if (repository.existsByTituloAndMensagem(dados.titulo(), dados.mensagem())) {
                return ResponseEntity.badRequest().build();
            }
            Topico t = topico.get();
            t.setTitulo(dados.titulo());
            t.setMensagem(dados.mensagem());
            t.setAutor(dados.autor());
            t.setCurso(dados.curso());
            repository.save(t);
            return ResponseEntity.ok(new TopicoResponseDTO(t.getId(), t.getTitulo(), t.getMensagem(), t.getDataCriacao(),
                    t.getStatus(), t.getAutor(), t.getCurso()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Optional<Topico> topico = repository.findById(id);
        if (topico.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/busca")
    public ResponseEntity<Page<TopicoResponseDTO>> buscarPorCursoEAno(@RequestParam String curso, @RequestParam int ano,
                                                                     @PageableDefault(size = 10, sort = {"dataCriacao"}) Pageable paginacao) {
        var page = repository.findByCursoAndYear(curso, ano, paginacao).map(topico -> new TopicoResponseDTO(topico.getId(),
                topico.getTitulo(), topico.getMensagem(), topico.getDataCriacao(), topico.getStatus(), topico.getAutor(), topico.getCurso()));
        return ResponseEntity.ok(page);
    }
}