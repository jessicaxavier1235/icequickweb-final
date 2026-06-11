package br.com.senac.icequick.repository;

import br.com.senac.icequick.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository
        extends JpaRepository<Pedido, Integer> {
}
