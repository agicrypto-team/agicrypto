const API_BASE_URL = "http://localhost:8080/api/usuarios";
const API_TRANSACOES = "http://localhost:8080/api/transacoes";
const API_CRYPTOS = "http://localhost:8080/api/criptomoedas";
const API_ATIVOS = "http://localhost:8080/api/ativos-carteira";

// 🔹 Variáveis globais para armazenar listas
let todasCriptos = [];
let ativosUsuario = [];

// 🔹 Função para carregar dados do usuário cliente
async function carregarUsuarioCliente() {
  const usernameEl = document.querySelector(".username");
  if (!usernameEl) return;

  try {
    const response = await fetch(`${API_BASE_URL}/eu`, {
      method: "GET",
      credentials: "same-origin",
    });

    if (response.ok) {
      const usuario = await response.json();
      usernameEl.textContent = usuario.nome;
      usernameEl.classList.remove("loading");

      // 🔹 Atualiza sessionStorage
      sessionStorage.setItem("usuarioId", usuario.id);
      sessionStorage.setItem("usuarioNome", usuario.nome);
      sessionStorage.setItem("usuarioTipo", usuario.tipo);

      // 🔹 Pré-carrega as listas de criptos
      await carregarListasCriptomoedas(usuario.id);

      // 🔹 Inicializa o datalist
      inicializarDatalist();

    } else if (response.status === 401) {
      alert("Sessão expirada. Faça login novamente.");
      window.location.replace("/pages/login/login.html");
    } else {
      console.error("Erro ao carregar dados do usuário:", response.status);
      usernameEl.textContent = "Cliente";
      usernameEl.classList.remove("loading");
    }

  } catch (error) {
    console.error("Erro ao buscar dados do usuário:", error);
    usernameEl.textContent = "Cliente";
    usernameEl.classList.remove("loading");
  }
}

// 🔹 Função para logout
async function realizarLogout() {
  try {
    await fetch(`${API_BASE_URL}/logout`, {
      method: "POST",
      credentials: "same-origin",
    });
  } catch (error) {
    console.error("Erro ao realizar logout:", error);
  } finally {
    sessionStorage.clear();
    window.location.replace("/pages/auth/login.html");
  }
}

// 🔹 Carrega todas as listas necessárias (compra e venda)
async function carregarListasCriptomoedas(usuarioId) {
  try {
    // 🔹 Todas as criptos (compra)
    const resCriptos = await fetch(API_CRYPTOS);
    if (resCriptos.ok) {
      const criptos = await resCriptos.json();
      todasCriptos = criptos.map(c => ({ nome: c.nome, sigla: c.sigla }));
    }

    // 🔹 Ativos do usuário (venda)
    const resAtivos = await fetch(`${API_ATIVOS}/do-usuario`, {
      method: "GET",
      credentials: "same-origin",
    });
    if (resAtivos.ok) {
      const ativos = await resAtivos.json();
      ativosUsuario = ativos.map(a => ({ nome: a.nome, sigla: a.sigla }));
    }

  } catch (err) {
    console.error("Erro ao carregar listas de criptomoedas:", err);
  }
}

// 🔹 Inicializa o datalist e eventos
function inicializarDatalist() {
  const cryptoInput = document.querySelector("#crypto");
  const tipoRadios = document.querySelectorAll("input[name='tipo']");
  if (!cryptoInput || tipoRadios.length === 0) return;

  // 🔹 Cria datalist apenas uma vez
  let datalist = document.querySelector("#cryptos-list");
  if (!datalist) {
    datalist = document.createElement("datalist");
    datalist.id = "cryptos-list";
    cryptoInput.setAttribute("list", datalist.id);
    document.body.appendChild(datalist);
  }

  // 🔹 Atualiza datalist de acordo com o radio selecionado
  function atualizarLista() {
    const tipoSelecionado = document.querySelector("input[name='tipo']:checked").value;
    let lista = tipoSelecionado === "compra" ? todasCriptos : ativosUsuario;

    datalist.innerHTML = "";
    lista.forEach(c => {
      const option = document.createElement("option");
      option.value = `${c.nome} (${c.sigla})`;
      datalist.appendChild(option);
    });
  }

  // Atualiza ao mudar o radio
  tipoRadios.forEach(radio => radio.addEventListener("change", atualizarLista));

  // Atualiza inicialmente
  atualizarLista();

  // 🔹 Evento de envio de nova transação
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
    let siglaCripto = match ? match[1] : cryptoValue;

    try {
      // Busca ID da cripto no array carregado (não precisa de fetch)
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
        criptomoedaId: cripto.id, // você precisa garantir que id está presente no array ou buscar via fetch se necessário
        tipo: tipo,
        valor: valor,
        quantidadeCripto: 0
      };

      const res = await fetch(API_TRANSACOES, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (res.ok) {
        alert("Transação realizada com sucesso!");
        form.reset();
        equivalenciaEl.textContent = "0";

        // 🔹 Atualiza listas de venda após nova transação
        if (tipo === "venda") {
          await carregarListasCriptomoedas(usuarioId);
        }
        atualizarLista();

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

// 🔹 Inicialização ao carregar página
window.addEventListener("DOMContentLoaded", () => {
  carregarUsuarioCliente();

  const logoutBtn = document.querySelector(".btn-logout");
  if (logoutBtn) logoutBtn.addEventListener("click", realizarLogout);
});
