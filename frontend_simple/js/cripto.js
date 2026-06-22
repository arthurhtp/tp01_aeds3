/**
 * Criptografia (Fase V) — aba "Criptografia" da entidade Ambiente.
 *
 * Mostra uma lista dos ambientes com o nome real (texto claro) e, ao lado, o
 * mesmo nome criptografado com XOR (em forma de caractere), como ele fica
 * gravado no arquivo .dat.
 *
 * Há um campo de CHAVE: a chave informada é usada para cifrar os NOMES DOS
 * AMBIENTES — ao alterá-la, a coluna criptografada recalcula para todos os
 * ambientes. (É uma demonstração visual; não altera os dados gravados.)
 */

// Chave XOR padrão — idêntica à usada no backend (stockit.seguranca.XORCipher).
var XOR_CHAVE = "StockIt-XOR-2024";

// Guarda os ambientes carregados para recifrar quando a chave mudar.
var criptoAmbientes = [];

// Cifra um texto com XOR usando a chave informada e devolve os bytes.
function xorBytes(texto, chave) {
  var bytes = unescape(encodeURIComponent(texto)); // string -> bytes UTF-8
  var k = unescape(encodeURIComponent(chave));
  var out = [];
  for (var i = 0; i < bytes.length; i++) {
    out.push(bytes.charCodeAt(i) ^ (k.charCodeAt(i % k.length) & 0xff));
  }
  return out;
}

// Representa os bytes cifrados "em forma de caractere": mostra o caractere
// quando é imprimível (32-126) e um ponto '.' quando não é (estilo hexdump).
function xorParaChar(texto, chave) {
  if (texto == null || texto === "") return "";
  var bytes = xorBytes(texto, chave || XOR_CHAVE);
  var s = "";
  for (var i = 0; i < bytes.length; i++) {
    var b = bytes[i] & 0xff;
    s += b >= 32 && b < 127 ? String.fromCharCode(b) : ".";
  }
  return s;
}

async function renderCriptoView(container) {
  container.innerHTML = '<div class="loading">Carregando...</div>';

  try {
    criptoAmbientes = await API.ambiente.listar();

    var html = '<div class="panel">';
    html += "<h4>Criptografia XOR</h4>";

    // Campo de chave + botão: cifra os nomes dos ambientes ao clicar.
    html += '<div class="form-row" style="align-items:flex-end;">';
    html += '<div class="form-group" style="flex:1;max-width:360px;">';
    html += "<label>Chave de criptografia</label>";
    html +=
      '<input type="text" id="cripto-chave" value="' +
      XOR_CHAVE +
      '" placeholder="chave para cifrar os nomes">';
    html += "</div>";
    html += '<div class="form-group">';
    html += '<button class="btn-submit" onclick="recifrarAmbientes()">Criptografar</button>';
    html += "</div>";
    html += "</div>";

    if (!criptoAmbientes || criptoAmbientes.length === 0) {
      html += '<div class="empty-state">Nenhum ambiente cadastrado para exibir.</div></div>';
      container.innerHTML = html;
      return;
    }

    html += '<div class="table-container"><table><thead><tr>';
    html +=
      "<th>ID</th><th>Tipo</th><th>Nome (real)</th>" +
      "<th>Nome criptografado (XOR)</th>";
    html += "</tr></thead><tbody id=\"cripto-tbody\">";
    html += linhasCripto(null); // começa vazio: cifra só ao clicar em Criptografar
    html += "</tbody></table></div>";
    html += "</div>";

    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

// Monta as linhas da tabela. Se chave for null, a coluna cifrada fica em branco
// (aguardando o clique em "Criptografar"); caso contrário cifra cada nome.
function linhasCripto(chave) {
  var tipoMap = { 0: "Geladeira", 1: "Freezer", 2: "Despensa" };
  var html = "";
  criptoAmbientes.forEach(function (a) {
    var tipoNome = tipoMap[a.tipo] || "Tipo " + a.tipo;
    var celCifrada =
      chave == null
        ? '<span style="color:#aaa;">— clique em Criptografar —</span>'
        : '<code class="hex-inline">' +
          escapeHtmlCripto(xorParaChar(a.nome, chave)) +
          "</code>";
    html += "<tr>";
    html += "<td>" + a.id + "</td>";
    html += "<td>" + tipoNome + "</td>";
    html += "<td>" + escapeHtmlCripto(a.nome) + "</td>";
    html += "<td>" + celCifrada + "</td>";
    html += "</tr>";
  });
  return html;
}

// Cifra a coluna de todos os ambientes com a chave informada (ao clicar).
function recifrarAmbientes() {
  var chave = document.getElementById("cripto-chave").value;
  var tbody = document.getElementById("cripto-tbody");
  if (!chave) {
    alert("Informe uma chave de criptografia.");
    return;
  }
  if (tbody) tbody.innerHTML = linhasCripto(chave);
}

function escapeHtmlCripto(s) {
  return String(s == null ? "" : s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}
