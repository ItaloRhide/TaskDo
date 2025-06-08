package br.ueg.desenvolvimento.web.task_do.controller;

import br.ueg.desenvolvimento.web.task_do.model.Usuario;
import br.ueg.desenvolvimento.web.task_do.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String registrationSubmit(@Valid @ModelAttribute("usuario") Usuario usuario, 
                                    BindingResult result, 
                                    Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            usuarioService.registrarNovoUsuario(usuario);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}
