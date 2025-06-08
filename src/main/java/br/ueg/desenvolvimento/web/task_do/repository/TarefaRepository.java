package br.ueg.desenvolvimento.web.task_do.repository;

import br.ueg.desenvolvimento.web.task_do.model.Tarefa;
import br.ueg.desenvolvimento.web.task_do.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByUsuario(Usuario usuario);
    List<Tarefa> findByUsuarioAndStatus(Usuario usuario, Tarefa.StatusTarefa status);
}
