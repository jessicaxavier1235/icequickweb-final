package br.com.senac.icequick.dto;

import java.util.List;

public class PedidoDTO {

    private String formaPagamento;

    private List<PedidoItemDTO> itens;

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<PedidoItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItemDTO> itens) {
        this.itens = itens;
    }
}
