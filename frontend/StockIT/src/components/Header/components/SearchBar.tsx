import { SearchInput } from './SearchInput';
import { SearchDropdown } from './SearchDropdown';
import type { SearchResult } from '../../../types/home.types';

interface SearchBarProps {
  query: string;
  onQueryChange: (value: string) => void;
  showDropdown: boolean;
  loading: boolean;
  results: SearchResult[];
  onResultClick: (result: SearchResult) => void;
  onInputFocus: () => void;
  searchRef: React.RefObject<HTMLDivElement>;
}

export function SearchBar({
  query,
  onQueryChange,
  showDropdown,
  loading,
  results,
  onResultClick,
  onInputFocus,
  searchRef,
}: SearchBarProps) {
  return (
    <div className="flex-1 max-w-xl mx-auto relative" ref={searchRef}>
      <SearchInput
        value={query}
        onChange={e => onQueryChange(e)}
        onFocus={onInputFocus}
      />
      <SearchDropdown
        show={showDropdown}
        loading={loading}
        results={results}
        onResultClick={onResultClick}
      />
    </div>
  );
}
