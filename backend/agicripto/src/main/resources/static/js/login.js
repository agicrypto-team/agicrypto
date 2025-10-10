// --- Definições de Elementos ---
const loginForm = document.querySelector('form');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const recordar = document.getElementById('rememberMe');
const togglePassword = document.getElementById('togglePassword');

/* Inserir errorDisplay */
const cardBody = document.querySelector('.card-body');
const errorDisplay = document.createElement('div');
errorDisplay.className = 'alert alert-danger d-none'; // Escondido por padrão
cardBody.prepend(errorDisplay); // Insere o display de erro no topo do corpo do card

// --- URL da API (Ajuste a porta se necessário) ---
const API_BASE_URL = 'http://localhost:8080/api/usuarios';

// --- LÓGICA CORRIGIDA: Funcionalidade de Mostrar/Ocultar Senha ---
togglePassword.addEventListener('click', function (e) {
    // Alterna o tipo do input de senha entre 'password' e 'text'
    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordInput.setAttribute('type', type);

    // Alterna as classes do ícone para garantir que apenas um esteja ativo
    this.classList.toggle('fa-eye');
    this.classList.toggle('fa-eye-slash');
});


// --- LÓGICA DE LOGIN (Inalterada) ---
// Função assíncrona para lidar com o envio do formulário
loginForm.addEventListener('submit', async (event) => {
    // Previne o comportamento padrão do formulário de recarregar a página
    event.preventDefault();

    // Limpa mensagens de erro anteriores
    errorDisplay.classList.add('d-none');
    errorDisplay.textContent = '';

    const loginPayload = {
        email: emailInput.value.trim(),
        senha: passwordInput.value.trim(),
    };

    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin', // garante envio e recebimento do cookie JSESSIONID
            body: JSON.stringify(loginPayload),
        });

        console.log("Status da resposta:", response.status);

        if (response.ok) {
            const usuario = await response.json(); // cookie JSESSIONID
            console.log("Data recebida:", usuario);

            sessionStorage.setItem("usuarioNome", usuario.nome);
            sessionStorage.setItem("usuarioTipo", usuario.tipo);

            if (usuario.tipo === 'Admin') {
                window.location.replace("/pages/admin/homeAdmin.html");
            } else if (usuario.tipo === 'Cliente') {
                window.location.replace("/pages/cliente/homeCliente.html");
            } else {
                alert('Tipo de usuário desconhecido!');
            }

        } else if (response.status === 401) { // 401 UNAUTHORIZED (AutenticacaoException)
            const errorText = await response.text();
            errorDisplay.textContent =
                errorText || 'Email ou senha inválidos. Tente novamente.';
            errorDisplay.classList.remove('d-none');
        } else {
            errorDisplay.textContent = 'Erro no servidor. Tente novamente.';
            errorDisplay.classList.remove('d-none');
        }

    } catch (err) {
        errorDisplay.textContent = 'Não foi possível conectar ao servidor.';
        errorDisplay.classList.remove('d-none');
        console.error(err);
    }
});