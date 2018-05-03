package com.exemplo.algamoney.api.repository.lancamento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.exemplo.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.exemplo.algamoney.api.dto.LancamentoEstatisticaDia;
import com.exemplo.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.exemplo.algamoney.api.model.Lancamento;
import com.exemplo.algamoney.api.repository.filter.LancamentoFilter;
import com.exemplo.algamoney.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;
	

	@Override
	public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim) {

		CriteriaBuilder criteriaBuilder;
		CriteriaQuery<LancamentoEstatisticaPessoa> criteriaQuery;
		Root<Lancamento> root;

		// Objeto de contrucado dos criterios de busca
		criteriaBuilder = manager.getCriteriaBuilder();

		// objeto a ser devolvido no criterio
		criteriaQuery = criteriaBuilder.createQuery(LancamentoEstatisticaPessoa.class);

		// Objeto de onde sera buscado os dados
		root = criteriaQuery.from(Lancamento.class);

		// Constroi o objeto a ser devolvido e passa os parametros desse objeto a partir
		// dos dados do objeto de busca na base
		criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaPessoa.class, 
				root.get("tipo"),
				root.get("pessoa"),
				criteriaBuilder.sum(root.get("valor"))));


		//Parametros de data maior ou igual e menor ou igual
		criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimeto"), inicio),
				criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimeto"), fim));

		//Agrupamento da consulta
		criteriaQuery.groupBy(root.get("tipo"), root.get("pessoa"));

		TypedQuery<LancamentoEstatisticaPessoa> typedQuery = manager.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	

	@Override
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia) {

		CriteriaBuilder criteriaBuilder;
		CriteriaQuery<LancamentoEstatisticaCategoria> criteriaQuery;
		Root<Lancamento> root;

		// Objeto de contrucado dos criterios de busca
		criteriaBuilder = manager.getCriteriaBuilder();

		// objeto a ser devolvido no criterio
		criteriaQuery = criteriaBuilder.createQuery(LancamentoEstatisticaCategoria.class);

		// Objeto de onde sera buscado os dados
		root = criteriaQuery.from(Lancamento.class);

		// Constro o objeto a ser devolvido e passa os paramentros desse objeto a partir
		// dos dados do objeto de busca na base
		criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaCategoria.class, root.get("categoria"),
				criteriaBuilder.sum(root.get("valor"))));

		//Parametros de busca
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

		//Parametros de data maior ou igual e menor ou igual
		criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimeto"), primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimeto"), ultimoDia));

		//Agrupamento da consulta
		criteriaQuery.groupBy(root.get("categoria"));

		TypedQuery<LancamentoEstatisticaCategoria> typedQuery = manager.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}


	@Override
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia) {

		CriteriaBuilder criteriaBuilder;
		CriteriaQuery<LancamentoEstatisticaDia> criteriaQuery;
		Root<Lancamento> root;

		// Objeto de contrucado dos criterios de busca
		criteriaBuilder = manager.getCriteriaBuilder();

		// objeto a ser devolvido no criterio
		criteriaQuery = criteriaBuilder.createQuery(LancamentoEstatisticaDia.class);

		// Objeto de onde sera buscado os dados
		root = criteriaQuery.from(Lancamento.class);

		// Constro o objeto a ser devolvido e passa os paramentros desse objeto a partir
		// dos dados do objeto de busca na base
		criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticaDia.class, 
				root.get("tipo"),
				root.get("dataVencimeto"),
				criteriaBuilder.sum(root.get("valor"))));

		//Parametros de busca
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

		//Parametros de data maior ou igual e menor ou igual
		criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get("dataVencimeto"), primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get("dataVencimeto"), ultimoDia));

		//Agrupamento da consulta
		criteriaQuery.groupBy(root.get("tipo"), root.get("dataVencimeto"));

		TypedQuery<LancamentoEstatisticaDia> typedQuery = manager.createQuery(criteriaQuery);

		return typedQuery.getResultList();
	}

	
	
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {

		System.out.println("Desc: " + lancamentoFilter.getDescricao());

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<Lancamento> query = manager.createQuery(criteria);

		adicionarRestricosPaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);

		Root<Lancamento> root = criteria.from(Lancamento.class);

		criteria.select(builder.construct(ResumoLancamento.class, root.get("codigo"), root.get("descricao"),
				root.get("dataVencimeto"), root.get("dataPagamento"), root.get("valor"), root.get("tipo"),
				root.get("categoria").get("nome"), root.get("pessoa").get("nome")));

		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);

		adicionarRestricosPaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));

		return manager.createQuery(criteria).getSingleResult();
	}

	private void adicionarRestricosPaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistroPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistroPorPagina;

		query.setFirstResult(primeiroRegistroDaPagina);

		query.setMaxResults(totalRegistroPorPagina);

	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {

		List<Predicate> predicates = new ArrayList<>();

		if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {

			predicates.add(builder.like(builder.lower(root.get("descricao")),
					"%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}

		if (lancamentoFilter.getDataVencimentoDe() != null) {
			predicates.add(
					builder.greaterThanOrEqualTo(root.get("dataVencimeto"), lancamentoFilter.getDataVencimentoDe()));

		}

		if (lancamentoFilter.getDataVencimentoAte() != null) {
			predicates
					.add(builder.lessThanOrEqualTo(root.get("dataVencimeto"), lancamentoFilter.getDataVencimentoAte()));

		}

		// TODO Auto-generated method stub
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
