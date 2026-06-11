package br.com.senac.icequick.repository;

import br.com.senac.icequick.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByLoginAndSenhaAndAtivoTrue(
            String login,
            String senha
    );

    List<Usuario> findAllByLoginIgnoreCase(String login);
}
