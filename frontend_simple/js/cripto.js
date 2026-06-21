/**
 * Criptografia (Fase V) — aba "Criptografia" da entidade Ambiente.
 *
 * Mostra uma lista dos ambientes com o nome real (texto claro) e, ao lado, o
 * mesmo nome criptografado com XOR (em hexadecimal) — exatamente como ele fica
 * gravado no arquivo .dat.
 */

// Chave XOR — idêntica à usada no backend (stockit.seguranca.XORCipher).
var XOR_CHAVE = "StockIt-XOR-2024";

// Cifra um texto com XOR e devolve o resultado em hexadecimal ("0C 10 1B ...").
// Reproduz no front a mesma cifra do backend (operação simétrica).
function xorParaHex(texto) {
  if (texto == null) return "";
  var bytes = unescape(encodeURIComponent(texto)); // string -> bytes UTF-8
  var partes = [];
  for (var i = 0; i < bytes.length; i++) {
    var c = bytes.charCodeAt(i) ^ XOR_CHAVE.charCodeAt(i % XOR_CHAVE.length);
    partes.push(("0" + (c & 0xff).toString(16)).slice(-2).toUpperCase());
  }
  return partes.join(" ");
}

async function renderCriptoView(container) {
  container.innerHTML = '<div class="loading">Carregando...</div>';

  try {
    var dados = await API.ambiente.listar();

    var html = '<div class="panel">';
    html += "<h4>Criptografia XOR — campo sensível <code>nome</code> (Ambiente)</h4>";
    html += '<p style="font-size:0.8rem;color:#666;margin-bottom:16px;">';
    html +=
      "O nome do ambiente é um campo sensível, gravado <strong>cifrado</strong> no arquivo " +
      "<code>data/Ambiente/Ambiente.dat</code> com XOR. Abaixo, cada ambiente é exibido com o " +
      "nome real e, ao lado, o mesmo nome criptografado (em hexadecimal).";
    html += "</p>";

    if (!dados || dados.length === 0) {
      html +=
        '<div class="empty-state">Nenhum ambiente cadastrado para exibir.</div></div>';
      container.innerHTML = html;
      return;
    }

    var tipoMap = { 0: "Geladeira", 1: "Freezer", 2: "Despensa" };

    html += '<div class="table-container"><table><thead><tr>';
    html +=
      "<th>ID</th><th>Tipo</th><th>Nome (real)</th>" +
      "<th>Nome criptografado (XOR / hex)</th>";
    html += "</tr></thead><tbody>";

    dados.forEach(function (a) {
      var tipoNome = tipoMap[a.tipo] || "Tipo " + a.tipo;
      html += "<tr>";
      html += "<td>" + a.id + "</td>";
      html += "<td>" + tipoNome + "</td>";
      html += "<td>" + escapeHtmlCripto(a.nome) + "</td>";
      html +=
        '<td><code class="hex-inline">' +
        escapeHtmlCripto(xorParaHex(a.nome)) +
        "</code></td>";
      html += "</tr>";
    });

    html += "</tbody></table></div>";
    html +=
      '<p style="font-size:0.75rem;color:#888;margin-top:12px;">' +
      "Método: cifra simétrica XOR (Vernam com chave repetida). A mesma operação " +
      "cifra e decifra, pois <code>(D ^ K) ^ K = D</code>.";
    html += "</p>";
    html += "</div>";

    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

function escapeHtmlCripto(s) {
  return String(s == null ? "" : s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}
