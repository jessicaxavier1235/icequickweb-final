package br.com.senac.icequick.service;

import br.com.senac.icequick.dto.PedidoDTO;
import br.com.senac.icequick.dto.PedidoItemDTO;
import br.com.senac.icequick.model.Pedido;
import br.com.senac.icequick.model.PedidoItem;
import br.com.senac.icequick.model.Produto;
import br.com.senac.icequick.model.Usuario;
import br.com.senac.icequick.repository.PedidoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private ProdutoService produtoService;

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    public void salvar(Pedido pedido) {
        repository.save(pedido);
    }

    public void salvarPedido(PedidoDTO dto, Usuario usuario) {

        if (dto == null) {
            throw new RuntimeException("Pedido inválido.");
        }

        if (usuario == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }

        if (dto.getFormaPagamento() == null || dto.getFormaPagamento().isBlank()) {
            throw new RuntimeException("Forma de pagamento obrigatória.");
        }

        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new RuntimeException("O pedido precisa ter pelo menos um item.");
        }

        Pedido pedido = new Pedido();

        pedido.setDataHora(LocalDateTime.now());
        pedido.setFormaPagamento(dto.getFormaPagamento());
        pedido.setStatus("FINALIZADO");
        pedido.setUsuario(usuario);

        double total = 0.0;

        for (PedidoItemDTO itemDTO : dto.getItens()) {

            if (itemDTO.getProdutoId() == null) {
                throw new RuntimeException("Produto inválido.");
            }

            if (itemDTO.getQuantidade() == null || itemDTO.getQuantidade() <= 0) {
                throw new RuntimeException("Quantidade inválida.");
            }

            Produto produto = produtoService.buscarPorId(itemDTO.getProdutoId());

            if (produto == null) {
                throw new RuntimeException("Produto não encontrado.");
            }

            PedidoItem item = new PedidoItem();

            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            pedido.getItens().add(item);

            total += produto.getPreco() * itemDTO.getQuantidade();
        }

        pedido.setTotal(total);

        repository.save(pedido);
    }
}
