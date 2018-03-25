package com.exemplo.algamoney.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.algamoney.api.model.Lancamento;
import com.exemplo.algamoney.api.model.Pessoa;
import com.exemplo.algamoney.api.repository.LancamentoRepository;
import com.exemplo.algamoney.api.repository.PessoaRepository;
import com.exemplo.algamoney.api.service.exception.PessoaInexistenteOuInativoExcpetion;

@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	public Lancamento salvar(Lancamento lancamento) {
		Pessoa pessoa =pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
		
		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativoExcpetion();
		}
		
		return lancamentoRepository.save(lancamento);
		
	}
	
}
