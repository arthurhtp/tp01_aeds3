/**
 * Core - Navegação, tabs, cache, helpers de formulário
 */

var entidadeAtual = null;
var tabAtual = "listagem";
var editandoId = null;

// Cache global para dropdowns e resolução de FKs
var cacheCategorias = [];
var cacheAlimentos = [];
var cacheAmbientes = [];

// ==================== NAVEGAÇÃO ====================

function navegarEntidade(entidade) {
  entidadeAtual = entidade;
  editandoId = null;
  // Reset estado específico de cada entidade
  if (typeof resetAmbienteState === "function") resetAmbienteState();
  if (typeof resetAlimentoState === "function") resetAlimentoState();

  document.getElementById("tela-inicial").classList.add("hidden");
  document.getElementById("tela-entidade").classList.remove("hidden");
  document.getElementById("titulo-entidade").textContent = entidade;

  // Mostrar botão Árvore B+ só em Ambiente
  var btnArvore = document.querySelector(".btn-arvore");
  if (btnArvore) {
    if (entidade === "Ambiente") btnArvore.classList.remove("hidden");
    else btnArvore.classList.add("hidden");
  }

  tabAtual = "listagem";
  updateTabButtons();
  carregarCaches().then(function () {
    renderTab();
  });
}

function voltarInicio() {
  entidadeAtual = null;
  editandoId = null;
  document.getElementById("tela-entidade").classList.add("hidden");
  document.getElementById("tela-inicial").classList.remove("hidden");
}

function setTab(tab) {
  tabAtual = tab;
  editandoId = null;
  if (typeof resetAmbienteState === "function") resetAmbienteState();
  if (typeof resetAlimentoState === "function") resetAlimentoState();
  updateTabButtons();
  renderTab();
}

function updateTabButtons() {
  var btns = document.querySelectorAll(".acoes-bar .btn-acao");
  btns.forEach(function (btn) {
    btn.classList.remove("active");
  });
  var tabs = ["listagem", "criar", "hash", "encoding", "ordenacao", "arvore", "compressao"];
  var idx = tabs.indexOf(tabAtual);
  if (idx >= 0 && btns[idx]) btns[idx].classList.add("active");
}

function renderTab() {
  var container = document.getElementById("tab-content");
  switch (tabAtual) {
    case "listagem":
      renderListagem(container);
      break;
    case "criar":
      renderFormCriar(container);
      break;
    case "hash":
      renderHashView(container);
      break;
    case "encoding":
      renderEncodingView(container);
      break;
    case "ordenacao":
      renderOrdenacaoView(container);
      break;
    case "arvore":
      container.innerHTML = '<div id="arvore-inline"></div>';
      renderArvoreBMaisInline();
      break;
    case "compressao":
      renderCompressaoView(container);
      break;
  }
}

async function carregarCaches() {
  try {
    cacheCategorias = await API.categoriaAlimento.listar();
  } catch (e) {
    cacheCategorias = [];
  }
  try {
    cacheAlimentos = await API.alimento.listar();
  } catch (e) {
    cacheAlimentos = [];
  }
  try {
    cacheAmbientes = await API.ambiente.listar();
  } catch (e) {
    cacheAmbientes = [];
  }
}

// ==================== LISTAGEM GENÉRICA ====================

async function renderListagem(container) {
  // Delegar para módulo específico se necessário
  if (
    entidadeAtual === "Ambiente" &&
    typeof renderAmbienteListagem === "function"
  ) {
    renderAmbienteListagem(container);
    return;
  }
  if (
    entidadeAtual === "Alimento" &&
    typeof renderAlimentoListagem === "function"
  ) {
    renderAlimentoListagem(container);
    return;
  }

  container.innerHTML = '<div class="loading">Carregando...</div>';
  try {
    var dados = await getApiForEntity().listar();
    if (!dados || dados.length === 0) {
      container.innerHTML =
        '<div class="empty-state">Nenhum registro encontrado.</div>';
      return;
    }
    container.innerHTML = renderTabelaGenerica(dados);
  } catch (e) {
    container.innerHTML = '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

function renderTabelaGenerica(dados) {
  var html = "";
  if (editandoId !== null) {
    var item = dados.find(function (d) {
      return d.id === editandoId;
    });
    if (item) html += renderFormEditar(item);
  }

  html += '<div class="table-container"><table><thead><tr>';
  var colunas = getColunasEntidade();
  colunas.forEach(function (col) {
    html += "<th>" + col.label + "</th>";
  });
  html += "<th>Ações</th></tr></thead><tbody>";

  dados.forEach(function (item) {
    html += "<tr>";
    colunas.forEach(function (col) {
      var v = item[col.key];
      if (Array.isArray(v)) v = v.join(", ");
      html += "<td>" + (v != null ? v : "-") + "</td>";
    });
    html += '<td class="row-actions">';
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
  return html;
}

// ==================== CRUD GENÉRICO ====================

function iniciarEdicao(id) {
  editandoId = id;
  renderTab();
}
function cancelarEdicao() {
  editandoId = null;
  renderTab();
}

function renderFormEditar(item) {
  var html =
    '<div class="form-panel"><h3>Editando #' +
    item.id +
    '</h3><form onsubmit="event.preventDefault();">';
  html += '<div class="form-row">' + getCamposForm(item) + "</div>";
  html +=
    '<button class="btn-submit" onclick="submitUpdate(' +
    item.id +
    ')">Salvar</button>';
  html +=
    '<button class="btn-cancel" onclick="cancelarEdicao()">Cancelar</button>';
  html += '</form><div id="msg-form"></div></div>';
  return html;
}

function renderFormCriar(container) {
  var html =
    '<div class="form-panel"><h3>Novo registro</h3><form onsubmit="event.preventDefault();">';
  html += '<div class="form-row">' + getCamposForm(null) + "</div>";
  html += '<button class="btn-submit" onclick="submitCreate()">Criar</button>';
  html += '</form><div id="msg-form"></div></div>';
  container.innerHTML = html;
}

async function submitCreate() {
  try {
    var data = coletarDadosForm();
    var resultado = await getApiForEntity().criar(data);
    document.getElementById("msg-form").innerHTML =
      '<div class="msg-sucesso">Criado com ID ' + resultado.id + "</div>";
    document
      .querySelectorAll(".form-panel input, .form-panel select")
      .forEach(function (i) {
        if (i.tagName === "SELECT") i.selectedIndex = 0;
        else i.value = "";
      });
    await carregarCaches();
  } catch (e) {
    document.getElementById("msg-form").innerHTML =
      '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

async function submitUpdate(id) {
  try {
    var data = coletarDadosForm();
    await getApiForEntity().atualizar(id, data);
    editandoId = null;
    await carregarCaches();
    renderTab();
  } catch (e) {
    document.getElementById("msg-form").innerHTML =
      '<div class="msg-erro">Erro: ' + e.message + "</div>";
  }
}

async function deletarRegistro(id) {
  if (!confirm("Excluir registro #" + id + "?")) return;
  try {
    await getApiForEntity().deletar(id);
    await carregarCaches();
    renderTab();
  } catch (e) {
    alert("Erro ao excluir: " + e.message);
  }
}

// ==================== FORMULÁRIOS ====================

function getCamposForm(dados) {
  switch (entidadeAtual) {
    case "CategoriaAlimento":
      return campoTexto("nome", "Nome", dados ? dados.nome : "");
    case "Alimento":
      return (
        campoTexto("nome", "Nome", dados ? dados.nome : "") +
        campoTexto(
          "rotulos",
          "Rótulos (vírgula)",
          dados && dados.rotulos ? dados.rotulos.join(", ") : "",
        ) +
        campoSelect(
          "idCategoriaAlimento",
          "Categoria",
          cacheCategorias,
          dados ? dados.idCategoriaAlimento : null,
        )
      );
    case "Ambiente":
      return (
        campoTexto("nome", "Nome", dados ? dados.nome : "") +
        campoSelectFixo(
          "tipo",
          "Tipo",
          [
            { id: 0, nome: "Geladeira" },
            { id: 1, nome: "Freezer" },
            { id: 2, nome: "Despensa" },
          ],
          dados ? dados.tipo : null,
        )
      );
  }
  return "";
}

function coletarDadosForm() {
  switch (entidadeAtual) {
    case "CategoriaAlimento":
      return { nome: val("nome") };
    case "Alimento":
      var r = val("rotulos");
      return {
        nome: val("nome"),
        rotulos: r
          ? r
              .split(",")
              .map(function (s) {
                return s.trim();
              })
              .filter(function (s) {
                return s;
              })
          : [],
        idCategoriaAlimento: num("idCategoriaAlimento"),
      };
    case "Ambiente":
      return { nome: val("nome"), tipo: num("tipo") };
  }
}

// ==================== HELPERS DE CAMPO ====================

function campoTexto(id, label, valor) {
  return (
    '<div class="form-group"><label>' +
    label +
    '</label><input type="text" id="campo-' +
    id +
    '" value="' +
    (valor || "") +
    '"></div>'
  );
}
function campoNumero(id, label, valor) {
  return (
    '<div class="form-group"><label>' +
    label +
    '</label><input type="number" id="campo-' +
    id +
    '" value="' +
    (valor || "") +
    '"></div>'
  );
}
function campoData(id, label, valor) {
  return (
    '<div class="form-group"><label>' +
    label +
    '</label><input type="date" id="campo-' +
    id +
    '" value="' +
    (valor || "") +
    '"></div>'
  );
}
function campoSelect(id, label, opcoes, valorSelecionado) {
  var html =
    '<div class="form-group"><label>' +
    label +
    '</label><select id="campo-' +
    id +
    '">';
  html += '<option value="">-- selecione --</option>';
  opcoes.forEach(function (op) {
    html +=
      '<option value="' +
      op.id +
      '"' +
      (op.id == valorSelecionado ? " selected" : "") +
      ">" +
      op.id +
      " - " +
      op.nome +
      "</option>";
  });
  return html + "</select></div>";
}
function campoSelectFixo(id, label, opcoes, valorSelecionado) {
  var html =
    '<div class="form-group"><label>' +
    label +
    '</label><select id="campo-' +
    id +
    '">';
  opcoes.forEach(function (op) {
    html +=
      '<option value="' +
      op.id +
      '"' +
      (op.id == valorSelecionado ? " selected" : "") +
      ">" +
      op.nome +
      "</option>";
  });
  return html + "</select></div>";
}

function val(id) {
  var el = document.getElementById("campo-" + id);
  return el ? el.value : "";
}
function num(id) {
  return parseInt(val(id)) || 0;
}

// ==================== HELPERS ====================

function getApiForEntity() {
  switch (entidadeAtual) {
    case "CategoriaAlimento":
      return API.categoriaAlimento;
    case "Alimento":
      return API.alimento;
    case "Ambiente":
      return API.ambiente;
  }
}

function getColunasEntidade() {
  switch (entidadeAtual) {
    case "CategoriaAlimento":
      return [
        { key: "id", label: "ID" },
        { key: "nome", label: "Nome" },
      ];
    case "Alimento":
      return [
        { key: "id", label: "ID" },
        { key: "nome", label: "Nome" },
        { key: "rotulos", label: "Rótulos" },
        { key: "idCategoriaAlimento", label: "Categoria" },
      ];
    case "Ambiente":
      return [
        { key: "id", label: "ID" },
        { key: "nome", label: "Nome" },
        { key: "tipo", label: "Tipo" },
      ];
  }
  return [];
}
