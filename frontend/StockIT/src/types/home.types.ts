export interface Ambiente {
  id: number;
  nome?: string;
  nomeAmbiente?: string;
  tipo: 1 | 2 | 3;
  itens?: ItemAmbiente[];
}

export interface ItemAmbiente {
  alimentoId: number;
  quantidade: number;
  vencimento: string;
}

export interface Alimento {
  id: number;
  nome: string;
}

export interface SearchResult {
  type: 'ambiente' | 'alimento';
  id: number;
  nome: string;
  ambienteId?: number;
  ambienteNome?: string;
}

export interface Notificacao {
  texto: string;
  ambienteId: number;
  alimentoId: number;
}
