export const TIPOS = [
  { value: '1', label: 'Geladeira' },
  { value: '2', label: 'Freezer' },
  { value: '3', label: 'Despensa' },
];

export function tipoNumParaChar(tipo: number): string {
  return tipo === 1 ? '1' : tipo === 2 ? '2' : '3';
}
