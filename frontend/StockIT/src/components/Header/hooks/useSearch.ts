import { useState, useCallback } from 'react';
import type { SearchResult, Ambiente } from '../../../types/home.types';
import { ambientesService } from '../../../services/home.service';

export function useSearch() {
  const [query, setQuery] = useState('');
  const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loadingSearch, setLoadingSearch] = useState(false);

  const realizarPesquisa = useCallback(async (value: string) => {
    const q = value.toLowerCase().trim();
    if (!q) {
      setShowDropdown(false);
      setSearchResults([]);
      return;
    }

    setShowDropdown(true);
    setLoadingSearch(true);

    try {
      const ambientes = await ambientesService.getAll();
      const resultados: SearchResult[] = [];

      ambientes.forEach((env: Ambiente) => {
        const envName = env.nome ?? env.nomeAmbiente ?? 'Ambiente';
        if (envName.toLowerCase().includes(q)) {
          resultados.push({ type: 'ambiente', id: env.id, nome: envName });
        }
      });

      setSearchResults(resultados);
    } catch (err) {
      console.error('Erro ao pesquisar:', err);
      setSearchResults([]);
    } finally {
      setLoadingSearch(false);
    }
  }, []);

  const handleResultClick = (result: SearchResult) => {
    setShowDropdown(false);
    setQuery('');
    if (result.type === 'ambiente') {
      window.location.href = `../ambientes/ambiente.html?id=${result.id}`;
    }
  };

  return {
    query,
    setQuery,
    searchResults,
    showDropdown,
    setShowDropdown,
    loadingSearch,
    realizarPesquisa,
    handleResultClick,
  };
}
