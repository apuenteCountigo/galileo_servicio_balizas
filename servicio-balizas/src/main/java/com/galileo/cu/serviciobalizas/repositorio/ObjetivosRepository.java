package com.galileo.cu.serviciobalizas.repositorio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.galileo.cu.commons.models.Balizas;
import com.galileo.cu.commons.models.Objetivos;
import com.galileo.cu.commons.models.Unidades;

@RepositoryRestResource(exported = false)
public interface ObjetivosRepository extends CrudRepository<Objetivos, Long> {
    long countByBalizas(Balizas bl);
}
