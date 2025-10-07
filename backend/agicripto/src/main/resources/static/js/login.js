// --- Definições de Elementos ---
const loginForm = document.querySelector('form');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const recordar = document.getElementById('recordar');

/* Inserir errorDisplay */
const cardBody = document.querySelector('.card-body');
const errorDisplay = document.createElement('div');
errorDisplay.className = 'alert alert-danger d-none'; // Escondido por padrão
cardBody.prepend(errorDisplay); // Insere o display de erro no topo do corpo do card

// --- URL da API (Ajuste a porta se necessário) ---
const API_BASE_URL = 'http://localhost:8080/api/usuarios';


// Função assíncrona para lidar com o envio do formulário
loginForm.addEventListener('submit', async (event) => {

    // Previne o comportamento padrão do formulário de recarregar a página
    event.preventDefault();

    // Limpa mensagens de erro anteriores
    errorDisplay.classList.add('d-none');
    errorDisplay.textContent = '';

  const loginPayload = {
    email: emailInput.value,
    senha: passwordInput.value
  };

  try {

    const response = await fetch(`${API_BASE_URL}/login`, {

        method: 'POST',
        headers: { 'Content-Type': 'application/json' }, // O browser adiciona automaticamente o cookie de sessão (JSESSIONID) aqui
        credentials: 'same-origin',
        body: JSON.stringify(loginPayload)
        });

        // 2. Lida com a Resposta do Servidor
    if (response.ok) {
        // O backend criou a sessão e enviou cookie JSESSIONID; o navegador o armazenou.
        const usuario = await response.json(); // opcional: objeto do usuário retornado

        // ✅ Aqui entra a lógica de redirecionamento
        if (usuario.tipo === 'Admin') {
            window.location.replace("/pages/admin/homeAdmin.html");
        } else if (usuario.tipo === 'Cliente') {
            window.location.replace("/pages/cliente/homeCliente.html");
        } else {
            alert('Tipo de usuário desconhecido!');
        }

    } else if (response.status === 401) { // 401 UNAUTHORIZED (AutenticacaoException)

        const errorText = await response.text();
        errorDisplay.textContent = errorText || 'Email ou senha inválidos. Tente novamente.';
        errorDisplay.classList.remove('d-none');

    } else {
        errorDisplay.textContent = 'Erro no servidor. Tente novamente.';
        errorDisplay.classList.remove('d-none');
    }

    const data = await response.json();
    console.log("Data recebida:", data);

  } catch (err) {
    errorDisplay.textContent = 'Não foi possível conectar ao servidor.';
    errorDisplay.classList.remove('d-none');
    console.error(err);
  }
});