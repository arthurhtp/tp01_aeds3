/**
 * Criptografia (Fase V) — visualização da cifra XOR no campo sensível Ambiente.nome.
 *
 * Mostra, para cada ambiente, o nome em texto claro (o que a API devolve, já
 * decifrado) ao lado do conteúdo cifrado em hexadecimal — exatamente como fica
 * gravado no arquivo .dat. Serve para demonstrar a criptografia.
 */

async function renderCriptoView(container) {
  container.innerHTML = '<div class="loading">Carregando...</div>';

  try {
    var dados = await API.ambiente.criptografia();

    // ---- Playground interativo: cifrar / decifrar ----
    var html = '<div class="panel">';
    html += "<h4>Cifrar / Decifrar (XOR)</h4>";
    html += '<p style="font-size:0.8rem;color:#666;margin-bottom:16px;">';
    html +=
      "Digite um texto para cifrar com XOR, ou um hexadecimal para decifrar de volta. " +
      "É o mesmo método aplicado ao campo sensível dos ambientes.";
    html += "</p>";

    html += '<div class="form-row">';
    html += '<div class="form-group" style="flex:1;">';
    html += "<label>Texto (claro)</label>";
    html += '<input type="text" id="cripto-texto" placeholder="ex: Cofre Secreto">';
    html += "</div>";
    html += '<div class="form-group" style="align-self:flex-end;">';
    html += '<button class="btn-submit" onclick="cifrarTexto()">Cifrar →</button>';
    html += "</div>";
    html += "</div>";

    html += '<div class="form-row">';
    html += '<div class="form-group" style="flex:1;">';
    html += "<label>Hexadecimal (cifrado)</label>";
    html += '<input type="text" id="cripto-hex" placeholder="ex: 0C 10 1B 09">';
    html += "</div>";
    html += '<div class="form-group" style="align-self:flex-end;">';
    html += '<button class="btn-submit" onclick="decifrarHex()">← Decifrar</button>';
    html += "</div>";
    html += "</div>";

    html += '<div id="cripto-msg"></div>';
    html += "</div>"; // fim panel playground

    // ---- Visualização dos ambientes cadastrados ----
    html += '<div class="panel">';
    html += "<h4>Criptografia XOR — campo sensível <code>nome</code> (Ambiente)</h4>";
    html += '<p style="font-size:0.8rem;color:#666;margin-bottom:16px;">';
    html +=
      "O nome do ambiente é gravado <strong>cifrado</strong> no arquivo " +
      "<code>data/Ambiente/Ambiente.dat</code> usando XOR. A API e a interface " +
      "exibem o texto claro (decifrado na leitura); abaixo está o valor como ele " +
      "realmente aparece em disco.";
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
      "<th>ID</th><th>Tipo</th><th>Nome (claro)</th>" +
      "<th>Nome cifrado (hex, no .dat)</th><th>Bytes</th>";
    html += "</tr></thead><tbody>";

    dados.forEach(function (a) {
      var tipoNome = tipoMap[a.tipo] || "Tipo " + a.tipo;
      html += "<tr>";
      html += "<td>" + a.id + "</td>";
      html += "<td>" + tipoNome + "</td>";
      html += "<td>" + escapeHtmlCripto(a.nomeClaro) + "</td>";
      html += '<td><code class="hex-inline">' + a.nomeCifradoHex + "</code></td>";
      html += "<td>" + a.tamanhoBytes + "</td>";
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

// Cifra o texto digitado e preenche o campo hex.
async function cifrarTexto() {
  var texto = document.getElementById("cripto-texto").value;
  var msg = document.getElementById("cripto-msg");
  if (!texto) {
    msg.innerHTML = '<div class="msg-erro">Digite um texto para cifrar.</div>';
    return;
  }
  try {
    var res = await API.criptografia.cifrar(texto);
    document.getElementById("cripto-hex").value = res.hex;
    msg.innerHTML =
      '<div class="msg-sucesso">Cifrado (XOR): <code>' +
      escapeHtmlCripto(res.hex) +
      "</code></div>";
  } catch (e) {
    msg.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

// Decifra o hex digitado e preenche o campo de texto.
async function decifrarHex() {
  var hex = document.getElementById("cripto-hex").value;
  var msg = document.getElementById("cripto-msg");
  if (!hex) {
    msg.innerHTML = '<div class="msg-erro">Informe um hexadecimal para decifrar.</div>';
    return;
  }
  try {
    var res = await API.criptografia.decifrar(hex);
    document.getElementById("cripto-texto").value = res.texto;
    msg.innerHTML =
      '<div class="msg-sucesso">Decifrado: <code>' +
      escapeHtmlCripto(res.texto) +
      "</code></div>";
  } catch (e) {
    msg.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

function escapeHtmlCripto(s) {
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}
