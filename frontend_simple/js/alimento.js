/**
 * Alimento - Listagem com resolução de FK + N:N bidirecional (ver ambientes)
 */

var alimentoVerAmbientesId = null;

function resetAlimentoState() {
  alimentoVerAmbientesId = null;
}

async function renderAlimentoListagem(container) {
  // Se estamos vendo os ambientes de um alimento
  if (alimentoVerAmbientesId !== null) {
    renderAmbientesDoAlimento(container);
    return;
  }

  container.innerHTML = '<div class="loading">Carregando...</div>';
  try {
    var dados = await API.alimento.listar();
    if (!dados || dados.length === 0) {
      container.innerHTML =
        '<div class="empty-state">Nenhum registro encontrado.</div>';
      return;
    }

    // Resolver FK categoria
    var catMap = {};
    cacheCategorias.forEach(function (c) {
      catMap[c.id] = c.nome;
    });

    var html = "";
    if (editandoId !== null) {
      var item = dados.find(function (d) {
        return d.id === editandoId;
      });
      if (item) html += renderFormEditar(item);
    }

    html += '<div class="table-container"><table><thead><tr>';
    html +=
      "<th>ID</th><th>Nome</th><th>Rótulos</th><th>Categoria</th><th>Ações</th>";
    html += "</tr></thead><tbody>";

    dados.forEach(function (item) {
      var catNome = catMap[item.idCategoriaAlimento] || "";
      html += "<tr>";
      html += "<td>" + item.id + "</td>";
      html += "<td>" + item.nome + "</td>";
      html += "<td>" + (item.rotulos ? item.rotulos.join(", ") : "") + "</td>";
      html +=
        "<td>" +
        item.idCategoriaAlimento +
        (catNome ? ' <span class="fk-badge">' + catNome + "</span>" : "") +
        "</td>";
      html += '<td class="row-actions">';
      html +=
        '<button class="btn-row" onclick="verAmbientesDoAlimento(' +
        item.id +
        ')">ambientes</button>';
      html +=
        '<button class="btn-row" onclick="iniciarEdicao(' +
        item.id +
        ')">editar</button>';
      html +=
        '<button class="btn-row delete" onclick="deletarRegistro(' +
        item.id +
        ')">excluir</button>';
      html += "</td></tr>";
    });
    html += "</tbody></table></div>";
    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

// ==================== N:N BIDIRECIONAL: Alimento → Ambientes ====================

function verAmbientesDoAlimento(alimentoId) {
  alimentoVerAmbientesId = alimentoId;
  renderTab();
}

function voltarListaAlimentos() {
  alimentoVerAmbientesId = null;
  renderTab();
}

async function renderAmbientesDoAlimento(container) {
  container.innerHTML = '<div class="loading">Carregando...</div>';
  try {
    var alimento = cacheAlimentos.find(function (a) {
      return a.id === alimentoVerAmbientesId;
    }) || { nome: "Alimento #" + alimentoVerAmbientesId };
    var itens = await API.itemAmbiente.listarPorAlimento(
      alimentoVerAmbientesId,
    );

    var ambMap = {};
    cacheAmbientes.forEach(function (a) {
      ambMap[a.id] = a.nome;
    });
    var tipoMap = { 0: "Geladeira", 1: "Freezer", 2: "Despensa" };

    var html = "";
    html +=
      '<button class="btn-voltar" onclick="voltarListaAlimentos()">← voltar para alimentos</button>';
    html +=
      '<h3 style="margin-bottom:16px;">Ambientes que contêm: ' +
      alimento.nome +
      "</h3>";
    html +=
      '<p style="font-size:0.8rem;color:#666;margin-bottom:12px;">Relacionamento N:N — a partir do Alimento</p>';

    if (!itens || itens.length === 0) {
      html +=
        '<div class="empty-state">Este alimento não está em nenhum ambiente.</div>';
    } else {
      html += '<div class="table-container"><table><thead><tr>';
      html +=
        "<th>Ambiente</th><th>Tipo</th><th>Quantidade</th><th>Cadastro</th><th>Vencimento</th>";
      html += "</tr></thead><tbody>";
      itens.forEach(function (item) {
        var ambNome = ambMap[item.ambienteId] || "ID " + item.ambienteId;
        var amb = cacheAmbientes.find(function (a) {
          return a.id === item.ambienteId;
        });
        var tipo = amb ? tipoMap[amb.tipo] || "Tipo " + amb.tipo : "";
        html += "<tr>";
        html +=
          "<td>" +
          item.ambienteId +
          ' <span class="fk-badge">' +
          ambNome +
          "</span></td>";
        html += "<td>" + tipo + "</td>";
        html += "<td>" + item.quantidade + "</td>";
        html += "<td>" + (item.dataCadastro || "-") + "</td>";
        html += "<td>" + (item.dataVencimento || "-") + "</td>";
        html += "</tr>";
      });
      html += "</tbody></table></div>";
    }

    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}
