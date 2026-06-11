package br.com.senac.icequick.controller;

import br.com.senac.icequick.model.Usuario;
import br.com.senac.icequick.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/usuarios")
    public String listarUsuarios(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false, defaultValue = "false") Boolean mostrarInativos,
            Model model,
            HttpSession session) {

        if (semPermissaoUsuarios(session)) {
            return "redirect:/sem-acesso";
        }

        List<Usuario> usuarios = service.listarTodos();

        if (!mostrarInativos) {
            usuarios = usuarios.stream()
                    .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
                    .toList();
        }

        if (busca != null && !busca.isBlank()) {

            String texto = busca.toLowerCase();

            usuarios = usuarios.stream()
                    .filter(u
                            -> (u.getNome() != null && u.getNome().toLowerCase().contains(texto))
                    || (u.getLogin() != null && u.getLogin().toLowerCase().contains(texto)))
                    .toList();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("busca", busca);
        model.addAttribute("mostrarInativos", mostrarInativos);

        return "usuarios";
    }

    @GetMapping("/usuarios/novo")
    public String novoUsuario(
            Model model,
            HttpSession session) {

        if (semPermissaoUsuarios(session)) {
            return "redirect:/sem-acesso";
        }

        Usuario usuario = new Usuario();
        usuario.setAtivo(true);

        model.addAttribute("usuario", usuario);

        return "usuario-form";
    }

    @PostMapping("/usuarios/salvar")
    public String salvarUsuario(
            @ModelAttribute Usuario usuario,
            Model model,
            HttpSession session) {

        if (semPermissaoUsuarios(session)) {
            return "redirect:/sem-acesso";
        }

        try {
            service.salvar(usuario);
            return "redirect:/usuarios";

        } catch (RuntimeException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("erro", e.getMessage());
            return "usuario-form";
        }
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(
            @PathVariable Integer id,
            Model model,
            HttpSession session) {

        if (semPermissaoUsuarios(session)) {
            return "redirect:/sem-acesso";
        }

        Usuario usuario = service.buscarPorId(id);

        if (usuario == null) {
            return "redirect:/usuarios";
        }

        model.addAttribute("usuario", usuario);

        return "usuario-form";
    }

    @GetMapping("/usuarios/desativar/{id}")
    public String desativarUsuario(
            @PathVariable Integer id,
            HttpSession session) {

        if (semPermissaoUsuarios(session)) {
            return "redirect:/sem-acesso";
        }

        service.desativar(id);

        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/reativar/{id}")
    public String reativarUsuario(
            @PathVariable Integer id,
            HttpSession session) {

        if (semPermissaoUsuarios(session)) {
            return "redirect:/sem-acesso";
        }

        service.reativar(id);

        return "redirect:/usuarios";
    }

    private boolean semPermissaoUsuarios(HttpSession session) {

        Usuario usuario
                = (Usuario) session.getAttribute("usuarioLogado");

        return usuario == null
                || !"administrador".equals(usuario.getCargo());
    }
}
