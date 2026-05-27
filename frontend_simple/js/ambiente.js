/**
 * Ambiente - Listagem + sub-tela de itens (N:N bidirecional: Ambiente → Alimentos)
 */

var ambienteItensId = null;
var editandoItemId = null;

function resetAmbienteState() {
  ambienteItensId = null;
  editandoItemId = null;
}

async function renderAmbienteListagem(container) {
  // Se estamos vendo itens de um ambiente
  if (ambienteItensId !== null) {
    renderItensAmbiente(container);
    return;
  }

  container.innerHTML = '<div class="loading">Carregando...</div>';
  try {
    var dados = await API.ambiente.listar();
    if (!dados || dados.length === 0) {
      container.innerHTML =
        '<div class="empty-state">Nenhum registro encontrado.</div>';
      return;
    }

    var tipoMap = { 0: "Geladeira", 1: "Freezer", 2: "Despensa" };
    var html = "";

    if (editandoId !== null) {
      var item = dados.find(function (d) {
        return d.id === editandoId;
      });
      if (item) html += renderFormEditar(item);
    }

    html += '<div class="table-container"><table><thead><tr>';
    html += "<th>ID</th><th>Nome</th><th>Tipo</th><th>Ações</th>";
    html += "</tr></thead><tbody>";

    dados.forEach(function (item) {
      var tipoNome = tipoMap[item.tipo] || "Tipo " + item.tipo;
      html += "<tr>";
      html += "<td>" + item.id + "</td>";
      html += "<td>" + item.nome + "</td>";
      html +=
        "<td>" +
        item.tipo +
        ' <span class="fk-badge">' +
        tipoNome +
        "</span></td>";
      html += '<td class="row-actions">';
      html +=
        '<button class="btn-row" onclick="verItensAmbiente(' +
        item.id +
        ')">ver alimentos</button>';
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

// ==================== N:N BIDIRECIONAL: Ambiente → Alimentos ====================

function verItensAmbiente(ambienteId) {
  ambienteItensId = ambienteId;
  editandoItemId = null;
  renderTab();
}

function voltarListaAmbientes() {
  ambienteItensId = null;
  editandoItemId = null;
  renderTab();
}

async function renderItensAmbiente(container) {
  container.innerHTML = '<div class="loading">Carregando...</div>';
  try {
    var ambiente = cacheAmbientes.find(function (a) {
      return a.id === ambienteItensId;
    }) || { nome: "Ambiente #" + ambienteItensId };
    var itens = await API.itemAmbiente.listarPorAmbiente(ambienteItensId);

    var aliMap = {};
    cacheAlimentos.forEach(function (a) {
      aliMap[a.id] = a.nome;
    });

    var html = "";
    html +=
      '<button class="btn-voltar" onclick="voltarListaAmbientes()">← voltar para ambientes</button>';
    html +=
      '<h3 style="margin-bottom:8px;">Alimentos em: ' + ambiente.nome + "</h3>";
    html +=
      '<p style="font-size:0.8rem;color:#666;margin-bottom:16px;">Relacionamento N:N — a partir do Ambiente</p>';

    // Form de novo item
    html +=
      '<div class="form-panel"><h3>Adicionar item</h3><form onsubmit="event.preventDefault();">';
    html += '<div class="form-row">';
    html += campoSelect("novo-alimentoId", "Alimento", cacheAlimentos, null);
    html += campoNumero("novo-quantidade", "Quantidade", "");
    html += campoData("novo-dataCadastro", "Data Cadastro", "");
    html += campoData("novo-dataVencimento", "Data Vencimento", "");
    html += "</div>";
    html +=
      '<button class="btn-submit" onclick="criarItemAmbiente()">Adicionar</button>';
    html += '</form><div id="msg-item-criar"></div></div>';

    // Form de edição
    if (editandoItemId !== null) {
      var itemEdit = itens.find(function (i) {
        return i.id === editandoItemId;
      });
      if (itemEdit) {
        html +=
          '<div class="form-panel"><h3>Editando item #' +
          itemEdit.id +
          '</h3><form onsubmit="event.preventDefault();">';
        html += '<div class="form-row">';
        html += campoSelect(
          "edit-alimentoId",
          "Alimento",
          cacheAlimentos,
          itemEdit.alimentoId,
        );
        html += campoNumero(
          "edit-quantidade",
          "Quantidade",
          itemEdit.quantidade,
        );
        html += campoData(
          "edit-dataCadastro",
          "Data Cadastro",
          itemEdit.dataCadastro || "",
        );
        html += campoData(
          "edit-dataVencimento",
          "Data Vencimento",
          itemEdit.dataVencimento || "",
        );
        html += "</div>";
        html +=
          '<button class="btn-submit" onclick="salvarItemAmbiente(' +
          itemEdit.id +
          ')">Salvar</button>';
        html +=
          '<button class="btn-cancel" onclick="cancelarEdicaoItem()">Cancelar</button>';
        html += '</form><div id="msg-item-edit"></div></div>';
      }
    }

    // Tabela de itens
    if (!itens || itens.length === 0) {
      html += '<div class="empty-state">Nenhum item neste ambiente.</div>';
    } else {
      html += '<div class="table-container"><table>';
      html +=
        "<thead><tr><th>ID</th><th>Chave (ali,amb)</th><th>Alimento</th><th>Qtd</th><th>Cadastro</th><th>Vencimento</th><th>Ações</th></tr></thead><tbody>";
      itens.forEach(function (item) {
        var nomeAli = aliMap[item.alimentoId] || "ID " + item.alimentoId;
        html += "<tr>";
        html += "<td>" + item.id + "</td>";
        html +=
          "<td style='font-family:monospace;'>(" +
          item.alimentoId +
          "," +
          item.ambienteId +
          ")</td>";
        html +=
          "<td>" +
          item.alimentoId +
          ' <span class="fk-badge">' +
          nomeAli +
          "</span></td>";
        html += "<td>" + item.quantidade + "</td>";
        html += "<td>" + (item.dataCadastro || "-") + "</td>";
        html += "<td>" + (item.dataVencimento || "-") + "</td>";
        html += '<td class="row-actions">';
        html +=
          '<button class="btn-row" onclick="editarItemAmbiente(' +
          item.id +
          ')">editar</button>';
        html +=
          '<button class="btn-row delete" onclick="deletarItemAmbiente(' +
          item.id +
          ')">excluir</button>';
        html += "</td></tr>";
      });
      html += "</tbody></table></div>";
    }

    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

// ==================== CRUD de ItemAmbiente ====================

async function criarItemAmbiente() {
  try {
    var data = {
      alimentoId: num("novo-alimentoId"),
      ambienteId: ambienteItensId,
      quantidade: num("novo-quantidade"),
      dataCadastro: val("novo-dataCadastro"),
      dataVencimento: val("novo-dataVencimento"),
    };
    await API.itemAmbiente.criar(data);
    renderTab();
  } catch (e) {
    document.getElementById("msg-item-criar").innerHTML =
      '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

function editarItemAmbiente(id) {
  editandoItemId = id;
  renderTab();
}
function cancelarEdicaoItem() {
  editandoItemId = null;
  renderTab();
}

async function salvarItemAmbiente(id) {
  try {
    var data = {
      alimentoId: num("edit-alimentoId"),
      ambienteId: ambienteItensId,
      quantidade: num("edit-quantidade"),
      dataCadastro: val("edit-dataCadastro"),
      dataVencimento: val("edit-dataVencimento"),
    };
    await API.itemAmbiente.atualizar(id, data);
    editandoItemId = null;
    renderTab();
  } catch (e) {
    document.getElementById("msg-item-edit").innerHTML =
      '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

async function deletarItemAmbiente(id) {
  if (!confirm("Excluir item #" + id + "?")) return;
  try {
    await API.itemAmbiente.deletar(id);
    renderTab();
  } catch (e) {
    alert("Erro: " + e.message);
  }
}
