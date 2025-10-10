// ===================== CONFIGURAÇÕES GERAIS =====================
const API_BASE_URL = "http://localhost:8080/api/usuarios";
const API_HISTORICOS_URL = "http://localhost:8080/api/historicos";
const API_URL = "http://localhost:8080/api/criptomoedas";

// ===================== FUNÇÃO: PEGAR TOKEN JWT =====================
function getToken() {
    return localStorage.getItem('jwtToken') || sessionStorage.getItem('jwtToken');
}

// ===================== FUNÇÃO: PEGAR ID DO ADMIN =====================
function getAdminId() {
    const adminId = localStorage.getItem('id_admin_logado');
    if (!adminId) {
        console.error("ID do administrador não encontrado. Sessão expirada ou usuário não logado.");
        alert("Erro de autenticação. Faça login novamente.");
        return null;
    }
    return adminId;
}

// ===================== FUNÇÃO: CARREGAR DADOS DO ADMIN =====================
async function carregarUsuarioAdmin() {
    const usernameEl = document.querySelector(".username");
    if (!usernameEl) return;

    try {
        const token = getToken();
        const response = await fetch(`${API_BASE_URL}/eu`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        console.log("Status ao buscar /eu:", response.status);

        if (response.ok) {
            const usuario = await response.json();
            console.log("Usuário logado:", usuario);

            // Salva ID e dados no localStorage/sessionStorage
            localStorage.setItem("id_admin_logado", usuario.id);
            sessionStorage.setItem("usuarioNome", usuario.nome);
            sessionStorage.setItem("usuarioTipo", usuario.tipo);

            usernameEl.textContent = usuario.nome;
        } else {
            console.error("Erro ao carregar dados do usuário:", response.status);
            usernameEl.textContent = "Administrador";
        }
    } catch (error) {
        console.error("Erro ao buscar dados do administrador:", error);
        usernameEl.textContent = "Administrador";
    } finally {
        usernameEl.classList.remove("loading");
    }
}

// ===================== FUNÇÃO: LOGOUT =====================
async function realizarLogout() {
    try {
        await fetch(`${API_BASE_URL}/logout`, {
            method: "POST",
            credentials: "same-origin"
        });
    } catch (error) {
        console.error("Erro ao realizar logout:", error);
    } finally {
        sessionStorage.clear();
        localStorage.clear();
        window.location.replace("/pages/auth/login.html");
    }
}

// ===================== FUNÇÃO: CADASTRAR CRIPTOMOEDA =====================
async function cadastrarCriptomoeda(event) {
    event.preventDefault(); // Evita recarregar a página

    const adminId = getAdminId();
    if (!adminId) return;

    const nome = document.getElementById('nome').value.trim();
    const sigla = document.getElementById('sigla').value.trim();
    const icone = document.getElementById('link_imagem').value.trim();
    const token = getToken();
    const statusMessage = document.getElementById('statusMessage');

    if (!nome || !sigla || !icone) {
        statusMessage.textContent = 'Preencha todos os campos.';
        statusMessage.style.color = 'red';
        return;
    }

    // JSON enviado para o backend
    const criptoData = {
        nome: nome,
        sigla: sigla,
        icone: icone,
        id_responsavel: adminId // camelCase correto se o DTO Java usa assim
    };

    try {
        statusMessage.textContent = 'Enviando dados...';
        statusMessage.style.color = 'blue';

        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(criptoData)
        });

        if (!response.ok) {
            throw new Error(`Erro do servidor: ${response.status} - ${response.statusText}`);
        }

        const data = await response.json();

        statusMessage.textContent = `Criptomoeda ${data.nome} cadastrada com sucesso! (ID: ${data.id})`;
        statusMessage.style.color = 'green';

        document.getElementById('cadastro-form').reset();

    } catch (error) {
        console.error('Falha ao cadastrar:', error);
        statusMessage.textContent = `Falha ao cadastrar: ${error.message}`;
        statusMessage.style.color = 'red';
    }
}

/*
async function carregarGrafico(idCripto) {
  try {
    console.log(`🔎 Buscando dados do gráfico para ID: ${idCripto}`);

    const response = await fetch(`${API_HISTORICOS_URL}/grafico/${idCripto}`);

    if (!response.ok) {
      throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
    }

    // Lê o corpo apenas uma vez
    const rawText = await response.text();
    let dados;
    try {
      dados = JSON.parse(rawText);
    } catch (parseError) {
      console.error("❌ Erro ao converter JSON:", rawText.slice(0, 500), "...");
      throw new Error(`Falha ao parsear JSON: ${parseError.message}`);
    }

    console.log("📊 Dados recebidos (brutos):", dados);

    // 🔹 Converte datas e valores antes de passar ao gráfico
    const labels = dados.map(d => {
      // Garante formato de data legível (ajuste fuso se quiser)
      const data = new Date(d.momento);
      if (isNaN(data)) {
        console.warn("⚠️ Data inválida detectada:", d.momento);
        return "Data Inválida";
      }
      return data.toLocaleString("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
      });
    });

    const cotacoes = dados.map(d => Number(d.cotacao_momento));

    console.log("✅ Labels:", labels);
    console.log("✅ Cotações:", cotacoes);

    // 🔹 Criação (ou atualização) do gráfico
    const ctx = document.getElementById("graficoCotacoes").getContext("2d");

    // Se já existir um gráfico, destrói antes de recriar (evita duplicar)
    if (window.graficoCotacoes instanceof Chart) {
      window.graficoCotacoes.destroy();
    }

    window.graficoCotacoes = new Chart(ctx, {
      type: "line",
      data: {
        labels: labels,
        datasets: [{
          label: "Cotação ao longo do tempo",
          data: cotacoes,
          borderWidth: 2,
          borderColor: "rgba(75, 192, 192, 1)",
          fill: false,
          tension: 0.3,
          pointRadius: 3,
          pointHoverRadius: 5
        }]
      },
      options: {
        scales: {
          y: {
            beginAtZero: false,
            title: { display: true, text: "Cotação (R$)" }
          },
          x: {
            title: { display: true, text: "Data/Hora da Cotação" },
            ticks: { maxRotation: 45, minRotation: 45 }
          }
        },
        plugins: {
          legend: { display: true, position: "top" },
          tooltip: { mode: "index", intersect: false }
        }
      }
    });

  } catch (error) {
    console.error("🚨 Erro ao carregar gráfico:", error);
    alert(`Erro ao carregar gráfico: ${error.message}`);
  }
}
*/

// ===================== INICIALIZAÇÃO DA PÁGINA =====================
window.addEventListener("DOMContentLoaded", () => {
    carregarUsuarioAdmin();

    /*
    carregarGrafico(17);
    carregarGrafico(19);
    */

    const logoutBtn = document.querySelector(".btn-logout");
    if (logoutBtn) logoutBtn.addEventListener("click", realizarLogout);

    const form = document.getElementById('cadastro-form');
    if (form) form.addEventListener('submit', cadastrarCriptomoeda);
});

