$(document).on("click", "#toggleSenha", function () {

    let input = $("#senha");
    let tipo = input.attr("type");

    if (tipo === "password") {
        input.attr("type", "text");
        $(this).text("Ocultar");
    } else {
        input.attr("type", "password");
        $(this).text("Mostrar");
    }
});

$(document).on("submit", "#formUsuario", function (e) {

    let nome = ($("#nome").val() || "").trim();
    let sobrenome = ($("#sobrenome").val() || "").trim();
    let login = ($("#login").val() || "").trim();
    let senha = $("#senha").val() || "";
    let cargo = $("#cargo").val();

    $(".erro").text("");
    $(".form-control, .form-select").removeClass("is-invalid");

    let temErro = false;

    if (!nome) {
        $("#erroNome").text("Informe o nome");
        $("#nome").addClass("is-invalid");
        temErro = true;
    } else if (!/^[A-Za-zÀ-ÿ\s]+$/.test(nome)) {
        $("#erroNome").text("Somente letras");
        $("#nome").addClass("is-invalid");
        temErro = true;
    }

    if (!sobrenome) {
        $("#erroSobrenome").text("Informe o sobrenome");
        $("#sobrenome").addClass("is-invalid");
        temErro = true;
    } else if (!/^[A-Za-zÀ-ÿ\s]+$/.test(sobrenome)) {
        $("#erroSobrenome").text("Somente letras");
        $("#sobrenome").addClass("is-invalid");
        temErro = true;
    }

    if (!login) {
        $("#erroLogin").text("Informe o login");
        $("#login").addClass("is-invalid");
        temErro = true;
    } else if (login.length < 4) {
        $("#erroLogin").text("Mínimo 4 caracteres");
        $("#login").addClass("is-invalid");
        temErro = true;
    } else if (login.includes(" ")) {
        $("#erroLogin").text("Sem espaços");
        $("#login").addClass("is-invalid");
        temErro = true;
    }

    if (!senha) {
        $("#erroSenha").text("Informe a senha");
        $("#senha").addClass("is-invalid");
        temErro = true;
    } else if (senha.length < 8) {
        $("#erroSenha").text("Mínimo 8 caracteres");
        $("#senha").addClass("is-invalid");
        temErro = true;
    } else if (!/\d/.test(senha)) {
        $("#erroSenha").text("Deve conter um número");
        $("#senha").addClass("is-invalid");
        temErro = true;
    }

    if (!cargo) {
        $("#erroCargo").text("Selecione o cargo");
        $("#cargo").addClass("is-invalid");
        temErro = true;
    }

    if (temErro) {
        e.preventDefault();
        mostrarAlerta("Verifique os campos do formulário.");
    }
});