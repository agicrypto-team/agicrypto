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

            const canvas = document.createElement('canvas');
            canvas.id = `grafico-${sigla}`;
            canvas.width = 400;
            canvas.height = 200;
            document.getElementById('container-graficos').appendChild(canvas);

            const cor = gerarCorDaPaleta(index);

            new Chart(canvas, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: sigla,
                        data: valores,
                        borderWidth: 2,
                        borderColor: cor,
                        backgroundColor: cor.replace('1)', '0.1)'), // versão mais clara da mesma cor
                        fill: true,
                        tension: 0.2
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: true },
                        title: {
                            display: true,
                            text: `Variação da ${sigla}`,
                            color: '#000',
                            font: { size: 14, weight: 'bold' }
                        }
                    },
                    scales: {
                        x: {
                            ticks: { color: '#000' },
                            grid: { color: '#eee' }
                        },
                        y: {
                            ticks: { color: '#000' },
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