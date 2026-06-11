let callbackConfirmacao = null;

function mostrarMensagem(msg, tipo) {

    $(".alerta").remove();

    let alerta = $(`
        <div class="alerta ${tipo || ""}">
            ${msg}
        </div>
    `);

    $("body").append(alerta);

    setTimeout(function () {
        alerta.fadeOut(300, function () {
            $(this).remove();
        });
    }, 2200);
}

function mostrarAlerta(msg) {
    mostrarMensagem(msg, "");
}

function mostrarSucesso(msg) {
    mostrarMensagem(msg, "sucesso");
}

function abrirConfirmacao(msg, callback) {

    if (!$("#modalConfirmacao").length) {
        mostrarAlerta(msg);
        return;
    }

    $("#modalTexto").text(msg);
    callbackConfirmacao = callback;
    $("#modalConfirmacao").css("display", "flex");
}

$(document).on("click", "#btnSim", function () {

    $("#modalConfirmacao").hide();

    if (typeof callbackConfirmacao === "function") {
        callbackConfirmacao(true);
    }

    callbackConfirmacao = null;
});

$(document).on("click", "#btnNao", function () {

    $("#modalConfirmacao").hide();

    if (typeof callbackConfirmacao === "function") {
        callbackConfirmacao(false);
    }

    callbackConfirmacao = null;
});

function confirmarSaida(destino) {

    abrirConfirmacao("Deseja sair sem salvar?", function (resposta) {

        if (resposta) {
            window.location.href = destino;
        }

    });
}