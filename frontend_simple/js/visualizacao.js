/**
 * Visualização - Hash Extensível, Codificação, Árvore B+
 */

// ==================== HASH ====================

async function renderHashView(container) {
  var indices = getIndicesHash();
  var html = '<div class="panel"><h4>Hash Extensível</h4>';
  html += '<div class="hash-tabs">';
  indices.forEach(function (idx, i) {
    html +=
      '<button class="hash-tab' +
      (i === 0 ? " active" : "") +
      '" onclick="carregarHash(\'' +
      idx +
      "', this)\">" +
      idx +
      "</button>";
  });
  html +=
    '</div><div id="hash-resultado"><div class="loading">Carregando...</div></div></div>';
  container.innerHTML = html;
  if (indices.length > 0) carregarHash(indices[0]);
}

async function carregarHash(nomeIndice, btn) {
  if (btn) {
    document.querySelectorAll(".hash-tab").forEach(function (b) {
      b.classList.remove("active");
    });
    btn.classList.add("active");
  }
  var div = document.getElementById("hash-resultado");
  div.innerHTML = '<div class="loading">Carregando...</div>';

  try {
    var data = await API.dataView.hash(entidadeAtual, nomeIndice);
    var html =
      '<p style="font-size:0.8rem;color:#666;margin-bottom:12px;">Profundidade global: ' +
      data.profundidadeGlobal +
      " | Entradas: " +
      data.tamanhoDir +
      "</p>";

    html +=
      "<table><thead><tr><th>Idx</th><th>Bin</th><th>→ Bucket</th></tr></thead><tbody>";
    data.diretorio.forEach(function (e) {
      html +=
        "<tr><td>" +
        e.indice +
        '</td><td style="font-family:monospace;">' +
        e.binario +
        "</td><td>" +
        e.enderecoBucket +
        "</td></tr>";
    });
    html += "</tbody></table>";

    html += '<h4 style="margin-top:16px;">Buckets</h4>';
    data.buckets.forEach(function (bucket) {
      html += '<div class="hash-bucket">';
      html +=
        "<h5>@ " +
        bucket.endereco +
        " · prof. local: " +
        bucket.profundidadeLocal +
        " · count: " +
        bucket.count +
        "</h5>";
      bucket.slots.forEach(function (slot, i) {
        var cls = slot.ativo ? "ativo" : "inativo";
        html +=
          '<div class="hash-slot ' +
          cls +
          '">[' +
          i +
          "] " +
          slot.interpretado +
          "</div>";
      });
      html += "</div>";
    });

    div.innerHTML = html;
  } catch (e) {
    div.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

function getIndicesHash() {
  switch (entidadeAtual) {
    case "CategoriaAlimento":
      return ["CategoriaAlimentoPK", "CategoriaAlimentoNome"];
    case "Alimento":
      return ["AlimentoPK", "AlimentoNome", "AlimentoCategoria"];
    case "Ambiente":
      return ["AmbientePK", "AmbienteNome"];
  }
  return [];
}

// ==================== ENCODING ====================

async function renderEncodingView(container) {
  container.innerHTML =
    '<div class="panel"><h4>Codificação</h4><div class="loading">Carregando...</div></div>';

  try {
    var data = await API.dataView.dat(entidadeAtual);
    var html =
      '<div class="panel"><h4>Codificação — ' + entidadeAtual + ".dat</h4>";
    html +=
      '<p style="font-size:0.8rem;color:#666;margin-bottom:12px;">Último ID: ' +
      data.ultimoId +
      " · Tamanho: " +
      data.tamanhoArquivo +
      " bytes</p>";

    if (data.registros.length === 0) {
      html += '<div class="empty-state">Arquivo vazio.</div>';
    }

    data.registros.forEach(function (reg) {
      html += '<div class="encoding-registro">';
      html += '<div class="encoding-meta">';
      html += "<span>pos: " + reg.posicao + "</span>";
      html +=
        "<span>lápide: '" +
        reg.lapide +
        "' " +
        (reg.ativo ? "✓" : "✗") +
        "</span>";
      html += "<span>" + reg.tamanhoBytes + " bytes</span>";
      html += "</div>";
      html += '<div class="encoding-hex">' + reg.hexDados + "</div>";
      html += '<div class="encoding-legivel">' + reg.dadosLegiveis + "</div>";
      html += "</div>";
    });

    html += "</div>";
    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

// ==================== ÁRVORE B+ ====================

function renderArvoreBMaisTab(container) {
  container.innerHTML =
    '<div class="panel"><p style="color:#666;">A Árvore B+ indexa o ItemAmbiente (tabela N:N). Acesse via <b>Ambiente → itens → botão "Árvore B+"</b>.</p></div>';
}

async function renderArvoreBMaisInline() {
  var div = document.getElementById("arvore-inline");
  if (!div) return;
  div.innerHTML = '<div class="loading">Carregando...</div>';

  try {
    var arvore = await API.itemAmbiente.arvoreBMais();

    var html = '<div class="panel">';
    html += "<h4>Árvore B+ — Índice global de ItemAmbiente</h4>";
    html +=
      '<p style="font-size:0.8rem;color:#666;margin-bottom:16px;">Índice único que cobre todos os pares (alimento, ambiente). Chave composta: alimentoId × 100000 + ambienteId. Consulta ordenada sem sort em memória.</p>';

    html += '<div class="arvore-container">';
    html += renderNoArvore(arvore, 0);
    html += "</div>";

    html +=
      '<h4 style="margin-top:24px;">Listagem ordenada (via Árvore B+)</h4>';
    var ordenados = await API.itemAmbiente.listarOrdenado();
    if (ordenados.length === 0) {
      html += '<div class="empty-state">Nenhum item cadastrado.</div>';
    } else {
      var aliMap = {};
      cacheAlimentos.forEach(function (a) {
        aliMap[a.id] = a.nome;
      });
      var ambMap = {};
      cacheAmbientes.forEach(function (a) {
        ambMap[a.id] = a.nome;
      });

      html += '<div class="table-container"><table>';
      html +=
        "<thead><tr><th>Chave Composta</th><th>Alimento</th><th>Ambiente</th><th>Qtd</th><th>Cadastro</th><th>Vencimento</th></tr></thead><tbody>";
      ordenados.forEach(function (item) {
        var nomeAli = aliMap[item.alimentoId] || "";
        var nomeAmb = ambMap[item.ambienteId] || "";
        html += "<tr>";
        html +=
          '<td style="font-family:monospace;">' + item.chaveComposta + "</td>";
        html +=
          "<td>" +
          item.alimentoId +
          ' <span class="fk-badge">' +
          nomeAli +
          "</span></td>";
        html +=
          "<td>" +
          item.ambienteId +
          ' <span class="fk-badge">' +
          nomeAmb +
          "</span></td>";
        html += "<td>" + item.quantidade + "</td>";
        html += "<td>" + (item.dataCadastro || "-") + "</td>";
        html += "<td>" + (item.dataVencimento || "-") + "</td>";
        html += "</tr>";
      });
      html += "</tbody></table></div>";
    }

    html += "</div>";
    div.innerHTML = html;
  } catch (e) {
    div.innerHTML =
      '<div class="msg-erro">Erro ao carregar árvore B+: ' +
      e.message +
      "</div>";
  }
}

function renderNoArvore(no, nivel) {
  var html =
    '<div class="arvore-no" style="margin-left:' + nivel * 24 + 'px;">';

  if (no.folha) {
    html += '<div class="arvore-folha">';
    html += '<span class="arvore-tipo">Folha</span> ';
    html += '<span class="arvore-chaves">';
    for (var i = 0; i < no.chaves.length; i++) {
      if (i > 0) html += " | ";
      html +=
        '<span class="arvore-par">' +
        no.chavesDecodificadas[i] +
        " → id:" +
        no.valores[i] +
        "</span>";
    }
    html += "</span>";
    if (no.proxFolha >= 0) {
      html += ' <span class="arvore-link">→ próx</span>';
    }
    html += "</div>";
  } else {
    html += '<div class="arvore-interno">';
    html += '<span class="arvore-tipo">Interno</span> ';
    html +=
      '<span class="arvore-chaves">[' +
      no.chavesDecodificadas.join(" | ") +
      "]</span>";
    html += "</div>";
    if (no.filhos) {
      for (var j = 0; j < no.filhos.length; j++) {
        html += renderNoArvore(no.filhos[j], nivel + 1);
      }
    }
  }

  html += "</div>";
  return html;
}

// ==================== ORDENAÇÃO POR INTERCALAÇÃO BALANCEADA ====================

async function renderOrdenacaoView(container) {
  container.innerHTML =
    '<div class="panel"><h4>Ordenação por Intercalação Balanceada</h4><div class="loading">Carregando...</div></div>';

  try {
    var data = await API.ordenacao.executar(entidadeAtual);

    var html = '<div class="panel">';
    html += "<h4>Intercalação Balanceada — " + entidadeAtual + "</h4>";
    html +=
      '<p style="font-size:0.8rem;color:#666;margin-bottom:16px;">Ordenação em memória secundária por nome. Total: ' +
      data.totalRegistros +
      " registros | Blocos: " +
      data.blocosGerados +
      " | Passos de intercalação: " +
      data.totalPassos +
      "</p>";

    // Log do algoritmo
    html += '<div class="ordenacao-log">';
    html += '<h5 style="margin-bottom:8px;">Log do algoritmo</h5>';
    html +=
      '<div class="hex-dump" style="color:#d4d4d4;font-size:0.75rem;max-height:300px;overflow-y:auto;">';
    data.log.forEach(function (linha) {
      if (linha.startsWith("===")) {
        html +=
          '<span style="color:#569cd6;font-weight:bold;">' +
          linha +
          "</span>\n";
      } else if (linha.startsWith("---")) {
        html += '<span style="color:#dcdcaa;">' + linha + "</span>\n";
      } else {
        html += linha + "\n";
      }
    });
    html += "</div></div>";

    // Tabela resultado
    if (data.registrosOrdenados && data.registrosOrdenados.length > 0) {
      html +=
        '<h5 style="margin-top:16px;margin-bottom:8px;">Resultado ordenado</h5>';
      html += '<div class="table-container"><table><thead><tr>';
      var colunas = Object.keys(data.registrosOrdenados[0]);
      colunas.forEach(function (col) {
        html += "<th>" + col + "</th>";
      });
      html += "</tr></thead><tbody>";
      data.registrosOrdenados.forEach(function (reg) {
        html += "<tr>";
        colunas.forEach(function (col) {
          var v = reg[col];
          if (Array.isArray(v)) v = v.join(", ");
          html += "<td>" + (v != null ? v : "-") + "</td>";
        });
        html += "</tr>";
      });
      html += "</tbody></table></div>";
    }

    html += "</div>";
    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}
