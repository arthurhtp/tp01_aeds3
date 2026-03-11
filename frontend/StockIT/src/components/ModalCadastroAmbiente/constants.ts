export const TIPOS = [
  { value: '1', label: 'Refrigerador' },
  { value: '2', label: 'Congelador' },
  { value: '3', label: 'Despensa' },
];

export function tipoNumParaChar(tipo: number): string {
  return tipo === 1 ? '1' : tipo === 2 ? '2' : '3';
}
