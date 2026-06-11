package br.com.senac.icequick.controller;

import br.com.senac.icequick.model.Pedido;
import br.com.senac.icequick.model.PedidoItem;
import br.com.senac.icequick.model.Usuario;
import br.com.senac.icequick.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RelatorioController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/relatorio")
    public String relatorio(HttpSession session) {

        if (semPermissaoRelatorio(session)) {
            return "redirect:/sem-acesso";
        }

        return "relatorio";
    }

    @GetMapping("/relatorio/pedidos")
    @ResponseBody
    public List<Map<String, Object>> listarPedidosRelatorio(HttpSession session) {

        if (semPermissaoRelatorio(session)) {
            return new ArrayList<>();
        }

        List<Pedido> pedidos = pedidoService.listarTodos();
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (Pedido pedido : pedidos) {

            Map<String, Object> pedidoMap = new HashMap<>();

            pedidoMap.put("id", pedido.getId());
            pedidoMap.put("dataHora", pedido.getDataHora());
            pedidoMap.put("total", pedido.getTotal());
            pedidoMap.put("status", pedido.getStatus());
            pedidoMap.put("formaPagamento", pedido.getFormaPagamento());

            Usuario usuario = pedido.getUsuario();

            Map<String, Object> usuarioMap = new HashMap<>();

            if (usuario != null) {
                usuarioMap.put("nome", usuario.getNome());
                usuarioMap.put("cargo", usuario.getCargo());
            } else {
                usuarioMap.put("nome", "Sistema");
                usuarioMap.put("cargo", "");
            }

            pedidoMap.put("usuario", usuarioMap);

            List<Map<String, Object>> itens = new ArrayList<>();

            if (pedido.getItens() != null) {

                for (PedidoItem item : pedido.getItens()) {

                    Map<String, Object> itemMap = new HashMap<>();

                    itemMap.put("id", item.getId());
                    itemMap.put("quantidade", item.getQuantidade());
                    itemMap.put("precoUnitario", item.getPrecoUnitario());

                    Map<String, Object> produtoMap = new HashMap<>();

                    if (item.getProduto() != null) {
                        produtoMap.put("id", item.getProduto().getId());
                        produtoMap.put("nome", item.getProduto().getNome());
                    } else {
                        produtoMap.put("id", null);
                        produtoMap.put("nome", "Produto removido");
                    }

                    itemMap.put("produto", produtoMap);

                    itens.add(itemMap);
                }
            }

            pedidoMap.put("itens", itens);

            resposta.add(pedidoMap);
        }

        return resposta;
    }

    private boolean semPermissaoRelatorio(HttpSession session) {

        Usuario usuario
                = (Usuario) session.getAttribute("usuarioLogado");

        return usuario == null
                || !("gerente".equals(usuario.getCargo())
                || "administrador".equals(usuario.getCargo()));
    }
}
