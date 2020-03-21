package com.importacao.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.importacao.domain.Convidado;
import com.importacao.dto.ConvidadoDTO;
import com.importacao.dto.ConvidadoNewDTO;
import com.importacao.repositories.ConvidadoRepository;
import com.importacao.services.exception.DataIntegrityException;
import com.importacao.services.exception.ObjectNotFoundException;

@Service
public class ConvidadoService {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

	@Autowired
	private ConvidadoRepository convidadoRepository;

	public Convidado find(Integer id) {

		Optional<Convidado> obj = convidadoRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Convidado.class.getName()));
	}

	public Convidado fromDTO(ConvidadoNewDTO convidadoNewDTO) {

		Convidado convidado = new Convidado(null, convidadoNewDTO.getCpf(), convidadoNewDTO.getNome(),
				convidadoNewDTO.getNascimento(), convidadoNewDTO.getProfissao(), convidadoNewDTO.getEmail());

		convidado.getTelefones().add(convidadoNewDTO.getTelefone1());

		if (convidadoNewDTO.getTelefone2() != null) {
			convidado.getTelefones().add(convidadoNewDTO.getTelefone2());
		}
		if (convidadoNewDTO.getTelefone3() != null) {
			convidado.getTelefones().add(convidadoNewDTO.getTelefone3());
		}
		return convidado;
	}

	public Convidado insert(Convidado convidado) {

		convidado.setId(null);
		Convidado aux = convidadoRepository.findByCpf(convidado.getCpf());

		if (!(aux == null)) {
			throw new RuntimeException("CPF já existe");
		}
		convidado = convidadoRepository.save(convidado);
		return convidado;
	}

	public void exportaConvidados() {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("CONVIDADO");

		// Definindo alguns padroes de layout
		sheet.setDefaultColumnWidth(15);
		sheet.setDefaultRowHeight((short) 400);

		// Carregando os convidados
		Iterable<Convidado> convidados = (Iterable<Convidado>) convidadoRepository.findAll();

		int rownum = 0;
		int cellnum = 0;
		Cell cell;
		Row row;

		// Configurando estilos de células (Cores, alinhamento, formatação, etc..)
		CellStyle headerStyle = workbook.createCellStyle();

		headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		CellStyle textStyle = workbook.createCellStyle();

		textStyle.setAlignment(CellStyle.ALIGN_CENTER);
		textStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		CellStyle dataStyle = workbook.createCellStyle();

		dataStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
		dataStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		// Configurando Header
		row = sheet.createRow(rownum++);
		cell = row.createCell(cellnum++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("ID");

		cell = row.createCell(cellnum++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("CPF");

		cell = row.createCell(cellnum++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("NOME");

		cell = row.createCell(cellnum++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("NASCIMENTO");

		cell = row.createCell(cellnum++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("EMAIL");

		cell = row.createCell(cellnum++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("PROFISSAO");

		cell = row.createCell(cellnum++);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("Telefone");

		// Adicionando os dados dos convidados na planilha
		for (Convidado convidado : convidados) {

			row = sheet.createRow(rownum++);
			cellnum = 0;

			cell = row.createCell(cellnum++);
			cell.setCellStyle(textStyle);
			cell.setCellValue(convidado.getId());

			cell = row.createCell(cellnum++);
			cell.setCellStyle(textStyle);
			cell.setCellValue(convidado.getCpf());

			cell = row.createCell(cellnum++);
			cell.setCellStyle(textStyle);
			cell.setCellValue(convidado.getNome());

			cell = row.createCell(cellnum++);
			cell.setCellStyle(dataStyle);
			cell.setCellValue(convidado.getNascimento());

			cell = row.createCell(cellnum++);
			cell.setCellStyle(textStyle);
			cell.setCellValue(convidado.getEmail());

			cell = row.createCell(cellnum++);
			cell.setCellStyle(textStyle);
			cell.setCellValue(convidado.getProfissao());

			cell = row.createCell(cellnum++);
			cell.setCellStyle(textStyle);
			cell.setCellValue(convidado.getTelefones().toString());
		}

		try {

			// Escrevendo o arquivo em disco
			Calendar calendar = Calendar.getInstance();
			String diretorio = "C://temp//CONVIDADOS_" + sdf.format(calendar.getTime()) + ".xls";

			FileOutputStream out = new FileOutputStream(new File(diretorio));
			workbook.write(out);
			out.close();
			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delete(Integer id) {

		find(id);

		try {
			convidadoRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir um Convidado porque há dados relacionados");
		}
	}

	public Convidado update(Convidado convidado) {

		Convidado newConvidado = find(convidado.getId());
		updateData(newConvidado, convidado);
		return convidadoRepository.save(newConvidado);
	}

	private void updateData(Convidado newConvidado, Convidado convidado) {

		newConvidado.setNome(convidado.getNome());
		newConvidado.setEmail(convidado.getEmail());
	}

	public Convidado fromDTO(ConvidadoDTO convidadoDTO) {

		return new Convidado(convidadoDTO.getId(), null, convidadoDTO.getNome(), null, null, convidadoDTO.getEmail());
	}

	public List<Convidado> findAll() {
		return convidadoRepository.findAll();
	}
}
