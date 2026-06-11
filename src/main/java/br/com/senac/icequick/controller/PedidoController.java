package br.com.senac.icequick.controller;

import br.com.senac.icequick.dto.PedidoDTO;
import br.com.senac.icequick.model.Usuario;
import br.com.senac.icequick.service.PedidoService;
import br.com.senac.icequick.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PedidoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/pedidos")
    public String pedidos(
            Model model,
            HttpSession session) {

        if (semPermissaoPedidos(session)) {
            return "redirect:/sem-acesso";
        }

        model.addAttribute(
                "produtos",
                produtoService.listarTodos()
                        .stream()
                        .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                        .toList()
        );

        return "pedidos";
    }

    @PostMapping("/pedidos/salvar")
    @ResponseBody
    public ResponseEntity<Void> salvarPedido(
            @RequestBody PedidoDTO dto,
            HttpSession session) {

        if (semPermissaoPedidos(session)) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario
                = (Usuario) session.getAttribute("usuarioLogado");

        try {
            pedidoService.salvarPedido(dto, usuario);
            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            System.out.println("Erro ao salvar pedido: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean semPermissaoPedidos(HttpSession session) {

        Usuario usuario
                = (Usuario) session.getAttribute("usuarioLogado");

        return usuario == null
                || !("atendente".equals(usuario.getCargo())
                || "gerente".equals(usuario.getCargo())
                || "administrador".equals(usuario.getCargo()));
    }
}
