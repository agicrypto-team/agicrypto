// const API_HISTORICOS_URL = "http://localhost:8080/api/historicos";

async function carregarGraficos() {
    const API_HISTORICOS_URL = "http://localhost:8080/api/historicos";

    try {
        const response = await fetch(`${API_HISTORICOS_URL}`);
        const data = await response.json();

        const grupos = data.reduce((acc, item) => {
            if (!acc[item.sigla]) acc[item.sigla] = [];
            acc[item.sigla].push(item);
            return acc;
        }, {});

        Object.entries(grupos).forEach(([sigla, registros], index) => {
            const labels = registros.map(r => r.momento);
            const valores = registros.map(r => r.cotacaoMomento);

            // 🔹 Criação do card do gráfico
            const card = document.createElement('div');
            card.classList.add('grafico-card');

            // 🔹 Título acima do gráfico
            const titulo = document.createElement('h3');
            titulo.textContent = sigla;
            card.appendChild(titulo);

            // 🔹 Canvas do Chart.js
            const canvas = document.createElement('canvas');
            canvas.id = `grafico-${sigla}`;
            card.appendChild(canvas);

            // 🔹 Adiciona ao container principal
            document.getElementById('container-graficos').appendChild(card);

            const cor = gerarCorDaPaleta(index);

            new Chart(canvas, {
                type: 'line',
                data: {
                    labels,
                    datasets: [{
                        label: sigla,
                        data: valores,
                        borderWidth: 2,
                        borderColor: cor,
                        backgroundColor: cor.replace('1)', '0.1)'),
                        fill: true,
                        tension: 0.2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: false }, // escondemos legenda para deixar visual limpo
                        title: { display: false }
                    },
                    scales: {
                        x: {
                            ticks: { color: '#555', maxRotation: 45, minRotation: 45 },
                            grid: { color: '#eee' }
                        },
                        y: {
                            ticks: { color: '#555' },
                            grid: { color: '#eee' }
                        }
                    }
                }
            });
        });


    } catch (error) {
        console.error('Erro ao carregar gráficos:', error);
    }
}

// Gera variações sutis da cor base (#0064F5)
function gerarCorDaPaleta(index) {
    const baseHue = 215; // azul
    const variation = (index * 10) % 30; // pequenas variações
    return `rgba(${0 + variation}, ${100 + variation}, ${245}, 1)`;
}

window.addEventListener("DOMContentLoaded", () => {
    carregarGraficos();

});