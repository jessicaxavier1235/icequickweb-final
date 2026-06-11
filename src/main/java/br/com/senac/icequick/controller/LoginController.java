package br.com.senac.icequick.controller;

import br.com.senac.icequick.model.Usuario;
import br.com.senac.icequick.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/")
    public String abrirLoginRaiz() {
        return "login";
    }

    @GetMapping("/login")
    public String abrirLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String logar(
            @RequestParam String login,
            @RequestParam String senha,
            HttpSession session,
            Model model) {

        Usuario usuario = service.autenticar(login, senha);

        if (usuario == null) {
            model.addAttribute("erro", "Login ou senha inválidos!");
            return "login";
        }

        session.setAttribute("usuarioLogado", usuario);

        return "redirect:/pedidos";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    @GetMapping("/sem-acesso")
    public String semAcesso() {
        return "sem-acesso";
    }
}
