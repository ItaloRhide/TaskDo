package br.ueg.desenvolvimento.web.task_do.controller;

import br.ueg.desenvolvimento.web.task_do.model.Tarefa;
import br.ueg.desenvolvimento.web.task_do.model.Usuario;
import br.ueg.desenvolvimento.web.task_do.service.TarefaService;
import br.ueg.desenvolvimento.web.task_do.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsável pelo gerenciamento de tarefas no estilo Kanban.
 * Implementa operações CRUD e movimentação de tarefas entre colunas.
 * 
 * @author UEG Desenvolvimento Web
 */
@Controller
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Método auxiliar para obter o usuário autenticado no momento.
     * 
     * @return Objeto Usuario do usuário autenticado
     */
    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return (Usuario) usuarioService.loadUserByUsername(username);
    }

    /**
     * Exibe a visão Kanban com todas as tarefas do usuário organizadas por status.
     * 
     * @param model Modelo para adicionar atributos à view
     * @return Nome da view a ser renderizada
     */
    @GetMapping
    public String listarTarefas(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        
        // Buscar tarefas por status
        List<Tarefa> tarefasAFazer = tarefaService.listarTarefasPorUsuarioEStatus(usuario, Tarefa.StatusTarefa.A_FAZER);
        List<Tarefa> tarefasEmProgresso = tarefaService.listarTarefasPorUsuarioEStatus(usuario, Tarefa.StatusTarefa.EM_PROGRESSO);
        List<Tarefa> tarefasConcluidas = tarefaService.listarTarefasPorUsuarioEStatus(usuario, Tarefa.StatusTarefa.CONCLUIDO);
        
        model.addAttribute("tarefasAFazer", tarefasAFazer);
        model.addAttribute("tarefasEmProgresso", tarefasEmProgresso);
        model.addAttribute("tarefasConcluidas", tarefasConcluidas);
        model.addAttribute("usuario", usuario);
        
        return "tarefas";
    }

    /**
     * Exibe o formulário para criar uma nova tarefa.
     * 
     * @param model Modelo para adicionar atributos à view
     * @return Nome da view a ser renderizada
     */
    @GetMapping("/nova")
    public String novaTarefaForm(Model model) {
        model.addAttribute("tarefa", new Tarefa());
        return "tarefa-form";
    }

    /**
     * Processa o formulário de criação de nova tarefa.
     * 
     * @param titulo Título da tarefa
     * @param descricao Descrição da tarefa
     * @param dataVencimento Data de vencimento da tarefa
     * @param redirectAttributes Atributos para redirecionamento
     * @return Redirecionamento para a lista de tarefas
     */
    @PostMapping("/nova")
    public String salvarTarefa(@RequestParam String titulo,
                              @RequestParam String descricao,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimento,
                              RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = getUsuarioAutenticado();
            
            Tarefa tarefa = new Tarefa();
            tarefa.setTitulo(titulo);
            tarefa.setDescricao(descricao);
            tarefa.setDataVencimento(dataVencimento);
            tarefa.setStatus(Tarefa.StatusTarefa.A_FAZER);
            tarefa.setUsuario(usuario);
            
            tarefaService.salvar(tarefa);
            redirectAttributes.addFlashAttribute("mensagem", "Tarefa criada com sucesso!");
            
            return "redirect:/tarefas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar tarefa: " + e.getMessage());
            return "redirect:/tarefas";
        }
    }

    /**
     * Exibe o formulário para editar uma tarefa existente.
     * 
     * @param id ID da tarefa a ser editada
     * @param model Modelo para adicionar atributos à view
     * @param redirectAttributes Atributos para redirecionamento
     * @return Nome da view a ser renderizada ou redirecionamento
     */
    @GetMapping("/editar/{id}")
    public String editarTarefaForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = getUsuarioAutenticado();
            Tarefa tarefa = tarefaService.buscarPorId(id);
            
            // Verificar se a tarefa pertence ao usuário autenticado
            if (!tarefa.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para editar esta tarefa.");
                return "redirect:/tarefas";
            }
            
            model.addAttribute("tarefa", tarefa);
            model.addAttribute("statusOptions", Tarefa.StatusTarefa.values());
            return "tarefa-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao carregar tarefa: " + e.getMessage());
            return "redirect:/tarefas";
        }
    }

    /**
     * Processa o formulário de edição de tarefa.
     * 
     * @param id ID da tarefa a ser atualizada
     * @param titulo Novo título da tarefa
     * @param descricao Nova descrição da tarefa
     * @param dataVencimento Nova data de vencimento da tarefa
     * @param status Novo status da tarefa (opcional)
     * @param redirectAttributes Atributos para redirecionamento
     * @return Redirecionamento para a lista de tarefas
     */
    @PostMapping("/editar/{id}")
    public String atualizarTarefa(@PathVariable Long id,
                                 @RequestParam String titulo,
                                 @RequestParam String descricao,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimento,
                                 @RequestParam(required = false) Tarefa.StatusTarefa status,
                                 RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = getUsuarioAutenticado();
            Tarefa tarefa = tarefaService.buscarPorId(id);
            
            // Verificar se a tarefa pertence ao usuário autenticado
            if (!tarefa.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para editar esta tarefa.");
                return "redirect:/tarefas";
            }
            
            tarefa.setTitulo(titulo);
            tarefa.setDescricao(descricao);
            tarefa.setDataVencimento(dataVencimento);
            
            // Atualizar o status se fornecido
            if (status != null) {
                tarefa.setStatus(status);
            }
            
            tarefaService.salvar(tarefa);
            redirectAttributes.addFlashAttribute("mensagem", "Tarefa atualizada com sucesso!");
            
            return "redirect:/tarefas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar tarefa: " + e.getMessage());
            return "redirect:/tarefas";
        }
    }

    /**
     * Exclui uma tarefa existente.
     * 
     * @param id ID da tarefa a ser excluída
     * @param redirectAttributes Atributos para redirecionamento
     * @return Redirecionamento para a lista de tarefas
     */
    @GetMapping("/excluir/{id}")
    public String excluirTarefa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = getUsuarioAutenticado();
            Tarefa tarefa = tarefaService.buscarPorId(id);
            
            // Verificar se a tarefa pertence ao usuário autenticado
            if (!tarefa.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para excluir esta tarefa.");
                return "redirect:/tarefas";
            }
            
            tarefaService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagem", "Tarefa excluída com sucesso!");
            
            return "redirect:/tarefas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir tarefa: " + e.getMessage());
            return "redirect:/tarefas";
        }
    }

    /**
     * Endpoint AJAX para atualizar o status de uma tarefa (usado na interface Kanban).
     * 
     * @param id ID da tarefa a ser atualizada
     * @param status Novo status da tarefa
     * @return Mapa com resultado da operação
     */
    @PostMapping("/atualizar-status")
    @ResponseBody
    public Map<String, Object> atualizarStatus(@RequestParam Long id, @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Usuario usuario = getUsuarioAutenticado();
            Tarefa tarefa = tarefaService.buscarPorId(id);
            
            // Verificar se a tarefa pertence ao usuário autenticado
            if (!tarefa.getUsuario().getId().equals(usuario.getId())) {
                response.put("success", false);
                response.put("message", "Acesso negado");
                return response;
            }
            
            Tarefa.StatusTarefa novoStatus = Tarefa.StatusTarefa.valueOf(status);
            tarefaService.atualizarStatus(id, novoStatus);
            
            response.put("success", true);
            response.put("message", "Status atualizado com sucesso");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Endpoint direto para atualizar o status de uma tarefa (alternativa não-AJAX).
     * 
     * @param id ID da tarefa a ser atualizada
     * @param status Novo status da tarefa
     * @param redirectAttributes Atributos para redirecionamento
     * @return Redirecionamento para a lista de tarefas
     */
    @GetMapping("/atualizar-status/{id}/{status}")
    public String atualizarStatusDireto(@PathVariable Long id, 
                                       @PathVariable String status,
                                       RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = getUsuarioAutenticado();
            Tarefa tarefa = tarefaService.buscarPorId(id);
            
            // Verificar se a tarefa pertence ao usuário autenticado
            if (!tarefa.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para atualizar esta tarefa.");
                return "redirect:/tarefas";
            }
            
            Tarefa.StatusTarefa novoStatus = Tarefa.StatusTarefa.valueOf(status);
            tarefaService.atualizarStatus(id, novoStatus);
            
            redirectAttributes.addFlashAttribute("mensagem", "Status da tarefa atualizado com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar status: " + e.getMessage());
        }
        
        return "redirect:/tarefas";
    }
}
