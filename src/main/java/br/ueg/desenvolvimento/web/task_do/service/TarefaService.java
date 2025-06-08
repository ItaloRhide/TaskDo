package br.ueg.desenvolvimento.web.task_do.service;

import br.ueg.desenvolvimento.web.task_do.model.Tarefa;
import br.ueg.desenvolvimento.web.task_do.model.Usuario;
import br.ueg.desenvolvimento.web.task_do.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> listarTarefasPorUsuario(Usuario usuario) {
        return tarefaRepository.findByUsuario(usuario);
    }

    public List<Tarefa> listarTarefasPorUsuarioEStatus(Usuario usuario, Tarefa.StatusTarefa status) {
        return tarefaRepository.findByUsuarioAndStatus(usuario, status);
    }

    public Tarefa buscarPorId(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
    }

    public Tarefa salvar(Tarefa tarefa) {
        return tarefaRepository.save(tarefa);
    }

    public void excluir(Long id) {
        tarefaRepository.deleteById(id);
    }

    public Tarefa atualizarStatus(Long id, Tarefa.StatusTarefa novoStatus) {
        Tarefa tarefa = buscarPorId(id);
        tarefa.setStatus(novoStatus);
        return tarefaRepository.save(tarefa);
    }
}
