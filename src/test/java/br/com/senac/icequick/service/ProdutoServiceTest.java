package br.com.senac.icequick.service;

import br.com.senac.icequick.model.Produto;
import br.com.senac.icequick.repository.ProdutoRepository;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoServiceTest {

    private ProdutoService produtoService;
    private ProdutoRepository produtoRepository;

    @BeforeEach
    public void prepararTeste() throws Exception {

        produtoRepository = mock(ProdutoRepository.class);
        produtoService = new ProdutoService();

        Field campoRepository =
                ProdutoService.class.getDeclaredField("repository");

        campoRepository.setAccessible(true);
        campoRepository.set(produtoService, produtoRepository);
    }

    @Test
    public void deveSalvarProdutoValido() {

        Produto produto = new Produto();
        produto.setNome("Açaí");
        produto.setPreco(15.00);
        produto.setAtivo(true);

        when(produtoRepository.findAll()).thenReturn(new ArrayList<>());
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto resultado = produtoService.salvar(produto);

        assertNotNull(resultado);
        assertEquals("Açaí", resultado.getNome());
        assertEquals(15.00, resultado.getPreco());

        verify(produtoRepository).save(produto);
    }

    @Test
    public void deveDarErroQuandoNomeVazio() {

        Produto produto = new Produto();
        produto.setNome("");
        produto.setPreco(10.00);

        RuntimeException erro = assertThrows(
                RuntimeException.class,
                () -> produtoService.salvar(produto)
        );

        assertEquals("Informe o nome do produto.", erro.getMessage());

        verify(produtoRepository, never()).save(any());
    }

    @Test
    public void deveDarErroQuandoPrecoZero() {

        Produto produto = new Produto();
        produto.setNome("Sorvete");
        produto.setPreco(0.0);

        RuntimeException erro = assertThrows(
                RuntimeException.class,
                () -> produtoService.salvar(produto)
        );

        assertEquals("O preço deve ser maior que zero.", erro.getMessage());

        verify(produtoRepository, never()).save(any());
    }
}