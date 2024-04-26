package com.renecode.gestioncursos.repository;

import com.renecode.gestioncursos.entity.Curso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ICursoRepository extends JpaRepository<Curso, Integer> {

    /**
     * Al nombrar este método estoy siguiendo la nomenclatura de Spring Data JPA para consultas derivada.
     * ya que el nombre del método se utiliza para generar consultas SQL subyacente, por eso el nombre
     * tiene que reflejar la operación que deseo realizar en la ddbb.
     * <ul>
     *     <li>findBy : Indica que el método debe realizar una operación de búsqueda en la db</li>
     *     <li>Titulo : Es el nombre del campo de mi entidad (Curso) por el cual filtra los resultados, Spring Data JPA buscará en este campo</li>
     *     <li>Containing : Es un operador que indica que la consulta debe buscar registros donde el campo especificado contenga el valor proporcionadod</li>
     *     <li>IgnoreCase : Es un modificador que indica que la búsqueda debe ignorar la distinción entre mayúsculas y minúsculas</li>
     * </ul>
     * */
    Page<Curso> findByTituloContainingIgnoreCase(String keyword, Pageable pageable);


}
