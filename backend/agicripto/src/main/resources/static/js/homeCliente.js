const API_BASE_URL = "http://localhost:8080/api/usuarios";

async function carregarUsuario() {
  try {
    const response = await fetch(`${API_BASE_URL}/eu`, {
      method: "GET",
      credentials: "same-origin", // envia automaticamente o cookie da sessão
    });

    console.log("Status ao buscar /eu:", response.status);

    if (response.ok) {
      const usuario = await response.json();
      console.log("Usuário logado:", usuario);

      // Atualiza saudação com o nome retornado do backend
      document.querySelector(".username").textContent = usuario.nome;

      // (opcional) Atualiza sessionStorage para manter sincronizado
      sessionStorage.setItem("usuarioNome", usuario.nome);
      sessionStorage.setItem("usuarioTipo", usuario.tipo);

    } else if (response.status === 401) {
      alert("Sessão expirada. Faça login novamente.");
      window.location.replace("/pages/login/login.html");

    } else {
      console.error("Erro ao carregar dados do usuário:", response.status);
    }

  } catch (error) {
    console.error("Erro ao buscar dados do usuário:", error);
  }
}

// Logout
document.querySelector(".btn-logout").addEventListener("click", async () => {
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
});

// Executa ao carregar a página
carregarUsuario();
