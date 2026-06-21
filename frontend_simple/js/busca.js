/**
 * Busca por padrão (Fase V) — KMP / Boyer-Moore sobre o campo "nome" de Alimento.
 *
 * Fluxo pedido no TP:
 *   i.   menu com opção "Pesquisar por padrão (KMP / BM)"  -> aba "Buscar padrão"
 *   ii.  usuário escolhe o algoritmo                       -> select KMP/BM
 *   iii. usuário informa o padrão (string)                 -> input de texto
 *   iv.  sistema retorna registros encontrados             -> tabela de resultados
 */

function renderBuscaPadraoView(container) {
  var html = '<div class="panel">';
  html += "<h4>Pesquisar por padrão (KMP / BM)</h4>";
  html += '<p style="font-size:0.8rem;color:#666;margin-bottom:16px;">';
  html +=
    "Procura o padrão informado dentro do campo <code>nome</code> dos alimentos. " +
    "A busca não diferencia maiúsculas/minúsculas.";
  html += "</p>";

  html += '<form onsubmit="event.preventDefault();executarBuscaPadrao();">';
  html += '<div class="form-row">';

  html += '<div class="form-group">';
  html += "<label>Padrão (string)</label>";
  html +=
    '<input type="text" id="busca-padrao" placeholder="ex: arr, leite, ovo...">';
  html += "</div>";

  html += '<div class="form-group">';
  html += "<label>Algoritmo</label>";
  html += '<select id="busca-algoritmo">';
  html += '<option value="kmp">KMP (Knuth–Morris–Pratt)</option>';
  html += '<option value="bm">Boyer–Moore</option>';
  html += "</select>";
  html += "</div>";

  html += "</div>"; // form-row
  html += '<button class="btn-submit" type="submit">Pesquisar</button>';
  html += "</form>";

  html += '<div id="busca-resultado"></div>';
  html += "</div>";

  container.innerHTML = html;
}

async function executarBuscaPadrao() {
  var padrao = document.getElementById("busca-padrao").value.trim();
  var algoritmo = document.getElementById("busca-algoritmo").value;
  var div = document.getElementById("busca-resultado");

  if (!padrao) {
    div.innerHTML =
      '<div class="msg-erro">Informe um padrão para pesquisar.</div>';
    return;
  }

  div.innerHTML = '<div class="loading">Pesquisando...</div>';

  try {
    var res = await API.buscaPadrao.alimentos(padrao, algoritmo);

    var html = "";
    html += '<div class="busca-meta">';
    html +=
      "Algoritmo: <strong>" +
      res.algoritmo +
      "</strong> &middot; Padrão: <strong>" +
      res.padrao +
      "</strong> &middot; Encontrados: <strong>" +
      res.totalEncontrados +
      "</strong> &middot; Tempo: <strong>" +
      res.tempoMicrossegundos +
      " µs</strong>";
    html += "</div>";

    if (!res.resultados || res.resultados.length === 0) {
      html +=
        '<div class="empty-state">Nenhum alimento com esse padrão no nome.</div>';
      div.innerHTML = html;
      return;
    }

    html += '<div class="table-container"><table><thead><tr>';
    html += "<th>ID</th><th>Nome</th><th>Rótulos</th><th>Categoria</th>";
    html += "</tr></thead><tbody>";
    res.resultados.forEach(function (a) {
      var rotulos = Array.isArray(a.rotulos) ? a.rotulos.join(", ") : "-";
      html += "<tr>";
      html += "<td>" + a.id + "</td>";
      html += "<td>" + destacarPadrao(a.nome, res.padrao) + "</td>";
      html += "<td>" + (rotulos || "-") + "</td>";
      html += "<td>" + (a.idCategoriaAlimento || "-") + "</td>";
      html += "</tr>";
    });
    html += "</tbody></table></div>";

    div.innerHTML = html;
  } catch (e) {
    div.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

// Destaca (em <mark>) as ocorrências do padrão no nome, sem diferenciar caixa.
function destacarPadrao(nome, padrao) {
  if (!nome) return "-";
  if (!padrao) return nome;
  var idx = nome.toLowerCase().indexOf(padrao.toLowerCase());
  if (idx < 0) return nome;
  var out = "";
  var i = 0;
  while (idx >= 0) {
    out += escapeHtml(nome.substring(i, idx));
    out += "<mark>" + escapeHtml(nome.substring(idx, idx + padrao.length)) + "</mark>";
    i = idx + padrao.length;
    idx = nome.toLowerCase().indexOf(padrao.toLowerCase(), i);
  }
  out += escapeHtml(nome.substring(i));
  return out;
}

function escapeHtml(s) {
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}
