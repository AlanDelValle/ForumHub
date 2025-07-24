package com.ForumHub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ForumHub.model.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensagem(String titulo, String mensagem);
    Page<Topico> findByCursoAndYear(String curso, int year, Pageable pageable);
}