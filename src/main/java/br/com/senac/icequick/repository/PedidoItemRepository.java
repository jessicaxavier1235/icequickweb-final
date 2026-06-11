package br.com.senac.icequick.repository;

import br.com.senac.icequick.model.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoItemRepository
        extends JpaRepository<PedidoItem, Integer> {
}
