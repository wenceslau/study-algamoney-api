package com.exemplo.algamoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.algamoney.api.model.Lancamento;
import com.exemplo.algamoney.api.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {

}
