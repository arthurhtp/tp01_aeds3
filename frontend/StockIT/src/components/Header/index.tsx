import {
  Logo,
  SearchBar,
  NotificationBell,
  UserActions,
} from './components';
import { useSearch, useNotifications, useClickOutside } from './hooks';

interface HeaderProps {
  onLogout: () => void;
}

export function Header({ onLogout }: HeaderProps) {
  const {
    query,
    setQuery,
    searchResults,
    showDropdown,
    setShowDropdown,
    loadingSearch,
    realizarPesquisa,
    handleResultClick,
  } = useSearch();

  const { notificacoes, showNotif, setShowNotif, DIAS_LIMITE } = useNotifications();

  const searchRef = useClickOutside({
    onClickOutside: () => setShowDropdown(false),
  });

  const notifRef = useClickOutside({
    onClickOutside: () => setShowNotif(false),
  });

  return (
    <header className="bg-emerald-600 text-white px-8">
      <div className="flex items-center py-3 gap-4">
        <Logo />

        <SearchBar
          query={query}
          onQueryChange={(value) => {
            setQuery(value);
            realizarPesquisa(value);
          }}
          showDropdown={showDropdown}
          loading={loadingSearch}
          results={searchResults}
          onResultClick={handleResultClick}
          onInputFocus={() => query && realizarPesquisa(query)}
          searchRef={searchRef}
        />

        <div className="flex items-center gap-4 shrink-0">
          <NotificationBell
            show={showNotif}
            onToggle={() => setShowNotif(v => !v)}
            notificacoes={notificacoes}
            diasLimite={DIAS_LIMITE}
            notifRef={notifRef}
          />

          <UserActions onLogout={onLogout} />
        </div>
      </div>
    </header>
  );
}
