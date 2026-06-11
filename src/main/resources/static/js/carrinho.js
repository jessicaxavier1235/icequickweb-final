window.carrinho = window.carrinho || [];

function adicionarCarrinho(produto) {

    let item = carrinho.find(function (i) {
        return i.id === produto.id;
    });

    if (item) {
        item.qtd++;
    } else {
        carrinho.push({
            id: produto.id,
            nome: produto.nome,
            preco: parseFloat(produto.preco),
            qtd: 1
        });
    }

    renderizarCarrinho();
}

function renderizarCarrinho() {

    let tbody = $("#itensCarrinho");

    if (!tbody.length) {
        return;
    }

    tbody.empty();

    let total = 0;

    if (carrinho.length === 0) {

        tbody.append(`
            <tr class="carrinho-vazio">
                <td colspan="3">Seu carrinho está vazio</td>
            </tr>
        `);

        $("#total").text("0.00");
        $("#btnFinalizar").prop("disabled", false);

        return;
    }

    $("#btnFinalizar").prop("disabled", false);

    carrinho.forEach(function (item, index) {

        let subtotal = item.qtd * item.preco;
        total += subtotal;

        let tr = $("<tr>");

        tr.append(`<td>${item.nome}</td>`);

        let inputQtd = $(`
            <input type="number"
                   min="0"
                   value="${item.qtd}"
                   style="width:60px;text-align:center;">
        `);

        inputQtd.on("change", function () {

            let campo = $(this);
            let novaQtd = parseInt(campo.val());

            if (isNaN(novaQtd)) {
                campo.val(item.qtd);
                return;
            }

            if (novaQtd <= 0) {

                campo.val(item.qtd);

                abrirConfirmacao("Deseja remover este produto do carrinho?", function (resposta) {

                    if (resposta) {
                        carrinho.splice(index, 1);
                        renderizarCarrinho();
                        mostrarSucesso("Produto removido!");
                    } else {
                        renderizarCarrinho();
                    }

                });

                return;
            }

            item.qtd = novaQtd;
            renderizarCarrinho();
        });

        tr.append($("<td>").append(inputQtd));
        tr.append(`<td>R$ ${subtotal.toFixed(2)}</td>`);

        tbody.append(tr);
    });

    $("#total").text(total.toFixed(2));
}

$(document).on("click", "#btnLimparCarrinho", function () {

    if (!carrinho || carrinho.length === 0) {
        mostrarAlerta("O carrinho já está vazio!");
        return;
    }

    abrirConfirmacao("Deseja limpar o carrinho?", function (resposta) {

        if (resposta) {
            carrinho = [];
            renderizarCarrinho();
            mostrarSucesso("Carrinho limpo!");
        }

    });
});

function gerarNumeroPedido() {

    let hoje = new Date().toLocaleDateString();

    let ultimoDia = localStorage.getItem("dataPedido");
    let contador = localStorage.getItem("contadorPedido");

    if (ultimoDia !== hoje) {
        contador = 1;
    } else {
        contador = parseInt(contador || 0) + 1;
    }

    localStorage.setItem("dataPedido", hoje);
    localStorage.setItem("contadorPedido", contador);

    return contador;
}

$(document).on("click", "#btnFinalizar", function () {
    finalizarPedido();
});

function finalizarPedido() {

    if (!carrinho || carrinho.length === 0) {
        mostrarAlerta("Adicione produtos ao carrinho antes de finalizar!");
        return;
    }

    let pagamento = $("#formaPagamento").val();

    if (!pagamento) {
        mostrarAlerta("Selecione a forma de pagamento!");
        return;
    }

    let total = calcularTotalCarrinho();
    let numeroPedido = gerarNumeroPedido();

    montarResumoPedido(numeroPedido, total);

    $("#modalResumo").css("display", "flex");
}

function calcularTotalCarrinho() {

    return carrinho.reduce(function (s, item) {
        return s + (item.qtd * item.preco);
    }, 0);
}

function montarResumoPedido(numeroPedido, total) {

    let html = `
        <div class="resumo-box">
            <div class="resumo-numero">Pedido #${numeroPedido}</div>
            <div style="font-size:12px;">${new Date().toLocaleString()}</div>
    `;

    carrinho.forEach(function (item) {
        html += `
            <div class="resumo-item">
                <span>${item.nome} x${item.qtd}</span>
                <span>R$ ${(item.qtd * item.preco).toFixed(2)}</span>
            </div>
        `;
    });

    html += `
        <div class="resumo-total">
            <span>Total</span>
            <span style="font-weight:bold; color:#f06292;">
                R$ ${total.toFixed(2)}
            </span>
        </div>
    </div>
    `;

    $("#resumoPedido")
            .html(html)
            .data("numero", numeroPedido);
}

$(document).on("click", "#btnCancelarPedido", function () {
    $("#modalResumo").hide();
});

$(document).on("click", "#btnConfirmarPedido", function () {

    if (!carrinho || carrinho.length === 0) {
        $("#modalResumo").hide();
        mostrarAlerta("O carrinho está vazio!");
        return;
    }

    let itensParaImpressao = carrinho.slice();
    let totalVisual = calcularTotalCarrinho();
    let pagamento = $("#formaPagamento").val();
    let numeroPedido = $("#resumoPedido").data("numero");

    let itensDTO = carrinho.map(function (item) {
        return {
            produtoId: item.id,
            quantidade: item.qtd
        };
    });

    let pedido = {
        formaPagamento: pagamento,
        itens: itensDTO
    };

    $.ajax({
        url: "/pedidos/salvar",
        method: "POST",
        data: JSON.stringify(pedido),
        contentType: "application/json",

        success: function () {

            $("#modalResumo").hide();

            imprimirResumoPedido(
                    itensParaImpressao,
                    totalVisual,
                    pagamento,
                    numeroPedido
                    );

            carrinho = [];
            renderizarCarrinho();
            $("#formaPagamento").val("");

            mostrarSucesso("Pedido salvo com sucesso!");
        },

        error: function (xhr) {
            console.log("Erro ao salvar pedido:", xhr);

            if (xhr.status === 401) {
                mostrarAlerta("Sessão expirada. Faça login novamente.");
            } else if (xhr.status === 400) {
                mostrarAlerta("Verifique os dados do pedido.");
            } else {
                mostrarAlerta("Erro ao salvar o pedido! Código: " + xhr.status);
            }
        }
    });

});

function imprimirResumoPedido(itens, total, pagamento, numeroPedido) {

    let area = document.getElementById("cupomPrint");

    let html = `
        <div style="width:260px; margin:auto; text-align:center;">
            <img src="/img/logo.png" style="width:70px;">
            <h2>ICE CREAM SHOP</h2>
            <p>Pedido #${numeroPedido}</p>
            <hr>
    `;

    itens.forEach(function (item) {
        html += `
            <div class="resumo-item">
                <span>${item.nome} x${item.qtd}</span>
                <span>R$ ${(item.qtd * item.preco).toFixed(2)}</span>
            </div>
        `;
    });

    html += `
        <div class="resumo-total">
            <span>Total</span>
            <span>R$ ${total.toFixed(2)}</span>
        </div>

        <p>Pagamento: ${pagamento}</p>
        <p>${new Date().toLocaleString()}</p>
    </div>
    `;

    area.innerHTML = html;
    window.print();
}