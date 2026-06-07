/**
 * API Client - Comunicação com o backend StockIt (porta 8081)
 */
const BASE_URL = "http://localhost:8081";

const API = {
  categoriaAlimento: {
    listar: () => fetch(BASE_URL + "/categoria-alimento").then((r) => r.json()),
    buscar: (id) =>
      fetch(BASE_URL + "/categoria-alimento/" + id).then((r) => {
        if (!r.ok) throw new Error("Não encontrado");
        return r.json();
      }),
    criar: (data) =>
      fetch(BASE_URL + "/categoria-alimento", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    atualizar: (id, data) =>
      fetch(BASE_URL + "/categoria-alimento/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    deletar: (id) =>
      fetch(BASE_URL + "/categoria-alimento/" + id, { method: "DELETE" }).then(
        function (r) {
          if (r.status === 409)
            return r.json().then(function (d) {
              throw new Error(d.erro);
            });
          if (!r.ok) throw new Error("Erro ao excluir");
          return r.json();
        },
      ),
  },

  alimento: {
    listar: () => fetch(BASE_URL + "/alimentos").then((r) => r.json()),
    buscar: (id) =>
      fetch(BASE_URL + "/alimentos/" + id).then((r) => {
        if (!r.ok) throw new Error("Não encontrado");
        return r.json();
      }),
    criar: (data) =>
      fetch(BASE_URL + "/alimentos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    atualizar: (id, data) =>
      fetch(BASE_URL + "/alimentos/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    deletar: (id) =>
      fetch(BASE_URL + "/alimentos/" + id, { method: "DELETE" }).then(
        function (r) {
          if (r.status === 409)
            return r.json().then(function (d) {
              throw new Error(d.erro);
            });
          if (!r.ok) throw new Error("Erro ao excluir");
          return r.json();
        },
      ),
  },

  ambiente: {
    listar: () => fetch(BASE_URL + "/ambientes").then((r) => r.json()),
    buscar: (id) =>
      fetch(BASE_URL + "/ambientes/" + id).then((r) => {
        if (!r.ok) throw new Error("Não encontrado");
        return r.json();
      }),
    criar: (data) =>
      fetch(BASE_URL + "/ambientes", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    atualizar: (id, data) =>
      fetch(BASE_URL + "/ambientes/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => r.json()),
    deletar: (id) =>
      fetch(BASE_URL + "/ambientes/" + id, { method: "DELETE" }).then(
        function (r) {
          if (r.status === 409)
            return r.json().then(function (d) {
              throw new Error(d.erro);
            });
          if (!r.ok) throw new Error("Erro ao excluir");
          return r.json();
        },
      ),
  },

  itemAmbiente: {
    listar: () => fetch(BASE_URL + "/itens_ambiente").then((r) => r.json()),
    listarOrdenado: () =>
      fetch(BASE_URL + "/itens_ambiente/ordenado").then((r) => r.json()),
    listarPorAmbiente: (id) =>
      fetch(BASE_URL + "/itens_ambiente/por-ambiente/" + id).then((r) =>
        r.json(),
      ),
    listarPorAlimento: (id) =>
      fetch(BASE_URL + "/itens_ambiente/por-alimento/" + id).then((r) =>
        r.json(),
      ),
    arvoreBMais: () =>
      fetch(BASE_URL + "/itens_ambiente/arvore-b-mais").then((r) => r.json()),
    criar: (data) =>
      fetch(BASE_URL + "/itens_ambiente", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => {
        if (r.status === 409)
          return r.json().then((d) => {
            throw new Error(d.erro);
          });
        if (!r.ok) throw new Error("Erro ao criar");
        return r.json();
      }),
    atualizar: (id, data) =>
      fetch(BASE_URL + "/itens_ambiente/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }).then((r) => {
        if (r.status === 409)
          return r.json().then((d) => {
            throw new Error(d.erro);
          });
        if (!r.ok) throw new Error("Erro ao atualizar");
        return r.json();
      }),
    deletar: (id) =>
      fetch(BASE_URL + "/itens_ambiente/" + id, { method: "DELETE" }).then(
        (r) => r.json(),
      ),
  },

  dataView: {
    dat: (entidade) =>
      fetch(BASE_URL + "/data-view/entidades/" + entidade + "/dat").then((r) =>
        r.json(),
      ),
    hash: (entidade, indice) =>
      fetch(
        BASE_URL + "/data-view/entidades/" + entidade + "/hash/" + indice,
      ).then((r) => r.json()),
  },

  ordenacao: {
    executar: (entidade) => {
      var path =
        entidade === "CategoriaAlimento"
          ? "categoria-alimento"
          : entidade.toLowerCase();
      return fetch(BASE_URL + "/ordenacao/" + path).then(function (r) {
        return r.json();
      });
    },
  },

  compressao: {
    huffman: (entidade) =>
      fetch(BASE_URL + "/compressao/" + entidade + "/huffman", { method: "POST" }).then((r) => r.json()),
    lzw: (entidade) =>
      fetch(BASE_URL + "/compressao/" + entidade + "/lzw", { method: "POST" }).then((r) => r.json()),
    downloadHuffman: (entidade) => BASE_URL + "/compressao/" + entidade + "/huffman/download",
    downloadLzw: (entidade) => BASE_URL + "/compressao/" + entidade + "/lzw/download",
  },
};
