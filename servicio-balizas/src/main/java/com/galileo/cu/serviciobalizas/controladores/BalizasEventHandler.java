package com.galileo.cu.serviciobalizas.controladores;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galileo.cu.commons.models.AccionEntidad;
import com.galileo.cu.commons.models.Balizas;
import com.galileo.cu.commons.models.Estados;
import com.galileo.cu.commons.models.TipoEntidad;
import com.galileo.cu.commons.models.Trazas;
import com.galileo.cu.commons.models.Unidades;
import com.galileo.cu.commons.models.Usuarios;
import com.galileo.cu.serviciobalizas.clientes.TraccarFeign;
import com.galileo.cu.serviciobalizas.interceptores.ValidateAuthorization;
import com.galileo.cu.serviciobalizas.repositorio.BalizasRepository;
import com.galileo.cu.serviciobalizas.repositorio.EstadosRepository;
import com.galileo.cu.serviciobalizas.repositorio.TrazasRepository;
import com.galileo.cu.serviciobalizas.repositorio.UnidadesRepository;

@Component
@RepositoryEventHandler(Balizas.class)
public class BalizasEventHandler {
	@Autowired
	private TraccarFeign traccar;

	@Autowired
	BalizasRepository balizasRepository;

	@Autowired
	TrazasRepository trazasRepo;

	@Autowired
	EntityManager entMg;

	@Autowired
	HttpServletRequest req;

	@Autowired
	ObjectMapper objectMapper;

	String descripcionTraza;

	@Autowired
	UnidadesRepository unidadesRepo;

	@Autowired
	EstadosRepository estadosRepo;

	@HandleBeforeCreate
	public void handleBalizasCreate(Balizas balizas) {
		/* Validando Autorización */
		try {
			ValidateAuthorization val = new ValidateAuthorization();
			System.out.println("REQUEST HandleBeforeCreate: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Fallo el Usuario Enviado no Coincide con el Autenticado ");
			}
		} catch (Exception e) {
			System.out.println("Fallo Antes de Crear Baliza Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Fallo Antes de Crear Baliza Validando Autorización");
		}

		if (balizas != null) {
			/*
			 * if (balizasRepository.findFirstByPuerto(balizas.getPuerto()) != null) {
			 * throw new
			 * RuntimeException("Fallo al Insertar Baliza el puerto está en uso ");
			 * }else
			 */
			if (balizasRepository.findFirstByClave(balizas.getClave()) != null) {
				throw new RuntimeException("Fallo al Insertar Baliza, La Clave está en uso");
			}
		}

		try {
			Estados estado = new Estados();
			estado.setId(19);
			balizas.setEstados(estado);

			Balizas balizasUpdate = traccar.salvar(balizas);

			System.out.println("Creando balizasUpdate IdDataminer: " + balizasUpdate.getIdDataminer());
			balizas.setIdDataminer(balizasUpdate.getIdDataminer());
			System.out.println("Creando balizas IdDataminer: " + balizas.getIdDataminer());
			balizas.setIdElement(balizasUpdate.getIdElement());
			System.out.println("Creando balizas getIdElement: " + balizas.getIdElement());
		} catch (Exception e) {
			System.out.println("Fallo al Insertar la Baliza en Dataminer ");
			System.out.println(e.getMessage());
			if (e.getMessage().contains("Error, ya existe una baliza con ese nombre en DataMiner")) {
				throw new RuntimeException("Error, ya existe una baliza con ese nombre en DataMiner");
			}
			throw new RuntimeException("Fallo al Insertar Baliza en Dataminer ");
		}
	}

	@HandleAfterCreate
	public void handleBalizasAfterCreate(Balizas balizas) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			System.out.println("REQUEST HandleAfterCreate: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Fallo el Usuario Enviado no Coincide con el Autenticado ");
			}
		} catch (Exception e) {
			System.out.println("Fallo Antes de Crear Baliza Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Fallo Antes de Crear Baliza Validando Autorización: " + e.getMessage());
		}

		try {
			traccar.enviarIdBalizaBD(balizas);
		} catch (Exception e) {
			System.out.println("Fallo Enviando id de Baliza a Dataminer Parámetro 2009: " + e.getMessage());
			throw new RuntimeException("Fallo Enviando id de Baliza a Dataminer Parámetro 2009");
		}

		try {
			System.out.println("Insertar la Baliza en la Trazabilidad AfterCreate");
			Trazas traza = new Trazas();
			AccionEntidad accion = new AccionEntidad();
			Usuarios usuario = new Usuarios();
			TipoEntidad entidad = new TipoEntidad();

			entidad.setId(3);
			accion.setId(1);
			usuario.setId(Long.parseLong(val.getJwtObjectMap().getId()));

			traza.setAccionEntidad(accion);
			traza.setTipoEntidad(entidad);
			traza.setUsuario(usuario);
			traza.setIdEntidad((int) balizas.getId());
			traza.setDescripcion("Fue Creada una nueva Baliza: " + balizas.getClave());
			trazasRepo.save(traza);

		} catch (Exception e) {
			System.out.println("Fallo al Insertar la Baliza en la Trazabilidad");
			System.out.println(e.getMessage());
			throw new RuntimeException("Fallo al Insertar Baliza en la Trazabilidad");
		}
	}

	@HandleBeforeSave
	public void handleBalizasBeforeSave(Balizas balizas) {
		descripcionTraza = null;
		entMg.detach(balizas);
		System.out.println("Actualizando Baliza");
		try {
			if (balizas == null) {
				throw new RuntimeException("Fallo la Baliza no debe ser Nulo ");
			} else {
				Balizas btmp = balizasRepository.findById(balizas.getId());
				if (btmp.getUnidades() == null && balizas.getUnidades() != null) {
					Optional<Unidades> uni = unidadesRepo.findById(balizas.getUnidades().getId());
					System.out.println("ASIGNANDO A UNIDAD LA BALIZA:" + balizas.getClave() + " UNIDAD:"
							+ uni.get().getDenominacion());
					try {
						Estados estado = new Estados();
						estado.setId(18);
						balizas.setEstados(estado);
						balizas.setFechaAsignaUni(LocalDateTime.now());
						traccar.asignar(balizas);
						descripcionTraza = "La Baliza: " + balizas.getClave() + " Fue Asignada a la Unidad: "
								+ balizas.getUnidades().getDenominacion();
					} catch (Exception er) {
						System.out.println("Fallo al Intentar Asignar la Baliza:" + balizas.getClave()
								+ " a una Unidad en DataMiner");
						System.out.println(er.getMessage());
						throw new RuntimeException("Fallo al Intentar Asignar la Baliza:" + balizas.getClave()
								+ " a una Unidad en DataMiner");
					}
				} else if (btmp.getUnidades() != null && balizas.getUnidades() == null) {
					Optional<Unidades> uni = unidadesRepo.findById(btmp.getUnidades().getId());
					System.out.println(
							"DESASIGNAR BALIZA:" + balizas.getClave() + " UNIDAD:" + uni.get().getDenominacion());
					// PENDIENTE POR RAFAEL DESASIGNACIÓN EN EL API
					try {
						Estados estado = new Estados();
						estado.setId(21);
						balizas.setEstados(estado);
						traccar.desasignar(balizas);
						descripcionTraza = "La Baliza: " + balizas.getClave() + " Fue Desasignada de la Unidad: "
								+ btmp.getUnidades().getDenominacion();
					} catch (Exception er) {
						System.out.println(
								"Fallo al Intentar Desasignar la Baliza:" + balizas.getClave() + " en DataMiner");
						System.out.println(er.getMessage());
						throw new RuntimeException(
								"Fallo al Intentar Desasignar la Baliza:" + balizas.getClave() + " en DataMiner");
					}
				} else if (btmp.getEstados().getId() != balizas.getEstados().getId()) {
					traccar.cambiarEstado(balizas);
					Optional<Estados> est = estadosRepo.findById(balizas.getEstados().getId());
					Optional<Estados> estTmp = estadosRepo.findById(btmp.getEstados().getId());
					descripcionTraza = "Cambio el Estado de la Baliza: " + balizas.getClave() + " de "
							+ estTmp.get().getDescripcion() + " - " + est.get().getDescripcion();
				} else {
					descripcionTraza = "Fue actualizada la Baliza: " + balizas.getClave();
				}
			}
		} catch (Exception e) {
			System.out.println("Fallo General al Actualizar la Baliza:" + balizas.getClave());
			System.out.println(e.getMessage());
			throw new RuntimeException("Fallo General al Actualizar la Baliza ");
		}
	}

	@HandleAfterSave
	public void handleBalizasAfterSave(Balizas balizas) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			System.out.println("REQUEST HandleAfterSave: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Fallo de Autorización");
			}
		} catch (Exception e) {
			System.out.println("Fallo Después de Salvar Baliza Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Fallo Después de Salvar Baliza Validando Autorización: " + e.getMessage());
		}

		try {
			System.out.println("Salvando la Baliza en la Trazabilidad AfterSave");
			Trazas traza = new Trazas();
			AccionEntidad accion = new AccionEntidad();
			Usuarios usuario = new Usuarios();
			TipoEntidad entidad = new TipoEntidad();

			entidad.setId(3);
			accion.setId(3);
			usuario.setId(Long.parseLong(val.getJwtObjectMap().getId()));

			traza.setAccionEntidad(accion);
			traza.setTipoEntidad(entidad);
			traza.setUsuario(usuario);
			traza.setIdEntidad((int) balizas.getId());
			traza.setDescripcion(descripcionTraza);
			trazasRepo.save(traza);

		} catch (Exception e) {
			System.out.println("Fallo al Salvar la Baliza en la Trazabilidad");
			System.out.println(e.getMessage());
			throw new RuntimeException("Fallo al Salvar la Baliza en la Trazabilidad");
		}
	}

	@HandleBeforeDelete
	public void handleBalizasDelete(Balizas balizas) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			System.out.println("REQUEST HandleBeforeDelete: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Fallo de Autorización");
			}
		} catch (Exception e) {
			System.out.println("Fallo Antes de Eliminar Baliza Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Fallo Antes de Eliminar Baliza Validando Autorización: " + e.getMessage());
		}

		try {
			System.out.println("*****HandleBeforeDelete Servidor:" + balizas.getServidor().getIpServicio());
			traccar.borrar(balizas);
		} catch (Exception e) {
			System.out.println("Fallo al Eliminar la Baliza en Dataminer ");
			System.out.println(e.getMessage());
			throw new RuntimeException("Fallo al eliminar la Baliza en Dataminer ");
		}
	}

	@HandleAfterDelete
	public void handleBalizasAfterDelete(Balizas balizas) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			System.out.println("REQUEST HandleAfterDelete: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Fallo de Autorización");
			}
		} catch (Exception e) {
			System.out.println("Fallo Después de Eliminar Baliza Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Fallo Después de Eliminar Baliza Validando Autorización: " + e.getMessage());
		}

		try {
			System.out.println("Eliminar la Baliza en la Trazabilidad AfterDelete");
			Trazas traza = new Trazas();
			AccionEntidad accion = new AccionEntidad();
			Usuarios usuario = new Usuarios();
			TipoEntidad entidad = new TipoEntidad();

			entidad.setId(3);
			accion.setId(2);
			usuario.setId(Long.parseLong(val.getJwtObjectMap().getId()));

			traza.setAccionEntidad(accion);
			traza.setTipoEntidad(entidad);
			traza.setUsuario(usuario);
			traza.setIdEntidad((int) balizas.getId());
			traza.setDescripcion("Fue Eliminada la Baliza: " + balizas.getClave());
			trazasRepo.save(traza);

		} catch (Exception e) {
			System.out.println("Fallo al Insertar la Eliminación de la Baliza en la Trazabilidad");
			System.out.println(e.getMessage());
			throw new RuntimeException("Fallo al Insertar la Eliminación de la Baliza en la Trazabilidad");
		}
	}
}
