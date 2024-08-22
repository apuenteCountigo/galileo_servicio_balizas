package com.galileo.cu.serviciobalizas.repositorio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.galileo.cu.commons.models.Trazas;

@RepositoryRestResource(exported = false)
public interface TrazasRepository extends CrudRepository<Trazas, Long> {
    
}
