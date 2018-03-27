package com.exemplo.algamoney.api.repository.pessoa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.util.StringUtils;

import com.exemplo.algamoney.api.model.Pessoa;
import com.exemplo.algamoney.api.repository.filter.PessoaFilter;

public class PessoaRepositoryImpl implements PessoaRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public List<Pessoa> filtrar(PessoaFilter pessoaFilter) {

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Pessoa> criteria = builder.createQuery(Pessoa.class);

		Root<Pessoa> root = criteria.from(Pessoa.class);

		Predicate[] predicates = criarRestricoes(pessoaFilter, builder, root);
		criteria.where(predicates);
		
		TypedQuery<Pessoa> query = manager.createQuery(criteria);
		
		//adicionarRestricosPaginacao(query, pageable);

		return query.getResultList();
		//return new PageImpl<>(query.getResultList(), pageable, total(pessoaFilter));
	}

//	private Long total(PessoaFilter pessoaFilter) {
//	
//		CriteriaBuilder builder = manager.getCriteriaBuilder();
//		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
//		Root<Pessoa> root = criteria.from(Pessoa.class);
//		
//		Predicate[] predicates = criarRestricoes(pessoaFilter, builder, root);
//		criteria.where(predicates);
//		
//		criteria.select(builder.count(root));
//					
//		return manager.createQuery(criteria).getSingleResult();
//	}
//
//	private void adicionarRestricosPaginacao(TypedQuery<?> query, Pageable pageable) {
//
//		int paginaAtual = pageable.getPageNumber();
//		int totalRegistrosPorPagina = pageable.getPageSize();
//		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
//				
//		query.setFirstResult(primeiroRegistroDaPagina);
//		
//		query.setFirstResult(totalRegistrosPorPagina);
//	}

	private Predicate[] criarRestricoes(PessoaFilter pessoaFilter, CriteriaBuilder builder, Root<Pessoa> root) {

		List<Predicate> predicates = new ArrayList<>();
		
		System.out.println("pessoaFilter.getNome(): " + pessoaFilter.getNome());

		if (!StringUtils.isEmpty(pessoaFilter.getNome())) {
			predicates.add(builder.like(
					builder.lower(root.get("nome")),"%" + pessoaFilter.getNome().toLowerCase() + "%"));
		}		
		
		System.out.println("pessoaFilter.getAtivo(): " + pessoaFilter.getAtivo());
		if (pessoaFilter.getAtivo() != null) {
			predicates.add(builder.equal(root.get("ativo"), pessoaFilter.getAtivo()));
		}

		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
