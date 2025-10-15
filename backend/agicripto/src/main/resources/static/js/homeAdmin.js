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
    const adminId = localStorage.getItem('usuarioId');
    if (!adminId) {
        console.error("ID do administrador não encontrado. Sessão expirada ou usuário não logado.");
        alert("Erro de autenticação. Faça login novamente.");
        return null;
    }
    console.log("ID do admin logado:", adminId);
        return parseInt(adminId); // ou Number(id)
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
            localStorage.setItem("usuarioId", usuario.id);
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
    const icone = document.getElementById('icone').value.trim();
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
        id_responsavel: adminId
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
        console.error('');
        statusMessage.textContent = ``;
        statusMessage.style.color = '';
    }
}
// ===================== LISTAR CRIPTOMOEDAS =====================
async function listarCriptomoedas() {
    const tbody = document.querySelector("#tabelaCriptomoedas tbody");
    tbody.innerHTML = "<tr><td colspan='5'>Carregando...</td></tr>";

    try {
        const response = await fetch("http://localhost:8080/api/criptomoedas");
        if (!response.ok) throw new Error("Erro ao buscar criptomoedas.");

        const criptomoedas = await response.json();
        tbody.innerHTML = "";

        if (criptomoedas.length === 0) {
            tbody.innerHTML = "<tr><td colspan='5'>Nenhuma criptomoeda cadastrada.</td></tr>";
            return;
        }

        criptomoedas.forEach(cripto => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><img src="${cripto.icone}" alt="${cripto.nome}" width="40"></td>
                <td>${cripto.nome}</td>
                <td>${cripto.sigla}</td>
                <td><a href="${cripto.icone}" target="_blank">Ver imagem</a></td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-edit" onclick="editarCripto(${cripto.id})">Editar</button>
                        <button class="btn-delete" onclick="excluirCripto(${cripto.id})">Excluir</button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

    } catch (error) {
        console.error("Erro ao carregar criptomoedas:", error);
        tbody.innerHTML = "<tr><td colspan='5'>Erro ao carregar dados.</td></tr>";
    }
}

// ===================== EDITAR =====================
// ===================== ABRIR MODAL DE EDIÇÃO =====================
async function editarCripto(id) {
  const modal = document.getElementById("modalEditar");
  const inputId = document.getElementById("editarId");
  const inputNome = document.getElementById("editarNome");
  const inputSigla = document.getElementById("editarSigla");
  const inputIcone = document.getElementById("editarIcone");

  try {
    const response = await fetch(`http://localhost:8080/api/criptomoedas/${id}`);
    if (!response.ok) throw new Error("Erro ao carregar criptomoeda");
    const cripto = await response.json();

    inputId.value = id;
    inputNome.value = cripto.nome;
    inputSigla.value = cripto.sigla;
    inputIcone.value = cripto.icone;

    modal.style.display = "flex";
  } catch (error) {
    console.error("Erro ao carregar dados para edição:", error);
    alert("Erro ao carregar dados da criptomoeda.");
  }
}

// ===================== FECHAR MODAL =====================
document.getElementById("fecharModal").addEventListener("click", () => {
  document.getElementById("modalEditar").style.display = "none";
});

// ===================== SALVAR ALTERAÇÕES =====================
document.getElementById("formEditarCripto").addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("editarId").value;
  const criptoAtualizada = {
    nome: document.getElementById("editarNome").value,
    sigla: document.getElementById("editarSigla").value,
    icone: document.getElementById("editarIcone").value
  };

  try {
    const response = await fetch(`http://localhost:8080/api/criptomoedas/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(criptoAtualizada)
    });

    if (response.ok) {
      alert("Criptomoeda atualizada com sucesso!");
      document.getElementById("modalEditar").style.display = "none";
      listarCriptomoedas();
    } else {
      alert("Erro ao atualizar a criptomoeda.");
    }
  } catch (error) {
    console.error("Erro ao atualizar:", error);
  }
});


// ===================== EXCLUIR =====================
async function excluirCripto(id) {


    if (confirm("Tem certeza que deseja excluir esta criptomoeda?")) {
        try {
            const response = await fetch(`http://localhost:8080/api/criptomoedas/${id}`, {
                method: "DELETE"
            });

            if (response.ok) {
                alert("Criptomoeda excluída com sucesso!");
                listarCriptomoedas();
            } else {
                alert("Erro ao excluir a criptomoeda.");
            }
        } catch (error) {
            console.error("Erro na exclusão:", error);
        }
    }
}

// ===================== CARREGAR AUTOMATICAMENTE =====================
document.addEventListener("DOMContentLoaded", listarCriptomoedas);


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

