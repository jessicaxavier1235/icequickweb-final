package br.com.senac.icequick.service;

import br.com.senac.icequick.dto.PedidoDTO;
import br.com.senac.icequick.dto.PedidoItemDTO;
import br.com.senac.icequick.model.Pedido;
import br.com.senac.icequick.model.Produto;
import br.com.senac.icequick.model.Usuario;
import br.com.senac.icequick.repository.PedidoRepository;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PedidoServiceTest {

    private PedidoService pedidoService;
    private PedidoRepository pedidoRepository;
    private ProdutoService produtoService;

    @BeforeEach
    public void prepararTeste() throws Exception {

        pedidoRepository = mock(PedidoRepository.class);
        produtoService = mock(ProdutoService.class);

        pedidoService = new PedidoService();

        Field campoRepository =
                PedidoService.class.getDeclaredField("repository");

        campoRepository.setAccessible(true);
        campoRepository.set(pedidoService, pedidoRepository);

        Field campoProdutoService =
                PedidoService.class.getDeclaredField("produtoService");

        campoProdutoService.setAccessible(true);
        campoProdutoService.set(pedidoService, produtoService);
    }

    @Test
    public void deveDarErroQuandoPedidoSemFormaPagamento() {

        Usuario usuario = new Usuario();
        usuario.setNome("Jessica");

        PedidoItemDTO itemDTO = new PedidoItemDTO();
        itemDTO.setProdutoId(1);
        itemDTO.setQuantidade(1);

        PedidoDTO dto = new PedidoDTO();
        dto.setFormaPagamento("");
        dto.setItens(List.of(itemDTO));

        RuntimeException erro = assertThrows(
                RuntimeException.class,
                () -> pedidoService.salvarPedido(dto, usuario)
        );

        assertEquals("Forma de pagamento obrigatória.", erro.getMessage());

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    public void deveDarErroQuandoQuantidadeInvalida() {

        Usuario usuario = new Usuario();
        usuario.setNome("Jessica");

        PedidoItemDTO itemDTO = new PedidoItemDTO();
        itemDTO.setProdutoId(1);
        itemDTO.setQuantidade(0);

        PedidoDTO dto = new PedidoDTO();
        dto.setFormaPagamento("PIX");
        dto.setItens(List.of(itemDTO));

        RuntimeException erro = assertThrows(
                RuntimeException.class,
                () -> pedidoService.salvarPedido(dto, usuario)
        );

        assertEquals("Quantidade inválida.", erro.getMessage());

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    public void deveCalcularTotalDoPedido() {

        Usuario usuario = new Usuario();
        usuario.setNome("Jessica");

        Produto produto = new Produto();
        produto.setId(1);
        produto.setNome("Açaí");
        produto.setPreco(10.00);

        PedidoItemDTO itemDTO = new PedidoItemDTO();
        itemDTO.setProdutoId(1);
        itemDTO.setQuantidade(3);

        PedidoDTO dto = new PedidoDTO();
        dto.setFormaPagamento("PIX");
        dto.setItens(List.of(itemDTO));

        when(produtoService.buscarPorId(1)).thenReturn(produto);

        pedidoService.salvarPedido(dto, usuario);

        verify(pedidoRepository).save(argThat((Pedido pedido) ->
                pedido.getTotal().equals(30.00)
                && pedido.getItens().size() == 1
                && pedido.getItens().get(0).getQuantidade().equals(3)
                && pedido.getItens().get(0).getPrecoUnitario().equals(10.00)
        ));
    }
}