package com.importacao.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.importacao.domain.Convidado;
import com.importacao.dto.ConvidadoDTO;
import com.importacao.dto.ConvidadoNewDTO;
import com.importacao.services.ConvidadoService;

@RestController
@RequestMapping(value = "/convidados")
public class ConvidadoResource {

	@Autowired
	private ConvidadoService convidadoService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ConvidadoDTO>> find() {

		List<Convidado> list = convidadoService.findAll();
		List<ConvidadoDTO> listDto = list.stream().map(convidadoDTO -> new ConvidadoDTO(convidadoDTO))
				.collect(Collectors.toList());
		return ResponseEntity.ok(listDto);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Convidado> find(@PathVariable Integer id) {

		Convidado obj = convidadoService.find(id);
		return ResponseEntity.ok(obj);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody ConvidadoNewDTO convidadoNewDTO) {

		Convidado convidado = convidadoService.fromDTO(convidadoNewDTO);
		convidado = convidadoService.insert(convidado);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(convidado.getId())
				.toUri();
		return ResponseEntity.created(uri).build();
	}

	@RequestMapping(value = "/exportarConvidados", method = RequestMethod.POST)
	public void exportarConvidados() {

		convidadoService.exportaConvidados();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id) {

		convidadoService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@PathVariable Integer id, @Valid @RequestBody ConvidadoDTO convidadoDTO) {

		Convidado convidado = convidadoService.fromDTO(convidadoDTO);
		convidado.setId(id);
		convidado = convidadoService.update(convidado);
		return ResponseEntity.noContent().build();
	}
}
