package com.exemplo.algamoney.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.exemplo.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.exemplo.algamoney.api.dto.LancamentoEstatisticaDia;
import com.exemplo.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.exemplo.algamoney.api.model.Lancamento;
import com.exemplo.algamoney.api.repository.filter.LancamentoFilter;
import com.exemplo.algamoney.api.repository.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {

	public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim);

	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);

	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia);

	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);

	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);

}
