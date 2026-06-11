package br.com.senac.icequick.controller;

import br.com.senac.icequick.model.Produto;
import br.com.senac.icequick.model.Usuario;
import br.com.senac.icequick.service.ProdutoService;
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
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @GetMapping("/produtos")
    public String listarProdutos(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false, defaultValue = "false") Boolean mostrarInativos,
            Model model,
            HttpSession session) {

        if (semPermissaoProdutos(session)) {
            return "redirect:/sem-acesso";
        }

        List<Produto> produtos = service.listarTodos();

        if (!mostrarInativos) {
            produtos = produtos.stream()
                    .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                    .toList();
        }

        if (busca != null && !busca.isBlank()) {

            produtos = produtos.stream()
                    .filter(p
                            -> p.getNome() != null
                    && p.getNome()
                            .toLowerCase()
                            .contains(busca.toLowerCase()))
                    .toList();
        }

        model.addAttribute("produtos", produtos);
        model.addAttribute("busca", busca);
        model.addAttribute("mostrarInativos", mostrarInativos);

        return "produtos";
    }

    @GetMapping("/produtos/novo")
    public String novoProduto(
            Model model,
            HttpSession session) {

        if (semPermissaoProdutos(session)) {
            return "redirect:/sem-acesso";
        }

        Produto produto = new Produto();
        produto.setAtivo(true);

        model.addAttribute("produto", produto);

        return "produto-form";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(
            @ModelAttribute Produto produto,
            Model model,
            HttpSession session) {

        if (semPermissaoProdutos(session)) {
            return "redirect:/sem-acesso";
        }

        try {
            service.salvar(produto);
            return "redirect:/produtos";

        } catch (RuntimeException e) {
            model.addAttribute("produto", produto);
            model.addAttribute("erro", e.getMessage());
            return "produto-form";
        }
    }

    @GetMapping("/produtos/editar/{id}")
    public String editarProduto(
            @PathVariable Integer id,
            Model model,
            HttpSession session) {

        if (semPermissaoProdutos(session)) {
            return "redirect:/sem-acesso";
        }

        Produto produto = service.buscarPorId(id);

        if (produto == null) {
            return "redirect:/produtos";
        }

        model.addAttribute("produto", produto);

        return "produto-form";
    }

    @GetMapping("/produtos/desativar/{id}")
    public String desativarProduto(
            @PathVariable Integer id,
            HttpSession session) {

        if (semPermissaoProdutos(session)) {
            return "redirect:/sem-acesso";
        }

        service.desativar(id);

        return "redirect:/produtos";
    }

    @GetMapping("/produtos/reativar/{id}")
    public String reativarProduto(
            @PathVariable Integer id,
            HttpSession session) {

        if (semPermissaoProdutos(session)) {
            return "redirect:/sem-acesso";
        }

        service.reativar(id);

        return "redirect:/produtos";
    }

    private boolean semPermissaoProdutos(HttpSession session) {

        Usuario usuario
                = (Usuario) session.getAttribute("usuarioLogado");

        return usuario == null
                || !("gerente".equals(usuario.getCargo())
                || "administrador".equals(usuario.getCargo()));
    }
}
