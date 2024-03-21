package com.renecode.gestioncursos.repository;

import com.renecode.gestioncursos.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICursoRepository extends JpaRepository<Curso, Integer> {


}
