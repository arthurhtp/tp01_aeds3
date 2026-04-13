import type { Ambiente, Alimento, Categoria, ItemAmbiente } from './ambiente.types';

const API_URL = 'http://localhost:8081';
const URL_CATEGORIAS = `${API_URL}/categoria-alimento`

export const ambienteService = {
  async getById(id: number): Promise<Ambiente> {
    const res = await fetch(`${API_URL}/ambientes/${id}`);
    if (!res.ok) throw new Error('Erro ao buscar ambiente');
    return res.json();
  },

  async patchItens(id: number, itens: ItemAmbiente[]): Promise<void> {
    const res = await fetch(`${API_URL}/ambientes/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ itens }),
    });
    if (!res.ok) throw new Error('Erro ao atualizar ambiente');
  },

  async getAll(): Promise<Ambiente[]> {
    const res = await fetch(`${API_URL}/ambientes`);
    if (!res.ok) throw new Error('Erro ao buscar ambientes');
    return res.json();
  },
};

export const alimentoService = {
  async getById(id: number): Promise<Alimento> {
    const res = await fetch(`${API_URL}/alimentos/${id}`);
    if (!res.ok) throw new Error('Erro ao buscar alimento');
    return res.json();
  },

  async getAll(): Promise<Alimento[]> {
    const res = await fetch(`${API_URL}/alimentos`);
    if (!res.ok) throw new Error('Erro ao buscar alimentos');
    return res.json();
  },

  async criar(nome: string, idCategoria: number): Promise<number> {
    const res = await fetch(`${API_URL}/alimentos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome, idCategoria }),
    });
    const novo = await res.json();
    return novo.id;
  },

  async verificarOuCriar(nome: string, idCategoria: number): Promise<number> {
    const alimentos = await this.getAll();
    const existente = alimentos.find(
      a => (a.nome || '').trim().toLowerCase() === nome.trim().toLowerCase()
    );
    if (existente) return existente.id;
    return this.criar(nome, idCategoria);
  },
};

export const categoriaService = {
  

  async getAll(): Promise<Categoria[]> {
    const res = await fetch(`${URL_CATEGORIAS}`);
    if (!res.ok) throw new Error('Erro ao buscar categorias');
    return res.json();
  },

  async criar(nomeCategoria: string): Promise<Categoria> {
    const res = await fetch(`${URL_CATEGORIAS}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome:nomeCategoria }),
    });
    if (!res.ok) throw new Error('Erro ao criar categoria');
    return res.json();
  },

  async atualizar(id: number, nomeCategoria: string): Promise<void> {
    const res = await fetch(`${URL_CATEGORIAS}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id, nome:nomeCategoria }),
    });
    if (!res.ok) throw new Error('Erro ao atualizar categoria');
  },

  async deletar(id: number): Promise<void> {
    const res = await fetch(`${URL_CATEGORIAS}/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Erro ao deletar categoria');
  },
};

export const itensAmbienteService = {
  async getAll(): Promise<any[]> {
    const res = await fetch(`${API_URL}/itens_ambiente`);
    if (!res.ok) throw new Error('Erro ao buscar itens');
    return res.json();
  },

  async update(id: number, item: any): Promise<void> {
    const res = await fetch(`${API_URL}/itens_ambiente/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(item),
    });
    if (!res.ok) throw new Error('Erro ao atualizar item');
  },
};
