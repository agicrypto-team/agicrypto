// --- Definições de Elementos ---
const loginForm = document.querySelector('form');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');

// Vamos criar um elemento para exibir mensagens de erro logo abaixo do card
const cardBody = document.querySelector('.card-body');
const errorDisplay = document.createElement('div');
errorDisplay.className = 'alert alert-danger d-none'; // Escondido por padrão
cardBody.prepend(errorDisplay); // Insere o display de erro no topo do corpo do card

// --- URL da API (Ajuste a porta se necessário) ---
const API_BASE_URL = 'http://localhost:8080/usuarios';


// Função assíncrona para lidar com o envio do formulário
loginForm.addEventListener('submit', async (event) => {

    // Previne o comportamento padrão do formulário de recarregar a página
    event.preventDefault();

    // Limpa mensagens de erro anteriores
    errorDisplay.classList.add('d-none');
    errorDisplay.textContent = '';

    const email = emailInput.value;
    const senha = passwordInput.value;

    // 1. Constrói o DTO de Requisição de Login (LoginRequestDTO)
    const loginPayload = {

        email: email,
        senha: senha

    };

    try {

        const response = await fetch(`${API_BASE_URL}/login`, {

            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // O browser adiciona automaticamente o cookie de sessão (JSESSIONID) aqui
            },
            body: JSON.stringify(loginPayload)
        });

        // 2. Lida com a Resposta do Servidor
        if (response.ok) { // Status 200 OK

            // Login bem-sucedido: O cookie de sessão foi criado pelo backend.
            console.log('Login bem-sucedido! Redirecionando...');
            window.location.replace('/homeUsuario'); // Exemplo de página inicial

        } else if (response.status === 401) { // 401 UNAUTHORIZED (AutenticacaoException)

            const errorText = await response.text();
            errorDisplay.textContent = errorText || 'Email ou senha inválidos. Tente novamente.';
            errorDisplay.classList.remove('d-none');

        } else {

            // Lida com outros erros (500 Internal Server Error, etc.)
            errorDisplay.textContent = 'Ocorreu um erro no servidor. Tente novamente mais tarde.';
            errorDisplay.classList.remove('d-none');
            console.error('Erro HTTP:', response.status);

        }

    } catch (error) {

        // Lida com erros de rede (servidor offline, etc.)
        errorDisplay.textContent = 'Não foi possível conectar ao servidor. Verifique sua conexão.';
        errorDisplay.classList.remove('d-none');
        console.error('Erro de rede:', error);

    }
});