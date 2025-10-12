const API_BASE_URL = "http://localhost:8080/api/usuarios";
const API_TRANSACOES = "http://localhost:8080/api/transacoes";
const API_CRYPTOS = "http://localhost:8080/api/criptomoedas";
const API_ATIVOS = "http://localhost:8080/api/ativos-carteira";

// 🔹 Variáveis globais
let todasCriptos = [];
let ativosUsuario = [];

// 🔹 Carregar dados do usuário
async function carregarUsuarioCliente() {
  const usernameEl = document.querySelector(".username");
  if (!usernameEl) return;

  try {
    const response = await fetch(`${API_BASE_URL}/eu`, { method: "GET", credentials: "same-origin" });

    if (response.ok) {
      const usuario = await response.json();
      usernameEl.textContent = usuario.nome;
      usernameEl.classList.remove("loading");

      sessionStorage.setItem("usuarioId", usuario.id);
      sessionStorage.setItem("usuarioNome", usuario.nome);
      sessionStorage.setItem("usuarioTipo", usuario.tipo);

      await carregarListasCriptomoedas(usuario.id);
      inicializarDropdown();
    } else if (response.status === 401) {
      alert("Sessão expirada. Faça login novamente.");
      window.location.replace("/pages/login/login.html");
    } else {
      console.error("Erro ao carregar usuário:", response.status);
      usernameEl.textContent = "Cliente";
      usernameEl.classList.remove("loading");
    }
  } catch (error) {
    console.error("Erro ao buscar dados do usuário:", error);
    usernameEl.textContent = "Cliente";
    usernameEl.classList.remove("loading");
  }
}

// 🔹 Logout
async function realizarLogout() {
  try {
    await fetch(`${API_BASE_URL}/logout`, { method: "POST", credentials: "same-origin" });
  } catch (error) {
    console.error("Erro ao realizar logout:", error);
  } finally {
      sessionStorage.clear();
    window.location.replace("/pages/auth/login.html");
  }
}

// 🔹 Carrega listas de criptomoedas
async function carregarListasCriptomoedas(usuarioId) {
  try {
    const resCriptos = await fetch(API_CRYPTOS);
    if (resCriptos.ok) {
      const criptos = await resCriptos.json();
      todasCriptos = criptos.map(c => ({ id: c.id, nome: c.nome, sigla: c.sigla, icone: c.icone || "💰" }));
    }

    const resAtivos = await fetch(`${API_ATIVOS}/do-usuario`, { method: "GET", credentials: "same-origin" });
    if (resAtivos.ok) {
      const ativos = await resAtivos.json();
      ativosUsuario = ativos.map(a => ({ id: a.id, nome: a.nome, sigla: a.sigla, icone: a.icone || "💰" }));
    }
  } catch (err) {
    console.error("Erro ao carregar listas de criptomoedas:", err);
  }
}

// 🔹 Inicializa dropdown customizado
function inicializarDropdown() {
  const cryptoInput = document.querySelector("#crypto");
  const dropdown = document.querySelector(".crypto-dropdown");
  if (!cryptoInput || !dropdown) return;

  const tipoRadios = document.querySelectorAll("input[name='tipo']");

  function atualizarDropdown() {
    const tipoSelecionado = document.querySelector("input[name='tipo']:checked").value;
    const lista = tipoSelecionado === "compra" ? todasCriptos : ativosUsuario;

    dropdown.innerHTML = "";
    lista.forEach(c => {
      const item = document.createElement("div");
      item.className = "crypto-item";
      item.innerHTML = `<span class="crypto-name">${c.nome}</span>
                        <span class="crypto-sigla">(${c.sigla})</span>`;
      item.addEventListener("click", () => {
        cryptoInput.value = `${c.nome} (${c.sigla})`;
        dropdown.style.display = "none";
      });
      dropdown.appendChild(item);
    });
  }

  // Atualiza ao mudar o tipo
  tipoRadios.forEach(r => r.addEventListener("change", atualizarDropdown));
  atualizarDropdown();

  // Toggle dropdown ao focar
  cryptoInput.addEventListener("focus", () => { dropdown.style.display = "block"; });
  cryptoInput.addEventListener("blur", () => { setTimeout(() => dropdown.style.display = "none", 200); });

  // 🔹 Evento submit da transação
  const form = document.querySelector(".transaction-form");
  const valorInput = document.querySelector("#valor");
  const equivalenciaEl = document.querySelector("#equivalencia");

  form.addEventListener("submit", async e => {
    e.preventDefault();

    const tipo = document.querySelector("input[name='tipo']:checked").value;
    const usuarioId = sessionStorage.getItem("usuarioId");
    const valor = parseFloat(valorInput.value.replace(",", "."));
    const cryptoValue = cryptoInput.value;

    if (!cryptoValue || isNaN(valor)) {
      alert("Preencha corretamente a criptomoeda e o valor.");
      return;
    }

    const match = cryptoValue.match(/\(([^)]+)\)/);
    const siglaCripto = match ? match[1] : cryptoValue;

    let cripto = todasCriptos.find(c => c.sigla === siglaCripto || c.nome === cryptoValue);
    if (!cripto && tipo === "venda") {
      cripto = ativosUsuario.find(c => c.sigla === siglaCripto || c.nome === cryptoValue);
    }

    if (!cripto) {
      alert("Criptomoeda não encontrada.");
      return;
    }

    const payload = {
      usuarioId: parseInt(usuarioId),
      criptomoedaId: cripto.id,
      tipo: tipo,
      valor: valor,
      quantidadeCripto: 0
    };

    try {
      const res = await fetch(API_TRANSACOES, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (res.ok) {
        alert("Transação realizada com sucesso!");
        form.reset();
        equivalenciaEl.textContent = "0";

        if (tipo === "venda") await carregarListasCriptomoedas(usuarioId);
        atualizarDropdown();
      } else {
        const err = await res.json();
        alert("Erro: " + (err.message || "Falha ao criar transação"));
      }
    } catch (err) {
      console.error(err);
      alert("Erro ao processar transação.");
    }
  });
}

// 🔹 Inicialização da página
window.addEventListener("DOMContentLoaded", () => {
  carregarUsuarioCliente();

  const logoutBtn = document.querySelector(".btn-logout");
  if (logoutBtn) logoutBtn.addEventListener("click", realizarLogout);
});
