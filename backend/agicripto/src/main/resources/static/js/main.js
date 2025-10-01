async function verificarServidor() {
    try {
        const response = await fetch("/api/status");
        const data = await response.json();

        console.log("Backend ok: ", data.mensagem);
    } catch (error) {
        console.error("Erro de conexão com backend: ", error);
    }

}

document.addEventListener("DOMContentLoaded", verificarServidor);