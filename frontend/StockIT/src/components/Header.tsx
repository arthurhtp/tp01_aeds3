import { useState, useEffect, useRef, useCallback } from 'react';
import type { SearchResult, Notificacao, Ambiente, Alimento } from '../types/home.types';
import { alimentosService, ambientesService } from '../services/home.service';

const DIAS_LIMITE = 3;

function getNomeAmbiente(amb: Ambiente): string {
  return amb.nome ?? amb.nomeAmbiente ?? 'Ambiente';
}

interface HeaderProps {
  onLogout: () => void;
}

export default function Header({ onLogout }: HeaderProps) {
  const [query, setQuery] = useState('');
  const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([]);
  const [showNotif, setShowNotif] = useState(false);
  const [loadingSearch, setLoadingSearch] = useState(false);
  const searchRef = useRef<HTMLDivElement>(null);
  const notifRef = useRef<HTMLDivElement>(null);

  // Fechar ao clicar fora
  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (searchRef.current && !searchRef.current.contains(e.target as Node)) {
        setShowDropdown(false);
      }
      if (notifRef.current && !notifRef.current.contains(e.target as Node)) {
        setShowNotif(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Carregar notificações
  useEffect(() => {
    async function carregarNotificacoes() {
      try {
        const ambientes = await ambientesService.getAll();

        const hoje = new Date();
        const vencendo: Notificacao[] = [];

        ambientes.forEach(amb => {
          amb.itens?.forEach(item => {
            const vencimento = new Date(item.vencimento);
            const diffDias = (vencimento.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24);
            if (diffDias <= DIAS_LIMITE) {
              vencendo.push({
                texto: `Produto (${item.quantidade} un) - ${getNomeAmbiente(amb)}`,
                ambienteId: amb.id,
                alimentoId: item.alimentoId,
              });
            }
          });
        });

        setNotificacoes(vencendo);
      } catch (err) {
        console.error('Erro ao carregar notificações:', err);
      }
    }

    carregarNotificacoes();
  }, []);

const realizarPesquisa = useCallback(async (value: string) => {
  const q = value.toLowerCase().trim();
  if (!q) { setShowDropdown(false); setSearchResults([]); return; }

  setShowDropdown(true);
  setLoadingSearch(true);

  try {
    const [ambientes, alimentos]: [Ambiente[], Alimento[]] = await Promise.all([
      ambientesService.getAll(),
      alimentosService.getAll(),
    ]);

    const resultados: SearchResult[] = [];

    ambientes.forEach(env => {
      if (getNomeAmbiente(env).toLowerCase().includes(q)) {
        resultados.push({ type: 'ambiente', id: env.id, nome: getNomeAmbiente(env) });
      }
    });

    alimentos.forEach(alimento => {
      if (alimento.nome.toLowerCase().includes(q)) {
        resultados.push({ type: 'alimento', id: alimento.id, nome: alimento.nome, ambienteId: alimento.ambienteId, ambienteNome: alimento.ambienteNome });
      }
    });

    setSearchResults(resultados);
  } catch (err) {
    console.error('Erro ao pesquisar:', err);
  } finally {
    setLoadingSearch(false);
  }
}, []);

  function handleResultClick(result: SearchResult) {
    setShowDropdown(false);
    setQuery('');
    if (result.type === 'ambiente') {
      window.location.href = `../ambientes/ambiente.html?id=${result.id}`;
    } else {
      window.location.href = `../ambientes/ambiente.html?id=${result.ambienteId}&focus=${result.id}`;
    }
  }

  return (
    <header className="bg-emerald-600 text-white px-8">
      <div className="flex items-center py-3 gap-4">
        {/* Logo */}
        <div className="flex items-center gap-3 w-48 shrink-0">
          <h1 className="text-2xl font-bold text-white">StockIT</h1>
        </div>

        {/* Search */}
        <div className="flex-1 max-w-xl mx-auto relative" ref={searchRef}>
          <div className="flex items-center bg-white/90 rounded-full px-5 h-12 gap-3">
            <svg className="w-5 h-5 text-gray-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input
              type="text"
              value={query}
              onChange={e => { setQuery(e.target.value); realizarPesquisa(e.target.value); }}
              onFocus={() => query && realizarPesquisa(query)}
              placeholder="Pesquise produtos e ambientes..."
              className="flex-1 bg-transparent outline-none text-gray-800 text-base placeholder-gray-400"
            />
          </div>

          {showDropdown && (
            <div className="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden">
              {loadingSearch ? (
                <div className="px-4 py-3 text-gray-500 text-sm">Buscando...</div>
              ) : searchResults.length === 0 ? (
                <div className="px-4 py-3 text-gray-500 text-sm">Nenhum resultado encontrado.</div>
              ) : (
                searchResults.map((r, i) => (
                  <button
                    key={i}
                    onClick={() => handleResultClick(r)}
                    className="w-full text-left px-4 py-3 hover:bg-gray-50 border-b border-gray-100 last:border-0 text-sm text-gray-700 transition-colors"
                  >
                    <span className="text-xs text-emerald-600 font-medium mr-2">
                      {r.type === 'ambiente' ? '📦' : '🍎'}
                    </span>
                    {r.type === 'ambiente' ? r.nome : `${r.nome} (${r.ambienteNome})`}
                  </button>
                ))
              )}
            </div>
          )}
        </div>

        {/* User area */}
        <div className="flex items-center gap-4 shrink-0">
          {/* Notificações */}
          <div className="relative" ref={notifRef}>
            <button
              onClick={() => setShowNotif(v => !v)}
              className="relative text-white/80 hover:text-white transition-colors"
            >
              <svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
              {notificacoes.length > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
                  {notificacoes.length}
                </span>
              )}
            </button>

            {showNotif && (
              <div className="absolute top-full right-0 mt-3 bg-white border border-gray-200 rounded-2xl shadow-xl z-50 w-80 max-h-96 overflow-y-auto p-4">
                <p className="font-semibold text-gray-800 mb-3">Notificações</p>
                {notificacoes.length === 0 ? (
                  <p className="text-sm text-gray-500">Nenhum alimento vencendo nos próximos {DIAS_LIMITE} dias.</p>
                ) : (
                  notificacoes.map((n, i) => (
                    <div key={i} className="py-3 border-b border-gray-100 last:border-0 text-sm text-gray-700">
                      {n.texto}
                    </div>
                  ))
                )}
              </div>
            )}
          </div>

          {/* Logout */}
          <button
            onClick={onLogout}
            className="border border-white/60 text-white text-sm px-3 py-1.5 rounded-md hover:bg-white/10 transition-colors"
          >
            Sair
          </button>
        </div>
      </div>
    </header>
  );
}
