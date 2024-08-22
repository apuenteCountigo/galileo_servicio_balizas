package com.galileo.cu.serviciobalizas.repositorio;

import com.galileo.cu.commons.models.Balizas;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RepositoryRestResource(collectionResourceRel = "balizas", path = "balizas")
public interface BalizasRepository extends PagingAndSortingRepository<Balizas, Long> {
	@Query("SELECT b FROM Balizas b "
			+ " where (:unidad=0 or b.unidades.Id = :unidad) "
			+ " AND (:idEstadoBaliza=0 or b.estados.Id = :idEstadoBaliza) ")
	public Page<Balizas> filtro(int unidad, int idEstadoBaliza, Pageable p);

	@Query("SELECT b FROM Objetivos o LEFT JOIN o.balizas b "
			+ "WHERE o.balizas!=null "
			+ " AND ( "
			+ "			(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id=1)) "
			+ "	OR ( "
			+ "		(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id=2)) "
			+ "		AND (b.unidades.id=(SELECT unidad.id FROM UnidadesUsuarios WHERE usuario.id=:idAuth AND estado.id=6)) "
			+ "		) "
			+ " OR ("
			+ "		(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id>2)) "
			+ "		AND ("
			+ "		o.id IN (SELECT idEntidad FROM Permisos WHERE tipoEntidad.id=8 AND usuarios.id=:idAuth) "
			+ "			) "
			+ " 	) "
			+ ") "
			+ "AND (:objetivo='' OR o.descripcion like %:objetivo%) AND (:idObjetivo=0 OR o.id=:idObjetivo) ")
	public Page<Balizas> filtrarObjetivo(long idAuth, String objetivo, int idObjetivo, Pageable p);

	/* FILTRO PARA BALIZAS STOCK */
	@Query("SELECT b FROM Balizas b "
			+ "WHERE (:clave='' or b.clave like %:clave%) "
			+ "and (:marca='' or b.marca like %:marca%) "
			+ "and (:numSeries='' or b.numSerie like %:numSeries%) "
			+ "and (:compania='' or b.compania like %:compania%) "
			+ "and (:unidad=0 OR (:unidad>0 AND b.unidades.Id = :unidad) OR (:unidad=-1 AND b.unidades != null)  OR (:unidad=-2 AND b.unidades = null)) "
			+ " AND (:idEstadoBaliza=0 or b.estados.Id = :idEstadoBaliza) "
			+ "and ((:fechaFin!=null and :fechaInicio!=null and b.fechaAlta between :fechaInicio and :fechaFin) "
			+ "or (:fechaFin=null and :fechaInicio!=null and b.fechaAlta >=:fechaInicio) "
			+ "or (:fechaFin=null and :fechaInicio=null)) ")
	public Page<Balizas> buscarBalizas(int idEstadoBaliza, int unidad, String clave, String marca, String numSeries,
			String compania,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin, Pageable p);

	@RestResource(path = "buscarId")
	public Balizas findById(long id);

	public Balizas findFirstByClave(String nombre_baliza);

	public Balizas findFirstByPuerto(String puerto);

	@Query("SELECT b FROM Balizas b WHERE b.clave=:clave OR b.puerto=:puerto")
	public List<Balizas> validarUnicos(String clave, String puerto);

	public List<Balizas> findAll();

	/* FILTRO PARA BALIZAS con operacion */
	/*
	 * @Query("SELECT b.id, "
	 * + "b.estados, "
	 * + "b.tipoBaliza, "
	 * + "b.tipoContrato, "
	 * + "b.unidades, "
	 * + "b.clave, "
	 * + "b.marca, "
	 * + "b.modelo, "
	 * + "b.numSerie, "
	 * + "b.tipoCoordenada, "
	 * + "b.imei, "
	 * + "b.telefono1, "
	 * + "b.compania, "
	 * + "b.pin1, "
	 * + "b.pin2, "
	 * + "b.puk, "
	 * + "b.iccTarjeta, "
	 * + "b.fechaAlta, "
	 * + "b.fechaAsignaUni, "
	 * + "b.fechaAsignaOp, "
	 * + "b.idDataminer, "
	 * + "b.idElement, "
	 * + "b.puerto, "
	 * + "b.notas, "
	 * + "b.servidor FROM Balizas b ")
	 * public Page<Balizas> balizasOpObj(Pageable p);
	 */

}
