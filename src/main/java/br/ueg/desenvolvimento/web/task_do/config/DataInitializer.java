package br.ueg.desenvolvimento.web.task_do.config;

import br.ueg.desenvolvimento.web.task_do.model.Tarefa;
import br.ueg.desenvolvimento.web.task_do.model.Usuario;
import br.ueg.desenvolvimento.web.task_do.repository.TarefaRepository;
import br.ueg.desenvolvimento.web.task_do.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, TarefaRepository tarefaRepository) {
        return args -> {
            // Criar usuário de teste se não existir
            if (!usuarioRepository.existsByUsername("usuario")) {
                Usuario usuario = new Usuario();
                usuario.setNome("Usuário Teste");
                usuario.setUsername("usuario");
                usuario.setPassword(passwordEncoder.encode("senha"));
                usuario.setEmail("usuario@teste.com");
                usuarioRepository.save(usuario);

                // Criar algumas tarefas de exemplo para o usuário
                Tarefa tarefa1 = new Tarefa();
                tarefa1.setTitulo("Implementar autenticação");
                tarefa1.setDescricao("Implementar sistema de login e registro usando Spring Security");
                tarefa1.setDataVencimento(LocalDate.now().plusDays(3));
                tarefa1.setStatus(Tarefa.StatusTarefa.A_FAZER);
                tarefa1.setUsuario(usuario);
                tarefaRepository.save(tarefa1);

                Tarefa tarefa2 = new Tarefa();
                tarefa2.setTitulo("Desenvolver interface Kanban");
                tarefa2.setDescricao("Criar interface de arrastar e soltar para gerenciamento de tarefas");
                tarefa2.setDataVencimento(LocalDate.now().plusDays(5));
                tarefa2.setStatus(Tarefa.StatusTarefa.EM_PROGRESSO);
                tarefa2.setUsuario(usuario);
                tarefaRepository.save(tarefa2);

                Tarefa tarefa3 = new Tarefa();
                tarefa3.setTitulo("Configurar banco de dados");
                tarefa3.setDescricao("Configurar banco H2 e console web para depuração");
                tarefa3.setDataVencimento(LocalDate.now().plusDays(1));
                tarefa3.setStatus(Tarefa.StatusTarefa.CONCLUIDO);
                tarefa3.setUsuario(usuario);
                tarefaRepository.save(tarefa3);
            }
        };
    }
}
