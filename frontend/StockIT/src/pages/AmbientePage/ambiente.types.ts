export interface Alimento {
  id: number;
  nome: string;
  idCategoria?: number;
}

export interface ItemAmbiente {
  alimentoId: number;
  quantidade: number;
  cadastro: string;
  vencimento: string;
}

export interface Ambiente {
  id: number;
  nome: string;
  tipo: number;
  itens: ItemAmbiente[];
}

export interface Categoria {
  id: number;
  nome: string;
}

export interface AlimentoTabela {
  index: number;
  nome: string;
  cadastro: string;
  vencimento: string;
  quantidade: number;
  vencido: boolean;
}

export interface DadosCadastro {
  nome: string;
  idCategoria: number;
  quantidade: number;
  vencimento: string;
  cadastro: string;
}
