package br.com.senac.icequick.service;

import br.com.senac.icequick.model.Usuario;
import br.com.senac.icequick.repository.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Usuario salvar(Usuario usuario) {

        validarUsuario(usuario);

        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }

        return repository.save(usuario);
    }

    public void desativar(Integer id) {

        Usuario usuario = buscarPorId(id);

        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        usuario.setAtivo(false);
        repository.save(usuario);
    }

    public void reativar(Integer id) {

        Usuario usuario = buscarPorId(id);

        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        usuario.setAtivo(true);
        repository.save(usuario);
    }

    public Usuario autenticar(String login, String senha) {

        return repository.findByLoginAndSenhaAndAtivoTrue(
                login,
                senha
        );
    }

    private void validarUsuario(Usuario usuario) {

        if (usuario == null) {
            throw new RuntimeException("Usuário inválido.");
        }

        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new RuntimeException("Informe o nome.");
        }

        if (!usuario.getNome().matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            throw new RuntimeException("O nome deve conter somente letras.");
        }

        if (usuario.getSobrenome() == null || usuario.getSobrenome().isBlank()) {
            throw new RuntimeException("Informe o sobrenome.");
        }

        if (!usuario.getSobrenome().matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            throw new RuntimeException("O sobrenome deve conter somente letras.");
        }

        if (usuario.getLogin() == null || usuario.getLogin().isBlank()) {
            throw new RuntimeException("Informe o login.");
        }

        if (usuario.getLogin().length() < 4) {
            throw new RuntimeException("O login deve ter no mínimo 4 caracteres.");
        }

        if (usuario.getLogin().contains(" ")) {
            throw new RuntimeException("O login não pode conter espaços.");
        }

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new RuntimeException("Informe a senha.");
        }

        if (usuario.getSenha().length() < 8) {
            throw new RuntimeException("A senha deve ter no mínimo 8 caracteres.");
        }

        if (!usuario.getSenha().matches(".*\\d.*")) {
            throw new RuntimeException("A senha deve conter pelo menos um número.");
        }

        if (usuario.getCargo() == null || usuario.getCargo().isBlank()) {
            throw new RuntimeException("Selecione o cargo.");
        }

        boolean loginDuplicado = repository.findAllByLoginIgnoreCase(usuario.getLogin())
                .stream()
                .anyMatch(u
                        -> usuario.getId() == null
                || !u.getId().equals(usuario.getId())
                );

        if (loginDuplicado) {
            throw new RuntimeException("Login já cadastrado.");
        }
    }
}
