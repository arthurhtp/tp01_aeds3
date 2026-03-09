import type { Ambiente, Alimento } from "../types/home.types";

const API_URL = "http://localhost:8081";

export const ambientesService = {
  async getAll(): Promise<Ambiente[]> {
    const res = await fetch(`${API_URL}/ambientes`);
    if (!res.ok) throw new Error("Erro ao buscar ambientes");
    return res.json();
  },

  async getById(id: number): Promise<Ambiente> {
    const res = await fetch(`${API_URL}/ambientes/${id}`);
    if (!res.ok) throw new Error("Erro ao buscar ambiente");
    return res.json();
  },

  async create(nome: string, tipo: string): Promise<void> {
    const res = await fetch(`${API_URL}/ambientes`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nome: nome, tipo: tipo }),
    });
    if (!res.ok) throw new Error("Erro ao criar ambiente");
  },
  
  async update(id: number, nome: string, tipoChar: string): Promise<void> {
    const res = await fetch(`${API_URL}/ambientes/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nome: nome, tipo: tipoChar }),
    });
    if (!res.ok) throw new Error("Erro ao atualizar ambiente");
  },

  async delete(id: number): Promise<void> {
    const res = await fetch(`${API_URL}/ambientes/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Erro ao excluir ambiente");
  },
};

export const alimentosService = {
  async getAll(): Promise<Alimento[]> {
    const res = await fetch(`${API_URL}/alimentos`);
    if (!res.ok) throw new Error("Erro ao buscar alimentos");
    return res.json();
  },
};
