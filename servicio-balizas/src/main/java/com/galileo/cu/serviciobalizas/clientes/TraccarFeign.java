package com.galileo.cu.serviciobalizas.clientes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.galileo.cu.commons.models.Balizas;


@FeignClient(name="servicio-apis")
public interface TraccarFeign {

	@PostMapping("/salvarbalizaDataMiner")
	public Balizas salvar(@RequestBody Balizas balizas);
	
	@PostMapping("/asignarBalizaUnidadDataMiner")
	String asignar(@RequestBody Balizas balizas);
	
	@PostMapping("/desasignarBalizaUnidadDataMiner")
	String desasignar(@RequestBody Balizas balizas);
	
	@PostMapping("/borrarBalizaDataMiner")
	String borrar(@RequestBody Balizas balizas);

	@PostMapping("/estadoBalizaDataminer")
	String cambiarEstado(@RequestBody Balizas balizas);

	@PostMapping("/enviarIdBalizaBD")
	String enviarIdBalizaBD(@RequestBody Balizas balizas);
}
