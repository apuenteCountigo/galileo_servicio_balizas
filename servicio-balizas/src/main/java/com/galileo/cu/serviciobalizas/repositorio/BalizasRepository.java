package com.galileo.cu.serviciobalizas.repositorio;

import com.galileo.cu.commons.models.Balizas;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
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
			+ "WHERE (:clave IS NULL OR :clave='' OR b.clave like %:clave%) "
			+ "AND (:marca IS NULL OR :marca='' OR b.marca like %:marca%) "
			+ "AND (:numSerie IS NULL OR :numSerie='' OR b.numSerie like %:numSerie%) "
			+ "AND (:compania IS NULL OR :compania='' OR b.compania like %:compania%) "
			+ "AND (:objetivo IS NULL OR :objetivo = '' OR COALESCE(b.objetivo, '') LIKE %:objetivo%) "
			+ "AND (:modelo IS NULL OR :modelo='' OR b.modelo like %:modelo%) "
			+ "AND (:unidad=0 OR (:unidad>0 AND b.unidades.Id = :unidad) OR (:unidad=-1 AND b.unidades != null)  OR (:unidad=-2 AND b.unidades = null)) "
			+ " AND (:idEstadoBaliza=0 OR b.estados.Id = :idEstadoBaliza) "
			+ "AND ((:fechaFin!=null AND :fechaInicio!=null AND b.fechaAlta between :fechaInicio AND :fechaFin) "
			+ "OR (:fechaFin=null AND :fechaInicio!=null AND b.fechaAlta >=:fechaInicio) "
			+ "OR (:fechaFin=null AND :fechaInicio=null)) ")
	public Page<Balizas> buscarBalizas(int idEstadoBaliza, int unidad, String clave, String marca, String numSerie,
			String compania, String objetivo, String modelo,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin, Pageable p);

	@Query("SELECT b FROM Balizas b "
			+ "WHERE (:clave IS NULL OR :clave='' OR b.clave like %:clave%) "
			+ "AND (:marca IS NULL OR :marca='' OR b.marca like %:marca%) "
			+ "AND (:numSerie IS NULL OR :numSerie='' OR b.numSerie like %:numSerie%) "
			+ "AND (:compania IS NULL OR :compania='' OR b.compania like %:compania%) "
			+ "AND (:modelo IS NULL OR :modelo='' OR b.modelo like %:modelo%) "
			+ "AND (:unidad=0 OR (:unidad>0 AND b.unidades.Id = :unidad) OR (:unidad=-1 AND b.unidades != null)  OR (:unidad=-2 AND b.unidades = null)) "
			+ " AND (:idEstadoBaliza=0 OR b.estados.Id = :idEstadoBaliza) "
			+ "AND ((:fechaFin!=null AND :fechaInicio!=null AND b.fechaAlta between :fechaInicio AND :fechaFin) "
			+ "OR (:fechaFin=null AND :fechaInicio!=null AND b.fechaAlta >=:fechaInicio) "
			+ "OR (:fechaFin=null AND :fechaInicio=null)) ")
	public Page<Balizas> buscarBalizasStock(int idEstadoBaliza, int unidad, String clave, String marca, String numSerie,
			String compania, String modelo,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin, Pageable p);

	@Query("SELECT b FROM Balizas b WHERE " +
			"(:unidad IS NULL OR (:unidad>0 AND b.unidades.id = :unidad) OR (:unidad=-1 AND b.unidades != null)  OR (:unidad=-2 AND b.unidades = null)) AND "
			+
			"(:idEstadoBaliza IS NULL OR b.estados.id = :idEstadoBaliza) AND " +
			"(:clave IS NULL OR b.clave = :clave) AND " +
			"(:modelo IS NULL OR b.modelo = :modelo) AND " +
			"(:objetivo IS NULL OR b.objetivo = :objetivo) AND " +
			"(:numSerie IS NULL OR b.numSerie = :numSerie) AND " +
			"(:marca IS NULL OR b.marca = :marca) AND " +
			"(:compania IS NULL OR b.compania = :compania) AND " +
			"(:fechaInicio IS NULL OR :fechaFin IS NULL OR b.fechaAlta BETWEEN :fechaInicio AND :fechaFin)")
	List<Balizas> findByFilters(
			@Param("unidad") int unidad,
			@Param("idEstadoBaliza") Long idEstadoBaliza,
			@Param("clave") String clave,
			@Param("modelo") String modelo,
			@Param("objetivo") String objetivo,
			@Param("numSerie") String numSerie,
			@Param("marca") String marca,
			@Param("compania") String compania,
			@Param("fechaInicio") LocalDateTime fechaInicio,
			@Param("fechaFin") LocalDateTime fechaFin, Pageable p);

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
