const API_BASE_URL = "http://localhost:8080/api/usuarios";
const API_TRANSACOES = "http://localhost:8080/api/transacoes";
const API_CRYPTOS = "http://localhost:8080/api/criptomoedas";
const API_ATIVOS = "http://localhost:8080/api/ativos-carteira";

// 🔹 Função para carregar dados do usuário cliente
async function carregarUsuarioCliente() {
  const usernameEl = document.querySelector(".username");
  if (!usernameEl) return;

  try {
    const response = await fetch(`${API_BASE_URL}/eu`, {
      method: "GET",
      credentials: "same-origin",
    });

    console.log("Status ao buscar /eu:", response.status);

    if (response.ok) {
      const usuario = await response.json();
      console.log("Usuário logado:", usuario);

      usernameEl.textContent = usuario.nome;
      usernameEl.classList.remove("loading");

      // 🔹 Atualiza sessionStorage
      sessionStorage.setItem("usuarioId", usuario.id);
      sessionStorage.setItem("usuarioNome", usuario.nome);
      sessionStorage.setItem("usuarioTipo", usuario.tipo);

      // Depois que carregou o usuário, carregamos as criptos no input
      carregarCriptomoedas();
    }
    else if (response.status === 401) {
      alert("Sessão expirada. Faça login novamente.");
      window.location.replace("/pages/login/login.html");
    }
    else {
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

// 🔹 Função para carregar criptomoedas no input de transação
async function carregarCriptomoedas() {
  const cryptoInput = document.querySelector("#crypto");
  const tipoRadios = document.querySelectorAll("input[name='tipo']");
  if (!cryptoInput || tipoRadios.length === 0) return;

  // Cria uma datalist para sugestão de moedas
  let datalist = document.createElement("datalist");
  datalist.id = "cryptos-list";
  cryptoInput.setAttribute("list", datalist.id);
  document.body.appendChild(datalist);

  // Função para atualizar a lista conforme radio selecionado
  async function atualizarLista() {
    const tipoSelecionado = document.querySelector("input[name='tipo']:checked").value;
    let listaCriptos = [];

    if (tipoSelecionado === "compra") {
      // Comprar: todas as criptomoedas do banco
      const res = await fetch(API_CRYPTOS);
      listaCriptos = await res.json();
      listaCriptos = listaCriptos.map(c => `${c.nome} (${c.sigla})`);
    } else {
      // Vender: apenas ativos que o usuário possui
      const userId = sessionStorage.getItem("usuarioId");
      if (!userId) return;
      const res = await fetch(`${API_ATIVOS}/usuario/${userId}`);
      const ativos = await res.json();
      listaCriptos = ativos.map(a => `${a.criptomoedaNome} (${a.criptomoedaSigla})`);
    }

    // Limpa e preenche o datalist
    datalist.innerHTML = "";
    listaCriptos.forEach(nome => {
      const option = document.createElement("option");
      option.value = nome;
      datalist.appendChild(option);
    });
  }

  tipoRadios.forEach(radio => radio.addEventListener("change", atualizarLista));
  atualizarLista();

  // 🔹 Envio de nova transação
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

    // Extrai o nome da cripto entre parênteses ou o nome inteiro
    const match = cryptoValue.match(/\(([^)]+)\)/);
    let siglaCripto = match ? match[1] : cryptoValue;

    try {
      // Busca a criptomoeda no banco para pegar ID
      let resCripto = await fetch(API_CRYPTOS);
      let criptos = await resCripto.json();
      const cripto = criptos.find(c => c.sigla === siglaCripto || c.nome === cryptoValue);

      if (!cripto) {
        alert("Criptomoeda não encontrada no banco.");
        return;
      }

      const payload = {
        usuarioId: parseInt(usuarioId),
        criptomoedaId: cripto.id,
        tipo: tipo,
        valor: valor,
        quantidadeCripto: 0 // Aqui você pode calcular se quiser quantidade baseada em valor/cotação
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
        carregarCriptomoedas(); // Atualiza lista para venda
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
