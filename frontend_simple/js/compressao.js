/**
 * Compressão — Huffman e LZW por entidade
 * Exibe métricas + hex dump lado a lado (original vs comprimido).
 */

async function renderCompressaoView(container) {
  var html = '<div class="panel">';
  html += "<h4>Compressão — " + entidadeAtual + "</h4>";
  html += '<p style="font-size:0.8rem;color:#666;margin-bottom:20px;">';
  html += "Comprime todos os arquivos da entidade (<code>.dat .dir .bkt .bplus</code>) em um único arquivo de backup. ";
  html += "Escolha o algoritmo para visualizar os bytes originais e comprimidos:";
  html += "</p>";

  html += '<div class="compressao-btns">';
  html += '<button class="btn-compress huffman" onclick="executarCompressao(\'huffman\')">Huffman</button>';
  html += '<button class="btn-compress lzw" onclick="executarCompressao(\'lzw\')">LZW</button>';
  html += "</div>";

  html += '<div id="compressao-resultado"></div>';
  html += "</div>";

  container.innerHTML = html;
}

async function executarCompressao(algoritmo) {
  var div = document.getElementById("compressao-resultado");
  div.innerHTML = '<div class="loading">Comprimindo com ' + algoritmo.toUpperCase() + "...</div>";

  try {
    var resultado = algoritmo === "huffman"
      ? await API.compressao.huffman(entidadeAtual)
      : await API.compressao.lzw(entidadeAtual);

    if (resultado.erro) {
      div.innerHTML = '<div class="msg-erro">Erro: ' + resultado.erro + "</div>";
      return;
    }

    var m = resultado.metricas;
    var html = "";

    // --- Métricas ---
    html += '<div class="compressao-stats">';
    html += "<h5>Resultado — " + resultado.algoritmo + "</h5>";
    html += '<div class="stats-grid">';
    html += statItem("Tamanho original",    formatBytes(m.tamanhoOriginal));
    html += statItem("Tamanho comprimido",  formatBytes(m.tamanhoComprimido));
    html += statItem("Taxa de compressão",  m.taxaCompressao, true);
    html += statItem("Fator de compressão", m.fatorCompressao);
    html += "</div>";

    // Arquivos incluídos
    html += '<p style="font-size:0.8rem;color:#666;margin:12px 0 4px;">Arquivos incluídos no backup:</p>';
    html += '<ul style="font-size:0.8rem;color:#888;margin:0 0 16px;padding-left:20px;">';
    (resultado.arquivosIncluidos || []).forEach(function (a) {
      html += "<li><code>" + a + "</code></li>";
    });
    html += "</ul>";
    html += "</div>";

    // --- Hex dump lado a lado ---
    html += '<div class="hex-comparacao">';

    // Original
    html += '<div class="hex-panel">';
    html += '<div class="hex-panel-header">Original — ' + formatBytes(m.tamanhoOriginal) + '</div>';
    html += '<pre class="hex-dump">' + (resultado.hexOriginal || "(vazio)") + "</pre>";
    if (resultado.hexOriginalTruncado) {
      html += '<div class="hex-truncado">... truncado (exibindo primeiros 2 KB)</div>';
    }
    html += "</div>";

    // Comprimido
    html += '<div class="hex-panel">';
    html += '<div class="hex-panel-header">Comprimido (' + resultado.algoritmo + ') — ' + formatBytes(m.tamanhoComprimido) + '</div>';
    html += '<pre class="hex-dump">' + (resultado.hexComprimido || "(vazio)") + "</pre>";
    if (resultado.hexComprimidoTruncado) {
      html += '<div class="hex-truncado">... truncado (exibindo primeiros 2 KB)</div>';
    }
    html += "</div>";

    html += "</div>"; // hex-comparacao

    div.innerHTML = html;

  } catch (e) {
    div.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

function statItem(label, valor, destaque) {
  return '<div class="stat-item">'
    + '<span class="stat-label">' + label + '</span>'
    + '<span class="stat-value' + (destaque ? " destaque" : "") + '">' + valor + '</span>'
    + '</div>';
}

function formatBytes(bytes) {
  if (!bytes || bytes === 0) return "0 B";
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
  return (bytes / (1024 * 1024)).toFixed(2) + " MB";
}
