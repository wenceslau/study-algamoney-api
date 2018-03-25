package com.exemplo.algamoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exemplo.algamoney.api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
