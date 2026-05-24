/**
 * API Client - Comunicação com o backend StockIt (porta 8081)
 */
const BASE_URL = "http://localhost:8081";

const API = {
  // ==================== CATEGORIA ALIMENTO ====================
  categoriaAlimento: {
    listar: () => fetch(`${BASE_URL}/categoria-alimento`).then((r) => r.json()),
    buscar: (id) =>
      fetch(`${BASE_URL}/categoria-alimento/${id}`).then((r) => {
        if (!r.ok) throw new Error("Não encontrado");
        return r.json();
      }),
    criar: (data) =>
      fetch(`${BASE_URL}/categoria-alimento`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    atualizar: (id, data) =>
      fetch(`${BASE_URL}/categoria-alimento/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    deletar: (id) =>
      fetch(`${BASE_URL}/categoria-alimento/${id}`, {
        method: "DELETE",
      }).then((r) => r.json()),
  },

  // ==================== ALIMENTO ====================
  alimento: {
    listar: () => fetch(`${BASE_URL}/alimentos`).then((r) => r.json()),
    buscar: (id) =>
      fetch(`${BASE_URL}/alimentos/${id}`).then((r) => {
        if (!r.ok) throw new Error("Não encontrado");
        return r.json();
      }),
    criar: (data) =>
      fetch(`${BASE_URL}/alimentos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    atualizar: (id, data) =>
      fetch(`${BASE_URL}/alimentos/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    deletar: (id) =>
      fetch(`${BASE_URL}/alimentos/${id}`, {
        method: "DELETE",
      }).then((r) => r.json()),
  },

  // ==================== AMBIENTE ====================
  ambiente: {
    listar: () => fetch(`${BASE_URL}/ambientes`).then((r) => r.json()),
    buscar: (id) =>
      fetch(`${BASE_URL}/ambientes/${id}`).then((r) => {
        if (!r.ok) throw new Error("Não encontrado");
        return r.json();
      }),
    criar: (data) =>
      fetch(`${BASE_URL}/ambientes`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    atualizar: (id, data) =>
      fetch(`${BASE_URL}/ambientes/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    deletar: (id) =>
      fetch(`${BASE_URL}/ambientes/${id}`, {
        method: "DELETE",
      }).then((r) => r.json()),
  },

  // ==================== ITEM AMBIENTE ====================
  itemAmbiente: {
    listar: () => fetch(`${BASE_URL}/itens_ambiente`).then((r) => r.json()),
    buscar: (id) =>
      fetch(`${BASE_URL}/itens_ambiente/${id}`).then((r) => {
        if (!r.ok) throw new Error("Não encontrado");
        return r.json();
      }),
    criar: (data) =>
      fetch(`${BASE_URL}/itens_ambiente`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    atualizar: (id, data) =>
      fetch(`${BASE_URL}/itens_ambiente/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    deletar: (id) =>
      fetch(`${BASE_URL}/itens_ambiente/${id}`, {
        method: "DELETE",
      }).then((r) => r.json()),
  },

  // ==================== DATA VIEW (Visualização de dados) ====================
  dataView: {
    entidades: () =>
      fetch(`${BASE_URL}/data-view/entidades`).then((r) => r.json()),
    arquivos: (entidade) =>
      fetch(`${BASE_URL}/data-view/entidades/${entidade}/arquivos`).then((r) =>
        r.json(),
      ),
    dat: (entidade) =>
      fetch(`${BASE_URL}/data-view/entidades/${entidade}/dat`).then((r) =>
        r.json(),
      ),
    hash: (entidade, nomeIndice) =>
      fetch(
        `${BASE_URL}/data-view/entidades/${entidade}/hash/${nomeIndice}`,
      ).then((r) => r.json()),
    raw: (entidade, arquivo) =>
      fetch(`${BASE_URL}/data-view/entidades/${entidade}/raw/${arquivo}`).then(
        (r) => r.json(),
      ),
  },
};
