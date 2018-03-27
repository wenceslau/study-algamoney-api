package com.exemplo.algamoney.api.repository.pessoa;

import java.util.List;

import com.exemplo.algamoney.api.model.Pessoa;
import com.exemplo.algamoney.api.repository.filter.PessoaFilter;

public interface PessoaRepositoryQuery {

	public List<Pessoa> filtrar(PessoaFilter pessoaFilter);
	
}
