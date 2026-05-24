/**
 * StockIt Frontend
 */

let entidadeAtual = null;
let tabAtual = "listagem";
let editandoId = null;
let ambienteItensId = null; // ID do ambiente cujos itens estão sendo visualizados
let editandoItemId = null;

// Cache para dropdowns
let cacheCategorias = [];
let cacheAlimentos = [];
let cacheAmbientes = [];

// ==================== NAVEGAÇÃO ====================

function navegarEntidade(entidade) {
  entidadeAtual = entidade;
  ambienteItensId = null;
  editandoItemId = null;
  document.getElementById("tela-inicial").classList.add("hidden");
  document.getElementById("tela-entidade").classList.remove("hidden");
  document.getElementById("titulo-entidade").textContent = entidade;
  tabAtual = "listagem";
  updateTabButtons();
  carregarCaches().then(() => renderTab());
}

function voltarInicio() {
  entidadeAtual = null;
  editandoId = null;
  ambienteItensId = null;
  document.getElementById("tela-entidade").classList.add("hidden");
  document.getElementById("tela-inicial").classList.remove("hidden");
}

function setTab(tab) {
  tabAtual = tab;
  editandoId = null;
  ambienteItensId = null;
  editandoItemId = null;
  updateTabButtons();
  renderTab();
}

function updateTabButtons() {
  document
    .querySelectorAll(".acoes-bar .btn-acao")
    .forEach((btn) => btn.classList.remove("active"));
  const tabs = ["listagem", "criar", "hash", "encoding"];
  const idx = tabs.indexOf(tabAtual);
  if (idx >= 0)
    document
      .querySelectorAll(".acoes-bar .btn-acao")
      [idx].classList.add("active");
}

function renderTab() {
  const container = document.getElementById("tab-content");
  switch (tabAtual) {
    case "listagem":
      renderListagem(container);
      break;
    case "criar":
      renderFormCriar(container);
      break;
    case "hash":
      renderHash(container);
      break;
    case "encoding":
      renderEncoding(container);
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

// ==================== LISTAGEM ====================

async function renderListagem(container) {
  // Se estamos vendo itens de um ambiente
  if (entidadeAtual === "Ambiente" && ambienteItensId !== null) {
    renderItensAmbiente(container);
    return;
  }

  container.innerHTML = '<div class="loading">Carregando...</div>';

  try {
    let dados = await getApiForEntity().listar();
    if (!dados || dados.length === 0) {
      container.innerHTML =
        '<div class="empty-state">Nenhum registro encontrado.</div>';
      return;
    }

    dados = resolverFKs(dados);

    let html = "";

    if (editandoId !== null) {
      const item = dados.find((d) => d.id === editandoId);
      if (item) html += renderFormEditar(item);
    }

    html += '<div class="table-container"><table>';
    html += "<thead><tr>";
    const colunas = getColunasEntidade();
    colunas.forEach((col) => {
      html += `<th>${col.label}</th>`;
    });
    html += "<th>Ações</th></tr></thead><tbody>";

    dados.forEach((item) => {
      html += "<tr>";
      colunas.forEach((col) => {
        let val = item[col.key];
        if (col.fk && item[col.fk]) {
          val = `${val} <span class="fk-badge">${item[col.fk]}</span>`;
        }
        if (Array.isArray(val)) val = val.join(", ");
        html += `<td>${val ?? "-"}</td>`;
      });
      html += `<td class="row-actions">`;
      if (entidadeAtual === "Ambiente") {
        html += `<button class="btn-row" onclick="verItensAmbiente(${item.id})">itens</button>`;
      }
      html += `<button class="btn-row" onclick="iniciarEdicao(${item.id})">editar</button>`;
      html += `<button class="btn-row delete" onclick="deletarRegistro(${item.id})">excluir</button>`;
      html += `</td></tr>`;
    });

    html += "</tbody></table></div>";
    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = `<div class="msg-erro">Erro ao carregar: ${e.message}</div>`;
  }
}

// ==================== ITENS DO AMBIENTE ====================

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
    const ambiente = cacheAmbientes.find((a) => a.id === ambienteItensId) || {
      nome: "Ambiente #" + ambienteItensId,
    };
    const itens = await API.itemAmbiente.listar();
    const itensFiltrados = itens.filter(
      (i) => i.ambienteId === ambienteItensId,
    );

    const aliMap = {};
    cacheAlimentos.forEach((a) => {
      aliMap[a.id] = a.nome;
    });

    let html = "";
    html += `<button class="btn-voltar" onclick="voltarListaAmbientes()">← voltar para ambientes</button>`;
    html += `<h3 style="margin-bottom:16px;">Itens em: ${ambiente.nome}</h3>`;

    // Form de novo item
    html += `<div class="form-panel"><h3>Adicionar item</h3><form onsubmit="event.preventDefault();">`;
    html += `<div class="form-row">`;
    html += campoSelect("novo-alimentoId", "Alimento", cacheAlimentos, null);
    html += campoNumero("novo-quantidade", "Quantidade", "");
    html += campoData("novo-dataCadastro", "Data Cadastro", "");
    html += campoData("novo-dataVencimento", "Data Vencimento", "");
    html += `</div>`;
    html += `<button class="btn-submit" onclick="criarItemAmbiente()">Adicionar</button>`;
    html += `</form><div id="msg-item-criar"></div></div>`;

    // Form de edição (se editando)
    if (editandoItemId !== null) {
      const itemEdit = itensFiltrados.find((i) => i.id === editandoItemId);
      if (itemEdit) {
        html += `<div class="form-panel"><h3>Editando item #${itemEdit.id}</h3><form onsubmit="event.preventDefault();">`;
        html += `<div class="form-row">`;
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
        html += `</div>`;
        html += `<button class="btn-submit" onclick="salvarItemAmbiente(${itemEdit.id})">Salvar</button>`;
        html += `<button class="btn-cancel" onclick="cancelarEdicaoItem()">Cancelar</button>`;
        html += `</form><div id="msg-item-edit"></div></div>`;
      }
    }

    // Tabela de itens
    if (itensFiltrados.length === 0) {
      html += '<div class="empty-state">Nenhum item neste ambiente.</div>';
    } else {
      html += '<div class="table-container"><table>';
      html +=
        "<thead><tr><th>ID</th><th>Alimento</th><th>Qtd</th><th>Cadastro</th><th>Vencimento</th><th>Ações</th></tr></thead><tbody>";
      itensFiltrados.forEach((item) => {
        const nomeAli = aliMap[item.alimentoId] || `ID ${item.alimentoId}`;
        html += `<tr>`;
        html += `<td>${item.id}</td>`;
        html += `<td>${item.alimentoId} <span class="fk-badge">${nomeAli}</span></td>`;
        html += `<td>${item.quantidade}</td>`;
        html += `<td>${item.dataCadastro || "-"}</td>`;
        html += `<td>${item.dataVencimento || "-"}</td>`;
        html += `<td class="row-actions">`;
        html += `<button class="btn-row" onclick="editarItemAmbiente(${item.id})">editar</button>`;
        html += `<button class="btn-row delete" onclick="deletarItemAmbiente(${item.id})">excluir</button>`;
        html += `</td></tr>`;
      });
      html += "</tbody></table></div>";
    }

    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = `<div class="msg-erro">Erro: ${e.message}</div>`;
  }
}

async function criarItemAmbiente() {
  try {
    const data = {
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
      `<div class="msg-erro">Erro: ${e.message}</div>`;
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
    const data = {
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
      `<div class="msg-erro">Erro: ${e.message}</div>`;
  }
}

async function deletarItemAmbiente(id) {
  if (!confirm(`Excluir item #${id}?`)) return;
  try {
    await API.itemAmbiente.deletar(id);
    renderTab();
  } catch (e) {
    alert("Erro: " + e.message);
  }
}

// ==================== COLUNAS ====================

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
        {
          key: "idCategoriaAlimento",
          label: "Categoria",
          fk: "_categoriaNome",
        },
      ];
    case "Ambiente":
      return [
        { key: "id", label: "ID" },
        { key: "nome", label: "Nome" },
        { key: "tipo", label: "Tipo", fk: "_tipoNome" },
      ];
  }
  return [];
}

function resolverFKs(dados) {
  const catMap = {};
  cacheCategorias.forEach((c) => {
    catMap[c.id] = c.nome;
  });
  const tipoMap = { 0: "Geladeira", 1: "Freezer", 2: "Despensa" };

  dados.forEach((d) => {
    if (entidadeAtual === "Alimento") {
      d._categoriaNome = catMap[d.idCategoriaAlimento] || "";
    }
    if (entidadeAtual === "Ambiente") {
      d._tipoNome = tipoMap[d.tipo] || `Tipo ${d.tipo}`;
    }
  });
  return dados;
}

// ==================== EDIÇÃO ====================

function iniciarEdicao(id) {
  editandoId = id;
  renderTab();
}

function cancelarEdicao() {
  editandoId = null;
  renderTab();
}

function renderFormEditar(item) {
  let html =
    '<div class="form-panel"><h3>Editando #' +
    item.id +
    '</h3><form onsubmit="event.preventDefault();">';
  html += '<div class="form-row">';
  html += getCamposForm(item);
  html += "</div>";
  html += `<button class="btn-submit" onclick="submitUpdate(${item.id})">Salvar</button>`;
  html += `<button class="btn-cancel" onclick="cancelarEdicao()">Cancelar</button>`;
  html += '</form><div id="msg-form"></div></div>';
  return html;
}

async function submitUpdate(id) {
  try {
    const data = coletarDadosForm();
    await getApiForEntity().atualizar(id, data);
    editandoId = null;
    await carregarCaches();
    renderTab();
  } catch (e) {
    document.getElementById("msg-form").innerHTML =
      `<div class="msg-erro">Erro: ${e.message}</div>`;
  }
}

// ==================== CREATE ====================

function renderFormCriar(container) {
  let html =
    '<div class="form-panel"><h3>Novo registro</h3><form onsubmit="event.preventDefault();">';
  html += '<div class="form-row">';
  html += getCamposForm(null);
  html += "</div>";
  html += `<button class="btn-submit" onclick="submitCreate()">Criar</button>`;
  html += '</form><div id="msg-form"></div></div>';
  container.innerHTML = html;
}

async function submitCreate() {
  try {
    const data = coletarDadosForm();
    const resultado = await getApiForEntity().criar(data);
    document.getElementById("msg-form").innerHTML =
      `<div class="msg-sucesso">Criado com ID ${resultado.id}</div>`;
    document
      .querySelectorAll(".form-panel input, .form-panel select")
      .forEach((i) => {
        if (i.tagName === "SELECT") i.selectedIndex = 0;
        else i.value = "";
      });
    await carregarCaches();
  } catch (e) {
    document.getElementById("msg-form").innerHTML =
      `<div class="msg-erro">Erro: ${e.message}</div>`;
  }
}

// ==================== DELETE ====================

async function deletarRegistro(id) {
  if (!confirm(`Excluir registro #${id}?`)) return;
  try {
    await getApiForEntity().deletar(id);
    await carregarCaches();
    renderTab();
  } catch (e) {
    alert("Erro ao excluir: " + e.message);
  }
}

// ==================== HASH ====================

async function renderHash(container) {
  const indices = getIndicesHash();
  let html = '<div class="panel"><h4>Hash Extensível</h4>';
  html += '<div class="hash-tabs">';
  indices.forEach((idx, i) => {
    html += `<button class="hash-tab${i === 0 ? " active" : ""}" onclick="carregarHash('${idx}', this)">${idx}</button>`;
  });
  html +=
    '</div><div id="hash-resultado"><div class="loading">Carregando...</div></div></div>';
  container.innerHTML = html;
  if (indices.length > 0) carregarHash(indices[0]);
}

async function carregarHash(nomeIndice, btn) {
  if (btn) {
    document
      .querySelectorAll(".hash-tab")
      .forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");
  }
  const div = document.getElementById("hash-resultado");
  div.innerHTML = '<div class="loading">Carregando...</div>';

  try {
    const data = await API.dataView.hash(entidadeAtual, nomeIndice);
    let html = `<p style="font-size:0.8rem;color:#666;margin-bottom:12px;">Profundidade global: ${data.profundidadeGlobal} | Entradas: ${data.tamanhoDir}</p>`;

    html +=
      "<table><thead><tr><th>Idx</th><th>Bin</th><th>→ Bucket</th></tr></thead><tbody>";
    data.diretorio.forEach((e) => {
      html += `<tr><td>${e.indice}</td><td style="font-family:monospace;">${e.binario}</td><td>${e.enderecoBucket}</td></tr>`;
    });
    html += "</tbody></table>";

    html += '<h4 style="margin-top:16px;">Buckets</h4>';
    data.buckets.forEach((bucket) => {
      html += `<div class="hash-bucket">`;
      html += `<h5>@ ${bucket.endereco} · prof. local: ${bucket.profundidadeLocal} · count: ${bucket.count}</h5>`;
      bucket.slots.forEach((slot, i) => {
        const cls = slot.ativo ? "ativo" : "inativo";
        html += `<div class="hash-slot ${cls}">[${i}] ${slot.interpretado}</div>`;
      });
      html += `</div>`;
    });

    div.innerHTML = html;
  } catch (e) {
    div.innerHTML = `<div class="msg-erro">Erro: ${e.message}</div>`;
  }
}

// ==================== ENCODING ====================

async function renderEncoding(container) {
  container.innerHTML =
    '<div class="panel"><h4>Codificação dos Registros</h4><div class="loading">Carregando...</div></div>';

  try {
    const data = await API.dataView.dat(entidadeAtual);
    let html =
      '<div class="panel"><h4>Codificação — ' + entidadeAtual + ".dat</h4>";
    html += `<p style="font-size:0.8rem;color:#666;margin-bottom:12px;">Último ID: ${data.ultimoId} · Tamanho: ${data.tamanhoArquivo} bytes</p>`;

    if (data.registros.length === 0) {
      html += '<div class="empty-state">Arquivo vazio.</div>';
    }

    data.registros.forEach((reg) => {
      html += `<div class="encoding-registro">`;
      html += `<div class="encoding-meta">`;
      html += `<span>pos: ${reg.posicao}</span>`;
      html += `<span>lápide: '${reg.lapide}' ${reg.ativo ? "✓" : "✗"}</span>`;
      html += `<span>${reg.tamanhoBytes} bytes</span>`;
      html += `</div>`;
      html += `<div class="encoding-hex">${reg.hexDados}</div>`;
      html += `<div class="encoding-legivel">${reg.dadosLegiveis}</div>`;
      html += `</div>`;
    });

    html += "</div>";
    container.innerHTML = html;
  } catch (e) {
    container.innerHTML = `<div class="msg-erro">Erro: ${e.message}</div>`;
  }
}

// ==================== FORMULÁRIOS ====================

function getCamposForm(dados) {
  let html = "";
  switch (entidadeAtual) {
    case "CategoriaAlimento":
      html += campoTexto("nome", "Nome", dados?.nome || "");
      break;
    case "Alimento":
      html += campoTexto("nome", "Nome", dados?.nome || "");
      html += campoTexto(
        "rotulos",
        "Rótulos (vírgula)",
        dados?.rotulos?.join(", ") || "",
      );
      html += campoSelect(
        "idCategoriaAlimento",
        "Categoria",
        cacheCategorias,
        dados?.idCategoriaAlimento,
      );
      break;
    case "Ambiente":
      html += campoTexto("nome", "Nome", dados?.nome || "");
      html += campoSelectFixo(
        "tipo",
        "Tipo",
        [
          { id: 0, nome: "Geladeira" },
          { id: 1, nome: "Freezer" },
          { id: 2, nome: "Despensa" },
        ],
        dados?.tipo,
      );
      break;
  }
  return html;
}

function campoTexto(id, label, valor) {
  return `<div class="form-group"><label>${label}</label><input type="text" id="campo-${id}" value="${valor}"></div>`;
}

function campoNumero(id, label, valor) {
  return `<div class="form-group"><label>${label}</label><input type="number" id="campo-${id}" value="${valor}"></div>`;
}

function campoData(id, label, valor) {
  return `<div class="form-group"><label>${label}</label><input type="date" id="campo-${id}" value="${valor}"></div>`;
}

function campoSelect(id, label, opcoes, valorSelecionado) {
  let html = `<div class="form-group"><label>${label}</label><select id="campo-${id}">`;
  html += `<option value="">-- selecione --</option>`;
  opcoes.forEach((op) => {
    const sel = op.id == valorSelecionado ? " selected" : "";
    html += `<option value="${op.id}"${sel}>${op.id} - ${op.nome}</option>`;
  });
  html += `</select></div>`;
  return html;
}

function campoSelectFixo(id, label, opcoes, valorSelecionado) {
  let html = `<div class="form-group"><label>${label}</label><select id="campo-${id}">`;
  opcoes.forEach((op) => {
    const sel = op.id == valorSelecionado ? " selected" : "";
    html += `<option value="${op.id}"${sel}>${op.nome}</option>`;
  });
  html += `</select></div>`;
  return html;
}

function coletarDadosForm() {
  switch (entidadeAtual) {
    case "CategoriaAlimento":
      return { nome: val("nome") };
    case "Alimento":
      const r = val("rotulos");
      return {
        nome: val("nome"),
        rotulos: r
          ? r
              .split(",")
              .map((s) => s.trim())
              .filter((s) => s)
          : [],
        idCategoriaAlimento: num("idCategoriaAlimento"),
      };
    case "Ambiente":
      return { nome: val("nome"), tipo: num("tipo") };
  }
}

function val(id) {
  return document.getElementById("campo-" + id)?.value || "";
}
function num(id) {
  return parseInt(document.getElementById("campo-" + id)?.value) || 0;
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
