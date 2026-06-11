package br.com.senac.icequick.service;

import br.com.senac.icequick.model.Produto;
import br.com.senac.icequick.repository.ProdutoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto salvar(Produto produto) {

        validarProduto(produto);

        if (produto.getAtivo() == null) {
            produto.setAtivo(true);
        }

        return repository.save(produto);
    }

    public Produto buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void desativar(Integer id) {

        Produto produto = buscarPorId(id);

        if (produto == null) {
            throw new RuntimeException("Produto não encontrado.");
        }

        produto.setAtivo(false);
        repository.save(produto);
    }

    public void reativar(Integer id) {

        Produto produto = buscarPorId(id);

        if (produto == null) {
            throw new RuntimeException("Produto não encontrado.");
        }

        produto.setAtivo(true);
        repository.save(produto);
    }

    private void validarProduto(Produto produto) {

        if (produto == null) {
            throw new RuntimeException("Produto inválido.");
        }

        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new RuntimeException("Informe o nome do produto.");
        }

        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            throw new RuntimeException("O preço deve ser maior que zero.");
        }

        boolean nomeDuplicado = repository.findAll()
                .stream()
                .anyMatch(p
                        -> p.getNome() != null
                && p.getNome().equalsIgnoreCase(produto.getNome())
                && !p.getId().equals(produto.getId())
                );

        if (nomeDuplicado) {
            throw new RuntimeException("Produto já cadastrado.");
        }
    }
}
