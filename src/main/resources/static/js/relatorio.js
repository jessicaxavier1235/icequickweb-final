let paginaAtual = 1;
const itensPorPagina = 10;
let pedidosRelatorio = [];

$(document).ready(function () {
    carregarRelatorio();
});

$(document).on("change keyup", "#filtroPagamento, #filtroUsuario", function () {
    paginaAtual = 1;
    renderizarRelatorio();
});

$(document).on("click", "#btnAnterior", function () {
    if (paginaAtual > 1) {
        paginaAtual--;
        renderizarRelatorio();
    }
});

$(document).on("click", "#btnProximo", function () {
    paginaAtual++;
    renderizarRelatorio();
});

$(document).on("click", ".btn-detalhes", function () {
    let id = $(this).data("id");

    let pedido = pedidosRelatorio.find(function (p) {
        return p.id === id;
    });

    if (!pedido) {
        mostrarAlerta("Pedido não encontrado.");
        return;
    }

    abrirDetalhesPedido(pedido);
});

$(document).on("click", "#btnFecharDetalhes", function () {
    $("#modalDetalhes").hide();
});

$(document).on("click", "#btnImprimirRelatorio", function () {
    imprimirRelatorio();
});

function carregarRelatorio() {

    $.ajax({
        url: "/relatorio/pedidos",
        method: "GET",

        success: function (pedidos) {
            pedidosRelatorio = pedidos || [];
            paginaAtual = 1;
            renderizarRelatorio();
        },

        error: function (xhr) {
            console.log("Erro ao carregar relatório:", xhr);
            mostrarAlerta("Erro ao carregar relatório.");
        }
    });
}

function renderizarRelatorio() {

    let hoje = new Date().toLocaleDateString();

    let filtroPagamento = $("#filtroPagamento").val();
    let filtroUsuario = ($("#filtroUsuario").val() || "").toLowerCase();

    let pedidosFiltrados = pedidosRelatorio.filter(function (pedido) {

        let dataPedido = formatarDataSomente(pedido.dataHora);

        if (dataPedido !== hoje) {
            return false;
        }

        let pagamento = pedido.formaPagamento || "";

        if (filtroPagamento && pagamento !== filtroPagamento) {
            return false;
        }

        if (filtroUsuario) {
            let usuarioTexto = obterTextoUsuario(pedido).toLowerCase();

            if (!usuarioTexto.includes(filtroUsuario)) {
                return false;
            }
        }

        return true;
    });

    let totalPaginas = Math.ceil(pedidosFiltrados.length / itensPorPagina) || 1;

    if (paginaAtual > totalPaginas) {
        paginaAtual = totalPaginas;
    }

    if (paginaAtual < 1) {
        paginaAtual = 1;
    }

    let inicio = (paginaAtual - 1) * itensPorPagina;
    let pedidosPagina = pedidosFiltrados.slice(inicio, inicio + itensPorPagina);

    atualizarCardsResumo(hoje, pedidosFiltrados);
    atualizarTabela(pedidosPagina);
    atualizarPaginacao(totalPaginas);
}

function atualizarCardsResumo(hoje, pedidos) {

    $("#dataRelatorio").text(hoje);
    $("#totalPedidos").text(pedidos.length);

    let totalVendas = pedidos.reduce(function (soma, pedido) {
        return soma + (pedido.total || 0);
    }, 0);

    $("#totalVendas").text(totalVendas.toFixed(2));
}

function atualizarTabela(pedidos) {

    let tbody = $(".tabela-relatorio tbody");
    tbody.empty();

    if (!pedidos || pedidos.length === 0) {
        tbody.append(`
            <tr>
                <td colspan="6" class="text-center text-muted py-4">
                    Nenhuma venda registrada hoje
                </td>
            </tr>
        `);
        return;
    }

    pedidos.forEach(function (pedido) {

        tbody.append(`
            <tr>
                <td>${pedido.id}</td>
                <td>${formatarDataHora(pedido.dataHora)}</td>
                <td>${pedido.formaPagamento || "-"}</td>
                <td class="text-end">R$ ${(pedido.total || 0).toFixed(2)}</td>
                <td>${obterTextoUsuario(pedido)}</td>
                <td class="text-center">
                    <button class="btn-detalhes" data-id="${pedido.id}">
                        🔍 Ver
                    </button>
                </td>
            </tr>
        `);
    });
}

function atualizarPaginacao(totalPaginas) {

    $("#paginaAtual").text(paginaAtual);

    $("#btnAnterior").prop("disabled", paginaAtual <= 1);
    $("#btnProximo").prop("disabled", paginaAtual >= totalPaginas);
}

function abrirDetalhesPedido(pedido) {

    let html = `
        <p><strong>Pedido #${pedido.id}</strong></p>
        <p>${formatarDataHora(pedido.dataHora)}</p>
        <p><strong>Pagamento:</strong> ${pedido.formaPagamento || "-"}</p>
        <p><strong>Usuário:</strong> ${obterTextoUsuario(pedido)}</p>
        <hr>
    `;

    if (!pedido.itens || pedido.itens.length === 0) {

        html += `
            <p class="text-muted">Nenhum item encontrado neste pedido.</p>
        `;

    } else {

        pedido.itens.forEach(function (item) {

            let nomeProduto = "-";

            if (item.produto && item.produto.nome) {
                nomeProduto = item.produto.nome;
            }

            let quantidade = item.quantidade || 0;
            let preco = item.precoUnitario || 0;
            let subtotal = quantidade * preco;

            html += `
                <div class="resumo-item">
                    <span>${nomeProduto} x${quantidade}</span>
                    <span>R$ ${subtotal.toFixed(2)}</span>
                </div>
            `;
        });
    }

    html += `
        <hr>
        <div class="resumo-total">
            <span>Total</span>
            <span>R$ ${(pedido.total || 0).toFixed(2)}</span>
        </div>
    `;

    $("#detalhesPedido").html(html);
    $("#modalDetalhes").css("display", "flex");
}

function imprimirRelatorio() {

    let hoje = new Date().toLocaleDateString();

    let filtroPagamento = $("#filtroPagamento").val();
    let filtroUsuario = ($("#filtroUsuario").val() || "").toLowerCase();

    let pedidos = pedidosRelatorio.filter(function (pedido) {

        let dataPedido = formatarDataSomente(pedido.dataHora);

        if (dataPedido !== hoje) {
            return false;
        }

        if (filtroPagamento && pedido.formaPagamento !== filtroPagamento) {
            return false;
        }

        if (filtroUsuario) {
            let usuarioTexto = obterTextoUsuario(pedido).toLowerCase();

            if (!usuarioTexto.includes(filtroUsuario)) {
                return false;
            }
        }

        return true;
    });

    let totalPedidos = pedidos.length;

    let totalVendas = pedidos.reduce(function (soma, pedido) {
        return soma + (pedido.total || 0);
    }, 0);

    let html = `
        <html>
        <head>
            <title>Relatório</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    padding: 20px;
                }

                h2 {
                    text-align: center;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 20px;
                }

                th, td {
                    border: 1px solid #ccc;
                    padding: 6px;
                    font-size: 12px;
                }

                th {
                    background: #f2f2f2;
                }

                .total {
                    text-align: right;
                }
            </style>
        </head>
        <body>

            <h2>Relatório do dia</h2>

            <p><strong>Data:</strong> ${hoje}</p>
            <p><strong>Total de pedidos:</strong> ${totalPedidos}</p>
            <p><strong>Total vendido:</strong> R$ ${totalVendas.toFixed(2)}</p>

            <table>
                <thead>
                    <tr>
                        <th>Número</th>
                        <th>Data/Hora</th>
                        <th>Pagamento</th>
                        <th class="total">Total</th>
                        <th>Usuário</th>
                    </tr>
                </thead>
                <tbody>
    `;

    if (pedidos.length === 0) {

        html += `
            <tr>
                <td colspan="5" style="text-align:center;">
                    Nenhuma venda registrada hoje
                </td>
            </tr>
        `;

    } else {

        pedidos.forEach(function (pedido) {
            html += `
                <tr>
                    <td>${pedido.id}</td>
                    <td>${formatarDataHora(pedido.dataHora)}</td>
                    <td>${pedido.formaPagamento || "-"}</td>
                    <td class="total">R$ ${(pedido.total || 0).toFixed(2)}</td>
                    <td>${obterTextoUsuario(pedido)}</td>
                </tr>
            `;
        });
    }

    html += `
                </tbody>
            </table>
        </body>
        </html>
    `;

    let janela = window.open("", "", "width=900,height=700");

    janela.document.write(html);
    janela.document.close();

    janela.onload = function () {
        janela.print();
        janela.close();
    };
}

function formatarDataHora(dataHora) {

    if (!dataHora) {
        return "-";
    }

    let data = new Date(dataHora);

    if (isNaN(data.getTime())) {
        return dataHora;
    }

    return data.toLocaleString();
}

function formatarDataSomente(dataHora) {

    if (!dataHora) {
        return "";
    }

    let data = new Date(dataHora);

    if (isNaN(data.getTime())) {
        return "";
    }

    return data.toLocaleDateString();
}

function obterTextoUsuario(pedido) {

    if (!pedido || !pedido.usuario) {
        return "Sistema";
    }

    let nome = pedido.usuario.nome || "Sistema";
    let cargo = pedido.usuario.cargo || "";

    if (cargo) {
        return nome + " (" + cargo + ")";
    }

    return nome;
}

