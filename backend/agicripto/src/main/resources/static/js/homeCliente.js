const API_BASE_URL = "http://localhost:8080/api/usuarios";
const API_TRANSACOES = "http://localhost:8080/transacoes";
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
     ativosUsuario = ativos.map(a => ({
       id: a.criptomoeda?.id, // ID da cripto
       nome: a.criptomoeda?.nome,
       sigla: a.criptomoeda?.sigla,
       icone: a.criptomoeda?.icone || "💰"
     }));
   }

  } catch (err) {
    console.error("Erro ao carregar listas de criptomoedas:", err);
  }
  console.log("Ativos carregados:", ativosUsuario);

}


function inicializarDropdown() {
  const cryptoInput = document.querySelector("#crypto");
  const dropdown = document.querySelector(".crypto-dropdown");
  if (!cryptoInput || !dropdown) return;

  const tipoRadios = document.querySelectorAll("input[name='tipo']");

  function atualizarDropdown() {
    const tipoSelecionado = document.querySelector("input[name='tipo']:checked").value;
    console.log("Tipo:", tipoSelecionado, "Ativos:", ativosUsuario);
    const lista = tipoSelecionado === "compra" ? todasCriptos : ativosUsuario;

    dropdown.innerHTML = "";
    lista.forEach(c => {
      const item = document.createElement("div");
      item.className = "crypto-item";
      item.dataset.id = c.id; // 🔹 salva o ID no item
      item.innerHTML = `
        <span class="crypto-name">${c.nome}</span>
        <span class="crypto-sigla">(${c.sigla})</span>
      `;

      // 🔸 Impede o blur do input antes do clique
      item.addEventListener("mousedown", e => e.preventDefault());

      // 🔸 Clique realmente seleciona
      item.addEventListener("click", () => {
        cryptoInput.value = `${c.nome} (${c.sigla})`;
        cryptoInput.dataset.id = c.id; // 🔹 salva o ID selecionado no input
        dropdown.style.display = "none";
      });

      dropdown.appendChild(item);
    });
  }

  // Atualiza ao mudar o tipo
  tipoRadios.forEach(r => r.addEventListener("change", atualizarDropdown));
  atualizarDropdown();

  // Toggle dropdown ao focar
  cryptoInput.addEventListener("focus", () => {
    dropdown.style.display = "block";
  });

  // Fecha o dropdown só se o clique for fora
  cryptoInput.addEventListener("blur", () => {
    setTimeout(() => {
      if (!dropdown.contains(document.activeElement)) {
        dropdown.style.display = "none";
      }
    }, 150);
  });

  // 🔹 Evento submit da transação
  const form = document.querySelector(".transaction-form");
  const valorInput = document.querySelector("#valor");
  const equivalenciaEl = document.querySelector("#equivalencia");

  form.addEventListener("submit", async e => {
    e.preventDefault();

    const tipo = document.querySelector("input[name='tipo']:checked").value;
    const usuarioId = sessionStorage.getItem("usuarioId");
    carregarListasCriptomoedas(usuarioId);
    const valor = parseFloat(valorInput.value.replace(",", "."));
    const criptoId = parseInt(cryptoInput.dataset.id); // 🔹 pega o ID salvo no input

    if (!criptoId || isNaN(valor)) {
      alert("Selecione uma criptomoeda e informe o valor corretamente.");
      return;
    }

    const payload = {
      usuarioId: parseInt(usuarioId),
      criptomoedaId: criptoId,
      tipo: tipo.charAt(0).toUpperCase() + tipo.slice(1), // Compra ou Venda
      valor: valor,
      quantidadeCripto: 0
    };

    try {
      const res = await fetch(API_TRANSACOES, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "same-origin",
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        alert("Transação realizada com sucesso!");
        form.reset();
        equivalenciaEl.textContent = "0";
        cryptoInput.dataset.id = ""; // limpa o ID selecionado
        if (tipo === "venda") await carregarListasCriptomoedas(usuarioId);
        atualizarDropdown();
      } else {
        const err = await res.json().catch(() => ({}));
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
