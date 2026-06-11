$(document).on("submit", "#formProduto", function (e) {

    let nome = ($("#nomeProduto").val() || "").trim();
    let precoTexto = $("#precoProduto").val();
    let preco = parseFloat(precoTexto);

    $("#nomeProduto, #precoProduto").removeClass("input-erro");

    if (!nome) {
        e.preventDefault();
        $("#nomeProduto").addClass("input-erro");
        mostrarAlerta("Informe o nome do produto!");
        return;
    }

    if (!precoTexto || isNaN(preco)) {
        e.preventDefault();
        $("#precoProduto").addClass("input-erro");
        mostrarAlerta("Informe um preço válido!");
        return;
    }

    if (preco <= 0) {
        e.preventDefault();
        $("#precoProduto").addClass("input-erro");
        mostrarAlerta("O preço deve ser maior que zero!");
        return;
    }
});