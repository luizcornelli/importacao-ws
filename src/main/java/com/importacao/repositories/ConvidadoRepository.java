package com.importacao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.importacao.domain.Convidado;

@Repository
public interface ConvidadoRepository extends JpaRepository<Convidado, Integer> {

	@Transactional(readOnly = true)
	Convidado findByEmail(String email);

	@Query("SELECT obj FROM Convidado obj WHERE obj.cpf = :cpf")
	Convidado findByCpf(@Param("cpf") String cpf);
}
