package com.exemplo.algamoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.algamoney.api.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

}
